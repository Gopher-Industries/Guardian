import "./SuccessCelebrationOverlay.css";

const CONFETTI_COUNT = 28;

export default function SuccessCelebrationOverlay({ open, message }) {
  if (!open) return null;

  return (
    <div className="celebration-overlay-backdrop" role="status" aria-live="polite">
      <div className="celebration-confetti-layer" aria-hidden="true">
        {Array.from({ length: CONFETTI_COUNT }).map((_, index) => (
          <span
            key={index}
            className={`confetti-piece confetti-${(index % 4) + 1}`}
            style={{
              left: `${(index * 3.4) % 100}%`,
              animationDelay: `${(index % 7) * 0.12}s`,
              animationDuration: `${2.3 + (index % 5) * 0.22}s`,
            }}
          />
        ))}
      </div>

      <div className="celebration-card">
        <div className="celebration-badge">Success</div>
        <h3>Done successfully</h3>
        <p>{message}</p>
        <div className="celebration-progress" />
      </div>
    </div>
  );
}