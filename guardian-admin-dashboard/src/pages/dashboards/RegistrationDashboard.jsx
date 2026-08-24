import { motion } from "framer-motion";
import {
  CalendarCheck,
  Users,
  ClipboardList,
  Clock,
  ArrowRight,
} from "lucide-react";
import { Link } from "react-router-dom";
import { getAdminUser } from "../../utils/storage";

export default function RegistrationDashboard() {
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

            <h1>
              Welcome back{firstName ? `, ${firstName}` : ""}
            </h1>

            <p className="section-subtitle">
              Manage patient registrations, daily check-ins and administrative
              activities.
            </p>
          </div>

          <div className="hero-banner-actions">
            <Link to="/dashboard/patients" className="hero-link-card">
              <Users size={18} />

              <div>
                <strong>Patient Queue</strong>
                <span>View registered patients</span>
              </div>

              <ArrowRight size={16} />
            </Link>

            <Link to="/dashboard/patient-overview" className="hero-link-card">
              <CalendarCheck size={18} />

              <div>
                <strong>Daily Check-ins</strong>
                <span>Review today's patient activity</span>
              </div>

              <ArrowRight size={16} />
            </Link>
          </div>
        </div>
      </motion.section>

      <section className="stats-grid">
        <div className="stat-card">
          <CalendarCheck size={22} />
          <h3>Today's Check-ins</h3>
          <p>Check-in information will appear here</p>
        </div>

        <div className="stat-card">
          <Users size={22} />
          <h3>Patient Queue</h3>
          <p>Waiting patient information will appear here</p>
        </div>

        <div className="stat-card">
          <Clock size={22} />
          <h3>Waiting</h3>
          <p>Patient waiting information will appear here</p>
        </div>

        <div className="stat-card">
          <ClipboardList size={22} />
          <h3>Administrative Logs</h3>
          <p>Recent registration activity will appear here</p>
        </div>
      </section>

      <section className="dashboard-panels">
        <motion.article
          className="panel large"
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45, delay: 0.05 }}
        >
          <h3>Patient Queue</h3>

          <ul className="activity-list">
            <li>New patient registrations will appear here</li>
            <li>Patients waiting for check-in will appear here</li>
            <li>Completed check-ins will be updated here</li>
          </ul>
        </motion.article>

        <motion.article
          className="panel"
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45, delay: 0.12 }}
        >
          <h3>Administrative Logs</h3>

          <div className="mini-module-list">
            <div className="mini-module-item">
              <CalendarCheck size={18} />
              <span>Daily Check-ins</span>
            </div>

            <div className="mini-module-item">
              <Users size={18} />
              <span>Patient Registrations</span>
            </div>

            <div className="mini-module-item">
              <ClipboardList size={18} />
              <span>Registration Activity</span>
            </div>
          </div>
        </motion.article>
      </section>
    </div>
  );
}