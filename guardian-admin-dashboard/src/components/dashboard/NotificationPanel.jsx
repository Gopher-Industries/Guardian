import { useEffect, useState, useCallback } from "react";
import { 
  X, 
  Check, 
  Trash2, 
  Info, 
  AlertTriangle, 
  CheckCircle2, 
  XCircle, 
  Bell,
  Clock 
} from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
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

  const getIcon = (type) => {
    switch (type) {
      case "success": return <CheckCircle2 size={16} className="text-success" />;
      case "warning": return <AlertTriangle size={16} className="text-warning" />;
      case "error": return <XCircle size={16} className="text-danger" />;
      default: return <Info size={16} className="text-primary" />;
    }
  };

  const formatTime = (dateStr) => {
    const date = new Date(dateStr);
    const now = new Date();
    const diffInHours = (now - date) / (1000 * 60 * 60);
    
    if (diffInHours < 24 && date.getDate() === now.getDate()) {
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
    if (diffInHours < 48 && date.getDate() === now.getDate() - 1) {
      return "Yesterday";
    }
    return date.toLocaleDateString([], { month: 'short', day: 'numeric' });
  };

  if (!isOpen) return null;

  return (
    <>
      <motion.div 
        className="notification-dropdown-overlay" 
        onClick={onClose}
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
      />
      <motion.div 
        className="notification-dropdown" 
        onClick={(e) => e.stopPropagation()}
        initial={{ opacity: 0, y: 10, scale: 0.95 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        exit={{ opacity: 0, y: 10, scale: 0.95 }}
        transition={{ duration: 0.2, ease: "easeOut" }}
      >
        <div className="notification-dropdown-header">
          <div className="brand-lockup" style={{ gap: '8px' }}>
            <Bell size={18} />
            <h3>Notifications</h3>
          </div>
          <button className="icon-button" style={{ width: '32px', height: '32px' }} onClick={onClose} aria-label="Close menu">
            <X size={16} />
          </button>
        </div>

        <div className="notification-dropdown-content">
          {isLoading ? (
            <div className="notification-empty">
              <Loader text="Fetching alerts..." />
            </div>
          ) : error ? (
            <div className="notification-empty">
              <AlertTriangle size={32} style={{ opacity: 0.3, marginBottom: '12px' }} />
              <p className="form-error">{error}</p>
              <button className="ui-button secondary" onClick={fetchNotifications} style={{ marginTop: '12px' }}>
                Retry
              </button>
            </div>
          ) : notifications.length === 0 ? (
            <div className="notification-empty">
              <Bell size={32} style={{ opacity: 0.2, marginBottom: '12px' }} />
              <p>Everything caught up!</p>
            </div>
          ) : (
            <AnimatePresence mode="popLayout">
              {notifications.map((notif) => {
                const isRead = notif.isRead || notif.read;
                
                return (
                  <motion.div
                    key={notif.id}
                    layout
                    initial={{ opacity: 0, x: -10 }}
                    animate={{ opacity: 1, x: 0 }}
                    exit={{ opacity: 0, scale: 0.95 }}
                    className={`notification-item ${!isRead ? "unread" : ""} type-${notif.type || 'info'}`}
                  >
                    <div className="notification-item-header">
                      <div className="notification-item-title-wrap">
                        {getIcon(notif.type)}
                        <h4 className="notification-item-title">{notif.title}</h4>
                      </div>
                      <div className="notification-item-date">
                        <Clock size={10} style={{ marginRight: '3px' }} />
                        {formatTime(notif.createdAt || notif.date)}
                      </div>
                    </div>
                    <p className="notification-item-message">{notif.message}</p>
                    
                    <div className="notification-item-actions">
                      {!isRead && (
                        <button
                          className="notification-action-btn read-btn"
                          onClick={() => handleMarkAsRead(notif.id)}
                        >
                          <Check size={12} style={{ marginRight: '3px' }} />
                          Mark as read
                        </button>
                      )}
                      <button
                        className="notification-action-btn delete-btn"
                        onClick={() => handleDelete(notif.id)}
                      >
                        <Trash2 size={12} />
                      </button>
                    </div>
                  </motion.div>
                );
              })}
            </AnimatePresence>
          )}
        </div>
      </motion.div>
    </>
  );
}

