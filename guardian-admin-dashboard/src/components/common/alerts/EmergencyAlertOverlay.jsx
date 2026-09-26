import { AlertTriangle, HeartPulse, ShieldAlert, X } from "lucide-react";
import "./EmergencyAlertOverlay.css";

// Alert "types" this overlay knows how to present. Kartik's alert-detection
// service is expected to send a `type` matching one of these keys — if it
// sends something else, the overlay still renders with a generic look
// rather than breaking (see ALERT_CONFIG.default below).
const ALERT_CONFIG = {
  fall: {
    label: "Fall Detected",
    Icon: AlertTriangle,
    className: "alert-type-fall",
  },
  vitals: {
    label: "Vitals Anomaly",
    Icon: HeartPulse,
    className: "alert-type-vitals",
  },
  unauthorized: {
    label: "Unauthorized Activity",
    Icon: ShieldAlert,
    className: "alert-type-unauthorized",
  },
  default: {
    label: "Alert",
    Icon: AlertTriangle,
    className: "alert-type-fall",
  },
};

/**
 * Full-screen, high-visibility alert popup.
 *
 * Props:
 * - alert: { type: "fall" | "vitals" | "unauthorized", patientName?, message?, title? }
 *          or null/undefined to render nothing.
 * - onDismiss: called when the user closes the alert.
 *
 * This component only renders what it's given — it does not decide *when*
 * an alert should fire. That trigger (a Socket.IO event from the backend's
 * alert-detection service) is separate and gets wired in once that service
 * is ready; until then this can be shown directly for demos/testing.
 */
export default function EmergencyAlertOverlay({ alert, onDismiss }) {
  if (!alert) return null;

  const config = ALERT_CONFIG[alert.type] || ALERT_CONFIG.default;
  const { label, Icon, className } = config;

  return (
    <div
      className={`emergency-alert-backdrop ${className}`}
      role="alertdialog"
      aria-live="assertive"
      aria-label={alert.title || label}
    >
      <div className="emergency-alert-card">
        <button
          type="button"
          className="emergency-alert-dismiss"
          onClick={onDismiss}
          aria-label="Dismiss alert"
        >
          <X size={18} />
        </button>

        <div className="emergency-alert-icon-ring">
          <Icon size={48} />
        </div>

        <p className="emergency-alert-eyebrow">{label}</p>
        <h2 className="emergency-alert-title">
          {alert.title || label}
        </h2>

        {alert.patientName ? (
          <p className="emergency-alert-patient">
            Patient: <strong>{alert.patientName}</strong>
          </p>
        ) : null}

        {alert.message ? (
          <p className="emergency-alert-message">{alert.message}</p>
        ) : null}

        <button
          type="button"
          className="emergency-alert-acknowledge"
          onClick={onDismiss}
        >
          Acknowledge
        </button>
      </div>
    </div>
  );
}
