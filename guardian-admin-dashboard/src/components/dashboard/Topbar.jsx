import { useState, useEffect, useCallback } from "react";
import { Bell, Search, UserCircle2 } from "lucide-react";
import { getAdminUser } from "../../utils/storage";
import NotificationPanel from "./NotificationPanel";
import ConfirmationModal from "../common/ConfirmationModal";
import { 
  getNotifications, 
  deleteNotification 
} from "../../services/notificationService";

export default function Topbar() {
  const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  
  const admin = getAdminUser() || {
    fullname: "Guardian Admin",
    role: "admin",
  };

  const fetchNotifications = useCallback(async () => {
    try {
      const data = await getNotifications();
      setNotifications(Array.isArray(data) ? data : data?.notifications || []);
    } catch (err) {
      console.error("Failed to fetch notifications for badge", err);
    }
  }, []);

  useEffect(() => {
    fetchNotifications();
  }, [fetchNotifications]);

  const unreadCount = notifications.filter(n => !(n.isRead || n.read)).length;

  const handleDeleteRequest = (id) => {
    setSelectedId(id);
    setIsConfirmOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (!selectedId) return;
    try {
      await deleteNotification(selectedId);
      setNotifications(prev => prev.filter(n => n.id !== selectedId));
      setSelectedId(null);
    } catch (err) {
      console.error("Failed to delete notification", err);
    }
  };

  return (
    <header className="topbar">
      <div className="topbar-left">
        <div>
          <p className="topbar-eyebrow">Administrator Workspace</p>
          <h2 className="topbar-title">Dashboard Overview</h2>
        </div>
      </div>

      <div className="topbar-right">
        <div className="search-box">
          <Search size={16} />
          <input type="text" placeholder="Search records..." />
        </div>

        <div className="notification-wrapper" style={{ position: "relative" }}>
          <button 
            className="icon-button" 
            type="button" 
            aria-label="Notifications"
            onClick={() => setIsNotificationsOpen(!isNotificationsOpen)}
          >
            <Bell size={18} />
            {unreadCount > 0 && (
              <span className="notification-badge">
                {unreadCount > 9 ? '9+' : unreadCount}
              </span>
            )}
          </button>
          
          <NotificationPanel 
            isOpen={isNotificationsOpen} 
            onClose={() => setIsNotificationsOpen(false)} 
            notifications={notifications}
            setNotifications={setNotifications}
            refreshNotifications={fetchNotifications}
            onDeleteRequest={handleDeleteRequest}
          />
        </div>

        <div className="topbar-profile">
          <UserCircle2 size={20} />
          <div>
            <strong>{admin.fullname || "Guardian Admin"}</strong>
            <span>{admin.role || "admin"}</span>
          </div>
        </div>
      </div>

      <ConfirmationModal
        isOpen={isConfirmOpen}
        onClose={() => setIsConfirmOpen(false)}
        onConfirm={handleConfirmDelete}
        title="Delete Notification"
        message="Are you sure you want to remove this alert? This action cannot be reversed."
        confirmText="Delete Alert"
      />
    </header>
  );
}
