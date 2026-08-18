import { useEffect, useState, useCallback } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "../components/dashboard/Sidebar";
import Topbar from "../components/dashboard/Topbar";
import ConfirmationModal from "../components/common/ConfirmationModal";
import NotificationDrawer from "../components/dashboard/NotificationDrawer";
import Modal from "../components/common/Modal";
import { getTasks } from "../services/taskService";
import { Trash2, Clock } from "lucide-react";
import { 
  getNotifications, 
  deleteNotification 
} from "../services/notificationService";
import { getSupportTickets } from "../services/supportTicketService";

export default function AdminLayout() {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [isMobile, setIsMobile] = useState(window.innerWidth < 1100);
  const [mobileSidebarOpen, setMobileSidebarOpen] = useState(false);

  // Notifications State
  const [notifications, setNotifications] = useState([]);
  const [relatedTicket, setRelatedTicket] = useState(null);
  const [relatedTicketLoading, setRelatedTicketLoading] = useState(false);
  const [relatedTask, setRelatedTask] = useState(null);
  const [relatedTaskLoading, setRelatedTaskLoading] = useState(false);
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
          setNotifications(prev => prev.filter(n => n._id !== id));
        } catch (err) {
          console.error("Failed to delete notification", err);
        }
      }
    });
  };

  const extractRelatedId = (message = "") => {
  const match = message.match(/\(([a-f0-9]{24})\)/i);
  return match ? match[1] : null;
  };

  const handleViewNotification = async (notif, options = {}) => {
  const relatedId = extractRelatedId(notif.message);

  console.log("Notification clicked:", notif);
  console.log("Related ID:", relatedId);

  setIsDrawerOpen(false);

  // Clear previous related data
  setRelatedTicket(null);
  setRelatedTask(null);

  // Open the modal immediately
  setDetailModal({
    isOpen: true,
    notification: notif,
    title: options.title || "Notification Detail",
    description: options.description || "",
    confirmText: options.confirmText || "Delete Notification",
    cancelText: options.cancelText || "Close"
  });

  if (!relatedId) return;

  const title = notif.title?.toLowerCase() || "";

  const isSupportTicket = title.includes("support ticket");
  const isTask = title.includes("task");

  // SUPPORT TICKET
  if (isSupportTicket) {
    try {
      setRelatedTicketLoading(true);

      const data = await getSupportTickets({
        page: 1,
        limit: 100
      });

      const items = Array.isArray(data?.items)
        ? data.items
        : Array.isArray(data?.tickets)
        ? data.tickets
        : [];

      const ticket = items.find(
        (item) => item._id === relatedId
      );

      console.log("Related support ticket:", ticket);

      setRelatedTicket(ticket || null);
    } catch (err) {
      console.error(
        "Failed to load related support ticket",
        err
      );
    } finally {
      setRelatedTicketLoading(false);
    }
  }

  // TASK
  if (isTask) {
    try {
      setRelatedTaskLoading(true);

      const data = await getTasks({
        page: 1,
        limit: 100
      });

      const items = Array.isArray(data?.items)
        ? data.items
        : [];

      const task = items.find(
        (item) => item._id === relatedId
      );

      console.log("Related task:", task);

      setRelatedTask(task || null);
    } catch (err) {
      console.error(
        "Failed to load related task",
        err
      );
    } finally {
      setRelatedTaskLoading(false);
    }
  }
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
  open={detailModal.isOpen}
  onClose={closeDetailModal}
  title={detailModal.title}
  type={detailModal.notification?.type || "info"}
  footer={
    <>
      <button
        className="ui-button secondary"
        onClick={closeDetailModal}
      >
        {detailModal.cancelText}
      </button>

      <button
        className="ui-button danger-btn"
        onClick={() => {
          handleDeleteRequest(detailModal.notification._id);
          closeDetailModal();
        }}
        style={{
          display: "flex",
          alignItems: "center",
          gap: "8px"
        }}
      >
        <Trash2 size={16} />
        {detailModal.confirmText}
      </button>
    </>
  }
>
  {detailModal.notification && (
    <>
      <div
        className="detail-meta"
        style={{
          marginBottom: "20px",
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center"
        }}
      >
        <span
          className={`type-tag color-${
            detailModal.notification.type || "info"
          }`}
        >
          {detailModal.notification.type || "information"}
        </span>

        <div
          className="notification-item-date"
          style={{
            display: "flex",
            alignItems: "center",
            gap: "4px",
            fontSize: "0.8rem",
            color: "var(--text-muted)"
          }}
        >
          <Clock size={14} />

          {new Date(
            detailModal.notification.createdAt ||
              detailModal.notification.date
          ).toLocaleString([], {
            dateStyle: "medium",
            timeStyle: "short"
          })}
        </div>
      </div>

      {detailModal.description && (
        <p
          className="modal-subtitle"
          style={{
            color: "var(--text-muted)",
            marginBottom: "16px",
            fontSize: "0.95rem",
            lineHeight: "1.5"
          }}
        >
          {detailModal.description}
        </p>
      )}

      {/* Original notification message */}
      <div className="detail-message">
        {detailModal.notification.message}
      </div>

      {/* Loading related support ticket */}
      {relatedTicketLoading && (
        <div
          style={{
            marginTop: "20px",
            padding: "16px"
          }}
        >
          <p>Loading support ticket details...</p>
        </div>
      )}

      {/* Related support ticket details */}
      {relatedTicket && (
  <div
    style={{
      marginTop: "24px",
      paddingTop: "20px",
      borderTop: "1px solid var(--border-color)"
    }}
  >
    <h3
      style={{
        marginTop: 0,
        marginBottom: "16px"
      }}
    >
      Support Ticket Details
    </h3>

          <p>
            <strong>Subject:</strong>{" "}
            {relatedTicket.subject || "Not available"}
          </p>

          <p>
            <strong>Description:</strong>{" "}
            {relatedTicket.description || "Not available"}
          </p>

          <p>
            <strong>Status:</strong>{" "}
            {relatedTicket.status || "Not available"}
          </p>

          {relatedTicket.priority && (
            <p>
              <strong>Priority:</strong>{" "}
              {relatedTicket.priority}
            </p>
          )}

          {relatedTicket.user && (
            <>
              <p>
                <strong>Created by:</strong>{" "}
                {relatedTicket.user.fullname ||
                  relatedTicket.user.name ||
                  "Not available"}
              </p>

              {relatedTicket.user.email && (
                <p>
                  <strong>Email:</strong>{" "}
                  {relatedTicket.user.email}
                </p>
              )}
            </>
          )}
        </div>
      )}
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