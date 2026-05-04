import { NavLink, useNavigate } from "react-router-dom";
import {
  Bell,
  LayoutDashboard,
  LogOut,
  Settings,
  ShieldPlus,
  ClipboardList,
  Users,
  Building2,
  X,
} from "lucide-react";
import Logo from "../common/Logo";
import { ADMIN_NAV_ITEMS } from "../../utils/constants";
import { clearAuthStorage } from "../../utils/storage";

const iconMap = {
  dashboard: LayoutDashboard,
  "staff-management": Users,
  "org-assignment": Building2,
  patients: ShieldPlus,
  "patient-overview": ClipboardList,
  reports: Bell,
  settings: Settings,
  "nurse-roster": ClipboardList
};

export default function Sidebar({
  isMobile,
  mobileSidebarOpen,
  collapsed,
  onToggle,
  onCloseMobile,
}) {
  const navigate = useNavigate();

  const handleLogout = () => {
    clearAuthStorage();
    navigate("/login");
  };

  return (
    <aside
      className={`sidebar ${
        isMobile ? "mobile-sidebar" : "desktop-sidebar"
      } ${isMobile ? (mobileSidebarOpen ? "open" : "closed") : ""} ${
        !isMobile && collapsed ? "collapsed" : ""
      }`}
    >
      <div className="sidebar-pulse-line" />

      <div className="sidebar-inner">
        <div className="sidebar-header">
          <button
            type="button"
            className={`sidebar-brand-button ${
              collapsed && !isMobile ? "brand-button-collapsed" : ""
            }`}
            onClick={isMobile ? onCloseMobile : onToggle}
            aria-label="Toggle sidebar"
            title={collapsed && !isMobile ? "Expand sidebar" : "Collapse sidebar"}
          >
            <div className="sidebar-logo-wrap">
              <Logo size={collapsed && !isMobile ? "small" : "medium"} />
            </div>

            <div
              className={`sidebar-brand-copy ${
                collapsed && !isMobile ? "brand-copy-hidden" : ""
              }`}
            >
              <span className="sidebar-brand-title">Guardian Admin</span>
              <span className="sidebar-brand-subtitle">Care Operations</span>
            </div>
          </button>

          {isMobile ? (
            <button
              className="icon-button sidebar-close-button"
              type="button"
              onClick={onCloseMobile}
              aria-label="Close sidebar"
            >
              <X size={18} />
            </button>
          ) : null}
        </div>

        <nav className="sidebar-nav">
          {ADMIN_NAV_ITEMS.map((item) => {
            const Icon = iconMap[item.id] || LayoutDashboard;

            return (
              <NavLink
                key={item.id}
                to={item.path}
                end={
                      item.path === "/dashboard" ||
                      item.path === "/dashboard/patients"
                    }
                className={({ isActive }) =>
                  `sidebar-link ${isActive ? "active" : ""} ${
                    collapsed && !isMobile ? "icon-only" : ""
                  }`
                }
                title={collapsed && !isMobile ? item.label : ""}
              >
                <span className="sidebar-link-icon">
                  <Icon size={18} />
                </span>

                <span
                  className={`sidebar-link-label ${
                    collapsed && !isMobile ? "sidebar-label-hidden" : ""
                  }`}
                >
                  {item.label}
                </span>
              </NavLink>
            );
          })}
        </nav>

        <div className="sidebar-footer">
          <button
            className={`logout-button ${
              collapsed && !isMobile ? "icon-only" : ""
            }`}
            type="button"
            onClick={handleLogout}
            title={collapsed && !isMobile ? "Logout" : ""}
          >
            <span className="sidebar-link-icon">
              <LogOut size={18} />
            </span>

            <span
              className={`sidebar-link-label ${
                collapsed && !isMobile ? "sidebar-label-hidden" : ""
              }`}
            >
              Logout
            </span>
          </button>
        </div>
      </div>
    </aside>
  );
}