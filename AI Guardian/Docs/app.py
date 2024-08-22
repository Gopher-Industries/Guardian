from flask import Flask, render_template, request, jsonify

app = Flask(__name__)

PATIENT_TYPES = {
    'adult': {'height': 1.75, 'width': 0.5, 'speed': 1.0, 'tremor': 0},
    'child': {'height': 1.2, 'width': 0.3, 'speed': 0.8, 'tremor': 0},
    'elderly': {'height': 1.6, 'width': 0.5, 'speed': 0.6, 'tremor': 0},
    'adult_parkinsons': {'height': 1.75, 'width': 0.5, 'speed': 0.4, 'tremor': 0.05}
}

ROOM_TYPES = {
    'standard': {'bedSize': {'width': 2, 'length': 1}, 'hasChair': True},
    'icu': {'bedSize': {'width': 2.2, 'length': 1.2}, 'hasChair': False},
    'operatingRoom': {'bedSize': {'width': 2.5, 'length': 1.5}, 'hasChair': False}
}

@app.route('/')
def start():
    return render_template('start.html', patient_types=PATIENT_TYPES, room_types=ROOM_TYPES)

@app.route('/simulator', methods=['POST'])
def simulator():
    patient_type = request.form['patientType']
    room_type = request.form['roomType']
    patient_data = PATIENT_TYPES[patient_type]
    room_data = ROOM_TYPES[room_type]
    return render_template('simulator.html', patient_type=patient_type, room_type=room_type, patient_data=patient_data, room_data=room_data)

if __name__ == '__main__':
    app.run(debug=True)