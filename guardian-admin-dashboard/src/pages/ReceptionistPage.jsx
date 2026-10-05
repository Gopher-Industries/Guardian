import { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import {
  Search,
  Filter,
  CalendarClock,
  CheckCircle2,
  Activity,
  Users,
  Phone,
  DoorOpen,
  EyeOff,
  RefreshCw,
} from "lucide-react";
import { getAppointments } from "../services/appointmentService";
import "./ReceptionistPage.css";

const STATUS_OPTIONS = [
  { value: "All", label: "All Status" },
  { value: "booked", label: "Scheduled" },
  { value: "in-progress", label: "In Progress" },
  { value: "completed", label: "Completed" },
];

const STATUS_LABELS = {
  booked: "Scheduled",
  "in-progress": "In Progress",
  completed: "Completed",
};

const STATUS_CLASSES = {
  booked: "scheduled",
  "in-progress": "checked-in",
  completed: "completed",
};

function formatDatePart(record) {
  if (record.appointmentDate) return record.appointmentDate;
  if (!record.appointmentDateTime) return "—";
  const date = new Date(record.appointmentDateTime);
  return Number.isNaN(date.getTime()) ? "—" : date.toISOString().slice(0, 10);
}

function formatTimePart(record) {
  if (record.appointmentTime) return record.appointmentTime;
  if (!record.appointmentDateTime) return "—";
  const date = new Date(record.appointmentDateTime);
  return Number.isNaN(date.getTime()) ? "—" : date.toISOString().slice(11, 16);
}

function mapAppointment(record) {
  return {
    id: record.id || record._id,
    patientName: record.patient?.fullname || "Unknown patient",
    patientId: record.patient?.uuid || record.patient?.id || "—",
    doctorName: record.doctor?.fullname || "Unassigned",
    clinic: record.clinic?.trim() || "Unspecified",
    date: formatDatePart(record),
    time: formatTimePart(record),
    room: record.room?.trim() || "—",
    status: record.status,
    contactPhone: record.patient?.emergencyContactNumber || "—",
  };
}

export default function ReceptionistPage() {
  const [searchParams] = useSearchParams();
  const [searchTerm, setSearchTerm] = useState(searchParams.get("q") || "");
  const [selectedStatus, setSelectedStatus] = useState("All");
  const [selectedClinic, setSelectedClinic] = useState("All");

  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    setSearchTerm(searchParams.get("q") || "");
  }, [searchParams]);

  const loadAppointments = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await getAppointments({ page: 1, limit: 100 });
      const records = Array.isArray(data?.data) ? data.data : [];

      setAppointments(records.map(mapAppointment));
    } catch (err) {
      console.error("Load appointments error:", err);
      setError(
        err?.response?.data?.error ||
          err?.response?.data?.message ||
          err?.message ||
          "Failed to load appointments."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAppointments();
  }, []);

  const clinics = useMemo(
    () => ["All", ...new Set(appointments.map((a) => a.clinic))],
    [appointments]
  );

  const filteredAppointments = useMemo(() => {
    return appointments.filter((apt) => {
      const matchesSearch =
        `${apt.patientName} ${apt.patientId} ${apt.doctorName} ${apt.id}`
          .toLowerCase()
          .includes(searchTerm.toLowerCase());

      const matchesStatus =
        selectedStatus === "All" || apt.status === selectedStatus;

      const matchesClinic =
        selectedClinic === "All" || apt.clinic === selectedClinic;

      return matchesSearch && matchesStatus && matchesClinic;
    });
  }, [appointments, searchTerm, selectedStatus, selectedClinic]);

  const scheduledCount = appointments.filter((a) => a.status === "booked").length;
  const inProgressCount = appointments.filter((a) => a.status === "in-progress").length;
  const completedCount = appointments.filter((a) => a.status === "completed").length;

  const getStatusClass = (status) =>
    `receptionist-status ${STATUS_CLASSES[status] || "scheduled"}`;

  const getStatusLabel = (status) => STATUS_LABELS[status] || status;

  return (
    <section className="receptionist-page">
      <div className="receptionist-header">
        <div>
          <p className="receptionist-eyebrow">Guardian Monitor Admin</p>
          <h1>Receptionist</h1>
          <p className="receptionist-subtitle">
            Front-desk view of the appointment list. Only administrative
            details are shown here — medical notes and clinical records stay
            restricted to care team pages.
          </p>
        </div>

        <div className="receptionist-privacy-badge">
          <EyeOff size={16} />
          Medical notes hidden
        </div>
      </div>

      <div className="receptionist-summary-grid">
        <div className="receptionist-summary-card">
          <Users size={18} />
          <div>
            <strong>{appointments.length}</strong>
            <span>Total Appointments</span>
          </div>
        </div>

        <div className="receptionist-summary-card">
          <CalendarClock size={18} />
          <div>
            <strong>{scheduledCount}</strong>
            <span>Scheduled</span>
          </div>
        </div>

        <div className="receptionist-summary-card">
          <Activity size={18} />
          <div>
            <strong>{inProgressCount}</strong>
            <span>In Progress</span>
          </div>
        </div>

        <div className="receptionist-summary-card">
          <CheckCircle2 size={18} />
          <div>
            <strong>{completedCount}</strong>
            <span>Completed</span>
          </div>
        </div>
      </div>

      <div className="receptionist-toolbar">
        <div className="receptionist-search-box">
          <Search size={16} />
          <input
            type="text"
            placeholder="Search by patient, doctor, or appointment ID..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        <div className="receptionist-filter-group">
          <div className="receptionist-filter-select">
            <Filter size={16} />
            <select
              value={selectedClinic}
              onChange={(e) => setSelectedClinic(e.target.value)}
            >
              {clinics.map((clinic) => (
                <option key={clinic} value={clinic}>
                  {clinic === "All" ? "All Clinics" : clinic}
                </option>
              ))}
            </select>
          </div>

          <div className="receptionist-filter-select">
            <Filter size={16} />
            <select
              value={selectedStatus}
              onChange={(e) => setSelectedStatus(e.target.value)}
            >
              {STATUS_OPTIONS.map((status) => (
                <option key={status.value} value={status.value}>
                  {status.label}
                </option>
              ))}
            </select>
          </div>

          <button
            type="button"
            className="receptionist-refresh-button"
            onClick={loadAppointments}
            disabled={loading}
            title="Refresh appointments"
          >
            <RefreshCw size={16} className={loading ? "spin" : ""} />
            Refresh
          </button>
        </div>
      </div>

      <div className="receptionist-card">
        <div className="receptionist-card-header">
          <h3>Appointment List</h3>
          <p>Administrative appointment details only — no diagnosis, symptoms, or clinical notes.</p>
        </div>

        <div className="receptionist-table-wrap">
          <table className="receptionist-table">
            <thead>
              <tr>
                <th>Patient</th>
                <th>Doctor</th>
                <th>Clinic</th>
                <th>Date &amp; Time</th>
                <th>Room</th>
                <th>Contact</th>
                <th>Status</th>
              </tr>
            </thead>

            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="7" className="receptionist-empty-state">
                    Loading appointments...
                  </td>
                </tr>
              ) : error ? (
                <tr>
                  <td colSpan="7" className="receptionist-empty-state">
                    {error}
                  </td>
                </tr>
              ) : filteredAppointments.length > 0 ? (
                filteredAppointments.map((apt) => (
                  <tr key={apt.id}>
                    <td>
                      <div className="receptionist-name-cell">
                        <div className="receptionist-avatar">
                          {apt.patientName.charAt(0)}
                        </div>
                        <div>
                          <strong>{apt.patientName}</strong>
                          <span>{apt.patientId}</span>
                        </div>
                      </div>
                    </td>

                    <td>{apt.doctorName}</td>
                    <td>{apt.clinic}</td>
                    <td>
                      <div className="receptionist-datetime-cell">
                        <span>{apt.date}</span>
                        <span>{apt.time}</span>
                      </div>
                    </td>
                    <td>
                      <span className="receptionist-room-cell">
                        <DoorOpen size={14} /> {apt.room}
                      </span>
                    </td>
                    <td>
                      <span className="receptionist-contact-cell">
                        <Phone size={14} /> {apt.contactPhone}
                      </span>
                    </td>
                    <td>
                      <span className={getStatusClass(apt.status)}>
                        {getStatusLabel(apt.status)}
                      </span>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="7" className="receptionist-empty-state">
                    No appointments found for the selected filters.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
}
