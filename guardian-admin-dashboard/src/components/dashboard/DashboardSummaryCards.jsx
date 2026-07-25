import { motion } from "framer-motion";
import {
  Users,
  UserCheck,
  Stethoscope,
  ClipboardList,
  CheckCircle2,
  Clock,
  TrendingUp,
} from "lucide-react";
import "./DashboardSummaryCards.css"

const CARD_CONFIG = [
  {
    key: "totalPatients",
    label: "Total Patients",
    icon: Stethoscope,
    accent: "accent-teal",
    description: "All registered patients",
  },
  {
    key: "totalActivePatients",
    label: "Active Patients",
    icon: UserCheck,
    accent: "accent-green",
    description: "Currently active",
  },
  {
    key: "totalStaff",
    label: "Care Staff",
    icon: Users,
    accent: "accent-blue",
    description: "Organisation staff",
  },
  {
    key: "totalTasks",
    label: "Total Tasks",
    icon: ClipboardList,
    accent: "accent-purple",
    description: "All assigned tasks",
  },
  {
    key: "completedTasks",
    label: "Completed Tasks",
    icon: CheckCircle2,
    accent: "accent-emerald",
    description: "Successfully completed",
  },
  {
    key: "pendingTasks",
    label: "Pending Tasks",
    icon: Clock,
    accent: "accent-amber",
    description: "Awaiting completion",
  },
  {
    key: "taskCompletionRate",
    label: "Completion Rate",
    icon: TrendingUp,
    accent: "accent-rose",
    description: "Tasks completed (%)",
    suffix: "%",
  },
];

export default function DashboardSummaryCards({ summary, loading, error }) {
  if (loading) {
    return (
      <div className="dsc-grid">
        {CARD_CONFIG.map((card) => (
          <div key={card.key} className="dsc-card dsc-skeleton">
            <div className="dsc-skeleton-icon" />
            <div className="dsc-skeleton-line short" />
            <div className="dsc-skeleton-line long" />
          </div>
        ))}
      </div>
    );
  }

  if (error) {
    return (
      <div className="dsc-error">
        <p>⚠ Could not load summary: {error}</p>
      </div>
    );
  }

  if (!summary) return null;

  return (
    <div className="dsc-grid">
      {CARD_CONFIG.map((card, i) => {
        const Icon = card.icon;
        const value = summary[card.key] ?? "—";

        return (
          <motion.div
            key={card.key}
            className={`dsc-card ${card.accent}`}
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.35, delay: i * 0.06 }}
          >
            <div className="dsc-card-header">
              <div className="dsc-icon-wrap">
                <Icon size={18} />
              </div>
              <span className="dsc-label">{card.label}</span>
            </div>
            <div className="dsc-value">
              {value}
              {card.suffix && value !== "—" && (
                <span className="dsc-suffix">{card.suffix}</span>
              )}
            </div>
            <p className="dsc-description">{card.description}</p>
          </motion.div>
        );
      })}
    </div>
  );
}