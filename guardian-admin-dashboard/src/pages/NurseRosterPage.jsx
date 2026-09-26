import { useEffect, useMemo, useState } from "react";
import {
  Search,
  Filter,
  Plus,
  Clock3,
  UserRound,
  Phone,
  Mail,
  ClipboardList,
  Pencil,
  X,
  LogIn,
  LogOut,
  Stethoscope,
  UserX,
  HeartHandshake,
  Trash2,
} from "lucide-react";

import {
  getStaff,
  deactivateStaff,
} from "../services/staffService";

import {
  getRosters,
  createRoster,
  updateRoster,
  deleteRoster,
  clockOn,
  clockOff,
} from "../services/rosterService";

import "./NurseRoasterPage.css";

const initialShiftFormData = {
  assignedStaffId: "",
  shiftId: "",
  location: "",
  room: "",
  description: "",
  generalNotes: "",
  date: "",
  startTime: "08:00",
  endTime: "16:00",
};

const initialEditFormData = {
  location: "",
  room: "",
  description: "",
  generalNotes: "",
  date: "",
  startTime: "",
  endTime: "",
  assignedStaffId: "",
};

const getRoleName = (role) => {
  if (typeof role === "string") {
    return role.toLowerCase();
  }

  return (role?.name || "").toLowerCase();
};

const getDisplayRole = (role) => {
  if (role === "doctor") return "Doctor";
  if (role === "nurse") return "Nurse";
  if (role === "caretaker") return "Caretaker";
  return "Staff";
};

const getShiftCategory = (startTime) => {
  if (!startTime) return "Not assigned";

  const hour = Number(startTime.split(":")[0]);

  if (Number.isNaN(hour)) return "Not assigned";
  if (hour >= 5 && hour < 12) return "Morning";
  if (hour >= 12 && hour < 20) return "Evening";
  return "Night";
};

const getShiftDate = (shift) => {
  if (!shift?.date || !shift?.startTime) {
    return null;
  }

  const date = new Date(`${shift.date}T${shift.startTime}:00`);

  if (Number.isNaN(date.getTime())) {
    return null;
  }

  return date;
};

const formatShiftDateTime = (shift) => {
  const date = getShiftDate(shift);

  if (!date) {
    return "Not scheduled";
  }

  return date.toLocaleString("en-AU", {
    day: "numeric",
    month: "short",
    year: "numeric",
    hour: "numeric",
    minute: "2-digit",
  });
};

const formatClockTime = (dateValue) => {
  if (!dateValue) {
    return "";
  }

  const date = new Date(dateValue);

  if (Number.isNaN(date.getTime())) {
    return "";
  }

  return date.toLocaleString("en-AU", {
    day: "numeric",
    month: "short",
    hour: "numeric",
    minute: "2-digit",
  });
};

const sortShifts = (shifts) => {
  return [...shifts].sort((a, b) => {
    const first = getShiftDate(a)?.getTime() ?? 0;
    const second = getShiftDate(b)?.getTime() ?? 0;
    return first - second;
  });
};

const normaliseStaffMember = (
  staffMember,
  shifts = [],
  index = 0
) => {
  const role = getRoleName(staffMember.role);
  const sortedShifts = sortShifts(shifts);
  const now = Date.now();

  const activeShift =
    sortedShifts.find(
      (shift) =>
        Boolean(shift.clockOnTime) &&
        !shift.clockOffTime
    ) || null;

  const nextShiftRecord =
    sortedShifts.find((shift) => {
      const shiftDate = getShiftDate(shift);

      return (
        shiftDate &&
        shiftDate.getTime() >= now &&
        !shift.clockOffTime
      );
    }) || null;

  const mostRecentShift =
    [...sortedShifts]
      .reverse()
      .find((shift) => getShiftDate(shift)) || null;

  const displayShift =
    activeShift ||
    nextShiftRecord ||
    mostRecentShift;

  const shiftCategory = displayShift
    ? getShiftCategory(displayShift.startTime)
    : "Not assigned";

  const shift =
    displayShift
      ? `${shiftCategory} (${displayShift.startTime}–${displayShift.endTime})`
      : "Not assigned";

  const ward = displayShift
    ? [displayShift.location, displayShift.room]
        .filter(Boolean)
        .join(" — ")
    : "Unassigned";

  return {
    id: staffMember._id || `STAFF-${index + 1}`,
    sourceId: staffMember._id,
    isRosterOnly: Boolean(staffMember.isRosterOnly),
    name:
      staffMember.fullname ||
      staffMember.name ||
      "Unnamed Staff Member",
    email:
      staffMember.email ||
      "No email available",
    phone:
      staffMember.phone ||
      "No phone available",
    role,
    organization:
      staffMember.organization?.name ||
      "No organisation available",
    assignedPatients: Array.isArray(
      staffMember.assignedPatients
    )
      ? staffMember.assignedPatients.length
      : 0,
    shifts: sortedShifts,
    currentShift: displayShift,
    activeShift,
    nextShiftRecord,
    shiftCategory,
    shift,
    ward,
    status: activeShift
      ? "On Duty"
      : "Off Duty",
    nextShift: nextShiftRecord
      ? formatShiftDateTime(nextShiftRecord)
      : "Not scheduled",
    clockedIn: Boolean(activeShift),
    clockInTime:
      activeShift?.clockOnTime ||
      displayShift?.clockOnTime ||
      null,
    clockOutTime:
      displayShift?.clockOffTime ||
      null,
    approvalStatus:
      staffMember.approvalStatus || "",
    isActive:
      staffMember.isActive ??
      staffMember.active ??
      true,
  };
};

