import { useState } from "react";
import { Bell, Search, UserCircle2 } from "lucide-react";
import { getAdminUser } from "../../utils/storage";
import NotificationPanel from "./NotificationPanel";

export default function Topbar() {
  const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
  const admin = getAdminUser() || {
    fullname: "Guardian Admin",
    role: "admin",
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
          </button>
          
          <NotificationPanel 
            isOpen={isNotificationsOpen} 
            onClose={() => setIsNotificationsOpen(false)} 
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
    </header>
  );
}