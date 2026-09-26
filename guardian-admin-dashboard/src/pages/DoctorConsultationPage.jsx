import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import {
  ArrowLeft,
  Activity,
  Heart,
  Thermometer,
  Wind,
  Plus,
  Trash2,
} from "lucide-react";
import { getAdminUser } from "../utils/storage";
import { getAllPatients } from "../services/patientService";
import "./DoctorConsultationPage.css";

const NOTE_TYPES = [
  "General Consultation",
  "Diagnosis",
  "Prescription",
  "Follow-up",
];

const NOTE_TYPE_META = {
  "General Consultation": {
    tag: "General",
    color: "var(--primary)",
  },
  Diagnosis: {
    tag: "Diagnosis",
    color: "var(--warning)",
  },
  Prescription: {
    tag: "Prescription",
    color: "#a855f7",
  },
  "Follow-up": {
    tag: "Follow-up",
    color: "var(--primary)",
  },
  Urgent: {
    tag: "Urgent",
    color: "var(--danger)",
  },
};

const HISTORY_FILTERS = [
  "All",
  "General Consultation",
  "Diagnosis",
  "Prescription",
  "Follow-up",
  "Last 30 days",
];

const emptyForm = {
  noteType: "General Consultation",
  dateTime: "",
  bloodPressure: "",
  heartRate: "",
  temperature: "",
  respRate: "",
  notes: "",
};

function getPatientName(patient) {
  return patient?.fullname || patient?.name || "Unknown Patient";
}

function getInitials(name) {
  return (
    (name || "")
      .split(" ")
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0]?.toUpperCase())
      .join("") || "?"
  );
}

function getAge(patient) {
  if (patient?.age != null) return patient.age;

  if (!patient?.dateOfBirth) return null;

  const dob = new Date(patient.dateOfBirth);

  if (Number.isNaN(dob.getTime())) return null;

  const diff = Date.now() - dob.getTime();

  return Math.floor(
    diff / (365.25 * 24 * 60 * 60 * 1000)
  );
}

function getNurseName(patient) {
  const nurse = patient?.assignedNurses?.[0];

  return (
    nurse?.fullname ||
    nurse?.name ||
    "Unassigned"
  );
}

