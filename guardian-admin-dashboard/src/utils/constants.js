export const ADMIN_NAV_ITEMS = [
  { id: "dashboard", label: "Dashboard", path: "/dashboard" },
  {
    id: "staff-management",
    label: "Staff Management",
    path: "/dashboard/staff-management",
  },
  {
    id: "org-assignment",
    label: "Organisation",
    path: "/dashboard/org-assignment",
  },
  { id: "patients", label: "Patients", path: "/dashboard/patients" },
  { id: "nurse-roster", label: "Nurse Roster", path: "/dashboard/nurse-roster" },
  { id: "reports", label: "Reports", path: "/dashboard/reports" },
  { id: "settings", label: "Settings", path: "/dashboard/settings" },
];
export const DASHBOARD_STATS = [
  {
    title: "Patients",
    value: "--",
    description: "Patient data module to be connected",
    tone: "primary",
  },
  {
    title: "Critical Alerts",
    value: "--",
    description: "Live alerts integration pending",
    tone: "danger",
  },
  {
    title: "Care Staff",
    value: "--",
    description: "Organisation staff data pending",
    tone: "success",
  },
  {
    title: "Pending Reports",
    value: "--",
    description: "Reporting module to be connected",
    tone: "warning",
  },
];

export const STORAGE_KEYS = {
  token: "guardian_admin_token",
  user: "guardian_admin_user",
  email: "guardian_admin_email",
  pendingToken: "guardian_admin_pending_token",
  pendingUser: "guardian_admin_pending_user",
};

export const ROLE_OPTIONS = [
  { value: 'doctor', label: 'Doctor' },
  { value: 'nurse', label: 'Nurse' },
];