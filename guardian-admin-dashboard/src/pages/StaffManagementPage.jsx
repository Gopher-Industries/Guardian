import { useState, useEffect } from 'react';
import { UserRoundPlus } from 'lucide-react';
import DataTable from 'react-data-table-component';
import Modal from '../components/common/Modal';
import Button from '../components/common/Button';
import InputField from '../components/common/InputField';
import Dropdown from '../components/common/Dropdown';
import { getMyOrganizations } from '../services/orgService';
import { ROLE_OPTIONS } from '../utils/constants';

const initialData = [
  {
    id: 1,
    fullName: 'Alice Johnson',
    email: 'alice.johnson@guardian.com',
    role: 'Nurse',
    organization: 'Guardian Health',
    active: true,
  },
  {
    id: 2,
    fullName: 'Bob Smith',
    email: 'bob.smith@guardian.com',
    role: 'Doctor',
    organization: 'Guardian Health',
    active: true,
  },
  {
    id: 3,
    fullName: 'Carol White',
    email: 'carol.white@deakin.com',
    role: 'Administrator',
    organization: 'Deakin Medical Centre',
    active: true,
  },
  {
    id: 4,
    fullName: 'David Lee',
    email: 'david.lee@sunrise.com',
    role: 'Carer',
    organization: 'Sunrise Aged Care',
    active: true,
  },
  {
    id: 5,
    fullName: 'Emma Brown',
    email: 'emma.brown@guardian.com',
    role: 'Nurse',
    organization: 'Guardian Health',
    active: false,
  },
  {
    id: 6,
    fullName: 'Frank Wilson',
    email: 'frank.wilson@deakin.com',
    role: 'Doctor',
    organization: 'Deakin Medical Centre',
    active: true,
  },
  {
    id: 7,
    fullName: 'Grace Hall',
    email: 'grace.hall@sunrise.com',
    role: 'Carer',
    organization: 'Sunrise Aged Care',
    active: true,
  },
  {
    id: 8,
    fullName: 'Henry Adams',
    email: 'henry.adams@guardian.com',
    role: 'Administrator',
    organization: 'Guardian Health',
    active: false,
  },
  {
    id: 9,
    fullName: 'Iris Martinez',
    email: 'iris.martinez@deakin.com',
    role: 'Nurse',
    organization: 'Deakin Medical Centre',
    active: true,
  },
  {
    id: 10,
    fullName: 'James Taylor',
    email: 'james.taylor@sunrise.com',
    role: 'Doctor',
    organization: 'Sunrise Aged Care',
    active: true,
  },
  {
    id: 11,
    fullName: 'Karen Thomas',
    email: 'karen.thomas@guardian.com',
    role: 'Carer',
    organization: 'Guardian Health',
    active: true,
  },
  {
    id: 12,
    fullName: 'Liam Jackson',
    email: 'liam.jackson@deakin.com',
    role: 'Nurse',
    organization: 'Deakin Medical Centre',
    active: false,
  },
  {
    id: 13,
    fullName: 'Mia Harris',
    email: 'mia.harris@sunrise.com',
    role: 'Doctor',
    organization: 'Sunrise Aged Care',
    active: true,
  },
  {
    id: 14,
    fullName: 'Noah Clark',
    email: 'noah.clark@guardian.com',
    role: 'Administrator',
    organization: 'Guardian Health',
    active: true,
  },
  {
    id: 15,
    fullName: 'Olivia Lewis',
    email: 'olivia.lewis@deakin.com',
    role: 'Carer',
    organization: 'Deakin Medical Centre',
    active: true,
  },
  {
    id: 16,
    fullName: 'Peter Robinson',
    email: 'peter.robinson@sunrise.com',
    role: 'Nurse',
    organization: 'Sunrise Aged Care',
    active: false,
  },
  {
    id: 17,
    fullName: 'Quinn Walker',
    email: 'quinn.walker@guardian.com',
    role: 'Doctor',
    organization: 'Guardian Health',
    active: true,
  },
  {
    id: 18,
    fullName: 'Rachel Young',
    email: 'rachel.young@deakin.com',
    role: 'Administrator',
    organization: 'Deakin Medical Centre',
    active: true,
  },
  {
    id: 19,
    fullName: 'Samuel King',
    email: 'samuel.king@sunrise.com',
    role: 'Carer',
    organization: 'Sunrise Aged Care',
    active: true,
  },
  {
    id: 20,
    fullName: 'Tara Scott',
    email: 'tara.scott@guardian.com',
    role: 'Nurse',
    organization: 'Guardian Health',
    active: false,
  },
];

