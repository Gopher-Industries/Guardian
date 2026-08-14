import { useMemo } from "react";
import { motion } from "framer-motion";
import StatCard from "../../components/dashboard/StatCard";
import { NURSE_DASHBOARD_STATS } from "../../utils/constants";
import {
  ArrowRight,
  ListTodo,
  ClipboardList,
  Users,
} from "lucide-react";
import { Link } from "react-router-dom";
import { getAdminUser } from "../../utils/storage";

export default function NurseDashboard() {
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
              <p className="section-eyebrow">Guardian Monitor</p>
              <h1>Welcome back, {firstName}</h1>
              <p className="section-subtitle">
                A quick view of your assigned patients, today's tasks, and
                roster status.
              </p>
            </div>

            <div className="hero-banner-actions">
              <Link
                to="/dashboard/task-management"
                className="hero-link-card"
              >
                <ListTodo size={18} />
                <div>
                  <strong>Task Management</strong>
                  <span>View tasks assigned to you</span>
                </div>
                <ArrowRight size={16} />
              </Link>

              <Link
                to="/dashboard/nurse-roster"
                className="hero-link-card"
              >
                <ClipboardList size={18} />
                <div>
                  <strong>Nurse Roster</strong>
                  <span>Check your shift schedule</span>
                </div>
                <ArrowRight size={16} />
              </Link>
            </div>
          </div>
        </motion.section>
      )}

      {dashboardPreferences.statistics && (
        <section className="stats-grid">
          {NURSE_DASHBOARD_STATS.map((item) => (
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
            <h3>Recent activity</h3>
            <ul className="activity-list">
              <li>Tasks assigned to you will appear here</li>
              <li>Patient overview updates will appear here</li>
              <li>Roster changes will appear here</li>
            </ul>
          </motion.article>

          <motion.article
            className="panel"
            initial={{ opacity: 0, y: 18 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.45, delay: 0.12 }}
          >
            <h3>Quick links</h3>

            <div className="mini-module-list">
              <div className="mini-module-item">
                <Users size={18} />
                <span>Patients</span>
              </div>

              <div className="mini-module-item">
                <ClipboardList size={18} />
                <span>Patient Overview</span>
              </div>
            </div>
          </motion.article>
        </section>
      )}
    </div>
  );
}