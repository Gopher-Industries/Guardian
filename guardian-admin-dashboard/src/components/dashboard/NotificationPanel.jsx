import { useEffect, useState, useCallback } from "react";
import { X, Check, Trash2 } from "lucide-react";
import {
  getNotifications,
  markNotificationAsRead,
  deleteNotification,
} from "../../services/notificationService";
import Loader from "../common/Loader";

export default function NotificationPanel({ isOpen, onClose }) {
  const [notifications, setNotifications] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchNotifications = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await getNotifications();
      // Assume API returns an array, or data.notifications
      setNotifications(Array.isArray(data) ? data : data?.notifications || []);
    } catch (err) {
      setError("Failed to load notifications.");
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    if (isOpen) {
      fetchNotifications();
    }
  }, [isOpen, fetchNotifications]);

  const handleMarkAsRead = async (id) => {
    try {
      await markNotificationAsRead(id);
      setNotifications((prev) =>
        prev.map((notif) =>
          notif.id === id ? { ...notif, isRead: true, read: true } : notif
        )
      );
    } catch (err) {
      console.error("Failed to mark as read", err);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this notification?")) return;
    
    try {
      await deleteNotification(id);
      setNotifications((prev) => prev.filter((notif) => notif.id !== id));
    } catch (err) {
      console.error("Failed to delete notification", err);
    }
  };

  if (!isOpen) return null;

  return (
    <>
      <div className="notification-dropdown-overlay" onClick={onClose} />
      <div className="notification-dropdown" onClick={(e) => e.stopPropagation()}>
        <div className="notification-dropdown-header">
          <h3>Notifications</h3>
          <button className="icon-button" style={{ width: '32px', height: '32px' }} onClick={onClose} aria-label="Close menu">
            <X size={16} />
          </button>
        </div>

        <div className="notification-dropdown-content">
          {isLoading ? (
            <div className="notification-empty">
              <Loader text="Loading notifications..." />
            </div>
          ) : error ? (
            <div className="notification-empty">
              <p className="form-error">{error}</p>
              <button className="ui-button secondary" onClick={fetchNotifications} style={{ marginTop: '12px' }}>
                Retry
              </button>
            </div>
          ) : notifications.length === 0 ? (
            <div className="notification-empty">
              <p>No notifications yet.</p>
            </div>
          ) : (
            notifications.map((notif) => {
              // Different backends might use 'read' or 'isRead'
              const isRead = notif.isRead || notif.read;
              
              return (
                <div
                  key={notif.id}
                  className={`notification-item ${!isRead ? "unread" : ""}`}
                >
                  <div className="notification-item-header">
                    <h4 className="notification-item-title">{notif.title}</h4>
                    <span className="notification-item-date">
                      {new Date(notif.createdAt || notif.date).toLocaleDateString()}
                    </span>
                  </div>
                  <p className="notification-item-message">{notif.message}</p>
                  
                  <div className="notification-item-actions">
                    {!isRead && (
                      <button
                        className="notification-action-btn read-btn"
                        onClick={() => handleMarkAsRead(notif.id)}
                        title="Mark as read"
                      >
                        <Check size={14} style={{ marginRight: '4px', verticalAlign: 'middle' }} />
                        Mark Read
                      </button>
                    )}
                    <button
                      className="notification-action-btn delete-btn"
                      onClick={() => handleDelete(notif.id)}
                      title="Delete notification"
                    >
                      <Trash2 size={14} style={{ marginRight: '4px', verticalAlign: 'middle' }} />
                      Delete
                    </button>
                  </div>
                </div>
              );
            })
          )}
        </div>
      </div>
    </>
  );
}
