import { useEffect, useMemo, useState } from "react";
import {
  Search,
  Filter,
  Plus,
  CalendarDays,
  Clock3,
  UserRound,
  Phone,
  Mail,
  ClipboardList,
  Eye,
  Pencil,
  X,
} from "lucide-react";
import api from "../services/api";
import "./NurseRoasterPage.css";

const initialNurses = [
  {
    id: "NUR-001",
    sourceId: "mock-nurse-001",
    name: "Emily Stone",
    email: "emily.stone@guardian.com",
    phone: "+61 412 458 221",
    shift: "Morning",
    ward: "Ward A",
    status: "On Duty",
    assignedPatients: 5,
    nextShift: "16 May 2026, 7:00 AM",
  },
  {
    id: "NUR-002",
    sourceId: "mock-nurse-002",
    name: "Ava Lee",
    email: "ava.lee@guardian.com",
    phone: "+61 431 225 198",
    shift: "Evening",
    ward: "Ward B",
    status: "Off Duty",
    assignedPatients: 3,
    nextShift: "16 May 2026, 3:00 PM",
  },
  {
    id: "NUR-003",
    sourceId: "mock-nurse-003",
    name: "Nurse Emily",
    email: "nurse.emily@guardian.com",
    phone: "+61 490 785 640",
    shift: "Night",
    ward: "Ward C",
    status: "On Leave",
    assignedPatients: 0,
    nextShift: "18 May 2026, 11:00 PM",
  },
  {
    id: "NUR-004",
    sourceId: "mock-nurse-004",
    name: "Sophia Turner",
    email: "sophia.turner@guardian.com",
    phone: "+61 402 118 534",
    shift: "Morning",
    ward: "Ward A",
    status: "On Duty",
    assignedPatients: 4,
    nextShift: "16 May 2026, 7:00 AM",
  },
];

const initialFormData = {
  nurseId: "",
  shift: "Morning",
  ward: "",
  status: "On Duty",
  nextShift: "",
};

const formatNextShift = (dateValue) => {
  const date = new Date(dateValue);

  if (Number.isNaN(date.getTime())) {
    return dateValue;
  }

  return date.toLocaleString("en-AU", {
    day: "numeric",
    month: "short",
    year: "numeric",
    hour: "numeric",
    minute: "2-digit",
  });
};

