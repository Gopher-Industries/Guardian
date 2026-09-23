import { useState, useEffect, useCallback } from 'react';
import {
  UserRoundPlus,
  Users,
  Stethoscope,
  HeartPulse,
  Building2,
} from 'lucide-react';

import DataTable from '../components/common/DataTable';
import Modal from '../components/common/Modal';
import Toast from '../components/common/Toast';
import Button from '../components/common/Button';
import InputField from '../components/common/InputField';
import Dropdown from '../components/common/Dropdown';

import { getMyOrganizations } from '../services/orgService';

import {
  getStaff,
  createStaff,
  deactivateStaff,
  getAllDoctors,
  getAllNurses,
} from '../services/staffService';

import { ROLE_OPTIONS } from '../utils/constants';

const emptyForm = { userId: '' };
const emptyErrors = { userId: '' };

const ROLE_FILTER_OPTIONS = [
  { value: '', label: 'All Roles' },
  ...ROLE_OPTIONS,
];

function formatStaff(raw) {
  return raw.map((s) => ({
    id: s._id,
    fullName: s.fullname,
    email: s.email,
    role:
      ROLE_OPTIONS.find((r) => r.value === s.role?.name?.toLowerCase())
        ?.label ?? '-',
    organization: s.organization?.name ?? '-',
  }));
}

function formatCandidates(raw, defaultRole) {
  const candidates =
    raw.doctors ||
    raw.nurses ||
    raw.users ||
    raw.data ||
    [];

  return candidates.map((person) => ({
    id: person._id,
    fullName: person.fullname,
    email: person.email,
    role: person.role?.name ?? defaultRole,
  }));
}