const emptyForm = { fullName: '', email: '', role: '', organization: '' };
const emptyErrors = { fullName: '', email: '', role: '', organization: '' };

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const NAME_REGEX = /^[a-zA-Z\s'-]+$/;

function TableLoader() {
  return (
    <div className='table-loader'>
      <div className='table-loader-spinner' />
      <span>Loading staff...</span>
    </div>
  );
}

export default function StaffManagementPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState(emptyErrors);
  const [staff, setStaff] = useState([]);
  const [loading, setLoading] = useState(true);
  const [orgOptions, setOrgOptions] = useState([]);
  const [orgLoading, setOrgLoading] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setStaff(initialData);
      setLoading(false);
    }, 1200);
    return () => clearTimeout(timer);
  }, []);

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
    if (!fields.fullName.trim()) {
      errs.fullName = 'Full name is required.';
    } else if (fields.fullName.trim().length < 2) {
      errs.fullName = 'Full name must be at least 2 characters.';
    } else if (!NAME_REGEX.test(fields.fullName.trim())) {
      errs.fullName =
        'Full name can only contain letters, spaces, hyphens, and apostrophes.';
    }

    if (!fields.email.trim()) {
      errs.email = 'Email is required.';
    } else if (!EMAIL_REGEX.test(fields.email.trim())) {
      errs.email = 'Enter a valid email address.';
    } else if (
      staff.some(
        (s) => s.email.toLowerCase() === fields.email.trim().toLowerCase(),
      )
    ) {
      errs.email = 'A staff member with this email already exists.';
    }

    if (!fields.role) errs.role = 'Please select a role.';
    if (!fields.organization)
      errs.organization = 'Please select an organization.';

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

  function handleSave() {
    const errs = validate(form);
    if (Object.values(errs).some(Boolean)) {
      setErrors(errs);
      return;
    }
    setStaff((prev) => [
      ...prev,
      { id: prev.length + 1, ...form, active: true },
    ]);
    handleClose();
  }

  function toggleActive(id) {
    setStaff((prev) =>
      prev.map((s) => (s.id === id ? { ...s, active: !s.active } : s)),
    );
  }

  const columns = [
    {
      name: 'ID',
      selector: (row) => row.id,
      width: '70px',
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
          className={row.active ? 'btn-deactivate' : 'btn-activate'}
          onClick={() => toggleActive(row.id)}
        >
          {row.active ? 'Deactivate' : 'Activate'}
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
          marginBottom: '14px',
        }}
      >
        <h3 style={{ margin: 0 }}>Staff Management</h3>
        <Button onClick={() => setModalOpen(true)}>
          <UserRoundPlus size={18} />
          Add Staff
        </Button>
      </div>

      <DataTable
        columns={columns}
        data={staff}
        progressPending={loading}
        progressComponent={<TableLoader />}
        pagination
        paginationPerPage={10}
        paginationRowsPerPageOptions={[10, 20]}
      />

      <Modal open={modalOpen} onClose={handleClose} title='Add Staff'>
        <InputField
          label='Full Name'
          name='fullName'
          value={form.fullName}
          onChange={handleChange}
          placeholder='e.g. Alice Johnson'
          error={errors.fullName}
        />
        <InputField
          label='Email'
          type='email'
          name='email'
          value={form.email}
          onChange={handleChange}
          placeholder='e.g. alice@guardian.com'
          error={errors.email}
        />
        <Dropdown
          label='Role'
          name='role'
          value={form.role}
          onChange={handleChange}
          options={ROLE_OPTIONS}
          placeholder='Select a role'
          error={errors.role}
        />
        <Dropdown
          label='Organization'
          name='organization'
          value={form.organization}
          onChange={handleChange}
          options={orgOptions}
          placeholder={
            orgLoading ? 'Loading organizations...' : 'Select an organization'
          }
          error={errors.organization}
          disabled={orgLoading}
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
    </div>
  );
}
