import { useEffect, useState } from "react";
import {
  getLocations,
  createLocation,
  updateLocation,
  deleteLocation,
} from "../services/locationService";
 
export default function LocationPage() {
  const [locations, setLocations] = useState([]);
  const [search, setSearch] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [editingLocation, setEditingLocation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
 
  const emptyForm = {
    nameOfBuilding: "",
    address: "",
    openingHours: "",
    contactNumber: "",
    numberOfDoctors: "",
    numberOfNurses: "",
    numberOfRooms: "",
    patientCapacity: "",
    currentOccupancy: "",
    equipment: "",
    facilities: "",
    status: "active",
  };
 
  const [formData, setFormData] = useState(emptyForm);
 
  // Load locations from backend
  const loadLocations = async () => {
    try {
      setLoading(true);
      setError("");
 
      const data = await getLocations();
 
      // Handles different possible API response structures
      const locationList = Array.isArray(data)
        ? data
        : data?.locations || data?.data || [];
 
      setLocations(locationList);
    } catch (err) {
      console.error("Failed to load locations:", err);
      setError("Unable to load locations from the server.");
    } finally {
      setLoading(false);
    }
  };
 
  useEffect(() => {
    loadLocations();
  }, []);
 
  const filteredLocations = locations.filter((location) => {
    const searchValue = search.toLowerCase();
 
    return (
      (location.nameOfBuilding || "")
        .toLowerCase()
        .includes(searchValue) ||
      (location.address || "")
        .toLowerCase()
        .includes(searchValue) ||
      (location.openingHours || "")
        .toLowerCase()
        .includes(searchValue) ||
      (location.status || "")
        .toLowerCase()
        .includes(searchValue)
    );
  });
 
  const openAddModal = () => {
    setEditingLocation(null);
    setFormData(emptyForm);
    setShowModal(true);
  };
 
  const openEditModal = (location) => {
    setEditingLocation(location);
 
    setFormData({
      nameOfBuilding: location.nameOfBuilding || "",
      address: location.address || "",
      openingHours: location.openingHours || "",
      contactNumber: location.contactNumber || "",
      numberOfDoctors: location.numberOfDoctors ?? "",
      numberOfNurses: location.numberOfNurses ?? "",
      numberOfRooms: location.numberOfRooms ?? "",
      patientCapacity: location.patientCapacity ?? "",
      currentOccupancy: location.currentOccupancy ?? "",
      equipment: location.equipment || "",
      facilities: location.facilities || "",
      status: location.status || "active",
    });
 
    setShowModal(true);
  };
 
  const closeModal = () => {
    setShowModal(false);
    setEditingLocation(null);
    setFormData(emptyForm);
  };
 
  const handleChange = (event) => {
    const { name, value } = event.target;
 
    setFormData((previousData) => ({
      ...previousData,
      [name]: value,
    }));
  };
 
  const handleSubmit = async (event) => {
    event.preventDefault();
 
    if (
      !formData.nameOfBuilding.trim() ||
      !formData.address.trim()
    ) {
      alert("Please complete the required fields.");
      return;
    }
 
    const payload = {
      nameOfBuilding: formData.nameOfBuilding.trim(),
      address: formData.address.trim(),
      openingHours: formData.openingHours.trim(),
      contactNumber: formData.contactNumber.trim(),
 
      numberOfDoctors:
        Number(formData.numberOfDoctors) || 0,
 
      numberOfNurses:
        Number(formData.numberOfNurses) || 0,
 
      numberOfRooms:
        Number(formData.numberOfRooms) || 0,
 
      patientCapacity:
        Number(formData.patientCapacity) || 0,
 
      currentOccupancy:
        Number(formData.currentOccupancy) || 0,
 
      equipment: formData.equipment.trim(),
      facilities: formData.facilities.trim(),
      status: formData.status,
    };
 
    try {
      setSaving(true);
 
      if (editingLocation) {
        const id =
          editingLocation._id || editingLocation.id;
 
        await updateLocation(id, payload);
      } else {
        await createLocation(payload);
      }
 
      closeModal();
      await loadLocations();
    } catch (err) {
      console.error("Failed to save location:", err);
 
      const message =
        err?.response?.data?.message ||
        "Unable to save the location.";
 
      alert(message);
    } finally {
      setSaving(false);
    }
  };
 
  const handleDelete = async (location) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${
        location.nameOfBuilding || "this location"
      }"?`
    );
 
    if (!confirmed) {
      return;
    }
 
    try {
      const id = location._id || location.id;
 
      await deleteLocation(id);
      await loadLocations();
    } catch (err) {
      console.error("Failed to delete location:", err);
 
      const message =
        err?.response?.data?.message ||
        "Unable to delete the location.";
 
      alert(message);
    }
  };
 
  return (
<div style={styles.page}>
<div style={styles.header}>
<div>
<h1 style={styles.title}>
            Location Management
</h1>
 
          <p style={styles.subtitle}>
            Manage facility locations.
</p>
</div>
 
        <button
          type="button"
          style={styles.primaryButton}
          onClick={openAddModal}
>
          + Add Location
</button>
</div>
 
      <div style={styles.toolbar}>
<input
          type="text"
          placeholder="Search locations..."
          value={search}
          onChange={(event) =>
            setSearch(event.target.value)
          }
          style={styles.search}
        />
 
        <button
          type="button"
          onClick={loadLocations}
          style={styles.refreshButton}
>
          Refresh
</button>
</div>
 
      {error && (
<div style={styles.errorMessage}>
          {error}
</div>
      )}
 
      <div style={styles.tableContainer}>
<table style={styles.table}>
<thead>
<tr>
<th style={styles.th}>Location</th>
<th style={styles.th}>Address</th>
<th style={styles.th}>
                Opening Hours
</th>
<th style={styles.th}>Status</th>
<th style={styles.th}>Actions</th>
</tr>
</thead>
 
          <tbody>
            {loading ? (
<tr>
<td
                  colSpan="5"
                  style={styles.emptyState}
>
                  Loading locations...
</td>
</tr>
            ) : filteredLocations.length > 0 ? (
              filteredLocations.map((location) => (
<tr
                  key={
                    location._id ||
                    location.id ||
                    location.nameOfBuilding
                  }
>
<td style={styles.td}>
                    {location.nameOfBuilding}
</td>
 
                  <td style={styles.td}>
                    {location.address}
</td>
 
                  <td style={styles.td}>
                    {location.openingHours || "-"}
</td>
 
                  <td style={styles.td}>
<span
                      style={
                        location.status
                          ?.toLowerCase() === "active"
                          ? styles.activeStatus
                          : styles.inactiveStatus
                      }
>
                      {location.status || "Unknown"}
</span>
</td>
 
                  <td style={styles.td}>
<button
                      type="button"
                      style={styles.editButton}
                      onClick={() =>
                        openEditModal(location)
                      }
>
                      Edit
</button>
 
                    <button
                      type="button"
                      style={styles.deleteButton}
                      onClick={() =>
                        handleDelete(location)
                      }
>
                      Delete
</button>
</td>
</tr>
              ))
            ) : (
<tr>
<td
                  colSpan="5"
                  style={styles.emptyState}
>
                  No locations found.
</td>
</tr>
            )}
</tbody>
</table>
</div>
 
      {showModal && (
<div style={styles.overlay}>
<div style={styles.modal}>
<div style={styles.modalHeader}>
<h2 style={styles.modalTitle}>
                {editingLocation
                  ? "Edit Location"
                  : "Add Location"}
</h2>
 
              <button
                type="button"
                onClick={closeModal}
                style={styles.closeButton}
>
                ×
</button>
</div>
 
            <form onSubmit={handleSubmit}>
<div style={styles.formGrid}>
<div style={styles.formGroup}>
<label style={styles.label}>
                    Building Name *
</label>
 
                  <input
                    type="text"
                    name="nameOfBuilding"
                    value={formData.nameOfBuilding}
                    onChange={handleChange}
                    placeholder="Enter building name"
                    style={styles.input}
                  />
</div>
 
                <div style={styles.formGroup}>
<label style={styles.label}>
                    Address *
</label>
 
                  <input
                    type="text"
                    name="address"
                    value={formData.address}
                    onChange={handleChange}
                    placeholder="Enter address"
                    style={styles.input}
                  />
</div>
 
                <div style={styles.formGroup}>
<label style={styles.label}>
                    Opening Hours
</label>
 
                  <input
                    type="text"
                    name="openingHours"
                    value={formData.openingHours}
                    onChange={handleChange}
                    placeholder="e.g. Mon-Fri 8AM-5PM"
                    style={styles.input}
                  />
</div>
 
                <div style={styles.formGroup}>
<label style={styles.label}>
                    Contact Number
</label>
 
                  <input
                    type="text"
                    name="contactNumber"
                    value={formData.contactNumber}
                    onChange={handleChange}
                    placeholder="Enter contact number"
                    style={styles.input}
                  />
</div>
 
                <NumberInput
                  label="Number of Doctors"
                  name="numberOfDoctors"
                  value={formData.numberOfDoctors}
                  onChange={handleChange}
                />
 
                <NumberInput
                  label="Number of Nurses"
                  name="numberOfNurses"
                  value={formData.numberOfNurses}
                  onChange={handleChange}
                />
 
                <NumberInput
                  label="Number of Rooms"
                  name="numberOfRooms"
                  value={formData.numberOfRooms}
                  onChange={handleChange}
                />
 
                <NumberInput
                  label="Patient Capacity"
                  name="patientCapacity"
                  value={formData.patientCapacity}
                  onChange={handleChange}
                />
 
                <NumberInput
                  label="Current Occupancy"
                  name="currentOccupancy"
                  value={formData.currentOccupancy}
                  onChange={handleChange}
                />
 
                <div style={styles.formGroup}>
<label style={styles.label}>
                    Equipment
</label>
 
                  <input
                    type="text"
                    name="equipment"
                    value={formData.equipment}
                    onChange={handleChange}
                    placeholder="Available equipment"
                    style={styles.input}
                  />
</div>
 
                <div style={styles.formGroup}>
<label style={styles.label}>
                    Facilities
</label>
 
                  <input
                    type="text"
                    name="facilities"
                    value={formData.facilities}
                    onChange={handleChange}
                    placeholder="Available facilities"
                    style={styles.input}
                  />
</div>
 
                <div style={styles.formGroup}>
<label style={styles.label}>
                    Status
</label>
 
                  <select
                    name="status"
                    value={formData.status}
                    onChange={handleChange}
                    style={styles.input}
>
<option value="active">
                      Active
</option>
 
                    <option value="inactive">
                      Inactive
</option>
</select>
</div>
</div>
 
              <div style={styles.modalActions}>
<button
                  type="button"
                  onClick={closeModal}
                  style={styles.cancelButton}
                  disabled={saving}
>
                  Cancel
</button>
 
                <button
                  type="submit"
                  style={styles.primaryButton}
                  disabled={saving}
>
                  {saving
                    ? "Saving..."
                    : editingLocation
                      ? "Save Changes"
                      : "Add Location"}
</button>
</div>
</form>
</div>
</div>
      )}
</div>
  );
}
 
