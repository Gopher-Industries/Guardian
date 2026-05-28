import { useState, useEffect, useCallback } from 'react';
import { UserRoundPlus } from 'lucide-react';
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

export default function StaffManagementPage() {
  const [staff, setStaff] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [totalRows, setTotalRows] = useState(0);
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
        ROLE_OPTIONS.some((r) => r.value === s.role.toLowerCase()),
      );
      setStaff(filtered);
      setTotalRows(data.pagination?.total ?? 0);
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
        setOrgOptions(orgs.map((o) => ({ value: o._id, label: o.name })));
      } catch (err) {
        console.error('Failed to load organizations:', err);
      } finally {
        setOrgLoading(false);
      }
    };
    fetchOrgs();
  }, []);

  function validate(fields) {
    const errs = { ...emptyErrors };
    if (!fields.userId.trim()) {
      errs.userId = 'User ID is required.';
    }
    return errs;
  }

  function handleChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: '' }));
  }

  function handleClose() {
    setModalOpen(false);
    setForm(emptyForm);
    setErrors(emptyErrors);
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
      fetchStaff();
    } catch (err) {
      console.error('Failed to add staff:', err);
    }
  }

  async function handleConfirmDeactivate() {
    try {
      await deactivateStaff(confirmId);
      setStaff((prev) => prev.filter((s) => s.id !== confirmId));
    } catch (err) {
      console.error('Failed to deactivate staff:', err);
    } finally {
      setConfirmId(null);
    }
  }

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
        <button className='btn-deactivate' onClick={() => setConfirmId(row.id)}>
          Deactivate
        </button>
      ),
    },
  ];

  return (
    <div className='panel'>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '28px',
        }}
      >
        <h3 style={{ margin: 0 }}>Staff Management</h3>
        <Button onClick={() => setModalOpen(true)}>
          <UserRoundPlus size={18} />
          Add Staff
        </Button>
      </div>

      <div
        style={{
          display: 'flex',
          gap: '16px',
          marginBottom: '12px',
          alignItems: 'flex-end',
          justifyContent: 'space-between',
        }}
      >
        <div style={{ maxWidth: '240px' }}>
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
        <div style={{ display: 'flex', gap: '16px', alignItems: 'flex-end' }}>
          <div style={{ width: '200px' }}>
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
          <div style={{ width: '200px' }}>
            <Dropdown
              label='Filter by Organization'
              name='orgFilter'
              value={orgFilter}
              onChange={(e) => {
                setOrgFilter(e.target.value);
                setPage(1);
              }}
              options={[
                { value: '', label: 'All Organizations' },
                ...orgOptions,
              ]}
              placeholder={orgLoading ? 'Loading...' : 'All Organizations'}
              disabled={orgLoading}
            />
          </div>
        </div>
      </div>

      <DataTable
        columns={columns}
        data={staff}
        loading={loading}
        loadingMessage='Loading staff...'
        totalRows={totalRows}
        onChangePage={(newPage) => setPage(newPage)}
      />

      <Toast
        open={confirmId !== null}
        title='Deactivate Staff'
        message='Are you sure you want to deactivate this staff member?'
        confirmLabel='Deactivate'
        onConfirm={handleConfirmDeactivate}
        onCancel={() => setConfirmId(null)}
      />

      <Modal open={modalOpen} onClose={handleClose} title='Add Staff'>
        <InputField
          label='User ID'
          name='userId'
          value={form.userId}
          onChange={handleChange}
          placeholder='e.g. 64a1f2b3c4d5e6f7a8b9c0d1'
          error={errors.userId}
        />
        <div className='modal-footer'>
          <button
            className='btn-secondary'
            style={{ padding: '12px 18px' }}
            onClick={handleClose}
          >
            Cancel
          </button>
          <Button onClick={handleSave} style={{ padding: '12px 18px' }}>
            Save
          </Button>
        </div>
      </Modal>

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
