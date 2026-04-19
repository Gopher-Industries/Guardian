import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import {
  ArrowRight,
  Building2,
  CalendarDays,
  CircleAlert,
  FileText,
  RefreshCcw,
  ShieldCheck,
  Users,
} from "lucide-react";
import { Link } from "react-router-dom";
import Button from "../components/common/Button";
import Dropdown from "../components/common/Dropdown";
import { getMyOrganizations } from "../services/orgService";

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

export default function OrgAssignmentPage() {
  const [organizations, setOrganizations] = useState([]);
  const [selectedOrgId, setSelectedOrgId] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchOrganizations = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const response = await getMyOrganizations();
      const orgs = Array.isArray(response?.orgs) ? response.orgs : [];

      setOrganizations(orgs);
      setSelectedOrgId((prev) => prev || orgs[0]?._id || "");
    } catch (err) {
      console.error("Failed to fetch organizations:", err);
      setError("Failed to fetch organizations.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchOrganizations();
  }, [fetchOrganizations]);

  const selectedOrg =
    organizations.find((org) => org._id === selectedOrgId) || organizations[0];

  const organizationOptions = organizations.map((org) => ({
    value: org._id,
    label: org.name,
  }));

  const stats = selectedOrg
    ? [
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
      ]
    : [];

  if (loading) {
    return (
      <div className="panel">
        <h3 className="org-page-title">Organisation & Assignment</h3>
        <p className="org-muted-text">Loading organisation details...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="panel">
        <div className="org-error-header">
          <CircleAlert size={20} />
          <div>
            <h3 className="org-error-title">Organisation & Assignment</h3>
            <p className="org-muted-text">{error}</p>
          </div>
        </div>

        <Button onClick={fetchOrganizations}>
          <RefreshCcw size={18} />
          Try Again
        </Button>
      </div>
    );
  }

  if (!selectedOrg) {
    return (
      <div className="panel">
        <h3 className="org-page-title">Organisation & Assignment</h3>
        <p className="org-muted-text">No organisation data found yet.</p>
      </div>
    );
  }

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
            <p className="section-eyebrow">Organisation Overview</p>
            <h1>{selectedOrg.name}</h1>
            <p className="section-subtitle">
              View organisation information, staff count, current status, and
              prepare for organisation-related admin workflows.
            </p>
          </div>

          <div className="hero-banner-actions">
            <Link to="/dashboard/staff-management" className="hero-link-card">
              <Users size={18} />
              <div>
                <strong>Staff Management</strong>
                <span>View and manage staff linked with this organisation</span>
              </div>
              <ArrowRight size={16} />
            </Link>

            <Link to="/dashboard" className="hero-link-card">
              <Building2 size={18} />
              <div>
                <strong>Dashboard</strong>
                <span>Return to the main administrator workspace</span>
              </div>
              <ArrowRight size={16} />
            </Link>
          </div>
        </div>
      </motion.section>

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

      <section className="dashboard-panels">
        <motion.article
          className="panel large"
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45, delay: 0.05 }}
        >
          <div className="org-details-header">
            <h3 className="org-section-title">Organisation Details</h3>

            {organizationOptions.length > 1 && (
              <div className="org-dropdown-wrap">
                <Dropdown
                  label="Select Organisation"
                  name="selectedOrgId"
                  value={selectedOrgId}
                  onChange={(e) => setSelectedOrgId(e.target.value)}
                  options={organizationOptions}
                />
              </div>
            )}
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
            <Button onClick={fetchOrganizations}>
              <RefreshCcw size={18} />
              Refresh Data
            </Button>
          </div>
        </motion.article>
      </section>
    </div>
  );
}