function NumberInput({
  label,
  name,
  value,
  onChange,
}) {
  return (
<div style={styles.formGroup}>
<label style={styles.label}>{label}</label>
 
      <input
        type="number"
        min="0"
        name={name}
        value={value}
        onChange={onChange}
        style={styles.input}
      />
</div>
  );
}
 
const styles = {
  page: {
    padding: "32px",
  },
 
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "24px",
  },
 
  title: {
    margin: 0,
    fontSize: "32px",
    color: "#10264a",
  },
 
  subtitle: {
    marginTop: "8px",
    color: "#5f6b7a",
  },
 
  toolbar: {
    marginBottom: "20px",
    display: "flex",
    gap: "10px",
  },
 
  search: {
    width: "320px",
    padding: "12px",
    border: "1px solid #d4dce6",
    borderRadius: "8px",
    fontSize: "14px",
  },
 
  refreshButton: {
    padding: "10px 16px",
    background: "#ffffff",
    border: "1px solid #2499cf",
    color: "#167cad",
    borderRadius: "8px",
    cursor: "pointer",
  },
 
  errorMessage: {
    padding: "12px",
    marginBottom: "16px",
    background: "#fff1f0",
    color: "#b42318",
    borderRadius: "8px",
  },
 
  tableContainer: {
    background: "#ffffff",
    borderRadius: "12px",
    overflow: "hidden",
    boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
  },
 
  table: {
    width: "100%",
    borderCollapse: "collapse",
  },
 
  th: {
    textAlign: "left",
    padding: "16px",
    background: "#f5f8fc",
    color: "#10264a",
    borderBottom: "1px solid #e4e9f0",
  },
 
  td: {
    padding: "16px",
    borderBottom: "1px solid #edf0f4",
  },
 
  primaryButton: {
    background: "#2499cf",
    color: "#ffffff",
    border: "none",
    borderRadius: "8px",
    padding: "11px 18px",
    cursor: "pointer",
    fontWeight: "600",
  },
 
  editButton: {
    background: "#ffffff",
    border: "1px solid #2499cf",
    color: "#167cad",
    borderRadius: "6px",
    padding: "7px 12px",
    marginRight: "8px",
    cursor: "pointer",
  },
 
  deleteButton: {
    background: "#ffffff",
    border: "1px solid #d9534f",
    color: "#c0392b",
    borderRadius: "6px",
    padding: "7px 12px",
    cursor: "pointer",
  },
 
  activeStatus: {
    background: "#e8f7ee",
    color: "#207a45",
    padding: "5px 10px",
    borderRadius: "20px",
    fontSize: "13px",
  },
 
  inactiveStatus: {
    background: "#f1f2f4",
    color: "#667085",
    padding: "5px 10px",
    borderRadius: "20px",
    fontSize: "13px",
  },
 
  emptyState: {
    padding: "30px",
    textAlign: "center",
    color: "#667085",
  },
 
  overlay: {
    position: "fixed",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    background: "rgba(0,0,0,0.45)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 1000,
  },
 
  modal: {
    width: "720px",
    maxWidth: "90vw",
    maxHeight: "85vh",
    overflowY: "auto",
    background: "#ffffff",
    borderRadius: "12px",
    padding: "24px",
    boxShadow: "0 12px 40px rgba(0,0,0,0.2)",
  },
 
  modalHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "20px",
  },
 
  modalTitle: {
    margin: 0,
    color: "#10264a",
  },
 
  closeButton: {
    background: "transparent",
    border: "none",
    fontSize: "26px",
    cursor: "pointer",
  },
 
  formGrid: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: "16px",
  },
 
  formGroup: {
    marginBottom: "4px",
  },
 
  label: {
    display: "block",
    marginBottom: "7px",
    fontWeight: "600",
    color: "#253858",
  },
 
  input: {
    width: "100%",
    padding: "11px",
    border: "1px solid #d4dce6",
    borderRadius: "7px",
    boxSizing: "border-box",
    fontSize: "14px",
  },
 
  modalActions: {
    display: "flex",
    justifyContent: "flex-end",
    gap: "10px",
    marginTop: "24px",
  },
 
  cancelButton: {
    background: "#ffffff",
    border: "1px solid #ccd4df",
    borderRadius: "8px",
    padding: "10px 16px",
    cursor: "pointer",
  },
};