export default function NurseRoasterPage() {
  const [rosterNurses, setRosterNurses] = useState(initialNurses);

  const [searchTerm, setSearchTerm] = useState("");
  const [selectedShift, setSelectedShift] = useState("All");
  const [selectedStatus, setSelectedStatus] = useState("All");

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [availableNurses, setAvailableNurses] = useState([]);
  const [isLoadingNurses, setIsLoadingNurses] = useState(false);
  const [nurseFetchError, setNurseFetchError] = useState("");
  const [formError, setFormError] = useState("");
  const [formData, setFormData] = useState(initialFormData);

  useEffect(() => {
    if (!isModalOpen) {
      return;
    }

    const fetchAvailableNurses = async () => {
      setIsLoadingNurses(true);
      setNurseFetchError("");

      try {
        const response = await api.get("nurse/all", {
          params: {
            page: 1,
            limit: 100,
          },
        });

        const nurses = Array.isArray(response.data?.nurses)
          ? response.data.nurses
          : [];

        setAvailableNurses(nurses);

        if (nurses.length === 0) {
          setNurseFetchError(
            "No registered nurses are currently available."
          );
        }
      } catch (error) {
        console.error(
          "Unable to fetch nurses:",
          error.response?.data || error.message
        );

        setNurseFetchError(
          error.response?.data?.error ||
            "Unable to load nurses. Please try again."
        );
      } finally {
        setIsLoadingNurses(false);
      }
    };

    fetchAvailableNurses();
  }, [isModalOpen]);

  const filteredNurses = useMemo(() => {
    return rosterNurses.filter((nurse) => {
      const matchesSearch =
        `${nurse.name} ${nurse.email} ${nurse.phone} ${nurse.ward}`
          .toLowerCase()
          .includes(searchTerm.trim().toLowerCase());

      const matchesShift =
        selectedShift === "All" || nurse.shift === selectedShift;

      const matchesStatus =
        selectedStatus === "All" || nurse.status === selectedStatus;

      return matchesSearch && matchesShift && matchesStatus;
    });
  }, [
    rosterNurses,
    searchTerm,
    selectedShift,
    selectedStatus,
  ]);

  const onDutyCount = rosterNurses.filter(
    (nurse) => nurse.status === "On Duty"
  ).length;

  const offDutyCount = rosterNurses.filter(
    (nurse) => nurse.status === "Off Duty"
  ).length;

  const onLeaveCount = rosterNurses.filter(
    (nurse) => nurse.status === "On Leave"
  ).length;

  const selectedNurse = availableNurses.find(
    (nurse) => nurse._id === formData.nurseId
  );

  const openModal = () => {
    setFormData(initialFormData);
    setFormError("");
    setNurseFetchError("");
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setFormData(initialFormData);
    setFormError("");
    setNurseFetchError("");
  };

  const handleFormChange = (event) => {
    const { name, value } = event.target;

    setFormData((currentFormData) => ({
      ...currentFormData,
      [name]: value,
    }));

    setFormError("");
  };

  const handleAddNurse = (event) => {
    event.preventDefault();
    setFormError("");

    if (!formData.nurseId) {
      setFormError("Please select a nurse.");
      return;
    }

    if (!formData.ward.trim()) {
      setFormError("Please enter a ward.");
      return;
    }

    if (!formData.nextShift) {
      setFormError("Please select the next shift date and time.");
      return;
    }

    if (!selectedNurse) {
      setFormError("The selected nurse could not be found.");
      return;
    }

    const nurseAlreadyAdded = rosterNurses.some(
      (nurse) =>
        nurse.sourceId === selectedNurse._id ||
        nurse.email.toLowerCase() ===
          selectedNurse.email.toLowerCase()
    );

    if (nurseAlreadyAdded) {
      setFormError(
        "This nurse has already been added to the roster."
      );
      return;
    }

    const newRosterNurse = {
      id: `NUR-${String(rosterNurses.length + 1).padStart(
        3,
        "0"
      )}`,
      sourceId: selectedNurse._id,
      name: selectedNurse.fullname,
      email: selectedNurse.email,
      phone: selectedNurse.phone || "No phone available",
      shift: formData.shift,
      ward: formData.ward.trim(),
      status: formData.status,
      assignedPatients: Array.isArray(
        selectedNurse.assignedPatients
      )
        ? selectedNurse.assignedPatients.length
        : 0,
      nextShift: formatNextShift(formData.nextShift),
    };

    setRosterNurses((currentRoster) => [
      ...currentRoster,
      newRosterNurse,
    ]);

    closeModal();
  };

  const getStatusClass = (status) => {
    if (status === "On Duty") {
      return "nurse-status on-duty";
    }

    if (status === "Off Duty") {
      return "nurse-status off-duty";
    }

    return "nurse-status on-leave";
  };

  return (
    <section className="nurse-roaster-page">
      <div className="nurse-roaster-header">
        <div>
          <p className="nurse-roaster-eyebrow">
            Guardian Monitor Admin
          </p>

          <h1>Nurse Roster</h1>

          <p className="nurse-roaster-subtitle">
            Review nurse availability, assigned workload, and
            shift details from the admin workspace.
          </p>
        </div>

        <button
          className="nurse-primary-btn"
          type="button"
          onClick={openModal}
        >
          <Plus size={18} />
          Add Nurse
        </button>
      </div>

      <div className="nurse-summary-grid">
        <div className="nurse-summary-card">
          <UserRound size={18} />

          <div>
            <strong>{rosterNurses.length}</strong>
            <span>Total Nurses</span>
          </div>
        </div>

        <div className="nurse-summary-card">
          <Clock3 size={18} />

          <div>
            <strong>{onDutyCount}</strong>
            <span>On Duty</span>
          </div>
        </div>

        <div className="nurse-summary-card">
          <CalendarDays size={18} />

          <div>
            <strong>{offDutyCount}</strong>
            <span>Off Duty</span>
          </div>
        </div>

        <div className="nurse-summary-card">
          <ClipboardList size={18} />

          <div>
            <strong>{onLeaveCount}</strong>
            <span>On Leave</span>
          </div>
        </div>
      </div>

      <div className="nurse-toolbar">
        <div className="nurse-search-box">
          <Search size={16} />

          <input
            type="text"
            placeholder="Search by nurse, ward, email, or phone..."
            value={searchTerm}
            onChange={(event) =>
              setSearchTerm(event.target.value)
            }
          />
        </div>

        <div className="nurse-filter-group">
          <div className="nurse-filter-select">
            <Filter size={16} />

            <select
              value={selectedShift}
              onChange={(event) =>
                setSelectedShift(event.target.value)
              }
            >
              <option value="All">All Shifts</option>
              <option value="Morning">Morning</option>
              <option value="Evening">Evening</option>
              <option value="Night">Night</option>
            </select>
          </div>

          <div className="nurse-filter-select">
            <Filter size={16} />

            <select
              value={selectedStatus}
              onChange={(event) =>
                setSelectedStatus(event.target.value)
              }
            >
              <option value="All">All Status</option>
              <option value="On Duty">On Duty</option>
              <option value="Off Duty">Off Duty</option>
              <option value="On Leave">On Leave</option>
            </select>
          </div>
        </div>
      </div>

      <div className="nurse-roaster-card">
        <div className="nurse-roaster-card-header">
          <h3>Roster List</h3>

          <p>
            Current nurse records prepared in the admin dashboard
            interface.
          </p>
        </div>

        <div className="nurse-roaster-table-wrap">
          <table className="nurse-roaster-table">
            <thead>
              <tr>
                <th>Nurse</th>
                <th>Contact</th>
                <th>Shift</th>
                <th>Ward</th>
                <th>Patients</th>
                <th>Status</th>
                <th>Next Shift</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {filteredNurses.length > 0 ? (
                filteredNurses.map((nurse) => (
                  <tr key={nurse.id}>
                    <td>
                      <div className="nurse-name-cell">
                        <div className="nurse-avatar">
                          {nurse.name.charAt(0).toUpperCase()}
                        </div>

                        <div>
                          <strong>{nurse.name}</strong>
                          <span>{nurse.id}</span>
                        </div>
                      </div>
                    </td>

                    <td>
                      <div className="nurse-contact-cell">
                        <span>
                          <Mail size={14} />
                          {nurse.email}
                        </span>

                        <span>
                          <Phone size={14} />
                          {nurse.phone}
                        </span>
                      </div>
                    </td>

                    <td>{nurse.shift}</td>
                    <td>{nurse.ward}</td>
                    <td>{nurse.assignedPatients}</td>

                    <td>
                      <span
                        className={getStatusClass(nurse.status)}
                      >
                        {nurse.status}
                      </span>
                    </td>

                    <td>{nurse.nextShift}</td>

                    <td>
                      <div className="nurse-actions">
                        <button
                          className="nurse-icon-btn"
                          type="button"
                          aria-label={`View ${nurse.name}`}
                        >
                          <Eye size={16} />
                        </button>

                        <button
                          className="nurse-icon-btn"
                          type="button"
                          aria-label={`Edit ${nurse.name}`}
                        >
                          <Pencil size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td
                    colSpan="8"
                    className="nurse-empty-state"
                  >
                    No nurse records found for the selected
                    filters.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {isModalOpen && (
        <div
          className="nurse-modal-overlay"
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) {
              closeModal();
            }
          }}
        >
          <div
            className="nurse-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="add-nurse-modal-title"
          >
            <div className="nurse-modal-header">
              <div>
                <h2 id="add-nurse-modal-title">
                  Add Nurse to Roster
                </h2>

                <p>
                  Select an existing nurse and enter their roster
                  details.
                </p>
              </div>

              <button
                className="nurse-modal-close"
                type="button"
                onClick={closeModal}
                aria-label="Close add nurse modal"
              >
                <X size={20} />
              </button>
            </div>

            <form
              className="nurse-modal-form"
              onSubmit={handleAddNurse}
            >
              <div className="nurse-form-field">
                <label htmlFor="nurseId">Nurse</label>

                <select
                  id="nurseId"
                  name="nurseId"
                  value={formData.nurseId}
                  onChange={handleFormChange}
                  disabled={isLoadingNurses}
                >
                  <option value="">
                    {isLoadingNurses
                      ? "Loading nurses..."
                      : "Select a nurse"}
                  </option>

                  {availableNurses.map((nurse) => (
                    <option
                      key={nurse._id}
                      value={nurse._id}
                    >
                      {nurse.fullname} — {nurse.email}
                    </option>
                  ))}
                </select>
              </div>

              {selectedNurse && (
                <div className="nurse-selected-preview">
                  <strong>{selectedNurse.fullname}</strong>
                  <span>{selectedNurse.email}</span>
                  <span>
                    {selectedNurse.phone ||
                      "No phone available"}
                  </span>
                </div>
              )}

              <div className="nurse-form-grid">
                <div className="nurse-form-field">
                  <label htmlFor="shift">Shift</label>

                  <select
                    id="shift"
                    name="shift"
                    value={formData.shift}
                    onChange={handleFormChange}
                  >
                    <option value="Morning">Morning</option>
                    <option value="Evening">Evening</option>
                    <option value="Night">Night</option>
                  </select>
                </div>

                <div className="nurse-form-field">
                  <label htmlFor="status">Status</label>

                  <select
                    id="status"
                    name="status"
                    value={formData.status}
                    onChange={handleFormChange}
                  >
                    <option value="On Duty">On Duty</option>
                    <option value="Off Duty">Off Duty</option>
                    <option value="On Leave">On Leave</option>
                  </select>
                </div>
              </div>

              <div className="nurse-form-field">
                <label htmlFor="ward">Ward</label>

                <input
                  id="ward"
                  name="ward"
                  type="text"
                  placeholder="For example, Ward A"
                  value={formData.ward}
                  onChange={handleFormChange}
                />
              </div>

              <div className="nurse-form-field">
                <label htmlFor="nextShift">
                  Next shift
                </label>

                <input
                  id="nextShift"
                  name="nextShift"
                  type="datetime-local"
                  value={formData.nextShift}
                  onChange={handleFormChange}
                />
              </div>

              {nurseFetchError && (
                <p className="nurse-form-error" role="alert">
                  {nurseFetchError}
                </p>
              )}

              {formError && (
                <p className="nurse-form-error" role="alert">
                  {formError}
                </p>
              )}

              <div className="nurse-modal-actions">
                <button
                  className="nurse-secondary-btn"
                  type="button"
                  onClick={closeModal}
                >
                  Cancel
                </button>

                <button
                  className="nurse-primary-btn"
                  type="submit"
                  disabled={
                    isLoadingNurses ||
                    availableNurses.length === 0
                  }
                >
                  <Plus size={18} />
                  Add to Roster
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </section>
  );
}