import { useEffect, useState } from "react";
import { getAllPatients } from "../services/patientService";
import "./EmotionRecognitionPage.css";

export default function EmotionRecognitionPage() {
  const [selectedVideo, setSelectedVideo] = useState(null);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const [patients, setPatients] = useState([]);
  const [selectedPatientId, setSelectedPatientId] = useState("");
  const [loadingPatients, setLoadingPatients] = useState(true);

  useEffect(() => {
    const loadPatients = async () => {
      try {
        setLoadingPatients(true);

        const data = await getAllPatients();

        const patientList = Array.isArray(data)
          ? data
          : data?.patients || [];

        setPatients(patientList);
      } catch (error) {
        console.error("Failed to load patients:", error);
        setPatients([]);
      } finally {
        setLoadingPatients(false);
      }
    };

    loadPatients();
  }, []);

  const handleVideoChange = (event) => {
    const file = event.target.files[0];

    if (file) {
      setSelectedVideo(file);
      setResult(null);
    }
  };

  const handleAnalyseVideo = async () => {
    if (!selectedVideo || !selectedPatientId) {
      return;
    }

    console.log(
      "Selected patient ID from React:",
      selectedPatientId
    );

    const formData = new FormData();

    formData.append("video", selectedVideo);
    formData.append("patient_id", selectedPatientId);

    try {
      setLoading(true);
      setResult(null);

      const response = await fetch(
        "http://127.0.0.1:5001/analyse-video",
        {
          method: "POST",
          body: formData,
        }
      );

      const data = await response.json();

      setResult(data);
    } catch (error) {
      console.error("Error analysing video:", error);

      setResult({
        error: "Could not connect to the Emotion API",
      });
    } finally {
      setLoading(false);
    }
  };

  const getEmotionPercentage = (count) => {
    if (!result?.frames_analysed) {
      return 0;
    }

    return Math.round(
      (count / result.frames_analysed) * 100
    );
  };

  return (
    <div className="emotion-page">
      <div className="emotion-page-header">
        <div>
          <h1>Emotion Recognition</h1>

          <p>
            Analyse patient video footage to identify facial
            expressions and emotional patterns.
          </p>
        </div>
      </div>

      <div className="emotion-upload-card">
        <div className="upload-card-header">
          <div>
            <h2>Video Analysis</h2>

            <p>
              Upload a patient video to begin emotion recognition
              analysis.
            </p>
          </div>
        </div>

        <div 
          className="patient-selector">
          <label htmlFor="patient-select">
            Select Patient
          </label>

          <select
            id="patient-select"
            value={selectedPatientId}
            onChange={(e) =>
              setSelectedPatientId(e.target.value)
            }
            disabled={loadingPatients}
          >
            <option value="">
              {loadingPatients
                ? "Loading patients..."
                : "Choose a patient"}
            </option>

            {patients.map((patient) => (
                <option
                key={patient._id}
                value={patient._id}
                >
                {patient.fullname} - {patient._id.slice(-4)}
                </option>
            ))}
            
          </select>

        </div>

        <div className="upload-area">
          <div className="upload-icon">↑</div>

          <h3>Upload Video</h3>

          <p className="upload-description">
            Select a video file from your device
          </p>

          <label className="choose-video-button">
            Choose Video

            <input
              type="file"
              accept="video/*"
              onChange={handleVideoChange}
            />
          </label>

          {selectedVideo && (
            <div className="selected-file">
              <span className="file-icon">▶</span>

              <div>
                <strong>{selectedVideo.name}</strong>
                <p>Video ready for analysis</p>
              </div>
            </div>
          )}
        </div>

        {selectedVideo && (
          <button
            className="analyse-button"
            type="button"
            onClick={handleAnalyseVideo}
            disabled={loading}
          >
            {loading
              ? "Analysing Video..."
              : "Analyse Video"}
          </button>
        )}

        {loading && (
          <div className="analysis-loading">
            <div className="loading-spinner"></div>

            <div>
              <strong>Analysing video...</strong>

              <p>
                Detecting faces and classifying emotional
                expressions.
              </p>
            </div>
          </div>
        )}

        {result?.error && (
          <div className="analysis-error">
            <strong>Analysis failed</strong>
            <p>{result.error}</p>
          </div>
        )}

        {result &&
          !result.error &&
          !result.dominant_emotion &&
          result.message && (
            <div className="analysis-message">
              <strong>{result.message}</strong>

              {result.filename && (
                <p>File: {result.filename}</p>
              )}
            </div>
          )}
      </div>

      {result?.dominant_emotion && (
        <div className="results-section">
          <div className="results-heading">
            <div>
              <h2>Analysis Results</h2>

              <p>
                Emotion recognition results from the analysed
                video.
              </p>
            </div>

            {result.filename && (
              <span className="analysed-file">
                {result.filename}
              </span>
            )}
          </div>

          <div className="summary-grid">
            <div className="summary-card">
              <p>Dominant Emotion</p>

              <h3 className="dominant-emotion">
                {result.dominant_emotion}
              </h3>

              <span>
                Most frequently detected emotion
              </span>
            </div>

            <div className="summary-card">
              <p>Average Confidence</p>

              <h3>
                {(result.average_confidence * 100).toFixed(1)}%
              </h3>

              <span>
                Average model confidence
              </span>
            </div>

            <div className="summary-card">
              <p>Frames Analysed</p>

              <h3>{result.frames_analysed}</h3>

              <span>
                Frames containing detected faces
              </span>
            </div>
          </div>

          <div className="analysis-grid">
            <div className="result-card">
              <div className="card-heading">
                <div>
                  <h3>Emotion Distribution</h3>

                  <p>
                    Detected emotions across analysed frames
                  </p>
                </div>
              </div>

              <div className="emotion-bars">
                {Object.entries(
                  result.emotion_counts || {}
                )
                  .sort((a, b) => b[1] - a[1])
                  .map(([emotion, count]) => {
                    const percentage =
                      getEmotionPercentage(count);

                    return (
                      <div
                        className="emotion-bar-item"
                        key={emotion}
                      >
                        <div className="emotion-bar-label">
                          <span className="emotion-name">
                            {emotion}
                          </span>

                          <span>
                            {count} frame
                            {count !== 1 ? "s" : ""} ·{" "}
                            {percentage}%
                          </span>
                        </div>

                        <div className="emotion-bar-background">
                          <div
                            className="emotion-bar-fill"
                            style={{
                              width: `${percentage}%`,
                            }}
                          ></div>
                        </div>
                      </div>
                    );
                  })}
              </div>
            </div>

            <div className="result-card">
              <div className="card-heading">
                <div>
                  <h3>Analysis Summary</h3>

                  <p>
                    Overview of the classifier result
                  </p>
                </div>
              </div>

              <div className="analysis-summary">
                <div className="summary-row">
                  <span>Dominant emotion</span>

                  <strong className="emotion-badge">
                    {result.dominant_emotion}
                  </strong>
                </div>

                <div className="summary-row">
                  <span>Confidence</span>

                  <strong>
                    {(result.average_confidence * 100).toFixed(1)}%
                  </strong>
                </div>

                <div className="summary-row">
                  <span>Frames analysed</span>

                  <strong>
                    {result.frames_analysed}
                  </strong>
                </div>

                <div className="summary-row">
                  <span>
                    Emotion categories detected
                  </span>

                  <strong>
                    {
                      Object.keys(
                        result.emotion_counts || {}
                      ).length
                    }
                  </strong>
                </div>
              </div>
            </div>
          </div>

          <div className="result-card timeline-card">
            <div className="card-heading">
              <div>
                <h3>Emotion Timeline</h3>

                <p>
                  Emotion detected at each analysed point in the
                  video
                </p>
              </div>
            </div>

            <div className="timeline-table-wrapper">
              <table className="emotion-table">
                <thead>
                  <tr>
                    <th>Time</th>
                    <th>Detected Emotion</th>
                    <th>Confidence</th>
                  </tr>
                </thead>

                <tbody>
                  {(result.timeline || []).map(
                    (item, index) => (
                      <tr key={index}>
                        <td>{item.time}s</td>

                        <td>
                          <span className="table-emotion">
                            {item.emotion}
                          </span>
                        </td>

                        <td>
                          <div className="confidence-cell">
                            <div className="confidence-track">
                              <div
                                className="confidence-fill"
                                style={{
                                  width: `${
                                    item.confidence * 100
                                  }%`,
                                }}
                              ></div>
                            </div>

                            <strong>
                              {(
                                item.confidence * 100
                              ).toFixed(1)}
                              %
                            </strong>
                          </div>
                        </td>
                      </tr>
                    )
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}