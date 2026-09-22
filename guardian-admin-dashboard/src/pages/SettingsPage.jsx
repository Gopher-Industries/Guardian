import { useMemo, useState } from "react";
import {
  Moon,
  Sun,
  Bell,
  Building2,
  Mail,
  MessageSquareText,
  Lock,
  MonitorCog,
  ChevronRight,
  X,
} from "lucide-react";
import { getAdminUser } from "../utils/storage";
import "./SettingsPage.css";

export default function SettingsPage() {
  const admin = getAdminUser() || {
    fullname: "Guardian Admin",
    email: "admin@guardian.com",
    role: "admin",
    organization: { name: "Guardian Health Org" },
  };

  const [darkMode, setDarkMode] = useState(
    document.body.classList.contains("dark-theme")
  );
  const [emailAlerts, setEmailAlerts] = useState(true);
  const [systemNotifications, setSystemNotifications] = useState(true);
  const [dailySummary, setDailySummary] = useState(false);
  const [feedbackOpen, setFeedbackOpen] = useState(false);
  const [notificationsOpen, setNotificationsOpen] = useState(false);
  const [feedback, setFeedback] = useState("");

  const toggleDarkMode = () => {
    const next = !darkMode;
    setDarkMode(next);
    document.body.classList.toggle("dark-theme", next);
  };

  const feedbackWordCount = useMemo(() => {
    return feedback.trim() ? feedback.trim().split(/\s+/).length : 0;
  }, [feedback]);

  const handleFeedbackChange = (event) => {
    const value = event.target.value;
    const words = value.trim() ? value.trim().split(/\s+/) : [];
    if (words.length <= 200) {
      setFeedback(value);
    }
  };

  const handleSubmitFeedback = (event) => {
    event.preventDefault();
    setFeedbackOpen(false);
    setFeedback("");
  };

  const adminName = admin.fullname || "Guardian Admin";
  const adminEmail = admin.email || "admin@guardian.com";
  const adminRole = admin.role || "admin";
  const adminOrg =
    admin.organization?.name || admin.organizationName || "Guardian Health Org";

  return (
    <section className="settings-page">
      <div className="settings-header">
        <div>
          <p className="settings-eyebrow">Guardian Monitor Admin</p>
          <h1>Settings</h1>
          <p className="settings-subtitle">
            Manage your account preferences, appearance, notifications, and
            admin-side experience settings.
          </p>
        </div>
      </div>

      <div className="settings-layout">
        <div className="settings-row-grid">
          <div className="settings-card profile-card">
            <div className="settings-card-header">
              <div>
                <h3>Account Overview</h3>
                <p>Basic information for the currently logged-in admin user.</p>
              </div>
            </div>

            <div className="profile-overview">
              <div className="profile-avatar">{adminName.charAt(0)}</div>

              <div className="profile-meta">
                <h4>{adminName}</h4>
                <p>{adminRole}</p>
              </div>
            </div>

            <div className="settings-detail-grid compact-details">
              <div className="settings-detail-item">
                <div className="settings-detail-icon">
                  <Mail size={16} />
                </div>
                <div>
                  <span className="settings-detail-label">Email</span>
                  <strong>{adminEmail}</strong>
                </div>
              </div>

              <div className="settings-detail-item">
                <div className="settings-detail-icon">
                  <Building2 size={16} />
                </div>
                <div>
                  <span className="settings-detail-label">Organization</span>
                  <strong>{adminOrg}</strong>
                </div>
              </div>
            </div>
          </div>

          <div className="settings-card compact-card">
            <div className="settings-card-header">
              <div>
                <h3>Quick Settings</h3>
                <p>Frequently used admin controls and preferences.</p>
              </div>
            </div>

            <div className="settings-quick-grid">
              <button type="button" className="settings-action-tile">
                <div className="settings-action-left">
                  <Lock size={17} />
                  <span>Change Password</span>
                </div>
                <ChevronRight size={16} />
              </button>

              <button
                type="button"
                className="settings-action-tile"
                onClick={() => setNotificationsOpen(true)}
              >
                <div className="settings-action-left">
                  <Bell size={17} />
                  <span>Notifications</span>
                </div>
                <ChevronRight size={16} />
              </button>

              <button type="button" className="settings-action-tile">
                <div className="settings-action-left">
                  <MonitorCog size={17} />
                  <span>Dashboard Preferences</span>
                </div>
                <ChevronRight size={16} />
              </button>
            </div>
          </div>
        </div>

        <div className="settings-row-grid">
          <div className="settings-card">
            <div className="settings-card-header">
              <div>
                <h3>Appearance</h3>
                <p>Control how the admin dashboard is displayed.</p>
              </div>
            </div>

            <div className="settings-row">
              <div className="settings-row-copy">
                <div className="settings-row-title-wrap">
                  {darkMode ? <Moon size={17} /> : <Sun size={17} />}
                  <strong>Dark Mode</strong>
                </div>
                <p>Enable a darker interface style across the admin dashboard.</p>
              </div>

              <button
                type="button"
                className={`theme-toggle ${darkMode ? "active" : ""}`}
                onClick={toggleDarkMode}
                aria-label="Toggle dark mode"
              >
                <span className="theme-toggle-track">
                  <span className="theme-toggle-thumb" />
                </span>
              </button>
            </div>
          </div>

          <div className="settings-card compact-card">
            <div className="settings-card-header">
              <div>
                <h3>Feedback</h3>
                <p>Share suggestions or feedback about the admin dashboard.</p>
              </div>
            </div>

            <button
              type="button"
              className="settings-feedback-btn"
              onClick={() => setFeedbackOpen(true)}
            >
              <MessageSquareText size={17} />
              Share Feedback
            </button>
          </div>
        </div>
      </div>

      {notificationsOpen && (
        <div className="settings-modal-backdrop">
          <div className="settings-modal small-modal">
            <div className="settings-modal-top">
              <div>
                <p className="settings-modal-eyebrow">Notification Preferences</p>
                <h3>Manage Notifications</h3>
              </div>

              <button
                type="button"
                className="settings-modal-close"
                onClick={() => setNotificationsOpen(false)}
              >
                <X size={18} />
              </button>
            </div>

            <div className="settings-stack">
              <div className="settings-row">
                <div className="settings-row-copy">
                  <div className="settings-row-title-wrap">
                    <Bell size={17} />
                    <strong>Email Alerts</strong>
                  </div>
                  <p>Receive important admin updates via email.</p>
                </div>

                <button
                  type="button"
                  className={`mini-toggle ${emailAlerts ? "active" : ""}`}
                  onClick={() => setEmailAlerts((prev) => !prev)}
                >
                  <span />
                </button>
              </div>

              <div className="settings-row">
                <div className="settings-row-copy">
                  <div className="settings-row-title-wrap">
                    <Bell size={17} />
                    <strong>System Notifications</strong>
                  </div>
                  <p>Show dashboard-based alerts and administrative prompts.</p>
                </div>

                <button
                  type="button"
                  className={`mini-toggle ${systemNotifications ? "active" : ""}`}
                  onClick={() => setSystemNotifications((prev) => !prev)}
                >
                  <span />
                </button>
              </div>

              <div className="settings-row">
                <div className="settings-row-copy">
                  <div className="settings-row-title-wrap">
                    <Bell size={17} />
                    <strong>Daily Summary</strong>
                  </div>
                  <p>Get a daily summary of key administrative activity.</p>
                </div>

                <button
                  type="button"
                  className={`mini-toggle ${dailySummary ? "active" : ""}`}
                  onClick={() => setDailySummary((prev) => !prev)}
                >
                  <span />
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {feedbackOpen && (
        <div className="settings-modal-backdrop">
          <div className="settings-modal">
            <div className="settings-modal-top">
              <div>
                <p className="settings-modal-eyebrow">Guardian Feedback</p>
                <h3>Share Your Feedback</h3>
              </div>

              <button
                type="button"
                className="settings-modal-close"
                onClick={() => setFeedbackOpen(false)}
              >
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleSubmitFeedback}>
              <div className="settings-form-field">
                <label>Your Feedback</label>
                <textarea
                  value={feedback}
                  onChange={handleFeedbackChange}
                  placeholder="Write your feedback here in up to 200 words..."
                  rows="6"
                  required
                />
                <span className="settings-word-count">
                  {feedbackWordCount} / 200 words
                </span>
              </div>

              <div className="settings-modal-actions">
                <button
                  type="button"
                  className="settings-secondary-btn"
                  onClick={() => setFeedbackOpen(false)}
                >
                  Cancel
                </button>

                <button type="submit" className="settings-primary-btn">
                  Submit Feedback
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </section>
  );
}