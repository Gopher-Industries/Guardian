import { Link } from "react-router-dom";
import { ShieldAlert, FileQuestion } from "lucide-react";

const STATUS_CONFIG = {
  404: {
    icon: FileQuestion,
    title: "404 — Page Not Found",
    message: "The page you're looking for doesn't exist.",
  },
  403: {
    icon: ShieldAlert,
    title: "Access Denied",
    message:
      "You don't have permission to view this page. Contact your administrator if you believe this is a mistake.",
  },
};

export default function StatusPage({ type = 404 }) {
  const config = STATUS_CONFIG[type] || STATUS_CONFIG[404];
  const Icon = config.icon;

  return (
    <div className="status-page">
      <Icon size={48} className="status-page-icon" />
      <h1>{config.title}</h1>
      <p>{config.message}</p>
      <Link to="/dashboard" className="ui-button primary">
        Return to Dashboard
      </Link>
    </div>
  );
}