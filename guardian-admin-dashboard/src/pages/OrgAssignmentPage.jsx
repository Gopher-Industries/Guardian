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
import OrgDetailPage from './OrgDetailPage';
import { getOrganizations, createOrganization } from "../services/orgService";

const initialFormData = {
  name: '',
  description: '',
  active: 'true',
};

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
  const [selectedViewOrg, setSelectedViewOrg] = useState(null);

  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState(initialFormData);
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const fetchOrganizations = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const response = await getOrganizations();
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

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const resetForm = () => {
    setFormData(initialFormData);
  };

  const handleToggleForm = () => {
    setShowForm((prev) => !prev);
    setFormError('');
    setSuccessMessage('');
    if (showForm) resetForm();
  };

  const handleAddOrganization = async (e) => {
    e.preventDefault();

    if (!formData.name || !formData.description) {
      setFormError('Name and description are required.');
      setSuccessMessage('');
      return;
    }

    try {
      setSubmitting(true);
      setFormError('');
      setSuccessMessage('');

      const payload = {
        name: formData.name,
        description: formData.description,
        active: formData.active === 'true',
      };

      const response = await createOrganization(payload);

      setSuccessMessage('Organisation created successfully.');
      resetForm();
      setShowForm(false);
      await fetchOrganizations();
    } catch (err) {
      console.error('Add organization error:', err);
      setFormError(
        err?.response?.data?.message || err?.message || 'Failed to add organisation.'
      );
      setSuccessMessage('');
    } finally {
      setSubmitting(false);
    }
  };

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

  if (selectedViewOrg) {
    return (
      <OrgDetailPage
        org={selectedViewOrg}
        onBack={() => setSelectedViewOrg(null)}
      />
    );
  }

  return (
    <div className="dashboard-home">
      <div style={{ width: '100%' }}>
        <div
          style={{
            background: '#ffffff',
            border: '1px solid #d9e2ec',
            borderRadius: '24px',
            padding: '24px',
            boxShadow: '0 4px 14px rgba(15, 23, 42, 0.04)',
          }}
        >
          {/* Header */}
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'flex-start',
              gap: '16px',
              flexWrap: 'wrap',
              marginBottom: '20px',
            }}
          >
            <div>
              <h2 style={{ margin: 0, color: '#123b7a' }}>Organizations</h2>
              <p style={{ marginTop: '6px', color: '#5b6b7f' }}>
                Manage organizations linked to this admin account. Add new organizations or view existing ones.
              </p>
            </div>

            <button
              onClick={handleToggleForm}
              style={{
                border: 'none',
                borderRadius: '12px',
                padding: '10px 16px',
                fontWeight: 600,
                cursor: 'pointer',
                background: '#4ea3d8',
                color: '#ffffff',
              }}
            >
              {showForm ? 'Close Form' : 'Add Organization'}
            </button>
          </div>

          {/* Success Message */}
          {successMessage && (
            <div
              style={{
                marginBottom: '16px',
                padding: '12px 14px',
                borderRadius: '12px',
                background: '#e8f8ee',
                color: '#137333',
                fontWeight: 500,
              }}
            >
              {successMessage}
            </div>
          )}

          {/* Form */}
          {showForm && (
            <form
              onSubmit={handleAddOrganization}
              style={{
                marginBottom: '24px',
                padding: '20px',
                border: '1px solid #d9e2ec',
                borderRadius: '18px',
                background: '#f8fbff',
              }}
            >
              {formError && (
                <div
                  style={{
                    marginBottom: '16px',
                    padding: '12px 14px',
                    borderRadius: '12px',
                    background: '#fdecec',
                    color: '#b42318',
                    fontWeight: 500,
                  }}
                >
                  {formError}
                </div>
              )}

              <div
                style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
                  gap: '16px',
                }}
              >
                {/* Name */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  <label style={{ fontWeight: 600, color: '#2c3e50' }}>Name</label>
                  <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    placeholder="Enter organisation name"
                    style={{
                      height: '44px',
                      padding: '0 14px',
                      border: '1px solid #cfd8e3',
                      borderRadius: '12px',
                      outline: 'none',
                    }}
                  />
                </div>

                {/* Description */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  <label style={{ fontWeight: 600, color: '#2c3e50' }}>Description</label>
                  <input
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                    placeholder="Enter organisation description"
                    style={{
                      height: '44px',
                      padding: '0 14px',
                      border: '1px solid #cfd8e3',
                      borderRadius: '12px',
                      outline: 'none',
                    }}
                  />
                </div>

                {/* Status Radio */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  <label style={{ fontWeight: 600, color: '#2c3e50' }}>Status</label>
                  <div style={{ display: 'flex', gap: '24px', alignItems: 'center', height: '44px' }}>
                    <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', color: '#2c3e50' }}>
                      <input
                        type="radio"
                        name="active"
                        value="true"
                        checked={formData.active === 'true'}
                        onChange={handleInputChange}
                        style={{ accentColor: '#4ea3d8', width: '16px', height: '16px' }}
                      />
                      Active
                    </label>
                    <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', color: '#2c3e50' }}>
                      <input
                        type="radio"
                        name="active"
                        value="false"
                        checked={formData.active === 'false'}
                        onChange={handleInputChange}
                        style={{ accentColor: '#4ea3d8', width: '16px', height: '16px' }}
                      />
                      Inactive
                    </label>
                  </div>
                </div>
              </div>

              {/* Form Buttons */}
              <div
                style={{
                  display: 'flex',
                  justifyContent: 'flex-end',
                  gap: '12px',
                  marginTop: '20px',
                  flexWrap: 'wrap',
                }}
              >
                <button
                  type="button"
                  onClick={() => {
                    setShowForm(false);
                    resetForm();
                    setFormError('');
                    setSuccessMessage('');
                  }}
                  style={{
                    border: 'none',
                    borderRadius: '12px',
                    padding: '10px 16px',
                    fontWeight: 600,
                    cursor: 'pointer',
                    background: '#e9f1f8',
                    color: '#1b3a57',
                  }}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  disabled={submitting}
                  style={{
                    border: 'none',
                    borderRadius: '12px',
                    padding: '10px 16px',
                    fontWeight: 600,
                    cursor: submitting ? 'not-allowed' : 'pointer',
                    background: '#4ea3d8',
                    color: '#ffffff',
                    opacity: submitting ? 0.7 : 1,
                  }}
                >
                  {submitting ? 'Saving...' : 'Save Organisation'}
                </button>
              </div>
            </form>
          )}

          {/* Table */}
          {organizations.length === 0 ? (
            <div
              style={{
                padding: '24px',
                textAlign: 'center',
                color: '#5b6b7f',
                border: '1px dashed #cfd8e3',
                borderRadius: '16px',
              }}
            >
              No organizations found.
            </div>
          ) : (
            <>
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                  <thead>
                    <tr>
                      {['Name', 'Description', 'Active', 'Actions'].map((col) => (
                        <th
                          key={col}
                          style={{
                            textAlign: 'left',
                            padding: '14px 12px',
                            borderBottom: '1px solid #edf2f7',
                            color: '#123b7a',
                            fontSize: '14px',
                          }}
                        >
                          {col}
                        </th>
                      ))}
                    </tr>
                  </thead>

                  <tbody>
                    {organizations.map((org) => (
                      <tr key={org._id}>
                        <td style={{ padding: '14px 12px', borderBottom: '1px solid #edf2f7' }}>
                          {org.name || 'N/A'}
                        </td>
                        <td style={{ padding: '14px 12px', borderBottom: '1px solid #edf2f7' }}>
                          {org.description || 'N/A'}
                        </td>
                        <td style={{ padding: '14px 12px', borderBottom: '1px solid #edf2f7' }}>
                          <span
                            style={{
                              padding: '4px 10px',
                              borderRadius: '20px',
                              fontSize: '13px',
                              fontWeight: 600,
                              background: org.active ? '#e8f8ee' : '#f5f5f5',
                              color: org.active ? '#137333' : '#888',
                            }}
                          >
                            {org.active ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                        <td style={{ padding: '14px 12px', borderBottom: '1px solid #edf2f7' }}>
                          <button
                            onClick={() => setSelectedViewOrg(org)}
                            style={{
                              border: 'none',
                              borderRadius: '12px',
                              padding: '10px 14px',
                              fontWeight: 600,
                              cursor: 'pointer',
                              background: '#4ea3d8',
                              color: '#ffffff',
                            }}
                          >
                            View
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Pagination */}
              <div
                style={{
                  marginTop: '20px',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  gap: '12px',
                  flexWrap: 'wrap',
                }}
              >
                <div style={{ color: '#5b6b7f', fontSize: '14px' }}>
                  Total Organizations: {organizations.length} | Page 1 of 1
                </div>

                <div style={{ display: 'flex', gap: '10px' }}>
                  <button
                    style={{
                      border: 'none',
                      borderRadius: '12px',
                      padding: '10px 14px',
                      fontWeight: 600,
                      cursor: 'not-allowed',
                      background: '#e9f1f8',
                      color: '#1b3a57',
                      opacity: 0.6,
                    }}
                  >
                    Previous
                  </button>

                  <button
                    style={{
                      border: 'none',
                      borderRadius: '12px',
                      padding: '10px 14px',
                      fontWeight: 600,
                      cursor: 'not-allowed',
                      background: '#4ea3d8',
                      color: '#ffffff',
                      opacity: 0.6,
                    }}
                  >
                    Next
                  </button>
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}