import { useEffect, useState } from "react";
import { UserRound } from "lucide-react";
import "./LogsManagementPage.css";

export default function LogsManagementPage() {
  const [showAddLog, setShowAddLog] = useState(false);

  const [editingLogId, setEditingLogId] = useState(null);

  const [logType, setLogType] = useState("");
  const [selectedPatient, setSelectedPatient] = useState("");
  const [logDate, setLogDate] = useState("");
  const [logTime, setLogTime] = useState("");
  const [description, setDescription] = useState("");

  const [searchTerm, setSearchTerm] = useState("");
  const [patientFilter, setPatientFilter] = useState("");
  const [logTypeFilter, setLogTypeFilter] = useState("");
  const [dateFilter, setDateFilter] = useState("");

  const [logs, setLogs] = useState(() => {
    const savedLogs = localStorage.getItem("guardianLogs");

    return savedLogs ? JSON.parse(savedLogs) : [];
  });

  useEffect(() => {
    localStorage.setItem("guardianLogs", JSON.stringify(logs));
  }, [logs]);

  //mock data will be removed later when real patient data is connected
  const mockPatients = [
    {
      id: 1,
      name: "Ronik L",
    },
    {
      id: 2,
      name: "John Doe",
    },
    {
      id: 3,
      name: "Guardian Monitor",
    },
    {
      id: 4,
      name: "Jaime Leo",
    },
  ];

  const logTypes = [
    "Vital Signs",
    "Medication",
    "Discharge",
    "Lab Results",
  ];

  const resetForm = () => {
    setLogType("");
    setSelectedPatient("");
    setLogDate("");
    setLogTime("");
    setDescription("");
    setEditingLogId(null);
  };

  const openAddLogModal = () => {
    resetForm();
    setShowAddLog(true);
  };

  const closeModal = () => {
    setShowAddLog(false);
    resetForm();
  };

  const handleSaveLog = () => {
    if (
      !logType ||
      !selectedPatient ||
      !logDate ||
      !logTime ||
      !description.trim()
    ) {
      alert("Please complete all fields before saving the log.");
      return;
    }

    const selectedPatientData = mockPatients.find(
      (patient) => patient.id.toString() === selectedPatient
    );

    if (editingLogId) {
      setLogs((currentLogs) =>
        currentLogs.map((log) =>
          log.id === editingLogId
            ? {
                ...log,
                patient: selectedPatientData?.name || "",
                logType,
                date: logDate,
                time: logTime,
                description: description.trim(),
              }
            : log
        )
      );
    } else {
      const newLog = {
        id: Date.now(),
        patient: selectedPatientData?.name || "",
        logType,
        date: logDate,
        time: logTime,
        createdBy: "Admin",
        description: description.trim(),
      };

      setLogs((currentLogs) => [...currentLogs, newLog]);
    }

    closeModal();
  };

  const handleEditLog = (log) => {
    const patient = mockPatients.find(
      (item) => item.name === log.patient
    );

    setEditingLogId(log.id);
    setLogType(log.logType);
    setSelectedPatient(patient ? patient.id.toString() : "");
    setLogDate(log.date);
    setLogTime(log.time);
    setDescription(log.description);

    setShowAddLog(true);
  };

  const handleDeleteLog = (logId) => {
    setLogs((currentLogs) =>
      currentLogs.filter((log) => log.id !== logId)
    );
  };

  const filteredLogs = logs.filter((log) => {
    const search = searchTerm.toLowerCase();

    const matchesSearch =
      log.patient.toLowerCase().includes(search) ||
      log.logType.toLowerCase().includes(search) ||
      log.createdBy.toLowerCase().includes(search) ||
      log.description.toLowerCase().includes(search);

    const matchesPatient =
      !patientFilter || log.patient === patientFilter;

    const matchesLogType =
      !logTypeFilter || log.logType === logTypeFilter;

    const matchesDate =
      !dateFilter || log.date === dateFilter;

    return (
      matchesSearch &&
      matchesPatient &&
      matchesLogType &&
      matchesDate
    );
  });

  return (
    <div className="logs-management-page">
      <div className="logs-page-header">
        <div>
          <span className="logs-page-label">
            GUARDIAN MANAGEMENT
          </span>

          <h1>LOGS MANAGEMENT</h1>

          <p>
            View, search and manage patient logs.
          </p>
        </div>

        <button
          className="logs-add-button"
          onClick={openAddLogModal}
        >
          + Add Log
        </button>
      </div>

      {showAddLog && (
        <div className="logs-modal-overlay">
          <div className="logs-modal">
            <h2>
              {editingLogId ? "Edit Log" : "Add New Log"}
            </h2>

            <button
              className="logs-modal-close"
              type="button"
              onClick={closeModal}
            >
              ×
            </button>

            <div className="logs-form">
              <label>Log Type</label>

              <div className="log-type-options">
                {logTypes.map((type) => (
                  <button
                    key={type}
                    className={`log-type-option ${
                      type === "Vital Signs"
                        ? "log-type-vital"
                        : type === "Medication"
                        ? "log-type-medication"
                        : type === "Discharge"
                        ? "log-type-discharge"
                        : "log-type-lab"
                    } ${
                      logType === type ? "selected" : ""
                    }`}
                    type="button"
                    onClick={() => setLogType(type)}
                  >
                    {type}
                  </button>
                ))}
              </div>

              <label>Patient</label>

              <select
                value={selectedPatient}
                onChange={(event) =>
                  setSelectedPatient(event.target.value)
                }
              >
                <option value="">
                  Select Patient
                </option>

                {mockPatients.map((patient) => (
                  <option
                    key={patient.id}
                    value={patient.id}
                  >
                    {patient.name}
                  </option>
                ))}
              </select>

              <label>Date</label>

              <input
                type="date"
                value={logDate}
                onChange={(event) =>
                  setLogDate(event.target.value)
                }
              />

              <label>Time</label>

              <input
                type="time"
                value={logTime}
                onChange={(event) =>
                  setLogTime(event.target.value)
                }
              />

              <label>Description</label>

              <textarea
                placeholder="Write description of the log:"
                rows="3"
                value={description}
                onChange={(event) =>
                  setDescription(event.target.value)
                }
              />

              <div className="logs-form-actions">
                <button
                  className="cancel-button"
                  type="button"
                  onClick={closeModal}
                >
                  Cancel
                </button>

                <button
                  className="save-log-button"
                  type="button"
                  onClick={handleSaveLog}
                >
                  {editingLogId ? "Update Log" : "Save Log"}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      <div className="logs-toolbar">
        <input
          type="text"
          placeholder="Search logs..."
          value={searchTerm}
          onChange={(event) =>
            setSearchTerm(event.target.value)
          }
        />

        <select
          value={patientFilter}
          onChange={(event) =>
            setPatientFilter(event.target.value)
          }
        >
          <option value="">
            All Patients
          </option>

          {mockPatients.map((patient) => (
            <option
              key={patient.id}
              value={patient.name}
            >
              {patient.name}
            </option>
          ))}
        </select>

        <select
          value={logTypeFilter}
          onChange={(event) =>
            setLogTypeFilter(event.target.value)
          }
        >
          <option value="">
            All Log Types
          </option>

          {logTypes.map((type) => (
            <option key={type} value={type}>
              {type}
            </option>
          ))}
        </select>

        <input
          type="date"
          value={dateFilter}
          onChange={(event) =>
            setDateFilter(event.target.value)
          }
        />
      </div>

      <div className="logs-table-container">
        <table className="logs-table">
          <thead>
            <tr>
              <th>Patient</th>
              <th>Log Type</th>
              <th>Date & Time</th>
              <th>Created By</th>
              <th>Description</th>
              <th>Actions</th>
            </tr>
          </thead>

          <tbody>
            {filteredLogs.length === 0 ? (
              <tr>
                <td
                  colSpan="6"
                  className="logs-empty-state"
                >
                  No logs found
                </td>
              </tr>
            ) : (
              filteredLogs.map((log) => (
                <tr key={log.id}>
                  <td>
                    <div className="patient-cell">
                      <UserRound size={18} />
                      <span>{log.patient}</span>
                    </div>
                  </td>

                  <td>
                    <span
                      className={`log-type-badge ${
                        log.logType === "Vital Signs"
                          ? "log-type-vital"
                          : log.logType === "Medication"
                          ? "log-type-medication"
                          : log.logType === "Discharge"
                          ? "log-type-discharge"
                          : "log-type-lab"
                      }`}
                    >
                      {log.logType}
                    </span>
                  </td>

                  <td>
                    {log.date} {log.time}
                  </td>

                  <td>{log.createdBy}</td>

                  <td>{log.description}</td>

                  <td className="logs-actions">
                    <button
                      className="edit-log-button"
                      type="button"
                      onClick={() =>
                        handleEditLog(log)
                      }
                    >
                      Edit
                    </button>

                    <button
                      className="delete-log-button"
                      type="button"
                      onClick={() =>
                        handleDeleteLog(log.id)
                      }
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}