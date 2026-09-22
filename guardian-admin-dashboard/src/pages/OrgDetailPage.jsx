import { motion } from "framer-motion";
import {
  Building2,
  CalendarDays,
  FileText,
  RefreshCcw,
  ShieldCheck,
  Users,
} from "lucide-react";
import Button from "../components/common/Button";

function formatDate(dateValue) {
  if (!dateValue) return "-";
  const date = new Date(dateValue);
  if (Number.isNaN(date.getTime())) return "-";
  return date.toLocaleDateString("en-AU", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

export default function OrgDetailPage({ org, onBack }) {
  
  const selectedOrg = org || {
    _id: "1",
    name: "Guardian Health Org",
    description: "Primary org for admin testing",
    active: true,
    createdBy: "admin001",
    created_at: "2026-04-11",
    updated_at: "2026-04-11",
    staff: [{}, {}, {}],
  };

  const stats = [
    {
      title: "Staff Members",
      value: selectedOrg.staff?.length ?? 0,
      icon: Users,
      description: "Staff currently linked with this organisation.",
    },
    {
      title: "Status",
      value: selectedOrg.active ? "Active" : "Inactive",
      icon: ShieldCheck,
      description: "Current organisation status from backend data.",
    },
    {
      title: "Created On",
      value: formatDate(selectedOrg.created_at),
      icon: CalendarDays,
      description: "Date this organisation record was created.",
    },
  ];

  return (
    <div className="dashboard-home">
      {/* Back button */}
      <div style={{ marginBottom: "16px" }}>
        <button
          onClick={onBack}
          style={{
            border: "none",
            borderRadius: "12px",
            padding: "10px 16px",
            fontWeight: 600,
            cursor: "pointer",
            background: "#e9f1f8",
            color: "#1b3a57",
          }}
        >
          ← Back to Organisations
        </button>
      </div>

      {/* Stats Cards */}
      <section className="stats-grid">
        {stats.map((item, index) => {
          const Icon = item.icon;
          return (
            <motion.article
              key={item.title}
              className="panel"
              initial={{ opacity: 0, y: 18 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.45, delay: index * 0.06 }}
            >
              <div className="org-stat-top">
                <div>
                  <p className="org-stat-label">{item.title}</p>
                  <h2 className="org-stat-value">{item.value}</h2>
                </div>
                <div className="org-stat-icon">
                  <Icon size={20} />
                </div>
              </div>
              <p className="org-muted-text">{item.description}</p>
            </motion.article>
          );
        })}
      </section>

      {/* Organisation Details + Overview Summary */}
      <section className="dashboard-panels">
        <motion.article
          className="panel large"
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45, delay: 0.05 }}
        >
          <div className="org-details-header">
            <h3 className="org-section-title">Organisation Details</h3>
          </div>

          <div className="org-details-grid">
            <div>
              <p className="org-detail-label">Organisation ID</p>
              <div className="org-detail-value org-break-word">
                {selectedOrg._id || "-"}
              </div>
            </div>

            <div>
              <p className="org-detail-label">Name</p>
              <div className="org-detail-value">{selectedOrg.name || "-"}</div>
            </div>

            <div>
              <p className="org-detail-label">Description</p>
              <div className="org-detail-value">
                {selectedOrg.description || "No description available."}
              </div>
            </div>

            <div>
              <p className="org-detail-label">Active</p>
              <div className="org-detail-value">
                {selectedOrg.active ? "Yes" : "No"}
              </div>
            </div>

            <div>
              <p className="org-detail-label">Created By</p>
              <div className="org-detail-value org-break-word">
                {selectedOrg.createdBy || "-"}
              </div>
            </div>

            <div>
              <p className="org-detail-label">Created At</p>
              <div className="org-detail-value">
                {formatDate(selectedOrg.created_at)}
              </div>
            </div>

            <div>
              <p className="org-detail-label">Updated At</p>
              <div className="org-detail-value">
                {formatDate(selectedOrg.updated_at)}
              </div>
            </div>

            <div>
              <p className="org-detail-label">Staff Count</p>
              <div className="org-detail-value">
                {selectedOrg.staff?.length ?? 0}
              </div>
            </div>
          </div>
        </motion.article>

        <motion.article
          className="panel"
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45, delay: 0.12 }}
        >
          <h3 className="org-section-title">Overview Summary</h3>

          <div className="mini-module-list org-summary-list">
            <div className="mini-module-item">
              <Building2 size={18} />
              <span>Organisation data is connected to this admin account</span>
            </div>

            <div className="mini-module-item">
              <Users size={18} />
              <span>{selectedOrg.staff?.length ?? 0} linked staff member(s)</span>
            </div>

            <div className="mini-module-item">
              <ShieldCheck size={18} />
              <span>Status: {selectedOrg.active ? "Active" : "Inactive"}</span>
            </div>

            <div className="mini-module-item">
              <FileText size={18} />
              <span>Ready for future organisation workflows and extensions</span>
            </div>
          </div>

          <div className="org-refresh-wrap">
            <Button onClick={() => {}}>
              <RefreshCcw size={18} />
              Refresh Data
            </Button>
          </div>
        </motion.article>
      </section>
    </div>
  );
}