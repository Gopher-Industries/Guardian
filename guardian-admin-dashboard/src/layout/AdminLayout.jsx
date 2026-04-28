import { useEffect, useState, useCallback } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "../components/dashboard/Sidebar";
import Topbar from "../components/dashboard/Topbar";
import ConfirmationModal from "../components/common/ConfirmationModal";
import NotificationDrawer from "../components/dashboard/NotificationDrawer";
import Modal from "../components/common/Modal";
import { Trash2, Clock } from "lucide-react";
import { 
  getNotifications, 
  deleteNotification 
} from "../services/notificationService";

export default function AdminLayout() {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [isMobile, setIsMobile] = useState(window.innerWidth < 1100);
  const [mobileSidebarOpen, setMobileSidebarOpen] = useState(false);

  // Notifications State
  const [notifications, setNotifications] = useState([]);
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [detailModal, setDetailModal] = useState({
    isOpen: false,
    notification: null,
    title: "Notification Detail",
    description: "",
    confirmText: "Delete Notification",
    cancelText: "Close"
  });

  // Global Confirmation Modal State
  const [confirmModal, setConfirmModal] = useState({
    isOpen: false,
    title: "",
    message: "",
    confirmText: "",
    onConfirm: () => {},
    type: "danger"
  });

  const fetchNotifications = useCallback(async () => {
    try {
      const data = await getNotifications();
      setNotifications(Array.isArray(data) ? data : data?.notifications || []);
    } catch (err) {
      console.error("Failed to fetch notifications", err);
    }
  }, []);

  useEffect(() => {
    fetchNotifications();
    const handleResize = () => {
      const mobile = window.innerWidth < 1100;
      setIsMobile(mobile);

      if (mobile) {
        setMobileSidebarOpen(false);
      }
    };

    window.addEventListener("resize", handleResize);
    handleResize();

    return () => window.removeEventListener("resize", handleResize);
  }, [fetchNotifications]);

  const handleToggleSidebar = () => {
    if (isMobile) {
      setMobileSidebarOpen((prev) => !prev);
    } else {
      setSidebarCollapsed((prev) => !prev);
    }
  };

  const showConfirm = (options) => {
    setConfirmModal({
      ...options,
      isOpen: true
    });
  };

  const hideConfirm = () => {
    setConfirmModal(prev => ({ ...prev, isOpen: false }));
  };

  const handleDeleteRequest = (id) => {
    showConfirm({
      title: "Delete Notification",
      message: "Are you sure you want to remove this alert? This action cannot be reversed.",
      confirmText: "Delete Alert",
      onConfirm: async () => {
        try {
          await deleteNotification(id);
          setNotifications(prev => prev.filter(n => n.id !== id));
        } catch (err) {
          console.error("Failed to delete notification", err);
        }
      }
    });
  };

  const handleViewNotification = (notif, options = {}) => {
    setDetailModal({
      isOpen: true,
      notification: notif,
      title: options.title || "Notification Detail",
      description: options.description || "",
      confirmText: options.confirmText || "Delete Notification",
      cancelText: options.cancelText || "Close"
    });
  };

  const closeDetailModal = () => {
    setDetailModal(prev => ({ ...prev, isOpen: false }));
  };

  return (
    <div className="admin-shell">
      <Sidebar
        isMobile={isMobile}
        mobileSidebarOpen={mobileSidebarOpen}
        collapsed={sidebarCollapsed}
        onToggle={handleToggleSidebar}
        onCloseMobile={() => setMobileSidebarOpen(false)}
      />

      {isMobile && mobileSidebarOpen ? (
        <div
          className="sidebar-backdrop"
          onClick={() => setMobileSidebarOpen(false)}
        />
      ) : null}

      <div
        className={`admin-shell-main ${
          !isMobile
            ? sidebarCollapsed
              ? "sidebar-collapsed"
              : "sidebar-expanded"
            : ""
        }`}
      >
        <Topbar 
          notifications={notifications}
          setNotifications={setNotifications}
          onRefreshNotifications={fetchNotifications}
          onDeleteRequest={handleDeleteRequest}
          onOpenDrawer={() => setIsDrawerOpen(true)}
          onViewNotification={handleViewNotification}
        />
        <main className="admin-content">
          <Outlet context={{ 
            showConfirm, 
            hideConfirm, 
            notifications, 
            fetchNotifications,
            onViewNotification: handleViewNotification 
          }} />
        </main>
      </div>

      <NotificationDrawer 
        isOpen={isDrawerOpen}
        onClose={() => setIsDrawerOpen(false)}
        notifications={notifications}
        setNotifications={setNotifications}
        onDeleteRequest={handleDeleteRequest}
        onViewNotification={handleViewNotification}
      />

      <Modal
        isOpen={detailModal.isOpen}
        onClose={closeDetailModal}
        title={detailModal.title}
        type={detailModal.notification?.type || 'info'}
        footer={
          <>
            <button className="ui-button secondary" onClick={closeDetailModal}>
              {detailModal.cancelText}
            </button>
            <button 
              className="ui-button danger-btn"
              onClick={() => {
                handleDeleteRequest(detailModal.notification.id);
                closeDetailModal();
              }}
              style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
            >
              <Trash2 size={16} />
              {detailModal.confirmText}
            </button>
          </>
        }
      >
        {detailModal.notification && (
          <>
            <div className="detail-meta" style={{ marginBottom: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span className={`type-tag color-${detailModal.notification.type || 'info'}`}>
                {detailModal.notification.type || 'information'}
              </span>
              <div className="notification-item-date" style={{ display: 'flex', alignItems: 'center', gap: '4px', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                <Clock size={14} />
                {new Date(detailModal.notification.createdAt || detailModal.notification.date).toLocaleString([], {
                  dateStyle: 'medium',
                  timeStyle: 'short'
                })}
              </div>
            </div>

            {detailModal.description && (
              <p className="modal-subtitle" style={{ color: 'var(--text-muted)', marginBottom: '16px', fontSize: '0.95rem', lineHeight: '1.5' }}>
                {detailModal.description}
              </p>
            )}

            <div className="detail-message">
              {detailModal.notification.message}
            </div>
          </>
        )}
      </Modal>

      <ConfirmationModal
        isOpen={confirmModal.isOpen}
        onClose={hideConfirm}
        onConfirm={() => {
          confirmModal.onConfirm();
          hideConfirm();
        }}
        title={confirmModal.title}
        message={confirmModal.message}
        confirmText={confirmModal.confirmText}
        type={confirmModal.type}
      />
    </div>
  );
}