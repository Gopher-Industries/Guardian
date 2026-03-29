import { Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import OtpPage from "./pages/OtpPage";
import DashboardHome from "./pages/DashboardHome";
import AdminLayout from "./layout/AdminLayout";
import { getAuthToken } from "./utils/storage";
import StaffManagementPage from "./pages/StaffManagementPage";
import OrgAssignmentPage from "./pages/OrgAssignmentPage";
import PatientsPage from "./pages/PatientsPage";
import NurseRosterPage from "./pages/NurseRosterPage";
import ReportsPage from "./pages/ReportsPage";
import SettingsPage from "./pages/SettingsPage";
import "./App.css";

function ProtectedRoute({ children }) {
  const token = getAuthToken();
  return token ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/otp" element={<OtpPage />} />

      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <AdminLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<DashboardHome />} />
        <Route path="staff-management" element={<StaffManagementPage />} />
        <Route path="org-assignment" element={<OrgAssignmentPage />} />
        <Route path="patients" element={<PatientsPage />} />
        <Route path="nurse-roster" element={<NurseRosterPage />} />
        <Route path="reports" element={<ReportsPage />} />
        <Route path="settings" element={<SettingsPage />} />
      </Route>

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}