export default function StaffManagementPage() {
  const [staff, setStaff] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);

  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState(emptyErrors);

  const [orgOptions, setOrgOptions] = useState([]);
  const [orgLoading, setOrgLoading] = useState(true);
  const [orgFilter, setOrgFilter] = useState('');
  const [roleFilter, setRoleFilter] = useState('');
  const [search, setSearch] = useState('');

  const [confirmId, setConfirmId] = useState(null);
  const [successOpen, setSuccessOpen] = useState(false);

  const [staffCandidates, setStaffCandidates] = useState([]);
  const [candidateSearch, setCandidateSearch] = useState('');
  const [candidatesLoading, setCandidatesLoading] = useState(false);
  const [candidatesError, setCandidatesError] = useState('');

  const fetchStaff = useCallback(async () => {
    setLoading(true);

    try {
      const data = await getStaff({
        page,
        limit: 10,
        role: roleFilter,
        orgId: orgFilter,
        search,
      });

      const normalized = formatStaff(data.staff ?? []);

      const filtered = normalized.filter((s) =>
        ROLE_OPTIONS.some(
          (r) => r.label.toLowerCase() === s.role.toLowerCase(),
        ),
      );

      setStaff(filtered);
    } catch (err) {
      console.error('Failed to load staff:', err);
    } finally {
      setLoading(false);
    }
  }, [page, roleFilter, orgFilter, search]);

  useEffect(() => {
    fetchStaff();
  }, [fetchStaff]);

  useEffect(() => {
    const fetchOrgs = async () => {
      setOrgLoading(true);

      try {
        const orgs = await getMyOrganizations();

        setOrgOptions(
          orgs.map((o) => ({
            value: o._id,
            label: o.name,
          })),
        );
      } catch (err) {
        console.error('Failed to load organizations:', err);
      } finally {
        setOrgLoading(false);
      }
    };

    fetchOrgs();
  }, []);

  const fetchStaffCandidates = useCallback(async () => {
    setCandidatesLoading(true);
    setCandidatesError('');

    try {
      const [doctorsData, nursesData] = await Promise.all([
        getAllDoctors(),
        getAllNurses(),
      ]);

      const doctors = formatCandidates(doctorsData, 'Doctor');
      const nurses = formatCandidates(nursesData, 'Nurse');

      const combinedCandidates = [...doctors, ...nurses];

      const uniqueCandidates = Array.from(
        new Map(
          combinedCandidates.map((person) => [person.id, person]),
        ).values(),
      );

      setStaffCandidates(uniqueCandidates);
    } catch (err) {
      console.error('Failed to load staff candidates:', err);
      setCandidatesError('Unable to load available staff members.');
      setStaffCandidates([]);
    } finally {
      setCandidatesLoading(false);
    }
  }, []);

  function validate(fields) {
    const errs = { ...emptyErrors };

    if (!fields.userId.trim()) {
      errs.userId = 'Please select a staff member.';
    }

    return errs;
  }

  function handleOpenAddStaff() {
    setModalOpen(true);
    setCandidateSearch('');
    setForm(emptyForm);
    setErrors(emptyErrors);
    setCandidatesError('');

    fetchStaffCandidates();
  }

  function handleClose() {
    setModalOpen(false);
    setForm(emptyForm);
    setErrors(emptyErrors);
    setCandidateSearch('');
    setCandidatesError('');
  }

  async function handleSave() {
    const errs = validate(form);

    if (Object.values(errs).some(Boolean)) {
      setErrors(errs);
      return;
    }

    try {
      await createStaff(form.userId);
      handleClose();
      setSuccessOpen(true);
      await fetchStaff();
    } catch (err) {
      console.error('Failed to add staff:', err);
    }
  }

  async function handleConfirmDeactivate() {
    try {
      await deactivateStaff(confirmId);

      setStaff((prev) =>
        prev.filter((s) => s.id !== confirmId),
      );
    } catch (err) {
      console.error('Failed to deactivate staff:', err);
    } finally {
      setConfirmId(null);
    }
  }

  const filteredCandidates = staffCandidates.filter((person) => {
    const alreadyAdded = staff.some(
      (staffMember) => staffMember.id === person.id,
    );

    if (alreadyAdded) {
      return false;
    }

    const searchValue = candidateSearch
      .toLowerCase()
      .trim();

    if (!searchValue) {
      return true;
    }

    return (
      person.fullName
        .toLowerCase()
        .includes(searchValue) ||
      person.email
        .toLowerCase()
        .includes(searchValue) ||
      person.role
        .toLowerCase()
        .includes(searchValue)
    );
  });

  const selectedCandidate = staffCandidates.find(
    (person) => person.id === form.userId,
  );

  const filteredStaff = staff.filter((person) => {
    const searchValue = search.toLowerCase().trim();

    if (!searchValue) {
      return true;
    }

    return (
      person.fullName
        .toLowerCase()
        .includes(searchValue) ||
      person.email
        .toLowerCase()
        .includes(searchValue) ||
      person.role
        .toLowerCase()
        .includes(searchValue)
    );
  });

  const totalStaff = staff.length;

  const doctorCount = staff.filter(
    (person) =>
      person.role.toLowerCase() === 'doctor',
  ).length;

  const nurseCount = staff.filter(
    (person) =>
      person.role.toLowerCase() === 'nurse',
  ).length;

  const organizationCount = new Set(
    staff
      .map((person) => person.organization)
      .filter(
        (organization) =>
          organization && organization !== '-',
      ),
  ).size;

  const columns = [
    {
      name: 'ID',
      selector: (row) => row.id,
      sortable: true,
    },
    {
      name: 'Full Name',
      selector: (row) => row.fullName,
      sortable: true,
    },
    {
      name: 'Email',
      selector: (row) => row.email,
    },
    {
      name: 'Role',
      selector: (row) => row.role,
      sortable: true,
    },
    {
      name: 'Organization',
      selector: (row) => row.organization,
      sortable: true,
    },
    {
      name: 'Actions',
      cell: (row) => (
        <button
          className='btn-deactivate'
          onClick={() => setConfirmId(row.id)}
        >
          Deactivate
        </button>
      ),
    },
  ];

  return (
    <div
      style={{
        display: 'grid',
        gap: '22px',
      }}
    >
      {/* Header */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'flex-start',
          gap: '20px',
        }}
      >
        <div>
          <p
            style={{
              margin: '0 0 8px',
              textTransform: 'uppercase',
              letterSpacing: '0.08em',
              fontSize: '0.8rem',
              fontWeight: 700,
              color: 'var(--primary)',
            }}
          >
            Guardian Monitor Admin
          </p>

          <h1
            style={{
              margin: 0,
              fontSize: '2rem',
              color: 'var(--primary-dark)',
            }}
          >
            Staff Management
          </h1>

          <p
            style={{
              margin: '10px 0 0',
              color: 'var(--text-muted)',
              maxWidth: '720px',
            }}
          >
            Manage staff members, roles, and organisation assignments from the
            admin workspace.
          </p>
        </div>

        <Button onClick={handleOpenAddStaff}>
          <UserRoundPlus size={18} />
          Add Staff
        </Button>
      </div>

      {/* Summary cards */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns:
            'repeat(auto-fit, minmax(180px, 1fr))',
          gap: '16px',
        }}
      >
        <div
          style={{
            background: 'var(--surface)',
            border: '1px solid var(--border)',
            borderRadius: '20px',
            padding: '18px',
            boxShadow: 'var(--shadow-sm)',
            display: 'flex',
            gap: '12px',
            alignItems: 'center',
          }}
        >
          <Users size={18} />

          <div>
            <strong
              style={{
                display: 'block',
                fontSize: '1.4rem',
                color: 'var(--primary-dark)',
              }}
            >
              {totalStaff}
            </strong>

            <span
              style={{
                color: 'var(--text-muted)',
                fontSize: '0.92rem',
              }}
            >
              Total Staff
            </span>
          </div>
        </div>

        <div
          style={{
            background: 'var(--surface)',
            border: '1px solid var(--border)',
            borderRadius: '20px',
            padding: '18px',
            boxShadow: 'var(--shadow-sm)',
            display: 'flex',
            gap: '12px',
            alignItems: 'center',
          }}
        >
          <Stethoscope size={18} />

          <div>
            <strong
              style={{
                display: 'block',
                fontSize: '1.4rem',
                color: 'var(--primary-dark)',
              }}
            >
              {doctorCount}
            </strong>

            <span
              style={{
                color: 'var(--text-muted)',
                fontSize: '0.92rem',
              }}
            >
              Doctors
            </span>
          </div>
        </div>

        <div
          style={{
            background: 'var(--surface)',
            border: '1px solid var(--border)',
            borderRadius: '20px',
            padding: '18px',
            boxShadow: 'var(--shadow-sm)',
            display: 'flex',
            gap: '12px',
            alignItems: 'center',
          }}
        >
          <HeartPulse size={18} />

          <div>
            <strong
              style={{
                display: 'block',
                fontSize: '1.4rem',
                color: 'var(--primary-dark)',
              }}
            >
              {nurseCount}
            </strong>

            <span
              style={{
                color: 'var(--text-muted)',
                fontSize: '0.92rem',
              }}
            >
              Nurses
            </span>
          </div>
        </div>

        <div
          style={{
            background: 'var(--surface)',
            border: '1px solid var(--border)',
            borderRadius: '20px',
            padding: '18px',
            boxShadow: 'var(--shadow-sm)',
            display: 'flex',
            gap: '12px',
            alignItems: 'center',
          }}
        >
          <Building2 size={18} />

          <div>
            <strong
              style={{
                display: 'block',
                fontSize: '1.4rem',
                color: 'var(--primary-dark)',
              }}
            >
              {organizationCount}
            </strong>

            <span
              style={{
                color: 'var(--text-muted)',
                fontSize: '0.92rem',
              }}
            >
              Organizations
            </span>
          </div>
        </div>
      </div>

      {/* Search and filters */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          gap: '16px',
          alignItems: 'center',
        }}
      >
        <div style={{ minWidth: '340px' }}>
          <InputField
            label='Search'
            name='search'
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPage(1);
            }}
            placeholder='Search by name or email...'
          />
        </div>

        <div
          style={{
            display: 'flex',
            gap: '12px',
            alignItems: 'center',
          }}
        >
          <div style={{ width: '220px' }}>
            <Dropdown
              label='Filter by Role'
              name='roleFilter'
              value={roleFilter}
              onChange={(e) => {
                setRoleFilter(e.target.value);
                setPage(1);
              }}
              options={ROLE_FILTER_OPTIONS}
            />
          </div>

          <div style={{ width: '240px' }}>
            <Dropdown
              label='Filter by Organization'
              name='orgFilter'
              value={orgFilter}
              onChange={(e) => {
                setOrgFilter(e.target.value);
                setPage(1);
              }}
              options={[
                {
                  value: '',
                  label: 'All Organizations',
                },
                ...orgOptions,
              ]}
              placeholder={
                orgLoading
                  ? 'Loading...'
                  : 'All Organizations'
              }
              disabled={orgLoading}
            />
          </div>
        </div>
      </div>

      {/* Staff List */}
      <div
        style={{
          background: 'var(--surface)',
          border: '1px solid var(--border)',
          borderRadius: '24px',
          boxShadow: 'var(--shadow-sm)',
          overflow: 'hidden',
        }}
      >
        <div
          style={{
            padding: '22px 22px 10px',
          }}
        >
          <h3
            style={{
              margin: 0,
              color: 'var(--primary-dark)',
            }}
          >
            Staff List
          </h3>

          <p
            style={{
              margin: '6px 0 0',
              color: 'var(--text-muted)',
            }}
          >
            Current staff members available in the administrator workspace.
          </p>
        </div>

        <DataTable
          columns={columns}
          data={filteredStaff}
          loading={loading}
          loadingMessage='Loading staff...'
          totalRows={filteredStaff.length}
          onChangePage={(newPage) => setPage(newPage)}
          persistTableHead
          noDataComponent={
            <div
              style={{
                textAlign: 'center',
                color: 'var(--text-muted)',
                padding: '28px',
              }}
            >
              {search
                ? `No staff members found matching "${search}".`
                : 'No staff members available.'}
            </div>
          }
        />
      </div>

      {/* Deactivate confirmation */}
      <Toast
        open={confirmId !== null}
        title='Deactivate Staff'
        message='Are you sure you want to deactivate this staff member?'
        confirmLabel='Deactivate'
        onConfirm={handleConfirmDeactivate}
        onCancel={() => setConfirmId(null)}
      />

      {/* Add Staff Modal */}
      <Modal
        open={modalOpen}
        onClose={handleClose}
        title='Add Staff'
      >
        <div>
          <InputField
            label='Search Staff Member'
            name='candidateSearch'
            value={candidateSearch}
            onChange={(e) => {
              setCandidateSearch(e.target.value);

              if (form.userId) {
                setForm(emptyForm);
              }

              setErrors(emptyErrors);
            }}
            placeholder='Search by name, email or role...'
            disabled={candidatesLoading}
            error={errors.userId}
          />

          {candidatesLoading && (
            <p style={{ marginTop: '10px' }}>
              Loading available staff members...
            </p>
          )}

          {candidatesError && (
            <p
              role='alert'
              style={{
                marginTop: '10px',
                color: '#b42318',
              }}
            >
              {candidatesError}
            </p>
          )}

          {!candidatesLoading && !candidatesError && (
            <div
              style={{
                maxHeight: '240px',
                overflowY: 'auto',
                marginTop: '10px',
                border: '1px solid #d0d5dd',
                borderRadius: '10px',
              }}
            >
              {filteredCandidates.length > 0 ? (
                filteredCandidates.map((person) => (
                  <button
                    key={person.id}
                    type='button'
                    onClick={() => {
                      setForm({
                        userId: person.id,
                      });

                      setCandidateSearch(
                        person.fullName,
                      );

                      setErrors(emptyErrors);
                    }}
                    style={{
                      display: 'block',
                      width: '100%',
                      padding: '12px',
                      textAlign: 'left',
                      border: 'none',
                      borderBottom:
                        '1px solid #eaecf0',
                      background:
                        form.userId === person.id
                          ? '#eef8ff'
                          : '#ffffff',
                      cursor: 'pointer',
                    }}
                  >
                    <strong>
                      {person.fullName}
                    </strong>

                    <div
                      style={{
                        marginTop: '4px',
                        color: '#667085',
                        fontSize: '13px',
                      }}
                    >
                      {[
                        person.email,
                        person.role,
                      ]
                        .filter(Boolean)
                        .join(' • ')}
                    </div>
                  </button>
                ))
              ) : (
                <p
                  style={{
                    padding: '12px',
                    margin: 0,
                  }}
                >
                  No matching staff members found.
                </p>
              )}
            </div>
          )}

          {selectedCandidate && (
            <p
              style={{
                marginTop: '10px',
                color: '#027a48',
              }}
            >
              Selected: {selectedCandidate.fullName}
            </p>
          )}
        </div>

        <div className='modal-footer'>
          <button
            className='btn-secondary'
            style={{ padding: '12px 18px' }}
            onClick={handleClose}
          >
            Cancel
          </button>

          <Button
            onClick={handleSave}
            disabled={
              !form.userId || candidatesLoading
            }
            style={{ padding: '12px 18px' }}
          >
            Save
          </Button>
        </div>
      </Modal>

      {/* Success notification */}
      <Toast
        open={successOpen}
        variant='success'
        title='Staff Added'
        message='Staff member has been successfully added.'
        confirmLabel='OK'
        onConfirm={() => setSuccessOpen(false)}
      />
    </div>
  );
}