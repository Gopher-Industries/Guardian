import { Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import ForgotPasswordPage from "./pages/ForgotPasswordPage";
import DashboardHome from "./pages/DashboardHome";
import AdminLayout from "./layout/AdminLayout";
import { getAuthToken, getAdminUser } from "./utils/storage";
import StaffManagementPage from "./pages/StaffManagementPage";
import OrgAssignmentPage from "./pages/OrgAssignmentPage";
import PatientsPage from "./pages/PatientsPage";
import NurseRosterPage from "./pages/NurseRosterPage";
import SupportTicketPage from "./pages/SupportTicketPage";
import TaskManagementPage from "./pages/TaskManagementPage";
import ReportsPage from "./pages/ReportsPage";
import SettingsPage from "./pages/SettingsPage";
import DoctorAssignmentsPage from "./pages/DoctorAssignmentsPage";
import PatientOverviewPage from "./pages/PatientOverviewPage";
import StatusPage from "./pages/StatusPage";
import PendingApprovalsPage from "./pages/PendingApprovalsPage";
import "./App.css";

function RequireAuth({ children }) {
  const token = getAuthToken();
  if (!token) return <Navigate to="/login" replace />;
  return children;
}

function RequireRole({ allowed, children }) {
  const role = getAdminUser()?.role;
  if (!allowed.includes(role)) return <StatusPage type={403} />;
  return children;
}

export default function App() {
  const isAuthenticated = !!getAuthToken();

  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/forgot-password" element={<ForgotPasswordPage />} />

      <Route
        path="/dashboard"
        element={
          <RequireAuth>
            <AdminLayout />
          </RequireAuth>
        }
      >
        <Route index element={<DashboardHome />} />

        <Route
          path="staff-management"
          element={
            <RequireRole allowed={["admin"]}>
              <StaffManagementPage />
            </RequireRole>
          }
        />
        <Route
          path="org-assignment"
          element={
            <RequireRole allowed={["admin"]}>
              <OrgAssignmentPage />
            </RequireRole>
          }
        />
        <Route
          path="patients"
          element={
            <RequireRole allowed={["admin", "doctor", "nurse"]}>
              <PatientsPage />
            </RequireRole>
          }
        />
        <Route
          path="patient-overview"
          element={
            <RequireRole allowed={["admin", "doctor", "nurse"]}>
              <PatientOverviewPage />
            </RequireRole>
          }
        />
        <Route
          path="task-management"
          element={
            <RequireRole allowed={["admin", "doctor", "nurse"]}>
              <TaskManagementPage />
            </RequireRole>
          }
        />
        <Route
          path="nurse-roster"
          element={
            <RequireRole allowed={["admin", "doctor", "nurse"]}>
              <NurseRosterPage />
            </RequireRole>
          }
        />
        <Route
          path="support-ticket"
          element={
            <RequireRole allowed={["admin", "doctor", "nurse"]}>
              <SupportTicketPage />
            </RequireRole>
          }
        />
        <Route
          path="doctor-assignments"
          element={
            <RequireRole allowed={["admin", "doctor"]}>
              <DoctorAssignmentsPage />
            </RequireRole>
          }
        />
        <Route
          path="reports"
          element={
            <RequireRole allowed={["admin"]}>
              <ReportsPage />
            </RequireRole>
          }
        />
        <Route
          path="pending-approvals"
          element={
            <RequireRole allowed={["admin"]}>
              <PendingApprovalsPage />
            </RequireRole>
          }
        />
        <Route path="settings" element={<SettingsPage />} />

        {/* Catches unmatched paths WITHIN /dashboard, e.g. /dashboard/staffmanagement */}
        <Route path="*" element={<StatusPage type={404} />} />
        <Route path="task-management" element={<TaskManagementPage />} />

      </Route>

      {/* Catches everything outside /dashboard entirely, e.g. /staffmanagement */}
      <Route
        path="*"
        element={
          isAuthenticated ? (
            <Navigate to="/dashboard" replace />
          ) : (
            <Navigate to="/login" replace />
          )
        }
      />
    </Routes>
  );
}