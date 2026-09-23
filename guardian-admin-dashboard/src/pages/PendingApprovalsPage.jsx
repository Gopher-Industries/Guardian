import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { Check, X, Clock, UserCheck } from "lucide-react";
import Loader from "../components/common/Loader";
import ConfirmationModal from "../components/common/ConfirmationModal";
import Modal from "../components/common/Modal";
import { getPendingStaff, approveStaff, rejectStaff } from "../services/staffService";

export default function PendingApprovalsPage() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [confirmModal, setConfirmModal] = useState({ isOpen: false, request: null });
  const [rejectModal, setRejectModal] = useState({ isOpen: false, request: null });
  const [rejectionReason, setRejectionReason] = useState("");

  const fetchPending = async () => {
  setLoading(true);
  setError(null);
  try {
    const data = await getPendingStaff();
    console.log("PENDING STAFF RAW RESPONSE:", data); // TEMP — remove after checking
    setRequests(Array.isArray(data) ? data : data?.staff || data?.data || []);
  } catch (err) {
    console.error("Failed to fetch pending staff", err);
    setError("Couldn't load pending requests. Please try again.");
  } finally {
    setLoading(false);
  }
};

  useEffect(() => {
    fetchPending();
  }, []);

  const handleApprove = (request) => {
    setConfirmModal({ isOpen: true, request });
  };

  const confirmApprove = async () => {
    const { request } = confirmModal;
    try {
      await approveStaff(request._id);
      setRequests((prev) => prev.filter((r) => r._id !== request._id));
    } catch (err) {
      console.error("Failed to approve staff", err);
      // TODO: surface a toast error here
    }
    setConfirmModal({ isOpen: false, request: null });
  };

  const handleRejectOpen = (request) => {
    setRejectionReason("");
    setRejectModal({ isOpen: true, request });
  };

  const confirmReject = async () => {
    const { request } = rejectModal;
    try {
      await rejectStaff(request._id, rejectionReason);
      setRequests((prev) => prev.filter((r) => r._id !== request._id));
    } catch (err) {
      console.error("Failed to reject staff", err);
      // TODO: surface a toast error here
    }
    setRejectModal({ isOpen: false, request: null });
    setRejectionReason("");
  };

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
            <h1>Pending Account Requests</h1>
            <p className="section-subtitle">
              Review and action new account registrations awaiting approval.
            </p>
          </div>
        </div>
      </motion.section>

      <motion.section
        className="panel large"
        initial={{ opacity: 0, y: 18 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.45, delay: 0.05 }}
      >
        {loading ? (
          <Loader />
        ) : error ? (
          <div className="empty-state">
            <p>{error}</p>
            <button className="ui-button secondary" onClick={fetchPending}>
              Retry
            </button>
          </div>
        ) : requests.length === 0 ? (
          <div className="empty-state">
            <UserCheck size={40} />
            <p>No pending requests right now.</p>
          </div>
        ) : (
          <table className="ui-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Requested Role</th>
                <th>Submitted</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {requests.map((req) => (
                <tr key={req._id}>
                  <td>{req.name}</td>
                  <td>{req.email}</td>
                  <td>
                    <span className="type-tag color-info">{req.role?.name || req.role}</span>
                  </td>
                  <td>
                    <div style={{ display: "flex", alignItems: "center", gap: "6px", color: "var(--text-muted)", fontSize: "0.85rem" }}>
                      <Clock size={14} />
                      {new Date(req.created_at).toLocaleDateString()}
                    </div>
                  </td>
                  <td>
                    <div style={{ display: "flex", gap: "8px" }}>
                      <button type="button" className="ui-button primary" onClick={() => handleApprove(req)}>
                        <Check size={16} />
                        Approve
                      </button>
                      <button type="button" className="ui-button danger-btn" onClick={() => handleRejectOpen(req)}>
                        <X size={16} />
                        Reject
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </motion.section>

      <ConfirmationModal
        isOpen={confirmModal.isOpen}
        onClose={() => setConfirmModal({ isOpen: false, request: null })}
        onConfirm={confirmApprove}
        title="Approve Account Request"
        message={`Approve ${confirmModal.request?.name}'s account as ${confirmModal.request?.role?.name || confirmModal.request?.role}?`}
        confirmText="Approve"
        type="success"
      />

      <Modal
        open={rejectModal.isOpen}
        onClose={() => setRejectModal({ isOpen: false, request: null })}
        title="Reject Account Request"
      >
        <p className="modal-subtitle" style={{ marginBottom: "12px" }}>
          Please provide a reason for rejecting {rejectModal.request?.name}'s request.
        </p>
        <textarea
          className="ui-textarea"
          rows={4}
          value={rejectionReason}
          onChange={(e) => setRejectionReason(e.target.value)}
          placeholder="e.g. Unable to verify employment details"
          style={{ width: "100%" }}
        />
        <div style={{ display: "flex", justifyContent: "flex-end", gap: "8px", marginTop: "20px" }}>
          <button type="button" className="ui-button secondary" onClick={() => setRejectModal({ isOpen: false, request: null })}>
            Cancel
          </button>
          <button
            type="button"
            className="ui-button danger-btn"
            onClick={confirmReject}
            disabled={!rejectionReason.trim()}
          >
            <X size={16} />
            Reject Request
          </button>
        </div>
      </Modal>
    </div>
  );
}