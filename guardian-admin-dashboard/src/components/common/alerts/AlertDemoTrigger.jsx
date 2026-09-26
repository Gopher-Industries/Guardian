import { useState } from "react";
import EmergencyAlertOverlay from "./EmergencyAlertOverlay";
import "./AlertDemoTrigger.css";

// TEMPORARY: this component exists so the alert popup can be demoed and
// tested before Kartik's real alert-detection service is wired up. Once
// that's ready, the actual trigger will be a Socket.IO event listener
// (see notes in EmergencyAlertOverlay.jsx) instead of these buttons, and
// this file can be removed.
const DEMO_ALERTS = [
  {
    type: "fall",
    title: "Fall Detected",
    patientName: "Aarav Sharma",
    message: "Motion sensor detected a possible fall in Room 4B. Immediate check-in required.",
  },
  {
    type: "vitals",
    title: "Vitals Anomaly",
    patientName: "Sophia Brown",
    message: "Heart rate reading of 142 bpm is outside the safe range for this patient.",
  },
  {
    type: "unauthorized",
    title: "Unauthorized Activity",
    patientName: "Margaret Chen",
    message: "An unrecognized device attempted to access this patient's records.",
  },
];

export default function AlertDemoTrigger() {
  const [activeAlert, setActiveAlert] = useState(null);

  return (
    <div className="alert-demo-trigger">
      <p className="alert-demo-label">Simulate alert (demo only):</p>
      <div className="alert-demo-buttons">
        {DEMO_ALERTS.map((demo) => (
          <button
            key={demo.type}
            type="button"
            className="alert-demo-btn"
            onClick={() => setActiveAlert(demo)}
          >
            {demo.title}
          </button>
        ))}
      </div>

      <EmergencyAlertOverlay
        alert={activeAlert}
        onDismiss={() => setActiveAlert(null)}
      />
    </div>
  );
}
