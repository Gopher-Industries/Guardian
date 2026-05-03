import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import StatCard from "../components/dashboard/StatCard";
import { DASHBOARD_STATS } from "../utils/constants";
import { ArrowRight, Building2, Users, ShieldAlert, FileBarChart2 } from "lucide-react";
import { Link } from "react-router-dom";
import axios from "axios";
import DashboardSummaryCards from "../components/dashboard/DashboardSummaryCards";

export default function DashboardHome() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchSummary = async () => {
      try {
        const token = localStorage.getItem("guardian_admin_token");

        const response = await axios.get(
          "https://guardian-backend-git-fix-cors-patelrudra2306-5873s-projects.vercel.app/api/v1/admin/dashboard-summary",
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
              Accept: "application/json",
            },
            withCredentials: false,
          }
        );
        setSummary(response.data);
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
  }, []);

  return (
    <div className="dashboard-home">
      <motion.section
        className="hero-banner"
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.45 }}
      >
        <div className="hero-banner-content">
          <div>
            <p className="section-eyebrow">Guardian Monitor Admin</p>
            <h1>Welcome to the admin dashboard</h1>
            <p className="section-subtitle">
              This shell layout is now ready for staff management, organisation workflows,
              reporting, and future analytics modules.
            </p>
          </div>

          <div className="hero-banner-actions">
            <Link to="/dashboard/staff-management" className="hero-link-card">
              <Users size={18} />
              <div>
                <strong>Staff Management</strong>
                <span>Continue building admin-facing staff flows</span>
              </div>
              <ArrowRight size={16} />
            </Link>

            <Link to="/dashboard/org-assignment" className="hero-link-card">
              <Building2 size={18} />
              <div>
                <strong>Organisation & Assignment</strong>
                <span>Continue organisation and assignment workflows</span>
              </div>
              <ArrowRight size={16} />
            </Link>
          </div>
        </div>
      </motion.section>

      <DashboardSummaryCards
        summary={summary}
        loading={loading}
        error={error}
      />

      {/* <section className="stats-grid">
        {DASHBOARD_STATS.map((item) => (
          <StatCard key={item.title} {...item} />
        ))}
      </section> */}

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
    </div>
  );
}