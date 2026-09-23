import { useEffect, useMemo, useState } from "react";
import {
  getAllPatients,
  getPatientOverview,
} from "../services/patientService";

export default function PatientOverviewPage() {
  const [patients, setPatients] = useState([]);
  const [selectedPatientId, setSelectedPatientId] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [overview, setOverview] = useState(null);
  const [loadingPatients, setLoadingPatients] = useState(true);
  const [loadingOverview, setLoadingOverview] = useState(false);
  const [error, setError] = useState("");

  /*
   * IMPORTANT:
   * This is the same patient-loading approach used
   * in the previous working version.
   */
  const loadPatients = async () => {
    try {
      setLoadingPatients(true);
      setError("");

      const data = await getAllPatients();

      const patientList = Array.isArray(data)
        ? data
        : data?.patients || [];

      setPatients(patientList);
    } catch (err) {
      console.error("Failed to load patients:", err);

      setError(
        err?.response?.data?.message ||
          "Failed to load patients"
      );
    } finally {
      setLoadingPatients(false);
    }
  };

  useEffect(() => {
    loadPatients();
  }, []);

  const filteredPatients = useMemo(() => {
    const term = searchTerm.trim().toLowerCase();

    if (!term) {
      return patients;
    }

    return patients.filter((patient) => {
      const fullname =
        patient?.fullname?.toLowerCase() || "";

      const id =
        patient?._id?.toLowerCase() || "";

      return (
        fullname.includes(term) ||
        id.includes(term)
      );
    });
  }, [patients, searchTerm]);

  const handleSelectPatient = async (patientId) => {
    try {
      setSelectedPatientId(patientId);
      setLoadingOverview(true);
      setError("");

      const selectedPatient = patients.find(
        (patient) => patient._id === patientId
      );

      const orgId = selectedPatient?.organization;

      const data = await getPatientOverview(
        patientId,
        orgId
      );

      setOverview(data);
    } catch (err) {
      console.error(
        "Failed to load patient overview:",
        err
      );

      setError(
        err?.response?.data?.message ||
          "Failed to load patient overview"
      );

      setOverview(null);
      setSelectedPatientId("");
    } finally {
      setLoadingOverview(false);
    }
  };

  const handleChangePatient = () => {
    setSelectedPatientId("");
    setOverview(null);
    setError("");
  };

  const handleRefresh = async () => {
    if (!selectedPatientId) {
      await loadPatients();
      return;
    }

    await handleSelectPatient(selectedPatientId);
  };

  const patient = overview?.patient || {};

  const records = overview?.records || [];
  const carePlans = overview?.carePlans || [];
  const tasks = overview?.tasks || [];
  const logs = overview?.logs || [];

  // Existing backend data if available.
  const notes = overview?.notes || [];
  const vitals = overview?.vitals || [];

  return (
    <div style={styles.page}>

      {/* PAGE HEADER */}
      <div style={styles.header}>
        <div>
          <p style={styles.eyebrow}>
            DOCTOR WORKSPACE
          </p>

          <h1 style={styles.title}>
            Medical Records
          </h1>

          <p style={styles.subtitle}>
            Review patient information, clinical records,
            care plans and vital signs.
          </p>
        </div>

        <button
          type="button"
          style={styles.refreshButton}
          onClick={handleRefresh}
          disabled={loadingPatients || loadingOverview}
        >
          {loadingOverview
            ? "Refreshing..."
            : "Refresh"}
        </button>
      </div>

      {/* ERROR MESSAGE */}
      {error && (
        <div style={styles.errorBox}>
          <strong>Unable to load information</strong>
          <span>{error}</span>
        </div>
      )}

      {/* PATIENT SELECTION */}
      {!selectedPatientId && (
        <div style={styles.searchCard}>

          <div style={styles.searchHeader}>
            <div>
              <h2 style={styles.cardTitle}>
                Select Patient
              </h2>

              <p style={styles.cardSubtitle}>
                Search for a patient to view their
                medical information.
              </p>
            </div>

            <div style={styles.patientCount}>
              {patients.length}
            </div>
          </div>

          <div style={styles.searchContainer}>
            <span style={styles.searchIcon}>
              ⌕
            </span>

            <input
              type="text"
              placeholder="Search by patient name or ID"
              value={searchTerm}
              onChange={(e) =>
                setSearchTerm(e.target.value)
              }
              style={styles.searchInput}
            />
          </div>

          {loadingPatients ? (
            <div style={styles.loadingCard}>
              <div style={styles.spinner} />
              <p>Loading patients...</p>
            </div>
          ) : error ? (
            <p style={styles.error}>
              {error}
            </p>
          ) : filteredPatients.length === 0 ? (
            <div style={styles.emptyBox}>
              <div style={styles.emptyIcon}>
                —
              </div>

              <strong>
                No patients found
              </strong>

              <p style={styles.emptyText}>
                No patients match your search.
              </p>
            </div>
          ) : (
            <div style={styles.patientList}>

              {filteredPatients.map((item) => (
                <div
                  key={item._id}
                  style={styles.patientRow}
                >
                  <div style={styles.patientDetails}>

                    <div style={styles.avatar}>
                      {(
                        item.fullname ||
                        "P"
                      )
                        .charAt(0)
                        .toUpperCase()}
                    </div>

                    <div>
                      <h3 style={styles.patientName}>
                        {item.fullname ||
                          "Unknown Patient"}
                      </h3>

                      <p style={styles.patientMeta}>
                        Patient ID:{" "}
                        {item._id || "-"}
                      </p>

                      <p style={styles.patientMeta}>
                        Gender:{" "}
                        {item.gender || "-"}
                        &nbsp; | &nbsp;
                        Age:{" "}
                        {item.age ?? "-"}
                      </p>
                    </div>
                  </div>

                  <button
                    type="button"
                    style={styles.primaryButton}
                    onClick={() =>
                      handleSelectPatient(
                        item._id
                      )
                    }
                  >
                    View Medical Records
                  </button>
                </div>
              ))}

            </div>
          )}
        </div>
      )}

      {/* LOADING OVERVIEW */}
      {loadingOverview && (
        <div style={styles.loadingCard}>
          <div style={styles.spinner} />

          <p>
            Loading patient medical records...
          </p>
        </div>
      )}

      {/* SELECTED PATIENT */}
      {overview && !loadingOverview && (
        <>
          <button
            type="button"
            style={styles.changeButton}
            onClick={handleChangePatient}
          >
            ← Change Patient
          </button>

          {/* PATIENT PROFILE */}
          <div style={styles.patientHeader}>

            <div style={styles.patientHeaderTop}>

              <div style={styles.profileLeft}>

                <div style={styles.largeAvatar}>
                  {(
                    patient.fullname ||
                    "P"
                  )
                    .charAt(0)
                    .toUpperCase()}
                </div>

                <div>
                  <p style={styles.eyebrow}>
                    PATIENT PROFILE
                  </p>

                  <h2
                    style={styles.patientHeaderName}
                  >
                    {patient.fullname ||
                      "Unknown Patient"}
                  </h2>

                  <p style={styles.patientId}>
                    Patient ID:{" "}
                    {patient._id || "-"}
                  </p>
                </div>

              </div>

              <div style={styles.statusBadge}>
                Medical Record
              </div>

            </div>

            <div style={styles.patientInfoGrid}>

              <InfoItem
                label="Gender"
                value={
                  patient.gender || "-"
                }
              />

              <InfoItem
                label="Date of Birth"
                value={
                  patient.dateOfBirth
                    ? new Date(
                        patient.dateOfBirth
                      ).toLocaleDateString()
                    : "-"
                }
              />

              <InfoItem
                label="Organization"
                value={
                  patient.organization?.name ||
                  "Guardian Monitor"
                }
              />

              <InfoItem
                label="Caretaker"
                value={
                  patient.caretaker?.fullname ||
                  "-"
                }
              />

              <InfoItem
                label="Assigned Doctor"
                value={
                  patient.assignedDoctor
                    ?.fullname || "-"
                }
              />

              <InfoItem
                label="Assigned Nurses"
                value={
                  patient.assignedNurses?.length
                    ? `${patient.assignedNurses.length} assigned`
                    : "No nurses assigned"
                }
              />

            </div>
          </div>

          {/* SUMMARY CARDS */}
          <div style={styles.statsGrid}>

            <StatCard
              title="Medical Records"
              value={records.length}
              icon="📁"
            />

            <StatCard
              title="Clinical Notes"
              value={notes.length}
              icon="📝"
            />

            <StatCard
              title="Care Plans"
              value={carePlans.length}
              icon="✓"
            />

            <StatCard
              title="Vitals"
              value={vitals.length}
              icon="♥"
            />

          </div>

          {/* MEDICAL RECORDS */}
          <MedicalSection
            title="Medical Records"
            description="Patient medical history, files and clinical records."
            items={records}
          />

          {/* CLINICAL NOTES */}
          <MedicalSection
            title="Clinical Notes"
            description="Consultation and clinical notes associated with the patient."
            items={notes}
          />

          {/* CARE PLANS */}
          <MedicalSection
            title="Care Plans"
            description="Current care plans and treatment information."
            items={carePlans}
          />

          {/* VITALS */}
          <VitalsSection
            items={vitals}
          />

          {/* TASKS */}
          <MedicalSection
            title="Tasks"
            description="Tasks associated with this patient's care."
            items={tasks}
          />

          {/* ACTIVITY LOGS */}
          <MedicalSection
            title="Activity Logs"
            description="Recent activity associated with the patient record."
            items={logs}
          />
        </>
      )}

    </div>
  );
}