export default function NurseRoasterPage() {
  const [rosterStaff, setRosterStaff] =
    useState([]);

  const [isLoadingStaff, setIsLoadingStaff] =
    useState(false);

  const [staffFetchError, setStaffFetchError] =
    useState("");

  const [rosterFetchError, setRosterFetchError] =
    useState("");

  const [searchTerm, setSearchTerm] =
    useState("");

  const [selectedRole, setSelectedRole] =
    useState("All");

  const [selectedShift, setSelectedShift] =
    useState("All");

  const [selectedStatus, setSelectedStatus] =
    useState("All");

  const [isModalOpen, setIsModalOpen] =
    useState(false);

  const [formError, setFormError] =
    useState("");

  const [isSubmitting, setIsSubmitting] =
    useState(false);

  const [formData, setFormData] =
    useState(initialShiftFormData);

  const [editingStaff, setEditingStaff] =
    useState(null);

  const [editingShift, setEditingShift] =
    useState(null);

  const [editFormData, setEditFormData] =
    useState(initialEditFormData);

  const [isEditModalOpen, setIsEditModalOpen] =
    useState(false);

  const [editFormError, setEditFormError] =
    useState("");

  const [isUpdatingShift, setIsUpdatingShift] =
    useState(false);

  const [
    isDeactivatingStaffId,
    setIsDeactivatingStaffId,
  ] = useState("");

  const [clockingShiftId, setClockingShiftId] =
    useState("");

  const [deletingShiftId, setDeletingShiftId] =
    useState("");

  const fetchRosterStaff = async () => {
    setIsLoadingStaff(true);
    setStaffFetchError("");
    setRosterFetchError("");

    try {
      const staffData = await getStaff({
        page: 1,
        limit: 100,
      });

      const staff = Array.isArray(
        staffData?.staff
      )
        ? staffData.staff
        : [];

      let rosterItems = [];

      try {
        const rosterData = await getRosters({
          page: 1,
          limit: 100,
        });

        rosterItems = Array.isArray(
          rosterData?.items
        )
          ? rosterData.items
          : [];
      } catch (error) {
        console.error(
          "Unable to fetch roster shifts:",
          error.response?.data ||
            error.message
        );

        setRosterFetchError(
          error.response?.data?.message ||
            "Staff loaded, but roster shifts could not be loaded."
        );
      }

      const supportedStaff = staff.filter(
        (staffMember) => {
          const role = getRoleName(
            staffMember.role
          );

          return (
            role === "nurse" ||
            role === "doctor" ||
            role === "caretaker"
          );
        }
      );

      /*
       * If the admin staff endpoint does not yet include
       * caretakers, a caretaker assigned to a roster shift
       * can still be shown from the populated roster data.
       */
      const knownStaffIds = new Set(
        supportedStaff.map(
          (staffMember) => staffMember._id
        )
      );

      rosterItems.forEach((shift) => {
        const assignedStaff =
          shift?.assignedStaff;

        const assignedRole = getRoleName(
          assignedStaff?.role
        );

        if (
          assignedStaff?._id &&
          assignedRole === "caretaker" &&
          !knownStaffIds.has(
            assignedStaff._id
          )
        ) {
          supportedStaff.push({
            ...assignedStaff,
            isRosterOnly: true,
          });
          knownStaffIds.add(
            assignedStaff._id
          );
        }
      });

      const shiftsByStaffId = new Map();

      rosterItems.forEach((shift) => {
        const staffId =
          shift?.assignedStaff?._id ||
          shift?.assignedStaff;

        if (!staffId) {
          return;
        }

        if (
          !shiftsByStaffId.has(staffId)
        ) {
          shiftsByStaffId.set(
            staffId,
            []
          );
        }

        shiftsByStaffId
          .get(staffId)
          .push(shift);
      });

      const rosterMembers =
        supportedStaff.map(
          (staffMember, index) =>
            normaliseStaffMember(
              staffMember,
              shiftsByStaffId.get(
                staffMember._id
              ) || [],
              index
            )
        );

      setRosterStaff(rosterMembers);
    } catch (error) {
      console.error(
        "Unable to fetch staff roster:",
        error.response?.data ||
          error.message
      );

      setStaffFetchError(
        error.response?.data?.error ||
          error.response?.data?.message ||
          "Unable to load the staff roster from the backend."
      );

      setRosterStaff([]);
    } finally {
      setIsLoadingStaff(false);
    }
  };

  useEffect(() => {
    fetchRosterStaff();
  }, []);

  const filteredStaff = useMemo(() => {
    return rosterStaff.filter(
      (staffMember) => {
        const searchValue =
          searchTerm
            .trim()
            .toLowerCase();

        const matchesSearch =
          `${staffMember.name} ${staffMember.email} ${staffMember.phone} ${staffMember.ward} ${staffMember.role} ${staffMember.currentShift?.description || ""}`
            .toLowerCase()
            .includes(searchValue);

        const matchesRole =
          selectedRole === "All" ||
          staffMember.role ===
            selectedRole;

        const matchesShift =
          selectedShift === "All" ||
          staffMember.shiftCategory ===
            selectedShift;

        const matchesStatus =
          selectedStatus === "All" ||
          staffMember.status ===
            selectedStatus;

        return (
          matchesSearch &&
          matchesRole &&
          matchesShift &&
          matchesStatus
        );
      }
    );
  }, [
    rosterStaff,
    searchTerm,
    selectedRole,
    selectedShift,
    selectedStatus,
  ]);

  const nurseCount = rosterStaff.filter(
    (staffMember) =>
      staffMember.role === "nurse"
  ).length;

  const doctorCount = rosterStaff.filter(
    (staffMember) =>
      staffMember.role === "doctor"
  ).length;

  const caretakerCount =
    rosterStaff.filter(
      (staffMember) =>
        staffMember.role === "caretaker"
    ).length;

  const onDutyCount = rosterStaff.filter(
    (staffMember) =>
      staffMember.status === "On Duty"
  ).length;

  const offDutyCount = rosterStaff.filter(
    (staffMember) =>
      staffMember.status === "Off Duty"
  ).length;

  const openModal = () => {
    setFormData({
      ...initialShiftFormData,
      shiftId: `SHIFT-${Date.now()}`,
    });
    setFormError("");
    setIsModalOpen(true);
  };

  const closeModal = () => {
    if (isSubmitting) {
      return;
    }

    setIsModalOpen(false);
    setFormData(initialShiftFormData);
    setFormError("");
  };

  const handleFormChange = (event) => {
    const { name, value } =
      event.target;

    setFormData(
      (currentFormData) => ({
        ...currentFormData,
        [name]: value,
      })
    );

    setFormError("");
  };

  const handleCreateShift = async (
    event
  ) => {
    event.preventDefault();
    setFormError("");

    const requiredValues = [
      formData.assignedStaffId,
      formData.shiftId.trim(),
      formData.location.trim(),
      formData.room.trim(),
      formData.description.trim(),
      formData.date,
      formData.startTime,
      formData.endTime,
    ];

    if (
      requiredValues.some(
        (value) => !value
      )
    ) {
      setFormError(
        "Please complete all required shift fields."
      );
      return;
    }

    if (
      formData.endTime ===
      formData.startTime
    ) {
      setFormError(
        "Start time and end time cannot be the same."
      );
      return;
    }

    setIsSubmitting(true);

    try {
      await createRoster({
        shiftId:
          formData.shiftId.trim(),
        location:
          formData.location.trim(),
        room:
          formData.room.trim(),
        description:
          formData.description.trim(),
        generalNotes:
          formData.generalNotes.trim(),
        date: formData.date,
        startTime: formData.startTime,
        endTime: formData.endTime,
        assignedStaffId:
          formData.assignedStaffId,
      });

      await fetchRosterStaff();

      setIsModalOpen(false);
      setFormData(initialShiftFormData);
      setFormError("");
    } catch (error) {
      console.error(
        "Unable to create roster shift:",
        error.response?.data ||
          error.message
      );

      if (
        error.response?.status === 409
      ) {
        setFormError(
          "That Shift ID already exists. Please use a different Shift ID."
        );
      } else if (
        error.response?.status === 403
      ) {
        setFormError(
          "Only an administrator can create roster shifts."
        );
      } else {
        setFormError(
          error.response?.data?.message ||
            error.response?.data?.error ||
            "Unable to create this roster shift."
        );
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClockOn = async (
    staffMember
  ) => {
    const shift =
      staffMember.currentShift;

    if (!shift?.shiftId) {
      window.alert(
        "This staff member does not have a shift to clock on."
      );
      return;
    }

    setClockingShiftId(
      shift.shiftId
    );

    try {
      await clockOn(shift.shiftId);
      await fetchRosterStaff();
    } catch (error) {
      console.error(
        "Unable to clock on:",
        error.response?.data ||
          error.message
      );

      window.alert(
        error.response?.data?.message ||
          "Unable to clock on this shift."
      );
    } finally {
      setClockingShiftId("");
    }
  };

  const handleClockOff = async (
    staffMember
  ) => {
    const shift =
      staffMember.activeShift ||
      staffMember.currentShift;

    if (!shift?.shiftId) {
      window.alert(
        "This staff member does not have an active shift."
      );
      return;
    }

    setClockingShiftId(
      shift.shiftId
    );

    try {
      await clockOff(shift.shiftId);
      await fetchRosterStaff();
    } catch (error) {
      console.error(
        "Unable to clock off:",
        error.response?.data ||
          error.message
      );

      window.alert(
        error.response?.data?.message ||
          "Unable to clock off this shift."
      );
    } finally {
      setClockingShiftId("");
    }
  };

  const openEditModal = (
    staffMember
  ) => {
    const shift =
      staffMember.currentShift;

    if (!shift?.shiftId) {
      window.alert(
        "This staff member does not currently have a roster shift to edit."
      );
      return;
    }

    setEditingStaff(staffMember);
    setEditingShift(shift);

    setEditFormData({
      location:
        shift.location || "",
      room:
        shift.room || "",
      description:
        shift.description || "",
      generalNotes:
        shift.generalNotes || "",
      date:
        shift.date || "",
      startTime:
        shift.startTime || "",
      endTime:
        shift.endTime || "",
      assignedStaffId:
        shift.assignedStaff?._id ||
        shift.assignedStaff ||
        staffMember.sourceId,
    });

    setEditFormError("");
    setIsEditModalOpen(true);
  };

  const closeEditModal = () => {
    if (isUpdatingShift) {
      return;
    }

    setIsEditModalOpen(false);
    setEditingStaff(null);
    setEditingShift(null);
    setEditFormData(
      initialEditFormData
    );
    setEditFormError("");
  };

  const handleEditFormChange = (
    event
  ) => {
    const { name, value } =
      event.target;

    setEditFormData(
      (currentData) => ({
        ...currentData,
        [name]: value,
      })
    );

    setEditFormError("");
  };

  const handleUpdateRoster = async (
    event
  ) => {
    event.preventDefault();

    if (!editingShift?.shiftId) {
      return;
    }

    const requiredValues = [
      editFormData.location.trim(),
      editFormData.room.trim(),
      editFormData.description.trim(),
      editFormData.date,
      editFormData.startTime,
      editFormData.endTime,
      editFormData.assignedStaffId,
    ];

    if (
      requiredValues.some(
        (value) => !value
      )
    ) {
      setEditFormError(
        "Please complete all required shift fields."
      );
      return;
    }

    setIsUpdatingShift(true);

    try {
      await updateRoster(
        editingShift.shiftId,
        {
          location:
            editFormData.location.trim(),
          room:
            editFormData.room.trim(),
          description:
            editFormData.description.trim(),
          generalNotes:
            editFormData.generalNotes.trim(),
          date:
            editFormData.date,
          startTime:
            editFormData.startTime,
          endTime:
            editFormData.endTime,
          assignedStaffId:
            editFormData.assignedStaffId,
        }
      );

      await fetchRosterStaff();
      closeEditModal();
    } catch (error) {
      console.error(
        "Unable to update roster shift:",
        error.response?.data ||
          error.message
      );

      setEditFormError(
        error.response?.data?.message ||
          error.response?.data?.error ||
          "Unable to update this roster shift."
      );
    } finally {
      setIsUpdatingShift(false);
    }
  };

  const handleDeleteShift = async (
    staffMember
  ) => {
    const shift =
      staffMember.currentShift;

    if (!shift?.shiftId) {
      return;
    }

    const confirmed =
      window.confirm(
        `Delete shift ${shift.shiftId} for ${staffMember.name}?`
      );

    if (!confirmed) {
      return;
    }

    setDeletingShiftId(
      shift.shiftId
    );

    try {
      await deleteRoster(
        shift.shiftId
      );
      await fetchRosterStaff();
    } catch (error) {
      console.error(
        "Unable to delete roster shift:",
        error.response?.data ||
          error.message
      );

      window.alert(
        error.response?.data?.message ||
          "Unable to delete this roster shift."
      );
    } finally {
      setDeletingShiftId("");
    }
  };

  const handleDeactivateStaff =
    async (staffMember) => {
      const confirmed =
        window.confirm(
          `Deactivate ${staffMember.name}?`
        );

      if (!confirmed) {
        return;
      }

      setIsDeactivatingStaffId(
        staffMember.sourceId
      );

      try {
        await deactivateStaff(
          staffMember.sourceId
        );

        setRosterStaff(
          (currentRoster) =>
            currentRoster.filter(
              (member) =>
                member.sourceId !==
                staffMember.sourceId
            )
        );
      } catch (error) {
        console.error(
          "Unable to deactivate staff member:",
          error.response?.data ||
            error.message
        );

        window.alert(
          error.response?.data?.error ||
            error.response?.data?.message ||
            "Unable to deactivate this staff member."
        );
      } finally {
        setIsDeactivatingStaffId("");
      }
    };

  const getStatusClass = (status) => {
    if (status === "On Duty") {
      return "nurse-status on-duty";
    }

    return "nurse-status off-duty";
  };

  return (
    <section className="nurse-roaster-page">
      <div className="nurse-roaster-header">
        <div>
          <p className="nurse-roaster-eyebrow">
            Guardian Monitor Admin
          </p>

          <h1>Staff Roster</h1>

          <p className="nurse-roaster-subtitle">
            Review nurse, doctor and caretaker
            workload, roster shifts, work areas,
            and attendance from the admin workspace.
          </p>
        </div>

        <button
          className="nurse-primary-btn"
          type="button"
          onClick={openModal}
        >
          <Plus size={18} />
          Create Shift
        </button>
      </div>

      <div className="nurse-summary-grid">
        <div className="nurse-summary-card">
          <UserRound size={18} />
          <div>
            <strong>
              {rosterStaff.length}
            </strong>
            <span>Total Staff</span>
          </div>
        </div>

        <div className="nurse-summary-card">
          <ClipboardList size={18} />
          <div>
            <strong>
              {nurseCount}
            </strong>
            <span>Nurses</span>
          </div>
        </div>

        <div className="nurse-summary-card">
          <Stethoscope size={18} />
          <div>
            <strong>
              {doctorCount}
            </strong>
            <span>Doctors</span>
          </div>
        </div>

        <div className="nurse-summary-card">
          <HeartHandshake size={18} />
          <div>
            <strong>
              {caretakerCount}
            </strong>
            <span>Caretakers</span>
          </div>
        </div>

        <div className="nurse-summary-card">
          <Clock3 size={18} />
          <div>
            <strong>
              {onDutyCount}
            </strong>
            <span>
              On Duty ({offDutyCount} Off Duty)
            </span>
          </div>
        </div>
      </div>

      {staffFetchError && (
        <p
          className="nurse-form-error"
          role="alert"
        >
          {staffFetchError}
        </p>
      )}

      {rosterFetchError && (
        <p
          className="nurse-form-error"
          role="alert"
        >
          {rosterFetchError}
        </p>
      )}

      <div className="nurse-toolbar">
        <div className="nurse-search-box">
          <Search size={16} />

          <input
            type="text"
            placeholder="Search staff, role, work area, email, phone, or shift..."
            value={searchTerm}
            onChange={(event) =>
              setSearchTerm(
                event.target.value
              )
            }
          />
        </div>

        <div className="nurse-filter-group">
          <div className="nurse-filter-select">
            <Filter size={16} />

            <select
              value={selectedRole}
              onChange={(event) =>
                setSelectedRole(
                  event.target.value
                )
              }
            >
              <option value="All">
                All Roles
              </option>
              <option value="nurse">
                Nurses
              </option>
              <option value="doctor">
                Doctors
              </option>
              <option value="caretaker">
                Caretakers
              </option>
            </select>
          </div>

          <div className="nurse-filter-select">
            <Filter size={16} />

            <select
              value={selectedShift}
              onChange={(event) =>
                setSelectedShift(
                  event.target.value
                )
              }
            >
              <option value="All">
                All Shifts
              </option>
              <option value="Morning">
                Morning
              </option>
              <option value="Evening">
                Evening
              </option>
              <option value="Night">
                Night
              </option>
              <option value="Not assigned">
                Not Assigned
              </option>
            </select>
          </div>

          <div className="nurse-filter-select">
            <Filter size={16} />

            <select
              value={selectedStatus}
              onChange={(event) =>
                setSelectedStatus(
                  event.target.value
                )
              }
            >
              <option value="All">
                All Status
              </option>
              <option value="On Duty">
                On Duty
              </option>
              <option value="Off Duty">
                Off Duty
              </option>
            </select>
          </div>
        </div>
      </div>

      <div className="nurse-roaster-card">
        <div className="nurse-roaster-card-header">
          <h3>Staff Roster List</h3>

          <p>
            Staff information comes from the staff API.
            Shift and attendance information comes from
            the roster API.
          </p>
        </div>

        <div className="nurse-roaster-table-wrap">
          <table className="nurse-roaster-table">
            <thead>
              <tr>
                <th>Staff Member</th>
                <th>Role</th>
                <th>Contact</th>
                <th>Shift</th>
                <th>Work Area</th>
                <th>Patients</th>
                <th>Status</th>
                <th>Next Shift</th>
                <th>Attendance</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {isLoadingStaff ? (
                <tr>
                  <td
                    colSpan="10"
                    className="nurse-empty-state"
                  >
                    Loading staff roster...
                  </td>
                </tr>
              ) : filteredStaff.length > 0 ? (
                filteredStaff.map(
                  (staffMember) => {
                    const shift =
                      staffMember.currentShift;

                    const shiftIsBusy =
                      Boolean(
                        shift?.shiftId &&
                          clockingShiftId ===
                            shift.shiftId
                      );

                    const shiftCompleted =
                      Boolean(
                        shift?.clockOnTime &&
                          shift?.clockOffTime
                      );

                    return (
                      <tr
                        key={
                          staffMember.sourceId
                        }
                      >
                        <td>
                          <div className="nurse-name-cell">
                            <div className="nurse-avatar">
                              {staffMember.name
                                .charAt(0)
                                .toUpperCase()}
                            </div>

                            <div>
                              <strong>
                                {staffMember.name}
                              </strong>

                              <span>
                                {staffMember.sourceId}
                              </span>
                            </div>
                          </div>
                        </td>

                        <td>
                          <strong>
                            {getDisplayRole(
                              staffMember.role
                            )}
                          </strong>
                        </td>

                        <td>
                          <div className="nurse-contact-cell">
                            <span>
                              <Mail size={14} />
                              {staffMember.email}
                            </span>

                            <span>
                              <Phone size={14} />
                              {staffMember.phone}
                            </span>
                          </div>
                        </td>

                        <td>
                          <div className="nurse-contact-cell">
                            <span>
                              {staffMember.shift}
                            </span>

                            {shift?.shiftId && (
                              <span>
                                {shift.shiftId}
                              </span>
                            )}
                          </div>
                        </td>

                        <td>
                          {staffMember.ward}
                        </td>

                        <td>
                          {
                            staffMember.assignedPatients
                          }
                        </td>

                        <td>
                          <span
                            className={getStatusClass(
                              staffMember.status
                            )}
                          >
                            {staffMember.status}
                          </span>
                        </td>

                        <td>
                          {staffMember.nextShift}
                        </td>

                        <td>
                          <div className="nurse-contact-cell">
                            {staffMember.clockedIn ? (
                              <>
                                <span>
                                  <Clock3 size={14} />
                                  Clocked on
                                </span>

                                {staffMember.clockInTime && (
                                  <span>
                                    {formatClockTime(
                                      staffMember.clockInTime
                                    )}
                                  </span>
                                )}
                              </>
                            ) : shiftCompleted ? (
                              <>
                                <span>
                                  <Clock3 size={14} />
                                  Shift completed
                                </span>

                                {staffMember.clockOutTime && (
                                  <span>
                                    {formatClockTime(
                                      staffMember.clockOutTime
                                    )}
                                  </span>
                                )}
                              </>
                            ) : (
                              <span>
                                <Clock3 size={14} />
                                Not clocked on
                              </span>
                            )}
                          </div>
                        </td>

                        <td>
                          <div className="nurse-actions">
                            {shift &&
                              !shiftCompleted &&
                              (staffMember.clockedIn ? (
                                <button
                                  className="nurse-icon-btn"
                                  type="button"
                                  title="Clock Off"
                                  aria-label={`Clock off ${staffMember.name}`}
                                  disabled={
                                    shiftIsBusy
                                  }
                                  onClick={() =>
                                    handleClockOff(
                                      staffMember
                                    )
                                  }
                                >
                                  <LogOut size={16} />
                                </button>
                              ) : (
                                <button
                                  className="nurse-icon-btn"
                                  type="button"
                                  title="Clock On"
                                  aria-label={`Clock on ${staffMember.name}`}
                                  disabled={
                                    shiftIsBusy
                                  }
                                  onClick={() =>
                                    handleClockOn(
                                      staffMember
                                    )
                                  }
                                >
                                  <LogIn size={16} />
                                </button>
                              ))}

                            <button
                              className="nurse-icon-btn"
                              type="button"
                              title={
                                shift
                                  ? "Edit roster shift"
                                  : "No shift available to edit"
                              }
                              aria-label={`Edit shift for ${staffMember.name}`}
                              disabled={!shift}
                              onClick={() =>
                                openEditModal(
                                  staffMember
                                )
                              }
                            >
                              <Pencil size={16} />
                            </button>

                            <button
                              className="nurse-icon-btn"
                              type="button"
                              title={
                                shift
                                  ? "Delete roster shift"
                                  : "No shift available to delete"
                              }
                              aria-label={`Delete shift for ${staffMember.name}`}
                              disabled={
                                !shift ||
                                deletingShiftId ===
                                  shift?.shiftId
                              }
                              onClick={() =>
                                handleDeleteShift(
                                  staffMember
                                )
                              }
                            >
                              <Trash2 size={16} />
                            </button>

                            <button
                              className="nurse-icon-btn nurse-deactivate-btn"
                              type="button"
                              title={
                                staffMember.isRosterOnly
                                  ? "Roster-only staff cannot be deactivated here"
                                  : "Deactivate staff member"
                              }
                              aria-label={`Deactivate ${staffMember.name}`}
                              disabled={
                                staffMember.isRosterOnly ||
                                isDeactivatingStaffId ===
                                staffMember.sourceId
                              }
                              onClick={() =>
                                handleDeactivateStaff(
                                  staffMember
                                )
                              }
                            >
                              <UserX size={16} />
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  }
                )
              ) : (
                <tr>
                  <td
                    colSpan="10"
                    className="nurse-empty-state"
                  >
                    No staff records found for the
                    selected filters.
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
            if (
              event.target ===
              event.currentTarget
            ) {
              closeModal();
            }
          }}
        >
          <div
            className="nurse-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="create-shift-modal-title"
          >
            <div className="nurse-modal-header">
              <div>
                <h2 id="create-shift-modal-title">
                  Create Roster Shift
                </h2>

                <p>
                  Assign a real backend roster shift
                  to a nurse, doctor, or caretaker.
                </p>
              </div>

              <button
                className="nurse-modal-close"
                type="button"
                onClick={closeModal}
                aria-label="Close create shift modal"
                disabled={isSubmitting}
              >
                <X size={20} />
              </button>
            </div>

            <form
              className="nurse-modal-form"
              onSubmit={handleCreateShift}
            >
              <div className="nurse-form-field">
                <label htmlFor="assignedStaffId">
                  Staff member *
                </label>

                <select
                  id="assignedStaffId"
                  name="assignedStaffId"
                  value={
                    formData.assignedStaffId
                  }
                  onChange={handleFormChange}
                  disabled={isSubmitting}
                >
                  <option value="">
                    Select staff member
                  </option>

                  {rosterStaff.map(
                    (staffMember) => (
                      <option
                        key={
                          staffMember.sourceId
                        }
                        value={
                          staffMember.sourceId
                        }
                      >
                        {staffMember.name} —{" "}
                        {getDisplayRole(
                          staffMember.role
                        )}
                      </option>
                    )
                  )}
                </select>
              </div>

              <div className="nurse-form-grid">
                <div className="nurse-form-field">
                  <label htmlFor="shiftId">
                    Shift ID *
                  </label>

                  <input
                    id="shiftId"
                    name="shiftId"
                    type="text"
                    value={formData.shiftId}
                    onChange={handleFormChange}
                    disabled={isSubmitting}
                  />
                </div>

                <div className="nurse-form-field">
                  <label htmlFor="date">
                    Date *
                  </label>

                  <input
                    id="date"
                    name="date"
                    type="date"
                    value={formData.date}
                    onChange={handleFormChange}
                    disabled={isSubmitting}
                  />
                </div>
              </div>

              <div className="nurse-form-grid">
                <div className="nurse-form-field">
                  <label htmlFor="startTime">
                    Start time *
                  </label>

                  <input
                    id="startTime"
                    name="startTime"
                    type="time"
                    value={
                      formData.startTime
                    }
                    onChange={handleFormChange}
                    disabled={isSubmitting}
                  />
                </div>

                <div className="nurse-form-field">
                  <label htmlFor="endTime">
                    End time *
                  </label>

                  <input
                    id="endTime"
                    name="endTime"
                    type="time"
                    value={formData.endTime}
                    onChange={handleFormChange}
                    disabled={isSubmitting}
                  />
                </div>
              </div>

              <div className="nurse-form-grid">
                <div className="nurse-form-field">
                  <label htmlFor="location">
                    Location *
                  </label>

                  <input
                    id="location"
                    name="location"
                    type="text"
                    placeholder="Guardian Care Centre"
                    value={
                      formData.location
                    }
                    onChange={handleFormChange}
                    disabled={isSubmitting}
                  />
                </div>

                <div className="nurse-form-field">
                  <label htmlFor="room">
                    Room *
                  </label>

                  <input
                    id="room"
                    name="room"
                    type="text"
                    placeholder="Room 12"
                    value={formData.room}
                    onChange={handleFormChange}
                    disabled={isSubmitting}
                  />
                </div>
              </div>

              <div className="nurse-form-field">
                <label htmlFor="description">
                  Description *
                </label>

                <input
                  id="description"
                  name="description"
                  type="text"
                  placeholder="Morning patient monitoring"
                  value={
                    formData.description
                  }
                  onChange={handleFormChange}
                  disabled={isSubmitting}
                />
              </div>

              <div className="nurse-form-field">
                <label htmlFor="generalNotes">
                  General notes
                </label>

                <input
                  id="generalNotes"
                  name="generalNotes"
                  type="text"
                  placeholder="Optional shift notes"
                  value={
                    formData.generalNotes
                  }
                  onChange={handleFormChange}
                  disabled={isSubmitting}
                />
              </div>

              {formError && (
                <p
                  className="nurse-form-error"
                  role="alert"
                >
                  {formError}
                </p>
              )}

              <div className="nurse-modal-actions">
                <button
                  className="nurse-secondary-btn"
                  type="button"
                  onClick={closeModal}
                  disabled={isSubmitting}
                >
                  Cancel
                </button>

                <button
                  className="nurse-primary-btn"
                  type="submit"
                  disabled={
                    isSubmitting ||
                    rosterStaff.length === 0
                  }
                >
                  <Plus size={18} />
                  {isSubmitting
                    ? "Creating..."
                    : "Create Shift"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {isEditModalOpen &&
        editingStaff &&
        editingShift && (
          <div
            className="nurse-modal-overlay"
            onMouseDown={(event) => {
              if (
                event.target ===
                event.currentTarget
              ) {
                closeEditModal();
              }
            }}
          >
            <div
              className="nurse-modal"
              role="dialog"
              aria-modal="true"
              aria-labelledby="edit-roster-modal-title"
            >
              <div className="nurse-modal-header">
                <div>
                  <h2 id="edit-roster-modal-title">
                    Edit Roster Shift
                  </h2>

                  <p>
                    Update{" "}
                    {editingShift.shiftId} for{" "}
                    {editingStaff.name}.
                  </p>
                </div>

                <button
                  className="nurse-modal-close"
                  type="button"
                  onClick={closeEditModal}
                  aria-label="Close edit roster modal"
                  disabled={isUpdatingShift}
                >
                  <X size={20} />
                </button>
              </div>

              <form
                className="nurse-modal-form"
                onSubmit={
                  handleUpdateRoster
                }
              >
                <div className="nurse-selected-preview">
                  <strong>
                    {editingStaff.name}
                  </strong>
                  <span>
                    {getDisplayRole(
                      editingStaff.role
                    )}
                  </span>
                  <span>
                    Shift ID:{" "}
                    {editingShift.shiftId}
                  </span>
                </div>

                <div className="nurse-form-field">
                  <label htmlFor="editAssignedStaffId">
                    Assigned staff *
                  </label>

                  <select
                    id="editAssignedStaffId"
                    name="assignedStaffId"
                    value={
                      editFormData.assignedStaffId
                    }
                    onChange={
                      handleEditFormChange
                    }
                    disabled={isUpdatingShift}
                  >
                    {rosterStaff.map(
                      (staffMember) => (
                        <option
                          key={
                            staffMember.sourceId
                          }
                          value={
                            staffMember.sourceId
                          }
                        >
                          {staffMember.name} —{" "}
                          {getDisplayRole(
                            staffMember.role
                          )}
                        </option>
                      )
                    )}
                  </select>
                </div>

                <div className="nurse-form-grid">
                  <div className="nurse-form-field">
                    <label htmlFor="editDate">
                      Date *
                    </label>

                    <input
                      id="editDate"
                      name="date"
                      type="date"
                      value={
                        editFormData.date
                      }
                      onChange={
                        handleEditFormChange
                      }
                      disabled={isUpdatingShift}
                    />
                  </div>

                  <div className="nurse-form-field">
                    <label htmlFor="editLocation">
                      Location *
                    </label>

                    <input
                      id="editLocation"
                      name="location"
                      type="text"
                      value={
                        editFormData.location
                      }
                      onChange={
                        handleEditFormChange
                      }
                      disabled={isUpdatingShift}
                    />
                  </div>
                </div>

                <div className="nurse-form-grid">
                  <div className="nurse-form-field">
                    <label htmlFor="editStartTime">
                      Start time *
                    </label>

                    <input
                      id="editStartTime"
                      name="startTime"
                      type="time"
                      value={
                        editFormData.startTime
                      }
                      onChange={
                        handleEditFormChange
                      }
                      disabled={isUpdatingShift}
                    />
                  </div>

                  <div className="nurse-form-field">
                    <label htmlFor="editEndTime">
                      End time *
                    </label>

                    <input
                      id="editEndTime"
                      name="endTime"
                      type="time"
                      value={
                        editFormData.endTime
                      }
                      onChange={
                        handleEditFormChange
                      }
                      disabled={isUpdatingShift}
                    />
                  </div>
                </div>

                <div className="nurse-form-field">
                  <label htmlFor="editRoom">
                    Room *
                  </label>

                  <input
                    id="editRoom"
                    name="room"
                    type="text"
                    value={
                      editFormData.room
                    }
                    onChange={
                      handleEditFormChange
                    }
                    disabled={isUpdatingShift}
                  />
                </div>

                <div className="nurse-form-field">
                  <label htmlFor="editDescription">
                    Description *
                  </label>

                  <input
                    id="editDescription"
                    name="description"
                    type="text"
                    value={
                      editFormData.description
                    }
                    onChange={
                      handleEditFormChange
                    }
                    disabled={isUpdatingShift}
                  />
                </div>

                <div className="nurse-form-field">
                  <label htmlFor="editGeneralNotes">
                    General notes
                  </label>

                  <input
                    id="editGeneralNotes"
                    name="generalNotes"
                    type="text"
                    value={
                      editFormData.generalNotes
                    }
                    onChange={
                      handleEditFormChange
                    }
                    disabled={isUpdatingShift}
                  />
                </div>

                {editFormError && (
                  <p
                    className="nurse-form-error"
                    role="alert"
                  >
                    {editFormError}
                  </p>
                )}

                <div className="nurse-modal-actions">
                  <button
                    className="nurse-secondary-btn"
                    type="button"
                    onClick={
                      closeEditModal
                    }
                    disabled={isUpdatingShift}
                  >
                    Cancel
                  </button>

                  <button
                    className="nurse-primary-btn"
                    type="submit"
                    disabled={isUpdatingShift}
                  >
                    <Pencil size={18} />
                    {isUpdatingShift
                      ? "Saving..."
                      : "Save Changes"}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
    </section>
  );
}
