import { useState } from "react";

export default function LocationPage() {
  const [locations, setLocations] = useState([
    {
      id: 1,
      name: "Melbourne Medical Centre",
      address: "123 Collins Street",
      city: "Melbourne",
      status: "Active",
    },
    {
      id: 2,
      name: "Burwood Health Centre",
      address: "221 Burwood Highway",
      city: "Burwood",
      status: "Active",
    },
  ]);

  const [search, setSearch] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [editingLocation, setEditingLocation] = useState(null);

  const [formData, setFormData] = useState({
    name: "",
    address: "",
    city: "",
    status: "Active",
  });

  const filteredLocations = locations.filter((location) => {
    const searchValue = search.toLowerCase();

    return (
      location.name.toLowerCase().includes(searchValue) ||
      location.address.toLowerCase().includes(searchValue) ||
      location.city.toLowerCase().includes(searchValue) ||
      location.status.toLowerCase().includes(searchValue)
    );
  });

  const openAddModal = () => {
    setEditingLocation(null);

    setFormData({
      name: "",
      address: "",
      city: "",
      status: "Active",
    });

    setShowModal(true);
  };

  const openEditModal = (location) => {
    setEditingLocation(location);

    setFormData({
      name: location.name,
      address: location.address,
      city: location.city,
      status: location.status,
    });

    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setEditingLocation(null);

    setFormData({
      name: "",
      address: "",
      city: "",
      status: "Active",
    });
  };

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previousData) => ({
      ...previousData,
      [name]: value,
    }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    if (
      !formData.name.trim() ||
      !formData.address.trim() ||
      !formData.city.trim()
    ) {
      alert("Please complete all required fields.");
      return;
    }

    if (editingLocation) {
      setLocations((previousLocations) =>
        previousLocations.map((location) =>
          location.id === editingLocation.id
            ? {
                ...location,
                ...formData,
              }
            : location
        )
      );
    } else {
      const newLocation = {
        id: Date.now(),
        ...formData,
      };

      setLocations((previousLocations) => [
        ...previousLocations,
        newLocation,
      ]);
    }

    closeModal();
  };

  const handleDelete = (location) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${location.name}"?`
    );

    if (!confirmed) {
      return;
    }

    setLocations((previousLocations) =>
      previousLocations.filter(
        (item) => item.id !== location.id
      )
    );
  };

  return (
    <div style={styles.page}>
      <div style={styles.header}>
        <div>
          <h1 style={styles.title}>Location Management</h1>
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
          onChange={(event) => setSearch(event.target.value)}
          style={styles.search}
        />
      </div>

      <div style={styles.tableContainer}>
        <table style={styles.table}>
          <thead>
            <tr>
              <th style={styles.th}>Location</th>
              <th style={styles.th}>Address</th>
              <th style={styles.th}>City</th>
              <th style={styles.th}>Status</th>
              <th style={styles.th}>Actions</th>
            </tr>
          </thead>

          <tbody>
            {filteredLocations.length > 0 ? (
              filteredLocations.map((location) => (
                <tr key={location.id}>
                  <td style={styles.td}>
                    {location.name}
                  </td>

                  <td style={styles.td}>
                    {location.address}
                  </td>

                  <td style={styles.td}>
                    {location.city}
                  </td>

                  <td style={styles.td}>
                    <span
                      style={
                        location.status === "Active"
                          ? styles.activeStatus
                          : styles.inactiveStatus
                      }
                    >
                      {location.status}
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
              <div style={styles.formGroup}>
                <label style={styles.label}>
                  Location Name
                </label>

                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="Enter location name"
                  style={styles.input}
                />
              </div>

              <div style={styles.formGroup}>
                <label style={styles.label}>
                  Address
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
                  City
                </label>

                <input
                  type="text"
                  name="city"
                  value={formData.city}
                  onChange={handleChange}
                  placeholder="Enter city"
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
                  <option value="Active">
                    Active
                  </option>

                  <option value="Inactive">
                    Inactive
                  </option>
                </select>
              </div>

              <div style={styles.modalActions}>
                <button
                  type="button"
                  onClick={closeModal}
                  style={styles.cancelButton}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  style={styles.primaryButton}
                >
                  {editingLocation
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
  },

  search: {
    width: "320px",
    padding: "12px",
    border: "1px solid #d4dce6",
    borderRadius: "8px",
    fontSize: "14px",
  },

  tableContainer: {
    background: "#ffffff",
    borderRadius: "12px",
    overflow: "hidden",
    boxShadow: "0 2px 8px rgba(0, 0, 0, 0.05)",
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
    background: "rgba(0, 0, 0, 0.45)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 1000,
  },

  modal: {
    width: "460px",
    background: "#ffffff",
    borderRadius: "12px",
    padding: "24px",
    boxShadow: "0 12px 40px rgba(0, 0, 0, 0.2)",
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

  formGroup: {
    marginBottom: "16px",
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