/* =====================================================
   PATIENT INFORMATION COMPONENT
      </div>

    </div>
  );
}


/* =====================================================
   MEDICAL SECTION
===================================================== */

function MedicalSection({
  title,
  description,
  items,
}) {
  return (
    <section style={styles.section}>

      <div style={styles.sectionHeader}>

        <div>
          <h2 style={styles.sectionTitle}>
            {title}
          </h2>

          <p style={styles.sectionDescription}>
            {description}
          </p>
        </div>

        <span style={styles.sectionCount}>
          {items?.length || 0}
        </span>

      </div>

      {!items ||
      items.length === 0 ? (
        <div style={styles.emptyBox}>

          <div style={styles.emptyIcon}>
            +
          </div>

          <strong>
            No {title.toLowerCase()} available
          </strong>

          <p style={styles.emptyText}>
            Information will appear here
            when it becomes available.
          </p>

        </div>
      ) : (
        <div style={styles.recordsGrid}>

          {items.map(
            (item, index) => (
              <div
                key={
                  item?._id ||
                  item?.id ||
                  index
                }
                style={styles.recordCard}
              >

                <div style={styles.recordHeader}>

                  <div>
                    <strong>
                      {item?.title ||
                        item?.type ||
                        item?.recordType ||
                        title.slice(
                          0,
                          -1
                        )}
                    </strong>

                    {item?.status && (
                      <span
                        style={
                          styles.smallBadge
                        }
                      >
                        {item.status}
                      </span>
                    )}
                  </div>

                  <span
                    style={styles.recordDate}
                  >
                    {item?.createdAt
                      ? new Date(
                          item.createdAt
                        ).toLocaleDateString()
                      : item?.date
                      ? new Date(
                          item.date
                        ).toLocaleDateString()
                      : ""}
                  </span>

                </div>

                <div
                  style={
                    styles.recordContent
                  }
                >
                  {item?.description ||
                    item?.notes ||
                    item?.content ||
                    item?.name ||
                    "Record information available."}
                </div>

              </div>
            )
          )}

        </div>
      )}

    </section>
  );
}


