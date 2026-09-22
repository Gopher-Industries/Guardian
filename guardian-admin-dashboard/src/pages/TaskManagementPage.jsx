import { useEffect, useMemo, useState } from "react";
import {
  Plus,
  Pencil,
  Trash2,
  ClipboardList,
  Search,
  Filter,
  UserRound,
  Stethoscope,
  HeartHandshake,
  CalendarDays,
} from "lucide-react";
import { createTask, updateTask, deleteTask } from "../services/taskService";
import {
  getAdminPatients,
  getCaretakers,
  getNurses,
} from "../services/taskLookupService";
import SuccessCelebrationOverlay from "../components/common/SuccessCelebrationOverlay";
import "./TaskManagementPage.css";

// Temporary mock task records used for UI display
// TODO: Replace this with real backend task list fetching
// once a proper GET /api/v1/admin/tasks endpoint is available
const initialTasks = [
  {
    _id: "task-1",
    description: "Review daily patient observations",
    dueDate: "2026-05-20",
    priority: "high",
    status: "pending",
    patient: "Aarav Sharma",
    patientDob: "1958-03-12",
    caretaker: "Rahul Verma",
    caretakerEmail: "rahul@example.com",
    nurse_id: "Emily Stone",
    nurseEmail: "emily@example.com",
    report: "",
    created_at: "",
    updated_at: "",
  },
  {
    _id: "task-2",
    description: "Update medication reminder schedule",
    dueDate: "2026-05-22",
    priority: "medium",
    status: "in-progress",
    patient: "Sophia Brown",
    patientDob: "1961-07-08",
    caretaker: "Olivia James",
    caretakerEmail: "olivia@example.com",
    nurse_id: "Ava Lee",
    nurseEmail: "ava@example.com",
    report: "",
    created_at: "",
    updated_at: "",
  },
];

const emptyForm = {
  description: "",
  patientId: "",
  caretakerId: "",
  nurseId: "",
  dueDate: "",
  priority: "medium",
};

function formatPriorityLabel(value) {
  if (!value) return "-";
  return value.charAt(0).toUpperCase() + value.slice(1);
}

function formatStatusLabel(value) {
  if (!value) return "-";
  if (value === "in-progress") return "In Progress";
  return value.charAt(0).toUpperCase() + value.slice(1);
}

