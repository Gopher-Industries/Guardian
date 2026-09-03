import { useState } from "react";
import { motion } from "framer-motion";
import {
  UserPlus,
  BadgeCheck,
  MessageSquare,
  Ticket,
  Phone,
  CalendarDays,
  ClipboardList,
  Users,
  X,
} from "lucide-react";
import "./RegistrationDashboard.css";

export default function RegistrationDashboard() {
  const [visitorModalOpen, setVisitorModalOpen] = useState(false);
  const [noteModalOpen, setNoteModalOpen] = useState(false);

  const [visitors, setVisitors] = useState([
    {
      id: 1,
      time: "09:15",
      name: "Sarah Connor",
      purpose: "Quest Diagnostics",
      status: "Signed In",
    },
    {
      id: 2,
      time: "10:30",
      name: "Mark Davis",
      purpose: "BioMed Repairs",
      status: "Signed In",
    },
    {
      id: 3,
      time: "11:00",
      name: "Elena Rostova",
      purpose: "Pfizer Sales Rep",
      status: "Pending",
    },
  ]);

  const [notes, setNotes] = useState([
    "Dr. Cole on emergency call from 11:00 AM – 12:30 PM",
    "Lab courier delayed by 45 minutes today",
  ]);

  const [visitorForm, setVisitorForm] = useState({
    name: "",
    purpose: "",
    time: "",
    status: "Pending",
  });

  const [noteText, setNoteText] = useState("");

  const handleVisitorChange = (event) => {
    const { name, value } = event.target;

    setVisitorForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleAddVisitor = (event) => {
    event.preventDefault();

    if (!visitorForm.name || !visitorForm.purpose) {
      return;
    }

    const newVisitor = {
      id: Date.now(),
      name: visitorForm.name,
      purpose: visitorForm.purpose,
      time: visitorForm.time || new Date().toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
      }),
      status: visitorForm.status,
    };

    setVisitors((previous) => [...previous, newVisitor]);

    setVisitorForm({
      name: "",
      purpose: "",
      time: "",
      status: "Pending",
    });

    setVisitorModalOpen(false);
  };

  const handleAddNote = (event) => {
    event.preventDefault();

    if (!noteText.trim()) {
      return;
    }

    setNotes((previous) => [...previous, noteText.trim()]);
    setNoteText("");
    setNoteModalOpen(false);
  };

  const toggleVisitorStatus = (id) => {
    setVisitors((previous) =>
      previous.map((visitor) =>
        visitor.id === id
          ? {
              ...visitor,
              status:
                visitor.status === "Signed In"
                  ? "Signed Out"
                  : "Signed In",
            }
          : visitor
      )
    );
  };

  return (
    <div className="registration-dashboard">
      <motion.section
        className="registration-header"
        initial={{ opacity: 0, y: 12 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <div>
          <p className="section-eyebrow">Guardian Monitor</p>
          <h1>Registration & Front Desk</h1>
          <p>
            Manage visitors, front-desk activity, quick contacts and daily
            clinic information.
          </p>
        </div>
      </motion.section>

      <section className="registration-section">
        <h2>Quick Tools & Launchers</h2>

        <div className="quick-tools-grid">
          <button className="quick-tool">
            <UserPlus size={20} />
            <span>New Patient Form</span>
          </button>

          <button className="quick-tool">
            <BadgeCheck size={20} />
            <span>Issue Visitor Pass</span>
          </button>

          <button className="quick-tool">
            <MessageSquare size={20} />
            <span>Internal Desk Chat</span>
          </button>

          <button className="quick-tool">
            <Ticket size={20} />
            <span>IT Ticket</span>
          </button>
        </div>
      </section>

      <div className="registration-main-grid">
        <div className="registration-left-column">
          <motion.section
            className="registration-card"
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
          >
            <div className="registration-card-header">
              <div>
                <p className="section-eyebrow">Front Desk</p>
                <h2>Daily Visitor & Vendor Log</h2>
              </div>

              <button
                className="primary-registration-button"
                onClick={() => setVisitorModalOpen(true)}
              >
                <UserPlus size={17} />
                Log New Visitor
              </button>
            </div>

            <div className="visitor-table-wrapper">
              <table className="visitor-table">
                <thead>
                  <tr>
                    <th>Time</th>
                    <th>Visitor Name</th>
                    <th>Company / Purpose</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>

                <tbody>
                  {visitors.map((visitor) => (
                    <tr key={visitor.id}>
                      <td>{visitor.time}</td>
                      <td>{visitor.name}</td>
                      <td>{visitor.purpose}</td>

                      <td>
                        <span
                          className={`visitor-status ${
                            visitor.status === "Signed In"
                              ? "signed-in"
                              : visitor.status === "Signed Out"
                              ? "signed-out"
                              : "pending"
                          }`}
                        >
                          {visitor.status}
                        </span>
                      </td>

                      <td>
                        <button
                          className="visitor-action"
                          onClick={() => toggleVisitorStatus(visitor.id)}
                        >
                          {visitor.status === "Signed In"
                            ? "Sign Out"
                            : "Sign In"}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </motion.section>

          <motion.section
            className="registration-card"
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
          >
            <div className="registration-card-header">
              <div>
                <p className="section-eyebrow">Clinic Updates</p>
                <h2>Announcements & Shift Notes</h2>
              </div>

              <button
                className="secondary-registration-button"
                onClick={() => setNoteModalOpen(true)}
              >
                <ClipboardList size={17} />
                Log Note
              </button>
            </div>

            <ul className="registration-notes-list">
              {notes.map((note, index) => (
                <li key={`${note}-${index}`}>{note}</li>
              ))}
            </ul>
          </motion.section>
        </div>

        <div className="registration-right-column">
          <motion.section
            className="registration-card"
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
          >
            <div className="registration-card-title">
              <Phone size={20} />
              <h2>Emergency & Quick Contacts</h2>
            </div>

            <div className="contact-list">
              <div>
                <strong>Triage Desk</strong>
                <span>Ext: 101</span>
              </div>

              <div>
                <strong>Lab & Diagnostics</strong>
                <span>Ext: 108</span>
              </div>

              <div>
                <strong>Pharmacy Desk</strong>
                <span>Ext: 112</span>
              </div>

              <div>
                <strong>IT Desk Support</strong>
                <span>Ext: 404</span>
              </div>

              <div>
                <strong>Security / Maintenance</strong>
                <span>Ext: 999</span>
              </div>
            </div>
          </motion.section>

          <motion.section
            className="registration-card"
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
          >
            <div className="registration-card-title">
              <CalendarDays size={20} />
              <h2>Doctor Directory & Shifts</h2>
            </div>

            <div className="doctor-shift-list">
              <div>
                <strong>Dr. E. Brown</strong>
                <span className="doctor-busy">Busy</span>
                <small>Room 4 · 8AM–4PM</small>
              </div>

              <div>
                <strong>Dr. J. Cole</strong>
                <span className="doctor-available">Available</span>
                <small>Room 2 · 9AM–5PM</small>
              </div>

              <div>
                <strong>Dr. S. Lee</strong>
                <span className="doctor-off">Off-duty</span>
              </div>
            </div>
          </motion.section>

          <motion.section
            className="registration-card"
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
          >
            <div className="registration-card-title">
              <Users size={20} />
              <h2>Visitor Summary</h2>
            </div>

            <div className="visitor-summary">
              <div>
                <strong>{visitors.length}</strong>
                <span>Total Visitors</span>
              </div>

              <div>
                <strong>
                  {
                    visitors.filter(
                      (visitor) => visitor.status === "Signed In"
                    ).length
                  }
                </strong>
                <span>Currently Inside</span>
              </div>
            </div>
          </motion.section>
        </div>
      </div>

      {visitorModalOpen && (
        <div className="registration-modal-overlay">
          <div className="registration-modal">
            <div className="registration-modal-header">
              <div>
                <p className="section-eyebrow">Visitor Management</p>
                <h2>Log New Visitor</h2>
              </div>

              <button
                className="modal-close-button"
                onClick={() => setVisitorModalOpen(false)}
              >
                <X size={20} />
              </button>
            </div>

            <form
              className="registration-modal-form"
              onSubmit={handleAddVisitor}
            >
              <label>
                Visitor Name
                <input
                  type="text"
                  name="name"
                  value={visitorForm.name}
                  onChange={handleVisitorChange}
                  placeholder="Enter visitor name"
                  required
                />
              </label>

              <label>
                Company / Purpose
                <input
                  type="text"
                  name="purpose"
                  value={visitorForm.purpose}
                  onChange={handleVisitorChange}
                  placeholder="Enter company or purpose"
                  required
                />
              </label>

              <label>
                Time
                <input
                  type="time"
                  name="time"
                  value={visitorForm.time}
                  onChange={handleVisitorChange}
                />
              </label>

              <label>
                Status
                <select
                  name="status"
                  value={visitorForm.status}
                  onChange={handleVisitorChange}
                >
                  <option value="Pending">Pending</option>
                  <option value="Signed In">Signed In</option>
                </select>
              </label>

              <div className="registration-modal-actions">
                <button
                  type="button"
                  className="modal-cancel-button"
                  onClick={() => setVisitorModalOpen(false)}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="primary-registration-button"
                >
                  Add Visitor
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {noteModalOpen && (
        <div className="registration-modal-overlay">
          <div className="registration-modal">
            <div className="registration-modal-header">
              <div>
                <p className="section-eyebrow">Clinic Notes</p>
                <h2>Log New Note</h2>
              </div>

              <button
                className="modal-close-button"
                onClick={() => setNoteModalOpen(false)}
              >
                <X size={20} />
              </button>
            </div>

            <form
              className="registration-modal-form"
              onSubmit={handleAddNote}
            >
              <label>
                Note
                <textarea
                  value={noteText}
                  onChange={(event) => setNoteText(event.target.value)}
                  placeholder="Enter clinic announcement or shift note"
                  rows={5}
                  required
                />
              </label>

              <div className="registration-modal-actions">
                <button
                  type="button"
                  className="modal-cancel-button"
                  onClick={() => setNoteModalOpen(false)}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="primary-registration-button"
                >
                  Add Note
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}