import { useEffect, useMemo, useState } from "react";
import {
  assignDoctorToPatient,
  getDoctors,
  getPatientsByDoctor,
  unassignDoctorFromPatient,
} from "../services/doctorAssignmentService";

export default function DoctorAssignmentsPage() {
  const [doctors, setDoctors] = useState([]);
  const [selectedDoctorId, setSelectedDoctorId] = useState("");
  const [patients, setPatients] = useState([]);

  const [patientId, setPatientId] = useState("");
  const [newDoctorId, setNewDoctorId] = useState("");

  const [loadingDoctors, setLoadingDoctors] = useState(false);
  const [loadingPatients, setLoadingPatients] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const selectedDoctor = useMemo(() => {
    return doctors.find((doctor) => getId(doctor) === selectedDoctorId);
  }, [doctors, selectedDoctorId]);

  useEffect(() => {
    loadDoctors();
  }, []);

  useEffect(() => {
    if (selectedDoctorId) {
      loadPatientsForDoctor(selectedDoctorId);
    } else {
      setPatients([]);
    }
  }, [selectedDoctorId]);

  async function loadDoctors() {
    try {
      setLoadingDoctors(true);
      setError("");

      const data = await getDoctors();

      const doctorList =
        data?.doctors ||
        data?.data ||
        data?.items ||
        data?.results ||
        (Array.isArray(data) ? data : []);

      setDoctors(doctorList);
    } catch (err) {
      console.error(err);
      setError("Failed to load doctors. Please try again.");
    } finally {
      setLoadingDoctors(false);
    }
  }

  async function loadPatientsForDoctor(doctorId) {
    try {
      setLoadingPatients(true);
      setError("");
      setSuccess("");

      const data = await getPatientsByDoctor(doctorId);

      const patientList =
        data?.patients ||
        data?.data ||
        data?.items ||
        data?.results ||
        (Array.isArray(data) ? data : []);

      setPatients(patientList);
    } catch (err) {
      console.error(err);
      setError("Failed to load patients for this doctor.");
    } finally {
      setLoadingPatients(false);
    }
  }

  async function handleAssignDoctor(event) {
    event.preventDefault();

    if (!patientId || !newDoctorId) {
      setError("Please enter/select a patient and doctor before assigning.");
      return;
    }

    try {
      setActionLoading(true);
      setError("");
      setSuccess("");

      await assignDoctorToPatient(patientId, newDoctorId);

      setSuccess("Doctor assigned successfully.");
      setPatientId("");
      setNewDoctorId("");

      if (selectedDoctorId) {
        await loadPatientsForDoctor(selectedDoctorId);
      }
    } catch (err) {
      console.error(err);
      setError("Failed to assign doctor. Please check patient ID and try again.");
    } finally {
      setActionLoading(false);
    }
  }

  async function handleUnassignPatient(targetPatientId) {
    const confirmed = window.confirm(
      "Are you sure you want to unassign this doctor from the selected patient?"
    );

    if (!confirmed) return;

    try {
      setActionLoading(true);
      setError("");
      setSuccess("");

      await unassignDoctorFromPatient(targetPatientId);

      setSuccess("Doctor unassigned successfully.");

      if (selectedDoctorId) {
        await loadPatientsForDoctor(selectedDoctorId);
      }
    } catch (err) {
      console.error(err);
      setError("Failed to unassign doctor. Please try again.");
    } finally {
      setActionLoading(false);
    }
  }

  function getId(item) {
    return item?._id || item?.id || item?.doctorId || item?.patientId || "";
  }

  function getDoctorName(doctor) {
    return (
      doctor?.name ||
      doctor?.fullName ||
      doctor?.user?.name ||
      `${doctor?.firstName || ""} ${doctor?.lastName || ""}`.trim() ||
      "Unnamed Doctor"
    );
  }

  function getPatientName(patient) {
    return (
      patient?.name ||
      patient?.fullName ||
      patient?.user?.name ||
      `${patient?.firstName || ""} ${patient?.lastName || ""}`.trim() ||
      "Unnamed Patient"
    );
  }

  return (
    <section className="page-shell">
      <div className="page-header">
        <div>
          <p className="eyebrow">ADMIN DASHBOARD</p>
          <h1>Doctor Assignments</h1>
          <p className="page-subtitle">
            View doctors, check assigned patients, and manage doctor-patient
            assignments from one place.
          </p>
        </div>
      </div>

      {error ? <div className="alert alert-error">{error}</div> : null}
      {success ? <div className="alert alert-success">{success}</div> : null}

      <div className="dashboard-grid">
        <div className="dashboard-card">
          <h2>Select Doctor</h2>
          <p className="card-muted">
            Choose a doctor to view the patients currently assigned to them.
          </p>

          {loadingDoctors ? (
            <p>Loading doctors...</p>
          ) : (
            <select
              className="form-control"
              value={selectedDoctorId}
              onChange={(event) => setSelectedDoctorId(event.target.value)}
            >
              <option value="">Select doctor</option>
              {doctors.map((doctor) => (
                <option key={getId(doctor)} value={getId(doctor)}>
                  {getDoctorName(doctor)}
                </option>
              ))}
            </select>
          )}

          {selectedDoctor ? (
            <div className="summary-box">
              <strong>Selected Doctor:</strong>
              <span>{getDoctorName(selectedDoctor)}</span>
            </div>
          ) : null}
        </div>

        <div className="dashboard-card">
          <h2>Assign / Change Doctor</h2>
          <p className="card-muted">
            Enter the patient ID and select the doctor to assign or change the
            doctor assignment.
          </p>

          <form onSubmit={handleAssignDoctor} className="assignment-form">
            <label>
              Patient ID
              <input
                className="form-control"
                type="text"
                placeholder="Enter patient ID"
                value={patientId}
                onChange={(event) => setPatientId(event.target.value)}
              />
            </label>

            <label>
              Doctor
              <select
                className="form-control"
                value={newDoctorId}
                onChange={(event) => setNewDoctorId(event.target.value)}
              >
                <option value="">Select doctor</option>
                {doctors.map((doctor) => (
                  <option key={getId(doctor)} value={getId(doctor)}>
                    {getDoctorName(doctor)}
                  </option>
                ))}
              </select>
            </label>

            <button className="primary-button" type="submit" disabled={actionLoading}>
              {actionLoading ? "Saving..." : "Assign Doctor"}
            </button>
          </form>
        </div>
      </div>

      <div className="dashboard-card full-width-card">
        <div className="section-header">
          <div>
            <h2>Assigned Patients</h2>
            <p className="card-muted">
              Patients assigned to the selected doctor will appear here.
            </p>
          </div>

          {selectedDoctorId ? (
            <button
              className="secondary-button"
              onClick={() => loadPatientsForDoctor(selectedDoctorId)}
              disabled={loadingPatients}
            >
              Refresh
            </button>
          ) : null}
        </div>

        {!selectedDoctorId ? (
          <div className="empty-state">Please select a doctor first.</div>
        ) : loadingPatients ? (
          <div className="empty-state">Loading assigned patients...</div>
        ) : patients.length === 0 ? (
          <div className="empty-state">
            No patients are currently assigned to this doctor.
          </div>
        ) : (
          <div className="table-wrapper">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>Patient Name</th>
                  <th>Patient ID</th>
                  <th>Age</th>
                  <th>Gender</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {patients.map((patient) => {
                  const id = getId(patient);

                  return (
                    <tr key={id}>
                      <td>{getPatientName(patient)}</td>
                      <td>{id || "N/A"}</td>
                      <td>{patient?.age || patient?.dob || "N/A"}</td>
                      <td>{patient?.gender || "N/A"}</td>
                      <td>
                        <button
                          className="danger-button"
                          onClick={() => handleUnassignPatient(id)}
                          disabled={actionLoading}
                        >
                          Unassign
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </section>
  );
}