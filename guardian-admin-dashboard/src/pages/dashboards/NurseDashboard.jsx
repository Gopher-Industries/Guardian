import { useEffect, useMemo, useState } from "react";
import { motion } from "framer-motion";
import StatCard from "../../components/dashboard/StatCard";
import { getNurseDashboardSummary } from "../../services/nurseDashboardService";
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

  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let isMounted = true;

    async function loadSummary() {
      setLoading(true);
      setError("");

      try {
        const data = await getNurseDashboardSummary();
        if (isMounted) setSummary(data);
      } catch (err) {
        if (isMounted) {
          setError(
            err?.response?.data?.error ||
              "Could not load your dashboard summary right now."
          );
        }
      } finally {
        if (isMounted) setLoading(false);
      }
    }

    loadSummary();

    return () => {
      isMounted = false;
    };
  }, []);

  const stats = useMemo(() => {
    if (!summary) {
      const fallbackDescription = error ? "No data available" : "Loading...";
      return [
        { title: "Assigned Patients", value: "--", description: fallbackDescription, tone: "primary" },
        { title: "Pending Tasks", value: "--", description: fallbackDescription, tone: "warning" },
        { title: "Task Completion Rate", value: "--", description: fallbackDescription, tone: "success" },
        { title: "Recent Patient Logs", value: "--", description: fallbackDescription, tone: "danger" },
      ];
    }

    return [
      {
        title: "Assigned Patients",
        value: summary.totalPatients ?? "--",
        description: `${summary.totalActivePatients ?? 0} currently active`,
        tone: "primary",
      },
      {
        title: "Pending Tasks",
        value: summary.pendingTasks ?? "--",
        description: `${summary.overdueTasks ?? 0} overdue`,
        tone: "warning",
      },
      {
        title: "Task Completion Rate",
        value: `${summary.taskCompletionRate ?? 0}%`,
        description: `${summary.completedTasks ?? 0} of ${summary.totalTasks ?? 0} tasks completed`,
        tone: "success",
      },
      {
        title: "Recent Patient Logs",
        value: summary.recentLogsCount ?? "--",
        description: "In the last 7 days",
        tone: "danger",
      },
    ];
  }, [summary, error]);

  const activityItems = useMemo(() => {
    if (!summary) return [];

    const items = [];

    if (summary.overdueTasks > 0) {
      items.push(
        `You have ${summary.overdueTasks} overdue task${summary.overdueTasks === 1 ? "" : "s"}.`
      );
    }

    if (summary.pendingTasks > 0) {
      items.push(
        `${summary.pendingTasks} task${summary.pendingTasks === 1 ? " is" : "s are"} still pending.`
      );
    }

    if (summary.recentLogsCount > 0) {
      items.push(
        `${summary.recentLogsCount} patient log${summary.recentLogsCount === 1 ? "" : "s"} added in the last 7 days.`
      );
    }

    if (items.length === 0) {
      items.push("No recent activity to show right now.");
    }

    return items;
  }, [summary]);

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
          {stats.map((item) => (
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
              {loading ? (
                <li>Loading recent activity...</li>
              ) : error ? (
                <li>Recent activity will appear here once this is connected.</li>
              ) : (
                activityItems.map((item, index) => <li key={index}>{item}</li>)
              )}
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