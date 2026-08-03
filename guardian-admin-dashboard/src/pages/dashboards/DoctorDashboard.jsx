import { motion } from "framer-motion";
import StatCard from "../../components/dashboard/StatCard";
import { DOCTOR_DASHBOARD_STATS } from "../../utils/constants";
import { ArrowRight, Stethoscope, ListTodo, ClipboardList, Users } from "lucide-react";
import { Link } from "react-router-dom";
import { getAdminUser } from "../../utils/storage";

export default function DoctorDashboard() {
  const user = getAdminUser();
  const firstName = user?.fullname?.split(" ")[0] || "";
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
            <p className="section-eyebrow">Guardian Monitor</p>
            <h1>Welcome back, Dr. {firstName}</h1>
            <p className="section-subtitle">
              A quick view of your assigned patients, tasks, and nurse coordination.
            </p>
          </div>

          <div className="hero-banner-actions">
            <Link to="/dashboard/doctor-assignments" className="hero-link-card">
              <Stethoscope size={18} />
              <div>
                <strong>Doctor Assignments</strong>
                <span>View your assigned cases</span>
              </div>
              <ArrowRight size={16} />
            </Link>

            <Link to="/dashboard/task-management" className="hero-link-card">
              <ListTodo size={18} />
              <div>
                <strong>Task Management</strong>
                <span>Assign and track tasks for nurses</span>
              </div>
              <ArrowRight size={16} />
            </Link>
          </div>
        </div>
      </motion.section>

      <section className="stats-grid">
        {DOCTOR_DASHBOARD_STATS.map((item) => (
          <StatCard key={item.title} {...item} />
        ))}
      </section>

      <section className="dashboard-panels">
        <motion.article
          className="panel large"
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45, delay: 0.05 }}
        >
          <h3>Recent activity</h3>
          <ul className="activity-list">
            <li>Patient assignment data will appear here</li>
            <li>Task assignments to nurses will appear here</li>
            <li>Recent patient overview updates will appear here</li>
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

            <div className="mini-module-item">
              <ClipboardList size={18} />
              <span>Nurse Roster</span>
            </div>
          </div>
        </motion.article>
      </section>
    </div>
  );
}