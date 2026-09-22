from flask import Flask, request, jsonify
from flask_cors import CORS

import os
import tempfile
from collections import Counter
from emotion_history import create_analysis_record

import cv2
import torch
import torch.nn as nn

from PIL import Image
from torchvision import models, transforms
from ultralytics import YOLO
from huggingface_hub import hf_hub_download


app = Flask(__name__)
CORS(app)


# Emotion classes used by the model
EMOTIONS = [
    "angry",
    "disgusted",
    "fearful",
    "happy",
    "neutral",
    "sad",
    "surprised"
]


# Use GPU if available, otherwise CPU
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")


# ResNet model structure
class EmotionResNet(nn.Module):
    def __init__(self, num_classes):
        super().__init__()

        self.backbone = models.resnet34(weights=None)

        in_features = self.backbone.fc.in_features

        self.backbone.fc = nn.Sequential(
            nn.Linear(in_features, 256),
            nn.ReLU(),
            nn.Dropout(0.2),
            nn.Linear(256, num_classes)
        )

    def forward(self, x):
        return self.backbone(x)


# Load emotion model
model_path = os.path.join(
    os.path.dirname(os.path.abspath(__file__)),
    "best_resnet34.pth"
)

model = EmotionResNet(len(EMOTIONS)).to(device)

model.load_state_dict(
    torch.load(model_path, map_location=device)
)

model.eval()

print("Emotion model loaded successfully")
print("Using device:", device)


# Transform used before sending face image to the model
transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize(
        mean=[0.485, 0.456, 0.406],
        std=[0.229, 0.224, 0.225]
    )
])


# Load YOLO face detector
print("Loading face detection model...")

face_model_path = hf_hub_download(
    repo_id="arnabdhar/YOLOv8-Face-Detection",
    filename="model.pt"
)

face_model = YOLO(face_model_path)

print("Face detection model loaded successfully")


# Find a face in the current frame
def detect_face(frame):
    results = face_model(frame, verbose=False, conf=0.25)

    if not results or results[0].boxes is None:
        return None

    boxes = results[0].boxes

    if len(boxes) == 0:
        return None

    # For now use the first detected face
    box = boxes[0].xyxy[0].cpu().numpy()

    x1, y1, x2, y2 = map(int, box)

    height, width = frame.shape[:2]

    x1 = max(0, x1)
    y1 = max(0, y1)
    x2 = min(width, x2)
    y2 = min(height, y2)

    face = frame[y1:y2, x1:x2]

    if face.size == 0:
        return None

    face = cv2.cvtColor(face, cv2.COLOR_BGR2RGB)

    return face


# Predict the emotion from a cropped face
def predict_emotion(face):
    image = Image.fromarray(face).convert("RGB")

    image_tensor = transform(image).unsqueeze(0).to(device)

    with torch.no_grad():
        output = model(image_tensor)

        probabilities = torch.softmax(output, dim=1)

        confidence, prediction = torch.max(probabilities, 1)

    emotion = EMOTIONS[prediction.item()]

    return emotion, confidence.item()


# Simple test route
@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "status": "ok",
        "message": "Guardian Emotion API is running"
    })


# Analyse uploaded video
@app.route("/analyse-video", methods=["POST"])
def analyse_video():

    if "video" not in request.files:
        return jsonify({
            "error": "No video file was uploaded"
        }), 400

    video = request.files["video"]

    if video.filename == "":
        return jsonify({
            "error": "No video file was selected"
        }), 400

    # Get patient ID from the form
    patient_id = request.form.get("patient_id")

    print("Patient ID received:", patient_id, flush=True)

    if not patient_id or not patient_id.strip():
        return jsonify({
            "error": "No patient was selected"
        }), 400


    # Save the video temporarily
    extension = os.path.splitext(video.filename)[1]

    if not extension:
        extension = ".mp4"

    temp_file = tempfile.NamedTemporaryFile(
        delete=False,
        suffix=extension
    )

    temp_path = temp_file.name
    temp_file.close()

    video.save(temp_path)



    try:
        capture = cv2.VideoCapture(temp_path)

        if not capture.isOpened():
            return jsonify({
                "error": "Could not open video"
            }), 400


        fps = capture.get(cv2.CAP_PROP_FPS)

        if fps <= 0:
            fps = 30


        timeline = []

        frame_number = 0

        # Analyse about one frame per second
        frame_interval = max(int(fps), 1)


        while True:
            success, frame = capture.read()

            if not success:
                break

            if frame_number % frame_interval == 0:

                face = detect_face(frame)

                if face is not None:
                    emotion, confidence = predict_emotion(face)

                    time_seconds = frame_number / fps

                    timeline.append({
                        "time": round(time_seconds, 1),
                        "emotion": emotion,
                        "confidence": round(confidence, 4)
                    })

            frame_number += 1


        capture.release()


        # If no face was detected
        if len(timeline) == 0:
            return jsonify({
                "status": "completed",
                "patient_id": patient_id,
                "filename": video.filename,
                "message": "No faces were detected in the video",
                "dominant_emotion": None,
                "average_confidence": 0,
                "emotion_counts": {},
                "timeline": [],
                "frames_analysed": 0
            })


        # Count how many times each emotion appears
        emotion_counts = Counter(
            item["emotion"] for item in timeline
        )


        # Find most common emotion
        dominant_emotion = emotion_counts.most_common(1)[0][0]


        # Calculate average confidence
        average_confidence = sum(
            item["confidence"] for item in timeline
        ) / len(timeline)


        print(
            "Analysis completed for patient:",
            patient_id,
            flush=True
        )

        analysis_record = create_analysis_record(
            patient_id,
            {"timeline": timeline}
        )

        return jsonify({
            "status": "completed",
            "analysis_id": analysis_record["analysis_id"],
            "patient_id": patient_id,
            "filename": video.filename,
            "message": "Video analysed successfully",
            "dominant_emotion": dominant_emotion,
            "average_confidence": round(average_confidence, 3),
            "emotion_counts": dict(emotion_counts),
            "timeline": timeline,
            "frames_analysed": len(timeline)
        })


    except Exception as error:

        print("Error:", error)

        return jsonify({
            "error": "Video analysis failed",
            "details": str(error)
        }), 500


    finally:

        if os.path.exists(temp_path):
            os.remove(temp_path)


if __name__ == "__main__":
    app.run(
        debug=True,
        port=5001,
        use_reloader=False
    )