function formatDateOfBirth(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString("en-AU", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

function formatTableDate(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString("en-AU", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

function getTodayDateString() {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0");
  const day = String(today.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function normalizePatient(raw) {
  const id = raw?._id || raw?.id || raw?.patientId || "";
  const name =
    raw?.fullname ||
    raw?.fullName ||
    raw?.name ||
    raw?.patientName ||
    "Unnamed Patient";
  const dob =
    raw?.dateOfBirth || raw?.dob || raw?.birthDate || raw?.date_of_birth || "";

  return {
    id,
    name,
    dob,
    display: `${name}${dob ? ` · ${formatDateOfBirth(dob)}` : ""}`,
    raw,
  };
}

function normalizeCaretaker(raw) {
  const id = raw?._id || raw?.id || raw?.caretakerId || "";
  const name =
    raw?.fullname ||
    raw?.fullName ||
    raw?.name ||
    raw?.caretakerName ||
    "Unnamed Caretaker";
  const email = raw?.email || "";

  return {
    id,
    name,
    email,
    display: `${name}${email ? ` · ${email}` : ""}`,
    raw,
  };
}

function normalizeNurse(raw) {
  const id = raw?._id || raw?.id || raw?.nurseId || "";
  const name =
    raw?.fullname ||
    raw?.fullName ||
    raw?.name ||
    raw?.nurseName ||
    "Unnamed Nurse";
  const email = raw?.email || "";

  return {
    id,
    name,
    email,
    display: `${name}${email ? ` · ${email}` : ""}`,
    raw,
  };
}

export default function TaskManagementPage() {
  const [tasks, setTasks] = useState(initialTasks);
  const [searchTerm, setSearchTerm] = useState("");
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  const [form, setForm] = useState(emptyForm);

  const [patients, setPatients] = useState([]);
  const [caretakers, setCaretakers] = useState([]);
  const [nurses, setNurses] = useState([]);

  const [loading, setLoading] = useState(false);
  const [lookupLoading, setLookupLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [successOverlayOpen, setSuccessOverlayOpen] = useState(false);
  const [successOverlayMessage, setSuccessOverlayMessage] = useState("");

  const todayDate = getTodayDateString();

  useEffect(() => {
    async function loadLookups() {
      setLookupLoading(true);
      try {
        const [patientsRes, caretakersRes, nursesRes] = await Promise.all([
          getAdminPatients(),
          getCaretakers(),
          getNurses(),
        ]);

        const patientList = Array.isArray(patientsRes)
          ? patientsRes
          : patientsRes?.patients || patientsRes?.data || [];
        const caretakerList = Array.isArray(caretakersRes)
          ? caretakersRes
          : caretakersRes?.caretakers || caretakersRes?.data || [];
        const nurseList = Array.isArray(nursesRes)
          ? nursesRes
          : nursesRes?.nurses || nursesRes?.data || [];

        setPatients(patientList.map(normalizePatient));
        setCaretakers(caretakerList.map(normalizeCaretaker));
        setNurses(nurseList.map(normalizeNurse));
      } catch (err) {
        setErrorMessage(
          err?.response?.data?.message ||
            "Failed to load patient, caretaker, and nurse options."
        );
      } finally {
        setLookupLoading(false);
      }
    }

    loadLookups();
  }, []);

  const filteredTasks = useMemo(() => {
    return tasks.filter((task) => {
      const combined =
        `${task.description} ${task.patient} ${task.patientDob} ${task.caretaker} ${task.nurse_id} ${task.priority} ${task.status}`.toLowerCase();
      return combined.includes(searchTerm.toLowerCase());
    });
  }, [tasks, searchTerm]);

  const selectedPatient = patients.find((item) => item.id === form.patientId);
  const selectedCaretaker = caretakers.find(
    (item) => item.id === form.caretakerId
  );
  const selectedNurse = nurses.find((item) => item.id === form.nurseId);

  const showSuccessOverlay = (message) => {
    setSuccessOverlayMessage(message);
    setSuccessOverlayOpen(true);

    setTimeout(() => {
      setSuccessOverlayOpen(false);
    }, 3200);
  };

  const resetFormState = () => {
    setForm(emptyForm);
    setSelectedTask(null);
    setShowCreateModal(false);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleCreateTask = async (e) => {
    e.preventDefault();

    if (form.dueDate < todayDate) {
      setErrorMessage("Due date cannot be in the past.");
      return;
    }

    setLoading(true);
    setErrorMessage("");

    try {
      const payload = {
        description: form.description,
        patientId: form.patientId,
        dueDate: form.dueDate,
        caretakerId: form.caretakerId || undefined,
        nurseId: form.nurseId || undefined,
        priority: form.priority,
      };

      const result = await createTask(payload);
      const createdTask = result?.task;

      const normalizedTask = {
        _id: createdTask?._id || Date.now().toString(),
        description: createdTask?.description || form.description,
        dueDate: createdTask?.dueDate?.slice(0, 10) || form.dueDate,
        priority: createdTask?.priority || form.priority,
        status: createdTask?.status || "pending",
        patient: selectedPatient?.name || "-",
        patientDob: selectedPatient?.dob || "",
        caretaker: selectedCaretaker?.name || "-",
        caretakerEmail: selectedCaretaker?.email || "",
        nurse_id: selectedNurse?.name || "-",
        nurseEmail: selectedNurse?.email || "",
        report: createdTask?.report || "",
        created_at: createdTask?.created_at || "",
        updated_at: createdTask?.updated_at || "",
      };

      setTasks((prev) => [normalizedTask, ...prev]);
      resetFormState();
      showSuccessOverlay(result?.message || "Task created successfully.");
    } catch (err) {
      setErrorMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Failed to create task."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleEditClick = (task) => {
    const matchedPatient = patients.find((p) => p.name === task.patient);
    const matchedCaretaker = caretakers.find((c) => c.name === task.caretaker);
    const matchedNurse = nurses.find((n) => n.name === task.nurse_id);

    setSelectedTask(task);
    setForm({
      description: task.description || "",
      patientId: matchedPatient?.id || "",
      caretakerId: matchedCaretaker?.id || "",
      nurseId: matchedNurse?.id || "",
      dueDate: task.dueDate || "",
      priority: task.priority || "medium",
    });

    setShowCreateModal(true);
    setErrorMessage("");
  };

  const handleUpdateTask = async (e) => {
    e.preventDefault();

    if (form.dueDate < todayDate) {
      setErrorMessage("Due date cannot be in the past.");
      return;
    }

    setLoading(true);
    setErrorMessage("");

    try {
      const payload = {
        description: form.description,
        dueDate: form.dueDate,
        caretakerId: form.caretakerId || undefined,
        nurseId: form.nurseId || undefined,
        priority: form.priority,
      };

      const result = await updateTask(selectedTask._id, payload);
      const updatedTask = result?.task;

      setTasks((prev) =>
        prev.map((task) =>
          task._id === selectedTask._id
            ? {
                ...task,
                description: updatedTask?.description || form.description,
                dueDate: updatedTask?.dueDate?.slice(0, 10) || form.dueDate,
                priority: updatedTask?.priority || form.priority,
                status: updatedTask?.status || task.status,
                patient: selectedPatient?.name || task.patient,
                patientDob: selectedPatient?.dob || task.patientDob,
                caretaker: selectedCaretaker?.name || "-",
                caretakerEmail: selectedCaretaker?.email || "",
                nurse_id: selectedNurse?.name || "-",
                nurseEmail: selectedNurse?.email || "",
                updated_at: updatedTask?.updated_at || "",
              }
            : task
        )
      );

      resetFormState();
      showSuccessOverlay(result?.message || "Task updated successfully.");
    } catch (err) {
      setErrorMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Failed to update task."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteClick = (task) => {
    setSelectedTask(task);
    setShowDeleteModal(true);
    setErrorMessage("");
  };

  const confirmDelete = async () => {
    if (!selectedTask?._id) return;

    setLoading(true);
    setErrorMessage("");

    try {
      const result = await deleteTask(selectedTask._id);
      setTasks((prev) => prev.filter((task) => task._id !== selectedTask._id));
      setSelectedTask(null);
      setShowDeleteModal(false);
      showSuccessOverlay(result?.message || "Task deleted successfully.");
    } catch (err) {
      setErrorMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Failed to delete task."
      );
    } finally {
      setLoading(false);
    }
  };

  const getPriorityClass = (priority) => {
    if (priority === "high") return "priority-high";
    if (priority === "medium") return "priority-medium";
    return "priority-low";
  };

  const getStatusClass = (status) => {
    if (status === "completed") return "status-completed";
    if (status === "in-progress") return "status-progress";
    return "status-pending";
  };

  return (
    <section className="task-management-page">
      <SuccessCelebrationOverlay
        open={successOverlayOpen}
        message={successOverlayMessage}
      />

      <div className="task-page-header">
        <div>
          <p className="task-page-eyebrow">Guardian Monitor Admin</p>
          <h1>Task Management</h1>
          <p className="task-page-subtitle">
            Create, review, update, and manage patient-related administrative
            tasks.
          </p>
        </div>

        <button
          className="task-primary-btn"
          onClick={() => {
            setSelectedTask(null);
            setForm(emptyForm);
            setShowCreateModal(true);
            setErrorMessage("");
          }}
        >
          <Plus size={18} />
          Add Task
        </button>
      </div>

      <div className="task-summary-grid">
        <div className="task-summary-card">
          <ClipboardList size={18} />
          <div>
            <strong>{tasks.length}</strong>
            <span>Total Tasks</span>
          </div>
        </div>

        <div className="task-summary-card">
          <strong>{tasks.filter((t) => t.status === "pending").length}</strong>
          <span>Pending</span>
        </div>

        <div className="task-summary-card">
          <strong>{tasks.filter((t) => t.status === "in-progress").length}</strong>
          <span>In Progress</span>
        </div>

        <div className="task-summary-card">
          <strong>{tasks.filter((t) => t.status === "completed").length}</strong>
          <span>Completed</span>
        </div>
      </div>

      {errorMessage ? (
        <p className="task-error-message">{errorMessage}</p>
      ) : null}

      <div className="task-toolbar">
        <div className="task-search-box">
          <Search size={16} />
          <input
            type="text"
            placeholder="Search tasks, patients, or staff..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        <button className="task-filter-btn" type="button">
          <Filter size={16} />
          Filter
        </button>
      </div>

      <div className="task-table-card">
        <div className="task-table-header">
          <h3>Task List</h3>
          <p>Manage, review, and track administrative tasks from the dashboard.</p>
        </div>

        <div className="task-table-wrapper">
          <table className="task-table">
            <thead>
              <tr>
                <th>Description</th>
                <th>Patient Name</th>
                <th>DOB</th>
                <th>Assigned Staff</th>
                <th>Due Date</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {filteredTasks.length > 0 ? (
                filteredTasks.map((task) => (
                  <tr key={task._id}>
                    <td>{task.description}</td>
                    <td>{task.patient}</td>
                    <td>{formatDateOfBirth(task.patientDob)}</td>
                    <td>{task.nurse_id !== "-" ? task.nurse_id : task.caretaker}</td>
                    <td>{formatTableDate(task.dueDate)}</td>
                    <td>
                      <span className={`task-pill ${getPriorityClass(task.priority)}`}>
                        {formatPriorityLabel(task.priority)}
                      </span>
                    </td>
                    <td>
                      <span className={`task-pill ${getStatusClass(task.status)}`}>
                        {formatStatusLabel(task.status)}
                      </span>
                    </td>
                    <td>
                      <div className="task-actions">
                        <button
                          className="task-icon-btn"
                          onClick={() => handleEditClick(task)}
                          disabled={loading}
                          type="button"
                        >
                          <Pencil size={16} />
                        </button>
                        <button
                          className="task-icon-btn delete"
                          onClick={() => handleDeleteClick(task)}
                          disabled={loading}
                          type="button"
                        >
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="8" className="task-empty-state">
                    No tasks found for the current search.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showCreateModal && (
        <div className="task-modal-backdrop">
          <div className="task-modal task-modal-enhanced">
            <div className="task-modal-top">
              <div>
                <p className="task-modal-eyebrow">
                  {selectedTask ? "Update existing task" : "Create administrative task"}
                </p>
                <h3>{selectedTask ? "Edit Task" : "Create New Task"}</h3>
              </div>
            </div>

            <form onSubmit={selectedTask ? handleUpdateTask : handleCreateTask}>
              <div className="task-form-layout">
                <div className="task-form-main">
                  <div className="task-form-field task-form-field-full">
                    <label>Description</label>
                    <textarea
                      name="description"
                      value={form.description}
                      onChange={handleChange}
                      placeholder="Enter the task description"
                      required
                      rows="4"
                    />
                  </div>

                  <div className="task-form-grid polished-grid">
                    <div className="task-form-field">
                      <label>Patient</label>
                      <div className="task-select-shell">
                        <UserRound size={16} />
                        <select
                          name="patientId"
                          value={form.patientId}
                          onChange={handleChange}
                          required
                          disabled={!!selectedTask || lookupLoading}
                        >
                          <option value="">
                            {lookupLoading ? "Loading patients..." : "Select patient"}
                          </option>
                          {patients.map((patient) => (
                            <option key={patient.id} value={patient.id}>
                              {patient.display}
                            </option>
                          ))}
                        </select>
                      </div>
                    </div>

                    <div className="task-form-field">
                      <label>Due Date</label>
                      <div className="task-select-shell">
                        <CalendarDays size={16} />
                        <input
                          type="date"
                          name="dueDate"
                          value={form.dueDate}
                          onChange={handleChange}
                          min={todayDate}
                          required
                        />
                      </div>
                    </div>

                    <div className="task-form-field">
                      <label>Caretaker</label>
                      <div className="task-select-shell">
                        <HeartHandshake size={16} />
                        <select
                          name="caretakerId"
                          value={form.caretakerId}
                          onChange={handleChange}
                          disabled={lookupLoading}
                        >
                          <option value="">
                            {lookupLoading ? "Loading caretakers..." : "Select caretaker"}
                          </option>
                          {caretakers.map((caretaker) => (
                            <option key={caretaker.id} value={caretaker.id}>
                              {caretaker.display}
                            </option>
                          ))}
                        </select>
                      </div>
                    </div>

                    <div className="task-form-field">
                      <label>Nurse</label>
                      <div className="task-select-shell">
                        <Stethoscope size={16} />
                        <select
                          name="nurseId"
                          value={form.nurseId}
                          onChange={handleChange}
                          disabled={lookupLoading}
                        >
                          <option value="">
                            {lookupLoading ? "Loading nurses..." : "Select nurse"}
                          </option>
                          {nurses.map((nurse) => (
                            <option key={nurse.id} value={nurse.id}>
                              {nurse.display}
                            </option>
                          ))}
                        </select>
                      </div>
                    </div>

                    <div className="task-form-field">
                      <label>Priority</label>
                      <div className="task-select-shell">
                        <ClipboardList size={16} />
                        <select
                          name="priority"
                          value={form.priority}
                          onChange={handleChange}
                        >
                          <option value="high">High</option>
                          <option value="medium">Medium</option>
                          <option value="low">Low</option>
                        </select>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="task-form-side">
                  {selectedPatient ? (
                    <div className="task-selection-card">
                      <div className="task-selection-head">
                        <UserRound size={16} />
                        <strong>Selected Patient</strong>
                      </div>
                      <div className="task-selection-details">
                        <span><strong>Name:</strong> {selectedPatient.name}</span>
                        {selectedPatient.dob ? (
                          <span><strong>DOB:</strong> {formatDateOfBirth(selectedPatient.dob)}</span>
                        ) : null}
                      </div>
                    </div>
                  ) : (
                    <div className="task-selection-card placeholder">
                      <div className="task-selection-head">
                        <UserRound size={16} />
                        <strong>Patient Preview</strong>
                      </div>
                      <div className="task-selection-details">
                        <span>Select a patient to preview the chosen record here.</span>
                      </div>
                    </div>
                  )}

                  {selectedCaretaker ? (
                    <div className="task-selection-card compact">
                      <div className="task-selection-head">
                        <HeartHandshake size={16} />
                        <strong>Caretaker</strong>
                      </div>
                      <div className="task-selection-details">
                        <span>{selectedCaretaker.name}</span>
                        {selectedCaretaker.email ? <span>{selectedCaretaker.email}</span> : null}
                      </div>
                    </div>
                  ) : null}

                  {selectedNurse ? (
                    <div className="task-selection-card compact">
                      <div className="task-selection-head">
                        <Stethoscope size={16} />
                        <strong>Nurse</strong>
                      </div>
                      <div className="task-selection-details">
                        <span>{selectedNurse.name}</span>
                        {selectedNurse.email ? <span>{selectedNurse.email}</span> : null}
                      </div>
                    </div>
                  ) : null}
                </div>
              </div>

              <div className="task-modal-actions">
                <button
                  type="button"
                  className="task-secondary-btn"
                  onClick={resetFormState}
                  disabled={loading}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="task-primary-btn"
                  disabled={loading}
                >
                  {loading
                    ? selectedTask
                      ? "Updating..."
                      : "Creating..."
                    : selectedTask
                    ? "Update Task"
                    : "Create Task"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showDeleteModal && (
        <div className="task-modal-backdrop">
          <div className="task-modal delete-modal">
            <h3>Delete Task</h3>
            <p>
              Are you sure you want to delete{" "}
              <strong>{selectedTask?.description}</strong>?
            </p>

            <div className="task-modal-actions">
              <button
                className="task-secondary-btn"
                onClick={() => setShowDeleteModal(false)}
                disabled={loading}
                type="button"
              >
                Cancel
              </button>
              <button
                className="task-danger-btn"
                onClick={confirmDelete}
                disabled={loading}
                type="button"
              >
                {loading ? "Deleting..." : "Delete"}
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  );
}