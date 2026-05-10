import { useEffect, useState } from 'react';
import {
  getPatients,
  createPatient,
  deactivatePatient,
} from '../services/patientService';

const initialFormData = {
  fullname: '',
  gender: '',
  dateOfBirth: '',
  caretakerId: '',
  nurseId: '',
  doctorId: '',
  image: '',
  dateOfAdmitting: '',
  description: '',
};

function formatDate(dateString) {
  if (!dateString) return 'N/A';

  const date = new Date(dateString);
  if (Number.isNaN(date.getTime())) return dateString;

  return date.toLocaleDateString();
}

function PatientsPage() {
  const [patients, setPatients] = useState([]);
  const [pagination, setPagination] = useState({
    total: 0,
    page: 1,
    pages: 1,
    limit: 10,
  });

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [deactivatingId, setDeactivatingId] = useState('');
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState(initialFormData);

  const loadPatients = async (page = 1) => {
    try {
      setLoading(true);
      setError('');

      const data = await getPatients({ page, limit: 10 });

      setPatients(data?.patients || []);
      setPagination(
        data?.pagination || {
          total: 0,
          page: 1,
          pages: 1,
          limit: 10,
        }
      );
    } catch (err) {
      console.error('Load patients error:', err);
      setError(
        err?.response?.data?.message ||
          err?.message ||
          'Failed to load patients.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPatients();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const resetForm = () => {
    setFormData(initialFormData);
  };

  const handleToggleForm = () => {
    setShowForm((prev) => !prev);
    setError('');
    setSuccessMessage('');

    if (showForm) {
      resetForm();
    }
  };

  const handleAddPatient = async (e) => {
    e.preventDefault();

    if (
      !formData.fullname ||
      !formData.gender ||
      !formData.dateOfBirth ||
      !formData.caretakerId ||
      !formData.nurseId ||
      !formData.doctorId ||
      !formData.dateOfAdmitting
    ) {
      setError(
        'Full name, gender, date of birth, caretaker ID, nurse ID, doctor ID, and date of admitting are required.'
      );
      setSuccessMessage('');
      return;
    }

    try {
      setSubmitting(true);
      setError('');
      setSuccessMessage('');

      const payload = {
        fullname: formData.fullname,
        gender: formData.gender,
        dateOfBirth: formData.dateOfBirth,
        caretakerId: formData.caretakerId,
        nurseId: formData.nurseId,
        doctorId: formData.doctorId,
        image: formData.image,
        dateOfAdmitting: formData.dateOfAdmitting,
        description: formData.description,
      };

      const response = await createPatient(payload);

      setSuccessMessage(response?.message || 'Patient created.');
      resetForm();
      setShowForm(false);

      await loadPatients(pagination.page || 1);
    } catch (err) {
      console.error('Add patient error:', err);
      setError(
        err?.response?.data?.message ||
          err?.message ||
          'Failed to add patient.'
      );
      setSuccessMessage('');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeactivate = async (id) => {
    const confirmed = window.confirm(
      'Are you sure you want to deactivate this patient?'
    );

    if (!confirmed) return;

    try {
      setDeactivatingId(id);
      setError('');
      setSuccessMessage('');

      const response = await deactivatePatient(id);

      setSuccessMessage(response?.message || 'Patient deactivated successfully.');
      await loadPatients(pagination.page || 1);
    } catch (err) {
      console.error('Deactivate patient error:', err);
      setError(
        err?.response?.data?.message ||
          err?.message ||
          'Failed to deactivate patient.'
      );
      setSuccessMessage('');
    } finally {
      setDeactivatingId('');
    }
  };

  const handlePreviousPage = () => {
    if (pagination.page > 1) {
      loadPatients(pagination.page - 1);
    }
  };

  const handleNextPage = () => {
    if (pagination.page < pagination.pages) {
      loadPatients(pagination.page + 1);
    }
  };

  return (
    <div style={{ width: '100%' }}>
      <div
        style={{
          background: '#ffffff',
          border: '1px solid #d9e2ec',
          borderRadius: '24px',
          padding: '24px',
          boxShadow: '0 4px 14px rgba(15, 23, 42, 0.04)',
        }}
      >
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'flex-start',
            gap: '16px',
            flexWrap: 'wrap',
            marginBottom: '20px',
          }}
        >
          <div>
            <h2 style={{ margin: 0, color: '#123b7a' }}>Patients</h2>
            <p style={{ marginTop: '6px', color: '#5b6b7f' }}>
              Manage patients under your organisation.
            </p>
          </div>

          <button
            onClick={handleToggleForm}
            style={{
              border: 'none',
              borderRadius: '12px',
              padding: '10px 16px',
              fontWeight: 600,
              cursor: 'pointer',
              background: '#4ea3d8',
              color: '#ffffff',
            }}
          >
            {showForm ? 'Close Form' : 'Add Patient'}
          </button>
        </div>

        {successMessage && (
          <div
            style={{
              marginBottom: '16px',
              padding: '12px 14px',
              borderRadius: '12px',
              background: '#e8f8ee',
              color: '#137333',
              fontWeight: 500,
            }}
          >
            {successMessage}
          </div>
        )}

        {showForm && (
          <form
            onSubmit={handleAddPatient}
            style={{
              marginBottom: '24px',
              padding: '20px',
              border: '1px solid #d9e2ec',
              borderRadius: '18px',
              background: '#f8fbff',
            }}
          >
            <div
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
                gap: '16px',
              }}
            >
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <label style={{ fontWeight: 600, color: '#2c3e50' }}>
                  Full Name
                </label>
                <input
                  type="text"
                  name="fullname"
                  value={formData.fullname}
                  onChange={handleInputChange}
                  placeholder="Enter full name"
                  style={{
                    height: '44px',
                    padding: '0 14px',
                    border: '1px solid #cfd8e3',
                    borderRadius: '12px',
                    outline: 'none',
                  }}
                />
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <label style={{ fontWeight: 600, color: '#2c3e50' }}>
                  Gender
                </label>
                <select
                  name="gender"
                  value={formData.gender}
                  onChange={handleInputChange}
                  style={{
                    height: '44px',
                    padding: '0 14px',
                    border: '1px solid #cfd8e3',
                    borderRadius: '12px',
                    outline: 'none',
                    background: '#ffffff',
                  }}
                >
                  <option value="">Select gender</option>
                  <option value="male">male</option>
                  <option value="female">female</option>
                  <option value="other">other</option>
                </select>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <label style={{ fontWeight: 600, color: '#2c3e50' }}>
                  Date of Birth
                </label>
                <input
                  type="date"
                  name="dateOfBirth"
                  value={formData.dateOfBirth}
                  onChange={handleInputChange}
                  style={{
                    height: '44px',
                    padding: '0 14px',
                    border: '1px solid #cfd8e3',
                    borderRadius: '12px',
                    outline: 'none',
                  }}
                />
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <label style={{ fontWeight: 600, color: '#2c3e50' }}>
                  Caretaker ID
                </label>
                <input
                  type="text"
                  name="caretakerId"
                  value={formData.caretakerId}
                  onChange={handleInputChange}
                  placeholder="Enter caretaker ID"
                  style={{
                    height: '44px',
                    padding: '0 14px',
                    border: '1px solid #cfd8e3',
                    borderRadius: '12px',
                    outline: 'none',
                  }}
                />
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <label style={{ fontWeight: 600, color: '#2c3e50' }}>
                  Nurse ID
                </label>
                <input
                  type="text"
                  name="nurseId"
                  value={formData.nurseId}
                  onChange={handleInputChange}
                  placeholder="Enter nurse ID"
                  style={{
                    height: '44px',
                    padding: '0 14px',
                    border: '1px solid #cfd8e3',
                    borderRadius: '12px',
                    outline: 'none',
                  }}
                />
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <label style={{ fontWeight: 600, color: '#2c3e50' }}>
                  Doctor ID
                </label>
                <input
                  type="text"
                  name="doctorId"
                  value={formData.doctorId}
                  onChange={handleInputChange}
                  placeholder="Enter doctor ID"
                  style={{
                    height: '44px',
                    padding: '0 14px',
                    border: '1px solid #cfd8e3',
                    borderRadius: '12px',
                    outline: 'none',
                  }}
                />
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <label style={{ fontWeight: 600, color: '#2c3e50' }}>
                  Image URL
                </label>
                <input
                  type="text"
                  name="image"
                  value={formData.image}
                  onChange={handleInputChange}
                  placeholder="Enter image URL"
                  style={{
                    height: '44px',
                    padding: '0 14px',
                    border: '1px solid #cfd8e3',
                    borderRadius: '12px',
                    outline: 'none',
                  }}
                />
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <label style={{ fontWeight: 600, color: '#2c3e50' }}>
                  Date of Admitting
                </label>
                <input
                  type="date"
                  name="dateOfAdmitting"
                  value={formData.dateOfAdmitting}
                  onChange={handleInputChange}
                  style={{
                    height: '44px',
                    padding: '0 14px',
                    border: '1px solid #cfd8e3',
                    borderRadius: '12px',
                    outline: 'none',
                  }}
                />
              </div>
            </div>

            <div
              style={{
                display: 'flex',
                flexDirection: 'column',
                gap: '8px',
                marginTop: '16px',
              }}
            >
              <label style={{ fontWeight: 600, color: '#2c3e50' }}>
                Description
              </label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                placeholder="Enter patient description"
                rows={4}
                style={{
                  padding: '12px 14px',
                  border: '1px solid #cfd8e3',
                  borderRadius: '12px',
                  outline: 'none',
                  resize: 'vertical',
                  fontFamily: 'inherit',
                }}
              />
            </div>

            <div
              style={{
                display: 'flex',
                justifyContent: 'flex-end',
                gap: '12px',
                marginTop: '20px',
                flexWrap: 'wrap',
              }}
            >
              <button
                type="button"
                onClick={() => {
                  setShowForm(false);
                  resetForm();
                  setError('');
                  setSuccessMessage('');
                }}
                style={{
                  border: 'none',
                  borderRadius: '12px',
                  padding: '10px 16px',
                  fontWeight: 600,
                  cursor: 'pointer',
                  background: '#e9f1f8',
                  color: '#1b3a57',
                }}
              >
                Cancel
              </button>

              <button
                type="submit"
                disabled={submitting}
                style={{
                  border: 'none',
                  borderRadius: '12px',
                  padding: '10px 16px',
                  fontWeight: 600,
                  cursor: submitting ? 'not-allowed' : 'pointer',
                  background: '#4ea3d8',
                  color: '#ffffff',
                  opacity: submitting ? 0.7 : 1,
                }}
              >
                {submitting ? 'Saving...' : 'Save Patient'}
              </button>
            </div>
          </form>
        )}

        {loading ? (
          <div
            style={{
              padding: '24px',
              textAlign: 'center',
              color: '#5b6b7f',
              border: '1px dashed #cfd8e3',
              borderRadius: '16px',
            }}
          >
            Loading patients...
          </div>
        ) : error ? (
          <div
            style={{
              padding: '24px',
              textAlign: 'center',
              color: '#b42318',
              border: '1px dashed #f5c2c7',
              borderRadius: '16px',
              background: '#fdecec',
            }}
          >
            {error}
          </div>
        ) : patients.length === 0 ? (
          <div
            style={{
              padding: '24px',
              textAlign: 'center',
              color: '#5b6b7f',
              border: '1px dashed #cfd8e3',
              borderRadius: '16px',
            }}
          >
            No patients found.
          </div>
        ) : (
          <>
            <div style={{ overflowX: 'auto' }}>
              <table
                style={{
                  width: '100%',
                  borderCollapse: 'collapse',
                }}
              >
                <thead>
                  <tr>
                    <th
                      style={{
                        textAlign: 'left',
                        padding: '14px 12px',
                        borderBottom: '1px solid #edf2f7',
                        color: '#123b7a',
                        fontSize: '14px',
                      }}
                    >
                      Full Name
                    </th>
                    <th
                      style={{
                        textAlign: 'left',
                        padding: '14px 12px',
                        borderBottom: '1px solid #edf2f7',
                        color: '#123b7a',
                        fontSize: '14px',
                      }}
                    >
                      Gender
                    </th>
                    <th
                      style={{
                        textAlign: 'left',
                        padding: '14px 12px',
                        borderBottom: '1px solid #edf2f7',
                        color: '#123b7a',
                        fontSize: '14px',
                      }}
                    >
                      Date of Birth
                    </th>
                    <th
                      style={{
                        textAlign: 'left',
                        padding: '14px 12px',
                        borderBottom: '1px solid #edf2f7',
                        color: '#123b7a',
                        fontSize: '14px',
                      }}
                    >
                      Age
                    </th>
                    <th
                      style={{
                        textAlign: 'left',
                        padding: '14px 12px',
                        borderBottom: '1px solid #edf2f7',
                        color: '#123b7a',
                        fontSize: '14px',
                      }}
                    >
                      Caretaker
                    </th>
                    <th
                      style={{
                        textAlign: 'left',
                        padding: '14px 12px',
                        borderBottom: '1px solid #edf2f7',
                        color: '#123b7a',
                        fontSize: '14px',
                      }}
                    >
                      Doctor
                    </th>
                    <th
                      style={{
                        textAlign: 'left',
                        padding: '14px 12px',
                        borderBottom: '1px solid #edf2f7',
                        color: '#123b7a',
                        fontSize: '14px',
                      }}
                    >
                      Nurses
                    </th>
                    <th
                      style={{
                        textAlign: 'left',
                        padding: '14px 12px',
                        borderBottom: '1px solid #edf2f7',
                        color: '#123b7a',
                        fontSize: '14px',
                      }}
                    >
                      Actions
                    </th>
                  </tr>
                </thead>

                <tbody>
                  {patients.map((patient) => (
                    <tr key={patient._id}>
                      <td
                        style={{
                          padding: '14px 12px',
                          borderBottom: '1px solid #edf2f7',
                        }}
                      >
                        {patient.fullname || 'N/A'}
                      </td>
                      <td
                        style={{
                          padding: '14px 12px',
                          borderBottom: '1px solid #edf2f7',
                          textTransform: 'capitalize',
                        }}
                      >
                        {patient.gender || 'N/A'}
                      </td>
                      <td
                        style={{
                          padding: '14px 12px',
                          borderBottom: '1px solid #edf2f7',
                        }}
                      >
                        {formatDate(patient.dateOfBirth)}
                      </td>
                      <td
                        style={{
                          padding: '14px 12px',
                          borderBottom: '1px solid #edf2f7',
                        }}
                      >
                        {patient.age ?? 'N/A'}
                      </td>
                      <td
                        style={{
                          padding: '14px 12px',
                          borderBottom: '1px solid #edf2f7',
                        }}
                      >
                        {patient.caretaker?.fullname || 'N/A'}
                      </td>
                      <td
                        style={{
                          padding: '14px 12px',
                          borderBottom: '1px solid #edf2f7',
                        }}
                      >
                        {patient.assignedDoctor?.fullname || 'N/A'}
                      </td>
                      <td
                        style={{
                          padding: '14px 12px',
                          borderBottom: '1px solid #edf2f7',
                        }}
                      >
                        {patient.assignedNurses?.length
                          ? patient.assignedNurses
                              .map((nurse) => nurse.fullname)
                              .join(', ')
                          : 'N/A'}
                      </td>
                      <td
                        style={{
                          padding: '14px 12px',
                          borderBottom: '1px solid #edf2f7',
                        }}
                      >
                        <button
                          onClick={() => handleDeactivate(patient._id)}
                          disabled={deactivatingId === patient._id}
                          style={{
                            border: 'none',
                            borderRadius: '12px',
                            padding: '10px 14px',
                            fontWeight: 600,
                            cursor:
                              deactivatingId === patient._id
                                ? 'not-allowed'
                                : 'pointer',
                            background: '#ffe5e5',
                            color: '#b42318',
                            opacity: deactivatingId === patient._id ? 0.7 : 1,
                          }}
                        >
                          {deactivatingId === patient._id
                            ? 'Deactivating...'
                            : 'Deactivate'}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div
              style={{
                marginTop: '20px',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                gap: '12px',
                flexWrap: 'wrap',
              }}
            >
              <div style={{ color: '#5b6b7f', fontSize: '14px' }}>
                Total Patients: {pagination.total || 0} | Page{' '}
                {pagination.page || 1} of {pagination.pages || 1}
              </div>

              <div style={{ display: 'flex', gap: '10px' }}>
                <button
                  onClick={handlePreviousPage}
                  disabled={pagination.page <= 1}
                  style={{
                    border: 'none',
                    borderRadius: '12px',
                    padding: '10px 14px',
                    fontWeight: 600,
                    cursor: pagination.page <= 1 ? 'not-allowed' : 'pointer',
                    background: '#e9f1f8',
                    color: '#1b3a57',
                    opacity: pagination.page <= 1 ? 0.6 : 1,
                  }}
                >
                  Previous
                </button>

                <button
                  onClick={handleNextPage}
                  disabled={pagination.page >= pagination.pages}
                  style={{
                    border: 'none',
                    borderRadius: '12px',
                    padding: '10px 14px',
                    fontWeight: 600,
                    cursor:
                      pagination.page >= pagination.pages
                        ? 'not-allowed'
                        : 'pointer',
                    background: '#4ea3d8',
                    color: '#ffffff',
                    opacity: pagination.page >= pagination.pages ? 0.6 : 1,
                  }}
                >
                  Next
                </button>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default PatientsPage;