/* =====================================================
   VITALS SECTION
===================================================== */

function VitalsSection({ items }) {
  return (
    <section style={styles.section}>

      <div style={styles.sectionHeader}>

        <div>
          <h2 style={styles.sectionTitle}>
            Vitals
          </h2>

          <p style={styles.sectionDescription}>
            Patient vital measurements recorded during care.
          </p>
        </div>

        <span style={styles.sectionCount}>
          {items?.length || 0}
        </span>

      </div>

      {!items ||
      items.length === 0 ? (
        <div style={styles.emptyBox}>

          <div style={styles.emptyIcon}>
            +
          </div>

          <strong>
            No vitals available
          </strong>

          <p style={styles.emptyText}>
            Vital measurements will appear
            here when available.
          </p>

        </div>
      ) : (
        <div style={styles.vitalsGrid}>

          {items.map(
            (vital, index) => (
              <VitalCard
                key={
                  vital?._id ||
                  vital?.id ||
                  index
                }
                vital={vital}
              />
            )
          )}

        </div>
      )}

    </section>
  );
}


/* =====================================================
   VITAL CARD
===================================================== */

function VitalCard({ vital }) {
  const bloodPressure =
    vital?.bloodPressure ||
    vital?.bp ||
    vital?.blood_pressure ||
    "—";

  const heartRate =
    vital?.heartRate ||
    vital?.hr ||
    vital?.pulse ||
    "—";

  const temperature =
    vital?.temperature ||
    vital?.temp ||
    "—";

  const respiratoryRate =
    vital?.respiratoryRate ||
    vital?.respRate ||
    vital?.respiratory_rate ||
    "—";

  const oxygenSaturation =
    vital?.oxygenSaturation ||
    vital?.spo2 ||
    vital?.oxygen_saturation ||
    "—";

  const weight =
    vital?.weight || "—";

  const recordedDate =
    vital?.recordedAt ||
    vital?.createdAt ||
    vital?.date;

  return (
    <div style={styles.vitalCard}>

      <div style={styles.vitalTop}>

        <div>
          <h3 style={styles.vitalTitle}>
            Vital Signs
          </h3>

          {recordedDate && (
            <span style={styles.recordDate}>
              {new Date(
                recordedDate
              ).toLocaleString()}
            </span>
          )}
        </div>

      </div>

      <div style={styles.vitalGrid}>

        <VitalValue
          label="Blood Pressure"
          value={bloodPressure}
        />

        <VitalValue
          label="Heart Rate"
          value={
            heartRate === "—"
              ? "—"
              : `${heartRate} bpm`
          }
        />

        <VitalValue
          label="Temperature"
          value={
            temperature === "—"
              ? "—"
              : `${temperature} °C`
          }
        />

        <VitalValue
          label="Respiratory Rate"
          value={
            respiratoryRate === "—"
              ? "—"
              : `${respiratoryRate} /min`
          }
        />

        <VitalValue
          label="Oxygen Saturation"
          value={
            oxygenSaturation === "—"
              ? "—"
              : `${oxygenSaturation} %`
          }
        />

        <VitalValue
          label="Weight"
          value={
            weight === "—"
              ? "—"
              : `${weight} kg`
          }
        />

      </div>

    </div>
  );
}


