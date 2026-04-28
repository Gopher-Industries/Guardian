import { motion, AnimatePresence } from "framer-motion";
import { 
  X, 
  Check, 
  Trash2, 
  Clock, 
  Bell,
  Search,
  Filter
} from "lucide-react";
import { useState } from "react";
import { 
  markNotificationAsRead 
} from "../../services/notificationService";

export default function NotificationDrawer({ 
  isOpen, 
  onClose, 
  notifications, 
  setNotifications,
  onDeleteRequest,
  getIcon // We can pass this or redefine
}) {
  const [filter, setFilter] = useState("all"); // all, unread
  const [search, setSearch] = useState("");

  const filteredNotifications = notifications.filter(n => {
    const matchesFilter = filter === "all" || !(n.isRead || n.read);
    const matchesSearch = n.title.toLowerCase().includes(search.toLowerCase()) || 
                          n.message.toLowerCase().includes(search.toLowerCase());
    return matchesFilter && matchesSearch;
  });

  const handleMarkAllRead = async () => {
    try {
      // In a real app, you'd have an API endpoint for this
      // For now, we'll just map through
      const unreadIds = notifications.filter(n => !(n.isRead || n.read)).map(n => n.id);
      for (const id of unreadIds) {
        await markNotificationAsRead(id);
      }
      setNotifications(prev => prev.map(n => ({ ...n, read: true, isRead: true })));
    } catch (err) {
      console.error("Failed to mark all as read", err);
    }
  };

  const formatTimeFull = (dateStr) => {
    return new Date(dateStr).toLocaleString([], { 
      month: 'short', 
      day: 'numeric', 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <>
          <motion.div 
            className="drawer-overlay"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
          />
          <motion.div 
            className="notification-drawer"
            initial={{ x: "100%" }}
            animate={{ x: 0 }}
            exit={{ x: "100%" }}
            transition={{ type: "spring", damping: 30, stiffness: 300 }}
          >
            <div className="drawer-header">
              <div className="drawer-title-wrap">
                <Bell size={20} className="text-primary" />
                <h2>Notification Center</h2>
              </div>
              <button className="icon-button" onClick={onClose}>
                <X size={20} />
              </button>
            </div>

            <div className="drawer-filters">
              <div className="search-bar-mini">
                <Search size={14} />
                <input 
                  type="text" 
                  placeholder="Search alerts..." 
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
              </div>
              <div className="filter-tabs">
                <button 
                  className={`filter-tab ${filter === 'all' ? 'active' : ''}`}
                  onClick={() => setFilter('all')}
                >
                  All
                </button>
                <button 
                  className={`filter-tab ${filter === 'unread' ? 'active' : ''}`}
                  onClick={() => setFilter('unread')}
                >
                  Unread
                </button>
              </div>
            </div>

            <div className="drawer-actions-bar">
              <span className="count-label">{filteredNotifications.length} notifications</span>
              <button className="text-button" onClick={handleMarkAllRead}>
                <Check size={14} />
                Mark all as read
              </button>
            </div>

            <div className="drawer-content">
              {filteredNotifications.length === 0 ? (
                <div className="drawer-empty">
                  <Bell size={48} style={{ opacity: 0.1, marginBottom: '16px' }} />
                  <p>No notifications found</p>
                </div>
              ) : (
                filteredNotifications.map((notif) => {
                  const isRead = notif.isRead || notif.read;
                  return (
                    <div 
                      key={notif.id} 
                      className={`drawer-item ${!isRead ? 'unread' : ''} type-${notif.type || 'info'}`}
                    >
                      <div className="drawer-item-header">
                        <h3 className="drawer-item-title">{notif.title}</h3>
                        <span className="drawer-item-date">{formatTimeFull(notif.createdAt || notif.date)}</span>
                      </div>
                      <p className="drawer-item-message">{notif.message}</p>
                      <div className="drawer-item-footer">
                        <div className="type-tag">{notif.type || 'info'}</div>
                        <div className="drawer-item-actions">
                          {!isRead && (
                            <button 
                              className="action-icon-btn" 
                              onClick={() => markNotificationAsRead(notif.id).then(() => {
                                setNotifications(prev => prev.map(n => n.id === notif.id ? { ...n, read: true, isRead: true } : n));
                              })}
                              title="Mark as read"
                            >
                              <Check size={16} />
                            </button>
                          )}
                          <button 
                            className="action-icon-btn delete" 
                            onClick={() => onDeleteRequest(notif.id)}
                            title="Delete"
                          >
                            <Trash2 size={16} />
                          </button>
                        </div>
                      </div>
                    </div>
                  );
                })
              )}
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}
