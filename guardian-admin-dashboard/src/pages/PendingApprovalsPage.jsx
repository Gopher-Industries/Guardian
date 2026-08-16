import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { Check, X, Clock, UserCheck } from "lucide-react";
import Loader from "../components/common/Loader";
import ConfirmationModal from "../components/common/ConfirmationModal";
import Modal from "../components/common/Modal";

// TEMP MOCK — Swap for real API call once backend is ready
const MOCK_PENDING_USERS = [
  {
    _id: "6a3f79a95974c96e54527287",
    fullname: "Joe Doe",
    email: "joe.doe@guardianmonitor.com",
    role: { name: "caretaker" },
    approvalStatus: "pending",
    created_at: "2026-06-27T07:20:09.968Z",
  },
  {
    _id: "6a3f79a95974c96e54527288",
    fullname: "Amara Singh",
    email: "amara.singh@guardianmonitor.com",
    role: { name: "doctor" },
    approvalStatus: "pending",
    created_at: "2026-07-01T09:12:00.000Z",
  },
  {
    _id: "6a3f79a95974c96e54527289",
    fullname: "Leo Martins",
    email: "leo.martins@guardianmonitor.com",
    role: { name: "nurse" },
    approvalStatus: "pending",
    created_at: "2026-07-15T14:45:00.000Z",
  },
];

export default function PendingApprovalsPage() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);

  const [confirmModal, setConfirmModal] = useState({
    isOpen: false,
    request: null,
  });
  const [rejectModal, setRejectModal] = useState({
    isOpen: false,
    request: null,
  });
  const [rejectionReason, setRejectionReason] = useState("");

  useEffect(() => {
    // TEMP: mock fetch. Replace with API once ready
    const timer = setTimeout(() => {
      setRequests(MOCK_PENDING_USERS);
      setLoading(false);
    }, 400);
    return () => clearTimeout(timer);
  }, []);

  const handleApprove = (request) => {
    setConfirmModal({ isOpen: true, request });
  };

  const confirmApprove = () => {
    const { request } = confirmModal;
    // TEMP: replace with real call, e.g. approveUser(request._id)
    setRequests((prev) => prev.filter((r) => r._id !== request._id));
    setConfirmModal({ isOpen: false, request: null });
  };

  const handleRejectOpen = (request) => {
    setRejectionReason("");
    setRejectModal({ isOpen: true, request });
  };

  const confirmReject = () => {
    const { request } = rejectModal;
    // TEMP: replace with real call
    setRequests((prev) => prev.filter((r) => r._id !== request._id));
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
                  <td>{req.fullname}</td>
                  <td>{req.email}</td>
                  <td>
                    <span className="type-tag color-info">
                      {req.role?.name}
                    </span>
                  </td>
                  <td>
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "16px",
                        color: "var(--text-muted)",
                        fontSize: "0.85rem",
                      }}
                    >
                      <Clock size={14} />
                      {new Date(req.created_at).toLocaleDateString()}
                    </div>
                  </td>
                  <td>
                    <div style={{ display: "flex", gap: "18px" }}>
                      <button
                        className="ui-button primary"
                        onClick={() => handleApprove(req)}
                      >
                        <Check size={16} />
                        Approve
                      </button>
                      <button
                        className="ui-button danger-btn"
                        onClick={() => handleRejectOpen(req)}
                      >
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
        message={`Approve ${confirmModal.request?.fullname}'s account as ${confirmModal.request?.role?.name}?`}
        confirmText="Approve"
        type="success"
      />

      <Modal
        open={rejectModal.isOpen}
        onClose={() => setRejectModal({ isOpen: false, request: null })}
        title="Reject Account Request"
      >
        <p className="modal-subtitle" style={{ marginBottom: "12px" }}>
          Please provide a reason for rejecting {rejectModal.request?.fullname}
          's request.
        </p>
        <textarea
          className="ui-textarea"
          rows={4}
          value={rejectionReason}
          onChange={(e) => setRejectionReason(e.target.value)}
          placeholder="e.g. Unable to verify employment details"
          style={{ width: "100%" }}
        />

        <div
          style={{
            display: "flex",
            justifyContent: "flex-end",
            gap: "8px",
            marginTop: "20px",
          }}
        >
          <button
            type="button"
            className="ui-button secondary"
            onClick={() => setRejectModal({ isOpen: false, request: null })}
          >
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
