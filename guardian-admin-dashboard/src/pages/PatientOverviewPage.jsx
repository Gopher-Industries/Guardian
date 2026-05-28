import { useEffect, useMemo, useState } from "react";
import { getAllPatients, getPatientOverview } from "../services/patientService";

export default function PatientOverviewPage() {
  const [patients, setPatients] = useState([]);
  const [selectedPatientId, setSelectedPatientId] = useState("");
  const [searchTerm, setSearchTerm] = useState("");

  const [overview, setOverview] = useState(null);
  const [loadingPatients, setLoadingPatients] = useState(true);
  const [loadingOverview, setLoadingOverview] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchPatients = async () => {
      try {
        setLoadingPatients(true);
        setError("");

        const data = await getAllPatients();
        const patientList = Array.isArray(data) ? data : data?.patients || [];
        setPatients(patientList);
      } catch (err) {
        console.error("Failed to load patients:", err);
        setError(err?.response?.data?.message || "Failed to load patients");
      } finally {
        setLoadingPatients(false);
      }
    };

    fetchPatients();
  }, []);

  const filteredPatients = useMemo(() => {
    const term = searchTerm.trim().toLowerCase();

    if (!term) return patients;

    return patients.filter((patient) => {
      const fullname = patient?.fullname?.toLowerCase() || "";
      const id = patient?._id?.toLowerCase() || "";
      return fullname.includes(term) || id.includes(term);
    });
  }, [patients, searchTerm]);

  const handleSelectPatient = async (patientId) => {
    try {
      setSelectedPatientId(patientId);
      setLoadingOverview(true);
      setError("");

      const selectedPatient = patients.find((p) => p._id === patientId);
      const orgId = selectedPatient?.organization;
      const data = await getPatientOverview(patientId, orgId);

      setOverview(data);
    } catch (err) {
      console.error("Failed to load patient overview:", err);
      console.log("Backend error data:", err?.response?.data);
      setError(
        err?.response?.data?.message || "Failed to load patient overview"
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

  const patient = overview?.patient || {};
  const records = overview?.records || [];
  const carePlans = overview?.carePlans || [];
  const tasks = overview?.tasks || [];
  const logs = overview?.logs || [];

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <h1 style={styles.title}>Patient Overview</h1>

        <div style={styles.topSection}>
          <div style={styles.searchBlock}>
            <label style={styles.label}>Search Patient</label>
            <input
              type="text"
              placeholder="Search by name or patient ID"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={styles.input}
            />
          </div>
        </div>

        {loadingPatients ? (
          <p style={styles.message}>Loading patients...</p>
        ) : error && !overview ? (
          <p style={{ ...styles.message, color: "var(--danger)" }}>{error}</p>
        ) : !selectedPatientId ? (
          <div style={styles.listWrap}>
            {filteredPatients.length === 0 ? (
              <p style={styles.message}>No patients found.</p>
            ) : (
              filteredPatients.map((item) => (
                <div key={item._id} style={styles.patientRow}>
                  <div>
                    <h3 style={styles.patientName}>{item.fullname}</h3>
                    <p style={styles.patientMeta}>
                      Gender: {item.gender || "-"}
                    </p>
                    <p style={styles.patientMeta}>Age: {item.age ?? "-"}</p>
                  </div>

                  <button
                    style={styles.button}
                    onClick={() => handleSelectPatient(item._id)}
                  >
                    View Overview
                  </button>
                </div>
              ))
            )}
          </div>
        ) : null}

        {loadingOverview ? (
          <p style={styles.message}>Loading patient overview...</p>
        ) : overview ? (
          <>
            <button style={styles.secondaryButton} onClick={handleChangePatient}>
              Change Patient
            </button>

            <div style={styles.overviewCard}>
              <h2 style={styles.sectionTitle}>
                {patient.fullname || "Unknown Patient"}
              </h2>
              <p style={styles.infoText}>Gender: {patient.gender || "-"}</p>
              <p style={styles.infoText}>
                Date of Birth:{" "}
                {patient.dateOfBirth
                  ? new Date(patient.dateOfBirth).toLocaleDateString()
                  : "-"}
              </p>
              <p style={styles.infoText}>
                Organization: {patient.organization?.name || "Guardian Monitor"}
              </p>

              <h3 style={styles.subTitle}>Caretaker</h3>
              <p style={styles.infoText}>{patient.caretaker?.fullname || "-"}</p>
              <p style={styles.infoText}>{patient.caretaker?.email || "-"}</p>

              <h3 style={styles.subTitle}>Assigned Nurses</h3>
              {patient.assignedNurses?.length ? (
                patient.assignedNurses.map((nurse) => (
                  <div key={nurse._id} style={{ marginBottom: "8px" }}>
                    <p style={styles.infoText}>{nurse.fullname}</p>
                    <p style={styles.infoText}>{nurse.email}</p>
                  </div>
                ))
              ) : (
                <p style={styles.infoText}>No nurses assigned</p>
              )}

              <h3 style={styles.subTitle}>Assigned Doctor</h3>
              <p style={styles.infoText}>{patient.assignedDoctor?.fullname || "-"}</p>
              <p style={styles.infoText}>{patient.assignedDoctor?.email || "-"}</p>
            </div>

            <div style={styles.statsRow}>
              <div style={styles.statCard}>
                <h3 style={styles.statTitle}>Records</h3>
                <p style={styles.statValue}>{records.length}</p>
              </div>
              <div style={styles.statCard}>
                <h3 style={styles.statTitle}>Care Plans</h3>
                <p style={styles.statValue}>{carePlans.length}</p>
              </div>
              <div style={styles.statCard}>
                <h3 style={styles.statTitle}>Tasks</h3>
                <p style={styles.statValue}>{tasks.length}</p>
              </div>
              <div style={styles.statCard}>
                <h3 style={styles.statTitle}>Logs</h3>
                <p style={styles.statValue}>{logs.length}</p>
              </div>
            </div>

            <Section title="Records" items={records} />
            <Section title="Care Plans" items={carePlans} />
            <Section title="Tasks" items={tasks} />
            <Section title="Logs" items={logs} />
          </>
        ) : null}
      </div>
    </div>
  );
}

function Section({ title, items }) {
  return (
    <div style={{ marginTop: "24px" }}>
      <h2 style={{ color: "var(--primary-dark)" }}>{title}</h2>
      {!items?.length ? (
        <div style={styles.emptyBox}>No {title.toLowerCase()} available.</div>
      ) : (
        <div style={{ display: "grid", gap: "12px", marginTop: "12px" }}>
          {items.map((item, index) => (
            <div key={item?._id || index} style={styles.itemBox}>
              <pre
                style={{
                  margin: 0,
                  whiteSpace: "pre-wrap",
                  color: "var(--text)",
                }}
              >
                {JSON.stringify(item, null, 2)}
              </pre>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

const styles = {
  page: {
    padding: "24px",
  },
  card: {
    background: "var(--surface-soft)",
    borderRadius: "28px",
    padding: "28px",
    border: "1px solid var(--border)",
    boxShadow: "var(--shadow-sm)",
  },
  title: {
    color: "var(--primary-dark)",
    fontSize: "2rem",
    fontWeight: 700,
    marginBottom: "24px",
  },
  topSection: {
    display: "flex",
    gap: "16px",
    marginBottom: "20px",
  },
  searchBlock: {
    width: "100%",
  },
  label: {
    display: "block",
    marginBottom: "10px",
    fontSize: "1.1rem",
    fontWeight: 600,
    color: "var(--primary-dark)",
  },
  input: {
    width: "100%",
    maxWidth: "520px",
    padding: "16px 20px",
    borderRadius: "22px",
    border: "1px solid var(--border)",
    fontSize: "1rem",
    outline: "none",
    background: "var(--surface)",
    color: "var(--text)",
  },
  listWrap: {
    display: "grid",
    gap: "14px",
    marginBottom: "24px",
  },
  patientRow: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    background: "var(--surface)",
    borderRadius: "18px",
    padding: "18px",
    border: "1px solid var(--border)",
    gap: "16px",
  },
  patientName: {
    margin: "0 0 8px 0",
    color: "var(--primary-dark)",
  },
  patientMeta: {
    margin: "4px 0",
    color: "var(--text-muted)",
  },
  button: {
    background: "linear-gradient(90deg, var(--primary), #4f95c5)",
    color: "#fff",
    border: "none",
    borderRadius: "16px",
    padding: "12px 18px",
    cursor: "pointer",
    fontWeight: 600,
    boxShadow: "var(--shadow-sm)",
  },
  secondaryButton: {
    background: "var(--surface)",
    color: "var(--primary-dark)",
    border: "1px solid var(--border)",
    borderRadius: "14px",
    padding: "10px 16px",
    cursor: "pointer",
    fontWeight: 600,
    marginTop: "6px",
    marginBottom: "14px",
  },
  overviewCard: {
    background: "var(--surface)",
    borderRadius: "20px",
    padding: "20px",
    marginTop: "10px",
    border: "1px solid var(--border)",
  },
  sectionTitle: {
    color: "var(--primary-dark)",
    marginBottom: "12px",
  },
  subTitle: {
    color: "var(--primary-dark)",
    marginTop: "16px",
    marginBottom: "8px",
  },
  infoText: {
    color: "var(--text)",
    margin: "6px 0",
  },
  statsRow: {
    display: "grid",
    gridTemplateColumns: "repeat(4, 1fr)",
    gap: "14px",
    marginTop: "20px",
  },
  statCard: {
    background: "var(--surface)",
    borderRadius: "18px",
    padding: "18px",
    textAlign: "center",
    border: "1px solid var(--border)",
  },
  statTitle: {
    margin: "0 0 10px 0",
    color: "var(--primary-dark)",
  },
  statValue: {
    margin: 0,
    color: "var(--text)",
    fontSize: "1.45rem",
    fontWeight: 700,
  },
  emptyBox: {
    background: "var(--surface)",
    borderRadius: "14px",
    padding: "16px",
    border: "1px solid var(--border)",
    color: "var(--text-muted)",
  },
  itemBox: {
    background: "var(--surface)",
    borderRadius: "14px",
    padding: "16px",
    border: "1px solid var(--border)",
  },
  message: {
    marginTop: "18px",
    color: "var(--text)",
  },
};