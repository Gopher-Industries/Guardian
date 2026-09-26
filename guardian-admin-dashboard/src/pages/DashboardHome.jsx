
import { getAdminUser } from "../utils/storage";
import AdminDashboard from "./dashboards/AdminDashboard";
import DoctorDashboard from "./dashboards/DoctorDashboard";
import NurseDashboard from "./dashboards/NurseDashboard";



export default function DashboardHome() {
  const role = getAdminUser()?.role;

  if (role === "doctor") return <DoctorDashboard />;
  if (role === "nurse" || role === "caretaker") return <NurseDashboard />;
  return <AdminDashboard />; 
}