/* =====================================================
   VITAL VALUE

function VitalValue({
  label,
  value,
}) {
  return (
    <div style={styles.vitalValue}>

      <span style={styles.vitalLabel}>
        {label}
      </span>

      <strong style={styles.vitalNumber}>
        {value}
      </strong>

function TasksSection({ tasks }) {
  return (
    <div style={{ marginTop: "24px" }}>
      <h2 style={{ color: "var(--primary-dark)" }}>
        Tasks
      </h2>

      {!tasks?.length ? (
        <div style={styles.emptyBox}>
          No tasks available.
        </div>
      ) : (
        <div style={{ display: "grid", gap: "12px", marginTop: "12px" }}>
          {tasks.map((task) => (
            <div key={task._id} style={styles.itemBox}>

              <p style={styles.infoText}>
                <strong>ID:</strong> {task._id}
              </p>

              <p style={styles.infoText}>
                <strong>Title:</strong> {task.title}
              </p>

              <p style={styles.infoText}>
                <strong>Description:</strong> {task.description}
              </p>

              <p style={styles.infoText}>
                <strong>Due Date:</strong>{" "}
                {task.dueDate
                  ? new Date(task.dueDate).toLocaleDateString()
                  : "-"}
              </p>

              <p style={styles.infoText}>
                <strong>Priority:</strong> {task.priority}
              </p>

              <p style={styles.infoText}>
                <strong>Status:</strong> {task.status}
              </p>

              <p style={styles.infoText}>
                <strong>Patient ID:</strong> {task.patient}
              </p>

              <p style={styles.infoText}>
                <strong>Assignee ID:</strong> {task.assignee}
              </p>

              <p style={styles.infoText}>
                <strong>Created:</strong>{" "}
                {task.created_at
                  ? new Date(task.created_at).toLocaleDateString()
                  : "-"}
              </p>

              <p style={styles.infoText}>
                <strong>Updated:</strong>{" "}
                {task.updated_at
                  ? new Date(task.updated_at).toLocaleDateString()
                  : "-"}
              </p>

              <p style={styles.infoText}>
                <strong>Version:</strong> {task.__v}
              </p>

            </div>
          ))}
        </div>
      )}
    </div>
  );
}


/* =====================================================
   STYLES

const styles = {
  page: {
    padding: "28px",
    background: "#f5f8fb",
    minHeight: "100%",
    color: "#12385a",
    boxSizing: "border-box",
  },

  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "flex-start",
    marginBottom: "24px",
  },

  eyebrow: {
    margin: "0 0 6px",
    fontSize: "11px",
    fontWeight: 700,
    letterSpacing: "1.2px",
    color: "#2498c7",
  },

  title: {
    margin: 0,
    fontSize: "28px",
    fontWeight: 700,
    color: "#123f68",
  },

  subtitle: {
    margin: "7px 0 0",
    color: "#718294",
    fontSize: "14px",
  },

  refreshButton: {
    border: "1px solid #cbd9e4",
    background: "#ffffff",
    color: "#164c73",
    borderRadius: "9px",
    padding: "10px 17px",
    cursor: "pointer",
    fontWeight: 600,
    fontSize: "13px",
  },

  errorBox: {
    display: "flex",
    flexDirection: "column",
    gap: "4px",
    marginBottom: "18px",
    padding: "13px 16px",
    borderRadius: "10px",
    border: "1px solid #f2caca",
    background: "#fff5f5",
    color: "#b53b3b",
    fontSize: "12px",
  },

  searchCard: {
    background: "#ffffff",
    border: "1px solid #d9e5ee",
    borderRadius: "16px",
    padding: "24px",
    boxShadow:
      "0 4px 16px rgba(18, 56, 90, 0.06)",
  },

  searchHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "flex-start",
  },

  cardTitle: {
    margin: 0,
    fontSize: "20px",
    color: "#123f68",
  },

  cardSubtitle: {
    margin: "6px 0 18px",
    color: "#7b8997",
    fontSize: "13px",
  },

  patientCount: {
    minWidth: "32px",
    height: "32px",
    borderRadius: "50%",
    background: "#eef2ff",
    color: "#4f46e5",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "12px",
    fontWeight: 700,
  },

  searchContainer: {
    position: "relative",
    maxWidth: "520px",
  },

  searchIcon: {
    position: "absolute",
    left: "13px",
    top: "10px",
    fontSize: "20px",
    color: "#91a4b5",
  },

  searchInput: {
    width: "100%",
    padding: "13px 16px 13px 38px",
    border: "1px solid #cbd9e4",
    borderRadius: "9px",
    outline: "none",
    fontSize: "14px",
    boxSizing: "border-box",
  },

  patientList: {
    marginTop: "18px",
    display: "grid",
    gap: "10px",
  },

  patientRow: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    gap: "20px",
    padding: "15px 16px",
    border: "1px solid #dce7ef",
    borderRadius: "12px",
    background: "#fbfdff",
  },

  patientDetails: {
    display: "flex",
    alignItems: "center",
    gap: "13px",
    minWidth: 0,
  },

  avatar: {
    width: "42px",
    height: "42px",
    flexShrink: 0,
    borderRadius: "50%",
    background: "#e8f5fa",
    color: "#1684ae",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontWeight: 700,
    fontSize: "15px",
  },

  patientName: {
    margin: "0 0 5px",
    fontSize: "16px",
    color: "#123f68",
  },

  patientMeta: {
    margin: "3px 0",
    fontSize: "12px",
    color: "#748595",
  },

  primaryButton: {
    border: "none",
    borderRadius: "8px",
    padding: "10px 16px",
    background: "#2498c7",
    color: "#fff",
    cursor: "pointer",
    fontWeight: 600,
    fontSize: "12px",
    whiteSpace: "nowrap",
  },

  changeButton: {
    border: "1px solid #c9dce8",
    background: "#fff",
    color: "#164c73",
    borderRadius: "8px",
    padding: "9px 14px",
    cursor: "pointer",
    marginBottom: "14px",
    fontWeight: 600,
  },

  loadingCard: {
    background: "#fff",
    border: "1px solid #d7e4ed",
    borderRadius: "12px",
    padding: "30px",
    textAlign: "center",
    color: "#718494",
    marginTop: "18px",
  },

  spinner: {
    width: "26px",
    height: "26px",
    borderRadius: "50%",
    border: "3px solid #dbe8ef",
    borderTopColor: "#2498c7",
    margin: "0 auto 12px",
  },

  error: {
    marginTop: "18px",
    color: "#d64545",
  },

  emptyBox: {
    border: "1px dashed #cbdbe5",
    borderRadius: "10px",
    padding: "28px",
    textAlign: "center",
    color: "#526e82",
    background: "#fafcfd",
    marginTop: "18px",
  },

  emptyIcon: {
    width: "30px",
    height: "30px",
    lineHeight: "30px",
    margin: "0 auto 8px",
    borderRadius: "50%",
    background: "#e9f4f8",
    color: "#2498c7",
    fontWeight: 700,
  },

  emptyText: {
    margin: "5px 0 0",
    fontSize: "11px",
    color: "#9aa8b3",
  },

  patientHeader: {
    background: "#fff",
    border: "1px solid #d7e4ed",
    borderRadius: "16px",
    padding: "24px",
    boxShadow:
      "0 4px 16px rgba(18, 56, 90, 0.05)",
  },

  patientHeaderTop: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "flex-start",
    gap: "20px",
  },

  profileLeft: {
    display: "flex",
    alignItems: "center",
    gap: "15px",
  },

  largeAvatar: {
    width: "58px",
    height: "58px",
    borderRadius: "50%",
    background: "#e8f5fa",
    color: "#1684ae",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "22px",
    fontWeight: 700,
  },

  patientHeaderName: {
    margin: "4px 0 5px",
    fontSize: "23px",
    color: "#123f68",
  },

  patientId: {
    margin: 0,
    color: "#7b8997",
    fontSize: "12px",
  },

  statusBadge: {
    background: "#e8f5fa",
    color: "#1684ae",
    padding: "7px 12px",
    borderRadius: "20px",
    fontSize: "11px",
    fontWeight: 700,
  },

  patientInfoGrid: {
    display: "grid",
    gridTemplateColumns:
      "repeat(3, minmax(150px, 1fr))",
    gap: "12px",
    marginTop: "22px",
  },

  infoItem: {
    background: "#f7fafc",
    border: "1px solid #e1eaf0",
    borderRadius: "10px",
    padding: "12px",
  },

  infoLabel: {
    display: "block",
    fontSize: "11px",
    color: "#8493a0",
    marginBottom: "5px",
  },

  infoValue: {
    fontSize: "13px",
    color: "#234c6d",
  },

  statsGrid: {
    display: "grid",
    gridTemplateColumns:
      "repeat(4, minmax(150px, 1fr))",
    gap: "14px",
    margin: "18px 0",
  },

  statCard: {
    background: "#fff",
    border: "1px solid #d7e4ed",
    borderRadius: "13px",
    padding: "17px",
    display: "flex",
    alignItems: "center",
    gap: "12px",
  },

  statIcon: {
    width: "38px",
    height: "38px",
    borderRadius: "9px",
    background: "#eef5f8",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "16px",
  },

  statTitle: {
    display: "block",
    color: "#718494",
    fontSize: "12px",
    marginBottom: "6px",
  },

  statValue: {
    fontSize: "23px",
    color: "#123f68",
  },

  section: {
    background: "#fff",
    border: "1px solid #d7e4ed",
    borderRadius: "15px",
    padding: "20px",
    marginBottom: "18px",
  },

  sectionHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "flex-start",
    marginBottom: "14px",
  },

  sectionTitle: {
    margin: 0,
    color: "#123f68",
    fontSize: "18px",
  },

  sectionDescription: {
    margin: "5px 0 0",
    color: "#7b8997",
    fontSize: "12px",
  },

  sectionCount: {
    minWidth: "28px",
    height: "28px",
    borderRadius: "50%",
    background: "#f0f5f8",
    color: "#52738b",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "11px",
    fontWeight: 700,
  },

  recordsGrid: {
    display: "grid",
    gap: "10px",
  },

  recordCard: {
    border: "1px solid #dce7ee",
    borderRadius: "10px",
    padding: "14px",
    background: "#fbfdff",
  },

  recordHeader: {
    display: "flex",
    justifyContent: "space-between",
    gap: "15px",
    color: "#234c6d",
    fontSize: "13px",
  },

  recordDate: {
    color: "#8a99a5",
    fontSize: "11px",
    whiteSpace: "nowrap",
  },

  recordContent: {
    marginTop: "8px",
    color: "#647887",
    fontSize: "12px",
    lineHeight: 1.5,
  },

  smallBadge: {
    display: "inline-block",
    marginLeft: "8px",
    padding: "4px 7px",
    borderRadius: "5px",
    background: "#e8f5fa",
    color: "#1684ae",
    fontSize: "9px",
    fontWeight: 700,
  },

  vitalsGrid: {
    display: "grid",
    gap: "12px",
  },

  vitalCard: {
    border: "1px solid #dce7ee",
    borderRadius: "10px",
    padding: "15px",
    background: "#fbfdff",
  },

  vitalTop: {
    display: "flex",
    justifyContent: "space-between",
    marginBottom: "13px",
  },

  vitalTitle: {
    margin: 0,
    fontSize: "14px",
    color: "#234c6d",
  },

  vitalGrid: {
    display: "grid",
    gridTemplateColumns:
      "repeat(3, minmax(120px, 1fr))",
    gap: "10px",
  },

  vitalValue: {
    background: "#ffffff",
    border: "1px solid #e1eaf0",
    borderRadius: "8px",
    padding: "11px",
  },

  vitalLabel: {
    display: "block",
    color: "#8493a0",
    fontSize: "10px",
    marginBottom: "5px",
  },

  vitalNumber: {
    fontSize: "14px",
    color: "#234c6d",
  },
};