function formatDateTime(value) {
  const date = value ? new Date(value) : null;

  if (!date || Number.isNaN(date.getTime())) return "";

  return date.toLocaleString([], {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function isWithinLast30Days(value) {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return false;

  return (
    Date.now() - date.getTime() <=
    30 * 24 * 60 * 60 * 1000
  );
}

function isValidBloodPressure(value) {
  return /^\d{2,3}\/\d{2,3}$/.test(
    value.trim()
  );
}

function isValidVitalNumber(value) {
  return /^\d+(\.\d+)?$/.test(
    value.trim()
  );
}

function getNotesStorageKey(patientId) {
  return `guardian_consultation_notes_${patientId}`;
}

function loadStoredNotes(patientId) {
  try {
    const raw = localStorage.getItem(
      getNotesStorageKey(patientId)
    );

    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

export default function DoctorConsultationPage() {
  const { patientId } = useParams();

  const location = useLocation();

  const navigate = useNavigate();

  const [fetchedPatient, setFetchedPatient] =
    useState(null);

  const patient =
    location.state?.patient ||
    fetchedPatient || {
      _id: patientId,
      fullname: "Selected Patient",
    };

  const admin = getAdminUser() || {};

  const [activeTab, setActiveTab] =
    useState("note");

  const [notes, setNotes] = useState(() =>
    loadStoredNotes(patientId)
  );

  const [historyFilter, setHistoryFilter] =
    useState("All");

  const [expandedNoteId, setExpandedNoteId] =
    useState(null);

  const [form, setForm] =
    useState(emptyForm);

  const [formError, setFormError] =
    useState("");

  const [fieldErrors, setFieldErrors] =
    useState({});

  useEffect(() => {
    if (location.state?.patient) return;

    let cancelled = false;

    getAllPatients()
      .then((data) => {
        if (cancelled) return;

        const list = Array.isArray(data)
          ? data
          : data?.patients || [];

        const match = list.find(
          (item) => item._id === patientId
        );

        if (match) {
          setFetchedPatient(match);
        }
      })
      .catch(() => {});

    return () => {
      cancelled = true;
    };
  }, [patientId, location.state]);

  useEffect(() => {
    localStorage.setItem(
      getNotesStorageKey(patientId),
      JSON.stringify(notes)
    );
  }, [patientId, notes]);

  const patientName =
    getPatientName(patient);

  const age = getAge(patient);

  const nurseName =
    getNurseName(patient);

  const orgName =
    patient?.organization?.name ||
    "Guardian Monitor";

  const lastVitalsNote = useMemo(
    () =>
      notes.find(
        (note) =>
          note.bloodPressure ||
          note.heartRate ||
          note.temperature ||
          note.respRate
      ),
    [notes]
  );

  const filteredNotes = useMemo(() => {
    if (historyFilter === "All") {
      return notes;
    }

    if (historyFilter === "Last 30 days") {
      return notes.filter((note) =>
        isWithinLast30Days(note.dateTime)
      );
    }

    return notes.filter(
      (note) =>
        note.noteType === historyFilter
    );
  }, [notes, historyFilter]);

  const summary = useMemo(
    () => ({
      total: notes.length,

      last30: notes.filter((note) =>
        isWithinLast30Days(note.dateTime)
      ).length,

      urgent: notes.filter(
        (note) => note.urgent
      ).length,
    }),
    [notes]
  );

  function handleFormChange(
    field,
    value
  ) {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));

    setFieldErrors((prev) =>
      prev[field]
        ? {
            ...prev,
            [field]: "",
          }
        : prev
    );
  }

  function handleCancel() {
    setForm(emptyForm);

    setFormError("");

    setFieldErrors({});
  }

  function handleSaveNote(event) {
    event.preventDefault();

    if (!form.notes.trim()) {
      setFormError(
        "Please enter consultation notes before saving."
      );

      return;
    }

    const errors = {};

    if (
      form.bloodPressure.trim() &&
      !isValidBloodPressure(
        form.bloodPressure
      )
    ) {
      errors.bloodPressure =
        "Enter as systolic/diastolic, e.g. 120/80.";
    }

    if (
      form.heartRate.trim() &&
      !isValidVitalNumber(
        form.heartRate
      )
    ) {
      errors.heartRate =
        "Enter a number only, e.g. 76.";
    }

    if (
      form.temperature.trim() &&
      !isValidVitalNumber(
        form.temperature
      )
    ) {
      errors.temperature =
        "Enter a number only, e.g. 36.7.";
    }

    if (
      Object.keys(errors).length > 0
    ) {
      setFieldErrors(errors);

      setFormError(
        "Please fix the highlighted vitals before saving."
      );

      return;
    }

    const newNote = {
      id: `${Date.now()}`,

      noteType: form.noteType,

      dateTime:
        form.dateTime ||
        new Date().toISOString(),

      recordedBy:
        admin.fullname ||
        "Dr. " +
          (admin.name ||
            "Attending Doctor"),

      bloodPressure:
        form.bloodPressure.trim(),

      heartRate: form.heartRate.trim()
        ? `${form.heartRate.trim()} bpm`
        : "",

      temperature:
        form.temperature.trim()
          ? `${form.temperature.trim()}°C`
          : "",

      respRate:
        form.respRate.trim(),

      notes:
        form.notes.trim(),

      urgent:
        form.noteType === "Diagnosis" &&
        /urgent/i.test(form.notes),
    };

    setNotes((prev) => [
      newNote,
      ...prev,
    ]);

    setForm(emptyForm);

    setFormError("");

    setFieldErrors({});

    setActiveTab("history");
  }

  function handleDeleteNote(noteId) {
    const confirmed =
      window.confirm(
        "Are you sure you want to delete this consultation note? This cannot be undone."
      );

    if (!confirmed) return;

    setNotes((prev) =>
      prev.filter(
        (note) => note.id !== noteId
      )
    );

    setExpandedNoteId((prev) =>
      prev === noteId ? null : prev
    );
  }

  return (
    <section className="page-shell consultation-shell">

      <button
        className="back-link"
        type="button"
        onClick={() => navigate(-1)}
      >
        <ArrowLeft size={16} />

        Patient List
      </button>

      <div className="page-header">
        <div>
          <p className="eyebrow">
            Patient Management
          </p>

          <h1>
            Doctor Consultation
          </h1>

          <p className="page-subtitle">
            {patientName} &middot; capture and
            review consultation notes for this
            session.
          </p>
        </div>
      </div>

      <div className="patient-banner">

        <div className="patient-banner-info">

          <span className="patient-avatar">
            {getInitials(patientName)}
          </span>

          <div>
            <h3>
              {patientName}
            </h3>

            <p className="card-muted">
              {age != null
                ? `${age} yrs`
                : "Age N/A"}{" "}
              &middot;{" "}
              {patient?.gender || "N/A"}{" "}
              &middot; Nurse:{" "}
              {nurseName} &middot;{" "}
              {orgName}
            </p>
          </div>

        </div>

        <span className="status-pill">
          &bull; In Consultation
        </span>

      </div>

      <div className="consultation-tabs">

        <button
          type="button"
          className={`tab-button ${
            activeTab === "note"
              ? "active"
              : ""
          }`}
          onClick={() =>
            setActiveTab("note")
          }
        >
          <Plus size={16} />

          New Note
        </button>

        <button
          type="button"
          className={`tab-button ${
            activeTab === "history"
              ? "active"
              : ""
          }`}
          onClick={() =>
            setActiveTab("history")
          }
        >
          History Timeline
        </button>

      </div>

      {activeTab === "note" ? (

        <div className="consultation-grid">

          <div className="dashboard-card">

            <h2>
              Add Consultation Note
            </h2>

            <p className="card-muted">
              Record against{" "}
              {patientName}'s medical history
            </p>

            {formError ? (
              <div className="alert alert-error">
                {formError}
              </div>
            ) : null}

            <form
              onSubmit={handleSaveNote}
              className="consultation-form"
            >

              <label className="form-label">
                Note Type
              </label>

              <div className="pill-group">

                {NOTE_TYPES.map(
                  (type) => (
                    <button
                      key={type}
                      type="button"
                      className={`pill-button ${
                        form.noteType ===
                        type
                          ? "active"
                          : ""
                      }`}
                      onClick={() =>
                        handleFormChange(
                          "noteType",
                          type
                        )
                      }
                    >
                      {type}
                    </button>
                  )
                )}

              </div>

              <div className="form-row">

                <label className="form-label-block">

                  Date &amp; Time

                  <input
                    className="form-control"
                    type="datetime-local"
                    value={form.dateTime}
                    onChange={(e) =>
                      handleFormChange(
                        "dateTime",
                        e.target.value
                      )
                    }
                  />

                </label>

                <label className="form-label-block">

                  Recorded By

                  <input
                    className="form-control"
                    type="text"
                    value={
                      admin.fullname || ""
                    }
                    disabled
                    placeholder="Current doctor"
                  />

                </label>

              </div>

              <div className="form-row form-row-triple">

                <label className="form-label-block">

                  Blood Pressure

                  <input
                    className={`form-control ${
                      fieldErrors.bloodPressure
                        ? "input-error"
                        : ""
                    }`}
                    type="text"
                    placeholder="e.g. 120/80"
                    value={
                      form.bloodPressure
                    }
                    onChange={(e) =>
                      handleFormChange(
                        "bloodPressure",
                        e.target.value
                      )
                    }
                  />

                  {fieldErrors.bloodPressure ? (
                    <span className="field-error">
                      {
                        fieldErrors.bloodPressure
                      }
                    </span>
                  ) : null}

                </label>

                <label className="form-label-block">

                  Heart Rate (bpm)

                  <input
                    className={`form-control ${
                      fieldErrors.heartRate
                        ? "input-error"
                        : ""
                    }`}
                    type="text"
                    inputMode="numeric"
                    placeholder="e.g. 76"
                    value={
                      form.heartRate
                    }
                    onChange={(e) =>
                      handleFormChange(
                        "heartRate",
                        e.target.value
                      )
                    }
                  />

                  {fieldErrors.heartRate ? (
                    <span className="field-error">
                      {
                        fieldErrors.heartRate
                      }
                    </span>
                  ) : null}

                </label>

                <label className="form-label-block">

                  Temperature (°C)

                  <input
                    className={`form-control ${
                      fieldErrors.temperature
                        ? "input-error"
                        : ""
                    }`}
                    type="text"
                    inputMode="decimal"
                    placeholder="e.g. 36.7"
                    value={
                      form.temperature
                    }
                    onChange={(e) =>
                      handleFormChange(
                        "temperature",
                        e.target.value
                      )
                    }
                  />

                  {fieldErrors.temperature ? (
                    <span className="field-error">
                      {
                        fieldErrors.temperature
                      }
                    </span>
                  ) : null}

                </label>

              </div>

              <label className="form-label-block">

                Notes

                <textarea
                  className="ui-textarea"
                  rows={5}
                  placeholder="Enter consultation notes..."
                  value={form.notes}
                  onChange={(e) =>
                    handleFormChange(
                      "notes",
                      e.target.value
                    )
                  }
                />

              </label>

              <div className="form-actions">

                <button
                  type="button"
                  className="secondary-button"
                  onClick={handleCancel}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="primary-button"
                >
                  Save Note
                </button>

              </div>

            </form>

          </div>

          <div className="consultation-side">

            <div className="dashboard-card">

              <h2>
                Last Recorded Vitals
              </h2>

              {lastVitalsNote ? (
                <>
                  <p className="card-muted">
                    Captured{" "}
                    {formatDateTime(
                      lastVitalsNote.dateTime
                    )}{" "}
                    by{" "}
                    {
                      lastVitalsNote.recordedBy
                    }
                  </p>

                  <div className="vitals-grid">

                    <VitalTile
                      icon={Activity}
                      label="Blood Pressure"
                      value={
                        lastVitalsNote.bloodPressure
                      }
                    />

                    <VitalTile
                      icon={Heart}
                      label="Heart Rate"
                      value={
                        lastVitalsNote.heartRate
                      }
                    />

                    <VitalTile
                      icon={Thermometer}
                      label="Temperature"
                      value={
                        lastVitalsNote.temperature
                      }
                    />

                    <VitalTile
                      icon={Wind}
                      label="Resp. Rate"
                      value={
                        lastVitalsNote.respRate
                      }
                    />

                  </div>
                </>
              ) : (
                <div className="empty-state">
                  No vitals recorded yet.
                </div>
              )}

            </div>

            <div className="dashboard-card">

              <div className="section-header">

                <h2>
                  Recent History
                </h2>

                <button
                  type="button"
                  className="link-button"
                  onClick={() =>
                    setActiveTab("history")
                  }
                >
                  View all
                </button>

              </div>

              {notes.length === 0 ? (

                <div className="empty-state">
                  No recent notes.
                </div>

              ) : (

                <div className="recent-list">

                  {notes
                    .slice(0, 3)
                    .map((note) => (

                      <div
                        className="recent-item"
                        key={note.id}
                      >

                        <span
                          className="type-dot"
                          style={{
                            background:
                              NOTE_TYPE_META[
                                note.noteType
                              ]?.color ||
                              "var(--primary)",
                          }}
                        />

                        <div>

                          <strong>
                            {
                              NOTE_TYPE_META[
                                note.noteType
                              ]?.tag ||
                              note.noteType
                            }
                          </strong>

                          <p className="card-muted">
                            {note.notes.slice(
                              0,
                              60
                            )}

                            {note.notes.length >
                            60
                              ? "..."
                              : ""}
                          </p>

                        </div>

                      </div>

                    ))}

                </div>

              )}

            </div>

          </div>

        </div>

      ) : (

        <div className="consultation-grid">

          <div className="dashboard-card">

            <h2>
              Consultation History
            </h2>

            <p className="card-muted">
              All notes recorded for{" "}
              {patientName}
            </p>

            <div className="pill-group">

              {HISTORY_FILTERS.map(
                (filter) => (

                  <button
                    key={filter}
                    type="button"
                    className={`pill-button ${
                      historyFilter ===
                      filter
                        ? "active"
                        : ""
                    }`}
                    onClick={() =>
                      setHistoryFilter(
                        filter
                      )
                    }
                  >
                    {filter}
                  </button>

                )
              )}

            </div>

            {filteredNotes.length === 0 ? (

              <div className="empty-state">
                No consultation notes recorded yet.
              </div>

            ) : (

              <div className="history-list">

                {filteredNotes.map(
                  (note) => {

                    const meta =
                      NOTE_TYPE_META[
                        note.urgent
                          ? "Urgent"
                          : note.noteType
                      ] || {};

                    const expanded =
                      expandedNoteId ===
                      note.id;

                    const vitalsLine = [
                      note.bloodPressure &&
                        `BP ${note.bloodPressure}`,

                      note.heartRate &&
                        `HR ${note.heartRate}`,

                      note.temperature &&
                        `Temp ${note.temperature}`,
                    ]
                      .filter(Boolean)
                      .join(" · ");

                    return (
                      <div
                        className="history-item"
                        key={note.id}
                        style={{
                          borderLeftColor:
                            meta.color,
                        }}
                      >

                        <div className="history-item-header">

                          <span
                            className="type-tag"
                            style={{
                              background:
                                meta.color,
                            }}
                          >
                            {note.urgent
                              ? "Urgent"
                              : meta.tag}
                          </span>

                          <div className="history-item-header-right">

                            <span className="card-muted">
                              {formatDateTime(
                                note.dateTime
                              )}
                            </span>

                            <button
                              type="button"
                              className="icon-danger-button"
                              aria-label="Delete consultation note"
                              title="Delete consultation note"
                              onClick={() =>
                                handleDeleteNote(
                                  note.id
                                )
                              }
                            >
                              <Trash2 size={14} />
                            </button>

                          </div>

                        </div>

                        <p>
                          {expanded ||
                          note.notes.length <=
                            140
                            ? note.notes
                            : `${note.notes.slice(
                                0,
                                140
                              )}...`}
                        </p>

                        {vitalsLine ? (
                          <p className="card-muted">
                            {vitalsLine}
                          </p>
                        ) : null}

                        <div className="history-item-footer">

                          <span className="card-muted">
                            {
                              note.recordedBy
                            }
                          </span>

                          <button
                            type="button"
                            className="link-button"
                            onClick={() =>
                              setExpandedNoteId(
                                expanded
                                  ? null
                                  : note.id
                              )
                            }
                          >
                            {expanded
                              ? "Show less"
                              : "View full note >"}
                          </button>

                        </div>

                      </div>
                    );
                  }
                )}

              </div>

            )}

          </div>

          <div className="consultation-side">

            <div className="dashboard-card">

              <h2>
                Timeline Legend
              </h2>

              <p className="card-muted">
                Note type colour key
              </p>

              <ul className="legend-list">

                <li>
                  <span
                    className="type-dot"
                    style={{
                      background:
                        "var(--primary)",
                    }}
                  />
                  General / Follow-up
                </li>

                <li>
                  <span
                    className="type-dot"
                    style={{
                      background:
                        "var(--warning)",
                    }}
                  />
                  Diagnosis
                </li>

                <li>
                  <span
                    className="type-dot"
                    style={{
                      background:
                        "var(--danger)",
                    }}
                  />
                  Urgent
                </li>

                <li>
                  <span
                    className="type-dot"
                    style={{
                      background:
                        "#a855f7",
                    }}
                  />
                  Prescription
                </li>

              </ul>

            </div>

            <div className="dashboard-card">

              <h2>
                History Summary
              </h2>

              <p className="card-muted">
                Since{" "}
                {patientName.split(" ")[0]}{" "}
                was admitted
              </p>

              <div className="summary-rows">

                <div className="summary-row">
                  <span>
                    Total entries
                  </span>

                  <strong>
                    {summary.total}
                  </strong>
                </div>

                <div className="summary-row">
                  <span>
                    Last 30 days
                  </span>

                  <strong>
                    {summary.last30}
                  </strong>
                </div>

                <div className="summary-row">
                  <span>
                    Urgent flags
                  </span>

                  <strong>
                    {summary.urgent}
                  </strong>
                </div>

              </div>

              <button
                type="button"
                className="primary-button full-width"
                onClick={() =>
                  setActiveTab("note")
                }
              >
                <Plus size={16} />

                Add Consultation Note
              </button>

            </div>

          </div>

        </div>

      )}

    </section>
  );
}

function VitalTile(props) {
  const Icon = props.icon;

  return (
    <div className="vital-tile">

      <Icon size={16} />

      <strong>
        {props.value || "N/A"}
      </strong>

      <span>
        {props.label}
      </span>

    </div>
  );
}