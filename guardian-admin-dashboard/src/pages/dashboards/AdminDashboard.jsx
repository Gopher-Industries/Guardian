import { useEffect, useState, useMemo } from "react";
import { motion } from "framer-motion";
import {
  ArrowRight,
  Building2,
  Users,
  ShieldAlert,
  FileBarChart2,
} from "lucide-react";
import { Link } from "react-router-dom";
import api from "../../services/api";
import DashboardSummaryCards from "../../components/dashboard/DashboardSummaryCards";
import StatCard from "../../components/dashboard/StatCard";
import { DASHBOARD_STATS } from "../../utils/constants";
import { getAdminUser } from "../../utils/storage";

export default function AdminDashboard() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [lastUpdated, setLastUpdated] = useState(null);

  const dashboardPreferences = useMemo(() => {
    const saved = localStorage.getItem("dashboardPreferences");
    return saved
      ? JSON.parse(saved)
      : {
          heroBanner: true,
          statistics: true,
          dashboardPanels: true,
        };
  }, []);

  const user = getAdminUser();
  const firstName = user?.fullname?.split(" ")[0] || "";

  useEffect(() => {
    const fetchSummary = async () => {
      try {
        const response = await api.get("admin/dashboard-summary");
        setSummary(response.data);
        setLastUpdated(new Date());
        setError("");
      } catch (err) {
        setError(
          err?.response?.data?.message ||
            err?.message ||
            "Failed to load dashboard summary."
        );
      } finally {
        setLoading(false);
      }
    };

    fetchSummary();

    const interval = setInterval(() => {
      fetchSummary();
    }, 30000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="dashboard-home">
      {dashboardPreferences.heroBanner && (
        <motion.section
          className="hero-banner"
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45 }}
        >
          <div className="hero-banner-content">
            <div>
              <p className="section-eyebrow">Guardian Monitor Admin</p>
              <h1>Welcome back{firstName ? `, ${firstName}` : ""}</h1>
              <p className="section-subtitle">
                Monitor patients, staff, tasks, and system activity from one place.
              </p>
            </div>

            <div className="hero-banner-actions">
              <Link
                to="/dashboard/staff-management"
                className="hero-link-card"
              >
                <Users size={18} />
                <div>
                  <strong>Staff Management</strong>
                  <span>Manage staff members and administrative access</span>
                </div>
                <ArrowRight size={16} />
              </Link>

              <Link
                to="/dashboard/org-assignment"
                className="hero-link-card"
              >
                <Building2 size={18} />
                <div>
                  <strong>Organisation & Assignment</strong>
                  <span>Manage organisations and staff assignments</span>
                </div>
                <ArrowRight size={16} />
              </Link>
            </div>
          </div>
        </motion.section>
      )}

      {lastUpdated && (
        <div className="dashboard-metrics-header">
          <span>Live system statistics</span>
          <span className="dashboard-last-updated">
            Updated: {lastUpdated.toLocaleTimeString()}
          </span>
        </div>
      )}

      <DashboardSummaryCards
        summary={summary}
        loading={loading}
        error={error}
      />

      {dashboardPreferences.statistics && (
        <section className="stats-grid">
          {DASHBOARD_STATS.map((item) => (
            <StatCard key={item.title} {...item} />
          ))}
        </section>
      )}

      {dashboardPreferences.dashboardPanels && (
        <section className="dashboard-panels">
          <motion.article
            className="panel large"
            initial={{ opacity: 0, y: 18 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.45, delay: 0.05 }}
          >
            <h3>Shell layout progress</h3>
            <ul className="activity-list">
              <li>Login and OTP flow UI created</li>
              <li>Protected admin routing structure prepared</li>
              <li>Sidebar and topbar shell layout created</li>
              <li>Starter routes created for admin team members</li>
            </ul>
          </motion.article>

          <motion.article
            className="panel"
            initial={{ opacity: 0, y: 18 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.45, delay: 0.12 }}
          >
            <h3>Upcoming modules</h3>
            <div className="mini-module-list">
              <div className="mini-module-item">
                <ShieldAlert size={18} />
                <span>Alerts & Monitoring</span>
              </div>
              <div className="mini-module-item">
                <Users size={18} />
                <span>Staff Administration</span>
              </div>
              <div className="mini-module-item">
                <Building2 size={18} />
                <span>Organisation Workflows</span>
              </div>
              <div className="mini-module-item">
                <FileBarChart2 size={18} />
                <span>Reports & Analytics</span>
              </div>
            </div>
          </motion.article>
        </section>
      )}
    </div>
  );
}