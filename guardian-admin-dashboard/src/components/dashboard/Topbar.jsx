import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Bell, Search, UserCircle2, User, Users, Stethoscope } from "lucide-react";
import { getAdminUser } from "../../utils/storage";
import NotificationPanel from "./NotificationPanel";
import {
  getNotifications,
  deleteNotification
} from "../../services/notificationService";
import { getAllPatients } from "../../services/patientService";
import { getStaff } from "../../services/staffService";
import { getNurses } from "../../services/taskLookupService";

const SEARCH_DEBOUNCE_MS = 300;
const MIN_SEARCH_LENGTH = 2;
const MAX_RESULTS_PER_GROUP = 5;
const EMPTY_RESULTS = { patients: [], staff: [], nurses: [] };

function includesTerm(value, term) {
  return typeof value === "string" && value.toLowerCase().includes(term);
}

function toListArray(data, key) {
  if (Array.isArray(data)) return data;
  return data?.[key] || data?.data || [];
}

export default function Topbar({
  notifications,
  onRefreshNotifications,
  onDeleteRequest,
  onOpenDrawer,
  setNotifications,
  onViewNotification
}) {
  const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [isSearchFocused, setIsSearchFocused] = useState(false);
  const [isSearching, setIsSearching] = useState(false);
  const [searchResults, setSearchResults] = useState(EMPTY_RESULTS);
  const navigate = useNavigate();
  const searchWrapperRef = useRef(null);
  const searchRequestId = useRef(0);

  const admin = getAdminUser() || {
    fullname: "Guardian Admin",
    role: "admin",
  };

  const unreadCount = notifications.filter(n => !(n.isRead || n.read)).length;

  useEffect(() => {
    function handleClickOutside(e) {
      if (searchWrapperRef.current && !searchWrapperRef.current.contains(e.target)) {
        setIsSearchFocused(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    const term = searchTerm.trim().toLowerCase();

    if (term.length < MIN_SEARCH_LENGTH) {
      searchRequestId.current += 1;
      setSearchResults(EMPTY_RESULTS);
      setIsSearching(false);
      return;
    }

    const requestId = ++searchRequestId.current;
    setIsSearching(true);

    const handle = setTimeout(async () => {
      const [patientsRes, staffRes, nursesRes] = await Promise.allSettled([
        getAllPatients(),
        getStaff({ search: term, limit: MAX_RESULTS_PER_GROUP }),
        getNurses(),
      ]);

      if (requestId !== searchRequestId.current) return;

      const patients = toListArray(
        patientsRes.status === "fulfilled" ? patientsRes.value : [],
        "patients"
      )
        .filter((p) =>
          includesTerm(p?.fullname || p?.fullName || p?.name, term) ||
          includesTerm(p?._id || p?.id, term)
        )
        .slice(0, MAX_RESULTS_PER_GROUP)
        .map((p) => ({
          id: p._id || p.id,
          title: p.fullname || p.fullName || p.name || "Unnamed Patient",
          subtitle: p._id || p.id || "",
        }));

      const staff = toListArray(
        staffRes.status === "fulfilled" ? staffRes.value : [],
        "staff"
      )
        .filter((s) =>
          includesTerm(s?.fullname || s?.fullName, term) ||
          includesTerm(s?.email, term)
        )
        .slice(0, MAX_RESULTS_PER_GROUP)
        .map((s) => ({
          id: s._id || s.id,
          title: s.fullname || s.fullName || "Unnamed Staff",
          subtitle: s.email || "",
        }));

      const nurses = toListArray(
        nursesRes.status === "fulfilled" ? nursesRes.value : [],
        "nurses"
      )
        .filter((n) =>
          includesTerm(n?.fullname || n?.fullName || n?.name, term) ||
          includesTerm(n?.email, term)
        )
        .slice(0, MAX_RESULTS_PER_GROUP)
        .map((n) => ({
          id: n._id || n.id,
          title: n.fullname || n.fullName || n.name || "Unnamed Nurse",
          subtitle: n.email || "",
        }));

      setSearchResults({ patients, staff, nurses });
      setIsSearching(false);
    }, SEARCH_DEBOUNCE_MS);

    return () => clearTimeout(handle);
  }, [searchTerm]);

  const closeSearch = () => setIsSearchFocused(false);

  const goToPatient = (result) => {
    navigate(`/dashboard/patient-overview?q=${encodeURIComponent(result.title)}`);
    closeSearch();
  };

  const goToStaff = (result) => {
    navigate(`/dashboard/staff-management?q=${encodeURIComponent(result.title)}`);
    closeSearch();
  };

  const goToNurse = (result) => {
    navigate(`/dashboard/nurse-roster?q=${encodeURIComponent(result.title)}`);
    closeSearch();
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (searchResults.patients[0]) return goToPatient(searchResults.patients[0]);
    if (searchResults.staff[0]) return goToStaff(searchResults.staff[0]);
    if (searchResults.nurses[0]) return goToNurse(searchResults.nurses[0]);
  };

  const handleSearchKeyDown = (e) => {
    if (e.key === "Escape") closeSearch();
  };

  const totalResults =
    searchResults.patients.length + searchResults.staff.length + searchResults.nurses.length;
  const showDropdown = isSearchFocused && searchTerm.trim().length >= MIN_SEARCH_LENGTH;

  return (
    <header className="topbar">
      <div className="topbar-left">
        <div>
          <p className="topbar-eyebrow">Administrator Workspace</p>
          <h2 className="topbar-title">Dashboard Overview</h2>
        </div>
      </div>

      <div className="topbar-right">
        <div className="search-box-wrapper" ref={searchWrapperRef}>
          <form className="search-box" onSubmit={handleSearchSubmit} role="search">
            <Search size={16} />
            <input
              type="text"
              placeholder="Search records..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onFocus={() => setIsSearchFocused(true)}
              onKeyDown={handleSearchKeyDown}
            />
          </form>

          {showDropdown && (
            <div className="global-search-dropdown">
              {isSearching ? (
                <div className="global-search-empty">Searching...</div>
              ) : totalResults === 0 ? (
                <div className="global-search-empty">No matching records found.</div>
              ) : (
                <>
                  {searchResults.patients.length > 0 && (
                    <div className="global-search-group">
                      <p className="global-search-group-label">Patients</p>
                      {searchResults.patients.map((r) => (
                        <button
                          key={`patient-${r.id}`}
                          type="button"
                          className="global-search-item"
                          onClick={() => goToPatient(r)}
                        >
                          <User size={14} />
                          <span>
                            <strong>{r.title}</strong>
                            {r.subtitle && <small>{r.subtitle}</small>}
                          </span>
                        </button>
                      ))}
                    </div>
                  )}

                  {searchResults.staff.length > 0 && (
                    <div className="global-search-group">
                      <p className="global-search-group-label">Staff</p>
                      {searchResults.staff.map((r) => (
                        <button
                          key={`staff-${r.id}`}
                          type="button"
                          className="global-search-item"
                          onClick={() => goToStaff(r)}
                        >
                          <Users size={14} />
                          <span>
                            <strong>{r.title}</strong>
                            {r.subtitle && <small>{r.subtitle}</small>}
                          </span>
                        </button>
                      ))}
                    </div>
                  )}

                  {searchResults.nurses.length > 0 && (
                    <div className="global-search-group">
                      <p className="global-search-group-label">Nurses</p>
                      {searchResults.nurses.map((r) => (
                        <button
                          key={`nurse-${r.id}`}
                          type="button"
                          className="global-search-item"
                          onClick={() => goToNurse(r)}
                        >
                          <Stethoscope size={14} />
                          <span>
                            <strong>{r.title}</strong>
                            {r.subtitle && <small>{r.subtitle}</small>}
                          </span>
                        </button>
                      ))}
                    </div>
                  )}
                </>
              )}
            </div>
          )}
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
            refreshNotifications={onRefreshNotifications}
            onDeleteRequest={onDeleteRequest}
            onViewNotification={onViewNotification}
            onViewAll={() => {
              setIsNotificationsOpen(false);
              onOpenDrawer();
            }}
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
