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
  {
  id: "patient-overview",
  label: "Patient Overview",
  path: "/dashboard/patient-overview",
},
  {
    id: "task-management",
    label: "Task Management",
    path: "/dashboard/task-management",
  },
  
  { id: "nurse-roster", label: "Nurse Roster", path: "/dashboard/nurse-roster" },
  { id: "support-ticket", label: "Support Ticket", path: "/dashboard/support-ticket" },
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

export const TICKET_ISSUE_TYPE_OPTIONS = [
  { value: 'technical', label: 'Technical' },
  { value: 'billing', label: 'Billing' },
  { value: 'general', label: 'General' },
  { value: 'feature_request', label: 'Feature Request' },
];

export const TICKET_PRIORITY_OPTIONS = [
  { value: 'low', label: 'Low' },
  { value: 'medium', label: 'Medium' },
  { value: 'high', label: 'High' },
  { value: 'critical', label: 'Critical' },
];

export const TICKET_STATUS_OPTIONS = [
  { value: 'open', label: 'Open' },
  { value: 'in_progress', label: 'In Progress' },
  { value: 'resolved', label: 'Resolved' },
  { value: 'closed', label: 'Closed' },
];