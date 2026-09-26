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
  ArrowUp,
  ArrowDown,
  ArrowUpDown,
  AlertTriangle,
} from "lucide-react";
import {
  createTask,
  updateTask,
  deleteTask,
  getAdminTasks,
} from "../services/taskService";
import {
  getAdminPatients,
  getCaretakers,
  getNurses,
} from "../services/taskLookupService";
import SuccessCelebrationOverlay from "../components/common/SuccessCelebrationOverlay";
import AlertDemoTrigger from "../components/common/alerts/AlertDemoTrigger";
import "./TaskManagementPage.css";

// Backend status values use a space ("in progress"); the frontend UI/CSS
// classes use a hyphen ("in-progress"). These two helpers convert between
// them so nothing else in this file has to worry about the mismatch.
function toFrontendStatus(status) {
  if (status === "in progress") return "in-progress";
  return status || "pending";
}

// Reverse of toFrontendStatus — the backend expects "in progress" with a
// space, while the frontend UI/CSS classes use "in-progress" with a hyphen.
function toBackendStatus(status) {
  if (status === "in-progress") return "in progress";
  return status || "pending";
}

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
  const [tasks, setTasks] = useState([]);
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
  const [tasksLoading, setTasksLoading] = useState(false);
  // Tracks the _id of the task whose status dropdown is currently saving,
  // so only that row shows a "Saving..." state instead of disabling the
  // whole table like the create/edit `loading` flag does.
  const [statusUpdatingId, setStatusUpdatingId] = useState(null);
  const [errorMessage, setErrorMessage] = useState("");
  const [successOverlayOpen, setSuccessOverlayOpen] = useState(false);
  const [successOverlayMessage, setSuccessOverlayMessage] = useState("");

  // --- Filter state ---
  const [showFilterModal, setShowFilterModal] = useState(false);
  const [statusFilter, setStatusFilter] = useState("");
  const [priorityFilter, setPriorityFilter] = useState("");
  const [assigneeFilter, setAssigneeFilter] = useState("");

  // --- Sort state ---
  // sortField is one of: "" (no sort), "dueDate", "priority"
  const [sortField, setSortField] = useState("");
  const [sortDirection, setSortDirection] = useState("asc");

  const todayDate = getTodayDateString();

  // Maps a task record from GET /api/v1/tasks into the shape the table/form
  // use. `patientList` is passed in explicitly (rather than read from state)
  // because the backend's task list only returns the patient's fullname —
  // not their date of birth — so DOB is cross-referenced from the patient
  // lookup list we already fetch for the form dropdown.
  function mapTaskFromApi(item, patientList) {
    const patientMatch = patientList.find((p) => p.id === item.patient?._id);
    // NOTE: the backend Task model only has a single `assignee` field —
    // there's no separate caretaker/nurse on the API side. Until that's
    // resolved with the backend team, the same assignee is shown in both
    // the "Caretaker" and "Nurse" slots so the existing table/column logic
    // (which falls back from nurse -> caretaker) keeps working.
    const assigneeName = item.assignee?.fullname || "-";

    return {
      _id: item._id,
      description: item.description || "",
      dueDate: item.dueDate ? item.dueDate.slice(0, 10) : "",
      priority: item.priority || "medium",
      status: toFrontendStatus(item.status),
      patient: item.patient?.fullname || "-",
      patientId: item.patient?._id || "",
      patientDob: patientMatch?.dob || "",
      caretaker: assigneeName,
      caretakerId: item.assignee?._id || "",
      caretakerEmail: item.assignee?.email || "",
      nurse_id: assigneeName,
      nurseId: item.assignee?._id || "",
      nurseEmail: item.assignee?.email || "",
      report: item.report || "",
      created_at: item.created_at || "",
      updated_at: item.updated_at || "",
    };
  }

  const loadTasks = async (patientList) => {
    setTasksLoading(true);
    try {
      const result = await getAdminTasks();
      const items = result?.items || [];
      setTasks(items.map((item) => mapTaskFromApi(item, patientList || patients)));
    } catch (err) {
      setErrorMessage(
        err?.response?.data?.message || err?.message || "Failed to load tasks."
      );
    } finally {
      setTasksLoading(false);
    }
  };

  useEffect(() => {
    async function loadLookupsAndTasks() {
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

        const normalizedPatients = patientList.map(normalizePatient);

        setPatients(normalizedPatients);
        setCaretakers(caretakerList.map(normalizeCaretaker));
        setNurses(nurseList.map(normalizeNurse));

        // Pass the freshly-fetched patient list directly since setPatients()
        // above won't have updated state yet on this render.
        await loadTasks(normalizedPatients);
      } catch (err) {
        setErrorMessage(
          err?.response?.data?.message ||
            "Failed to load patient, caretaker, and nurse options."
        );
      } finally {
        setLookupLoading(false);
      }
    }

    loadLookupsAndTasks();
  }, []);

  // Assignee shown in the "Assigned Staff" column: nurse if present, else caretaker.
  const getAssigneeLabel = (task) =>
    task.nurse_id && task.nurse_id !== "-" ? task.nurse_id : task.caretaker;

  // Unique list of assignees currently present in the task list, used to populate the filter.
  const assigneeOptions = useMemo(() => {
    const set = new Set();
    tasks.forEach((task) => {
      const assignee = getAssigneeLabel(task);
      if (assignee && assignee !== "-") set.add(assignee);
    });
    return Array.from(set).sort();
  }, [tasks]);

  const hasActiveFilters = Boolean(
    statusFilter || priorityFilter || assigneeFilter
  );

  const filteredTasks = useMemo(() => {
    return tasks.filter((task) => {
      const combined =
        `${task.description} ${task.patient} ${task.patientDob} ${task.caretaker} ${task.nurse_id} ${task.priority} ${task.status}`.toLowerCase();
      const matchesSearch = combined.includes(searchTerm.toLowerCase());
      const matchesStatus = !statusFilter || task.status === statusFilter;
      const matchesPriority =
        !priorityFilter || task.priority === priorityFilter;
      const matchesAssignee =
        !assigneeFilter || getAssigneeLabel(task) === assigneeFilter;

      return (
        matchesSearch && matchesStatus && matchesPriority && matchesAssignee
      );
    });
  }, [tasks, searchTerm, statusFilter, priorityFilter, assigneeFilter]);

  // A task is overdue if its due date has passed and it hasn't been completed.
  const isTaskOverdue = (task) =>
    Boolean(task.dueDate) &&
    task.dueDate < todayDate &&
    task.status !== "completed";

  const PRIORITY_RANK = { high: 0, medium: 1, low: 2 };

  const sortedTasks = useMemo(() => {
    if (!sortField) return filteredTasks;

    const sorted = [...filteredTasks].sort((a, b) => {
      let result = 0;

      if (sortField === "dueDate") {
        // Empty due dates sort to the end regardless of direction.
        if (!a.dueDate && !b.dueDate) result = 0;
        else if (!a.dueDate) result = 1;
        else if (!b.dueDate) result = -1;
        else result = a.dueDate.localeCompare(b.dueDate);
      } else if (sortField === "priority") {
        result =
          (PRIORITY_RANK[a.priority] ?? 3) - (PRIORITY_RANK[b.priority] ?? 3);
      }

      return sortDirection === "asc" ? result : -result;
    });

    return sorted;
  }, [filteredTasks, sortField, sortDirection]);

  const handleSortClick = (field) => {
    if (sortField !== field) {
      setSortField(field);
      setSortDirection("asc");
      return;
    }
    // Clicking the same column again flips direction; a third click clears the sort.
    if (sortDirection === "asc") {
      setSortDirection("desc");
    } else {
      setSortField("");
      setSortDirection("asc");
    }
  };

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

  const clearFilters = () => {
    setStatusFilter("");
    setPriorityFilter("");
    setAssigneeFilter("");
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
      // Backend requires `title` and a single `assigneeId` (see Task.js —
      // there's no separate caretaker/nurse field). Until that's resolved
      // with the backend team, whichever of Nurse/Caretaker is picked is
      // sent as the assignee, preferring Nurse if both are set.
      const payload = {
        title: form.description,
        description: form.description,
        patientId: form.patientId,
        dueDate: form.dueDate,
        assigneeId: form.nurseId || form.caretakerId || undefined,
        priority: form.priority,
      };

      const result = await createTask(payload);
      await loadTasks();
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
    // Prefer matching by the ID stored on the task record. Falling back to
    // matching by display name only covers legacy/mock records that predate
    // storing IDs — name matching alone is what caused the dropdowns to
    // come up empty/unselected whenever a name didn't line up exactly or
    // the lookup lists hadn't loaded yet.
    const matchedPatient =
      patients.find((p) => p.id === task.patientId) ||
      patients.find((p) => p.name === task.patient);
    const matchedCaretaker =
      caretakers.find((c) => c.id === task.caretakerId) ||
      caretakers.find((c) => c.name === task.caretaker);
    const matchedNurse =
      nurses.find((n) => n.id === task.nurseId) ||
      nurses.find((n) => n.name === task.nurse_id);

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
        title: form.description,
        description: form.description,
        dueDate: form.dueDate,
        assigneeId: form.nurseId || form.caretakerId || undefined,
        priority: form.priority,
      };

      const result = await updateTask(selectedTask._id, payload);
      await loadTasks();
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

  // Quick one-click status change directly from the table row, so marking
  // a task "Completed" (or any other status) doesn't require opening the
  // full Edit modal. Sends only { status } — the backend applies a partial
  // update and leaves every other field on the task untouched.
  const handleStatusChange = async (task, newFrontendStatus) => {
    if (newFrontendStatus === task.status) return;

    setStatusUpdatingId(task._id);
    setErrorMessage("");

    try {
      const result = await updateTask(task._id, {
        status: toBackendStatus(newFrontendStatus),
      });
      await loadTasks();
      showSuccessOverlay(result?.message || "Task status updated.");
    } catch (err) {
      setErrorMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Failed to update task status."
      );
    } finally {
      setStatusUpdatingId(null);
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
      await loadTasks();
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

      {/* TEMPORARY demo — remove once Kartik's real alert-detection service
          sends the actual Socket.IO event to trigger these automatically. */}
      <AlertDemoTrigger />

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

        <button
          className="task-filter-btn"
          type="button"
          onClick={() => setShowFilterModal(true)}
        >
          <Filter size={16} />
          {hasActiveFilters
            ? `Filter (${
                [statusFilter, priorityFilter, assigneeFilter].filter(Boolean)
                  .length
              })`
            : "Filter"}
        </button>
      </div>

      {showFilterModal && (
        <div className="task-modal-backdrop">
          <div className="task-modal">
            <h3>Filter Tasks</h3>

            <div className="task-form-grid" style={{ marginTop: 18 }}>
              <div className="task-form-field">
                <label>Status</label>
                <div className="task-select-shell">
                  <ClipboardList size={16} />
                  <select
                    value={statusFilter}
                    onChange={(e) => setStatusFilter(e.target.value)}
                  >
                    <option value="">All statuses</option>
                    <option value="pending">Pending</option>
                    <option value="in-progress">In Progress</option>
                    <option value="completed">Completed</option>
                  </select>
                </div>
              </div>

              <div className="task-form-field">
                <label>Priority</label>
                <div className="task-select-shell">
                  <ClipboardList size={16} />
                  <select
                    value={priorityFilter}
                    onChange={(e) => setPriorityFilter(e.target.value)}
                  >
                    <option value="">All priorities</option>
                    <option value="high">High</option>
                    <option value="medium">Medium</option>
                    <option value="low">Low</option>
                  </select>
                </div>
              </div>

              <div className="task-form-field">
                <label>Assignee</label>
                <div className="task-select-shell">
                  <UserRound size={16} />
                  <select
                    value={assigneeFilter}
                    onChange={(e) => setAssigneeFilter(e.target.value)}
                  >
                    <option value="">All assignees</option>
                    {assigneeOptions.map((name) => (
                      <option key={name} value={name}>
                        {name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>

            <div className="task-modal-actions">
              <button
                type="button"
                className="task-secondary-btn"
                onClick={clearFilters}
                disabled={!hasActiveFilters}
              >
                Clear filters
              </button>
              <button
                type="button"
                className="task-primary-btn"
                onClick={() => setShowFilterModal(false)}
              >
                Done
              </button>
            </div>
          </div>
        </div>
      )}

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
                <th
                  className="task-sortable-th"
                  onClick={() => handleSortClick("dueDate")}
                >
                  <span className="task-th-label">
                    Due Date
                    {sortField === "dueDate" ? (
                      sortDirection === "asc" ? (
                        <ArrowUp size={13} />
                      ) : (
                        <ArrowDown size={13} />
                      )
                    ) : (
                      <ArrowUpDown size={13} className="task-sort-idle" />
                    )}
                  </span>
                </th>
                <th
                  className="task-sortable-th"
                  onClick={() => handleSortClick("priority")}
                >
                  <span className="task-th-label">
                    Priority
                    {sortField === "priority" ? (
                      sortDirection === "asc" ? (
                        <ArrowUp size={13} />
                      ) : (
                        <ArrowDown size={13} />
                      )
                    ) : (
                      <ArrowUpDown size={13} className="task-sort-idle" />
                    )}
                  </span>
                </th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {sortedTasks.length > 0 ? (
                sortedTasks.map((task) => {
                  const overdue = isTaskOverdue(task);
                  return (
                    <tr key={task._id} className={overdue ? "task-row-overdue" : ""}>
                      <td>{task.description}</td>
                      <td>{task.patient}</td>
                      <td>{formatDateOfBirth(task.patientDob)}</td>
                      <td>{getAssigneeLabel(task)}</td>
                      <td>
                        <span className={overdue ? "task-due-date-overdue" : ""}>
                          {formatTableDate(task.dueDate)}
                        </span>
                        {overdue ? (
                          <span className="task-overdue-badge">
                            <AlertTriangle size={12} />
                            Overdue
                          </span>
                        ) : null}
                      </td>
                      <td>
                        <span className={`task-pill ${getPriorityClass(task.priority)}`}>
                          {formatPriorityLabel(task.priority)}
                        </span>
                      </td>
                      <td>
                        <select
                          className={`task-pill task-status-select ${getStatusClass(task.status)}`}
                          value={task.status}
                          onChange={(e) => handleStatusChange(task, e.target.value)}
                          disabled={statusUpdatingId === task._id}
                          aria-label={`Change status for ${task.description}`}
                        >
                          <option value="pending">Pending</option>
                          <option value="in-progress">In Progress</option>
                          <option value="completed">Completed</option>
                        </select>
                        {statusUpdatingId === task._id ? (
                          <span className="task-status-saving">Saving...</span>
                        ) : null}
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
                  );
                })
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