import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { ArrowRight, Building2, Users } from "lucide-react";
import { Link } from "react-router-dom";
import api from "../services/api";
import DashboardSummaryCards from "../components/dashboard/DashboardSummaryCards";

export default function DashboardHome() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchSummary = async () => {
      try {
        const response = await api.get("admin/dashboard-summary");
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

      <DashboardSummaryCards
        summary={summary}
        loading={loading}
        error={error}
      />
    </div>
  );
}