import { useState } from 'react';
import { UserRoundPlus } from 'lucide-react';
import DataTable from 'react-data-table-component';
import Modal from '../components/common/Modal';
import Button from '../components/common/Button';
import InputField from '../components/common/InputField';
import Dropdown from '../components/common/Dropdown';

const ROLE_OPTIONS = [
  { value: 'Doctor', label: 'Doctor' },
  { value: 'Nurse', label: 'Nurse' },
  { value: 'Carer', label: 'Carer' },
  { value: 'Administrator', label: 'Administrator' },
];

const ORGANIZATION_OPTIONS = [
  { value: 'Guardian Health', label: 'Guardian Health' },
  { value: 'Deakin Medical Centre', label: 'Deakin Medical Centre' },
  { value: 'Sunrise Aged Care', label: 'Sunrise Aged Care' },
];

const initialData = [
  { id: 1,  fullName: 'Alice Johnson',    email: 'alice.johnson@guardian.com',    role: 'Nurse',         organization: 'Guardian Health',       active: true  },
  { id: 2,  fullName: 'Bob Smith',        email: 'bob.smith@guardian.com',        role: 'Doctor',        organization: 'Guardian Health',       active: true  },
  { id: 3,  fullName: 'Carol White',      email: 'carol.white@deakin.com',        role: 'Administrator', organization: 'Deakin Medical Centre', active: true  },
  { id: 4,  fullName: 'David Lee',        email: 'david.lee@sunrise.com',         role: 'Carer',         organization: 'Sunrise Aged Care',     active: true  },
  { id: 5,  fullName: 'Emma Brown',       email: 'emma.brown@guardian.com',       role: 'Nurse',         organization: 'Guardian Health',       active: false },
  { id: 6,  fullName: 'Frank Wilson',     email: 'frank.wilson@deakin.com',       role: 'Doctor',        organization: 'Deakin Medical Centre', active: true  },
  { id: 7,  fullName: 'Grace Hall',       email: 'grace.hall@sunrise.com',        role: 'Carer',         organization: 'Sunrise Aged Care',     active: true  },
  { id: 8,  fullName: 'Henry Adams',      email: 'henry.adams@guardian.com',      role: 'Administrator', organization: 'Guardian Health',       active: false },
  { id: 9,  fullName: 'Iris Martinez',    email: 'iris.martinez@deakin.com',      role: 'Nurse',         organization: 'Deakin Medical Centre', active: true  },
  { id: 10, fullName: 'James Taylor',     email: 'james.taylor@sunrise.com',      role: 'Doctor',        organization: 'Sunrise Aged Care',     active: true  },
  { id: 11, fullName: 'Karen Thomas',     email: 'karen.thomas@guardian.com',     role: 'Carer',         organization: 'Guardian Health',       active: true  },
  { id: 12, fullName: 'Liam Jackson',     email: 'liam.jackson@deakin.com',       role: 'Nurse',         organization: 'Deakin Medical Centre', active: false },
  { id: 13, fullName: 'Mia Harris',       email: 'mia.harris@sunrise.com',        role: 'Doctor',        organization: 'Sunrise Aged Care',     active: true  },
  { id: 14, fullName: 'Noah Clark',       email: 'noah.clark@guardian.com',       role: 'Administrator', organization: 'Guardian Health',       active: true  },
  { id: 15, fullName: 'Olivia Lewis',     email: 'olivia.lewis@deakin.com',       role: 'Carer',         organization: 'Deakin Medical Centre', active: true  },
  { id: 16, fullName: 'Peter Robinson',   email: 'peter.robinson@sunrise.com',    role: 'Nurse',         organization: 'Sunrise Aged Care',     active: false },
  { id: 17, fullName: 'Quinn Walker',     email: 'quinn.walker@guardian.com',     role: 'Doctor',        organization: 'Guardian Health',       active: true  },
  { id: 18, fullName: 'Rachel Young',     email: 'rachel.young@deakin.com',       role: 'Administrator', organization: 'Deakin Medical Centre', active: true  },
  { id: 19, fullName: 'Samuel King',      email: 'samuel.king@sunrise.com',       role: 'Carer',         organization: 'Sunrise Aged Care',     active: true  },
  { id: 20, fullName: 'Tara Scott',       email: 'tara.scott@guardian.com',       role: 'Nurse',         organization: 'Guardian Health',       active: false },
];

const emptyForm = { fullName: '', email: '', role: '', organization: '' };

export default function StaffManagementPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [staff, setStaff] = useState(initialData);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  function handleClose() {
    setModalOpen(false);
    setForm(emptyForm);
  }

  function handleSave() {
    if (!form.fullName || !form.email || !form.role || !form.organization) return;
    setStaff((prev) => ([
      ...prev,
      { id: prev.length + 1, ...form, active: true },
    ]));
    handleClose();
  }

  function toggleActive(id) {
    setStaff((prev) =>
      prev.map((s) => (s.id === id ? { ...s, active: !s.active } : s))
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
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '14px' }}>
        <h3 style={{ margin: 0 }}>Staff Management</h3>
        <Button onClick={() => setModalOpen(true)}>
          <UserRoundPlus size={18} />
          Add Staff
        </Button>
      </div>

      <DataTable columns={columns} data={staff} pagination paginationPerPage={10} paginationRowsPerPageOptions={[10, 20]} />

      <Modal open={modalOpen} onClose={handleClose} title="Add Staff">
        <InputField
          label="Full Name"
          name="fullName"
          value={form.fullName}
          onChange={handleChange}
          placeholder="e.g. Alice Johnson"
        />
        <InputField
          label="Email"
          type="email"
          name="email"
          value={form.email}
          onChange={handleChange}
          placeholder="e.g. alice@guardian.com"
        />
        <Dropdown
          label="Role"
          name="role"
          value={form.role}
          onChange={handleChange}
          options={ROLE_OPTIONS}
          placeholder="Select a role"
        />
        <Dropdown
          label="Organization"
          name="organization"
          value={form.organization}
          onChange={handleChange}
          options={ORGANIZATION_OPTIONS}
          placeholder="Select an organization"
        />
        <div className="modal-footer">
          <button className="btn-secondary" style={{ padding: '12px 18px' }} onClick={handleClose}>Cancel</button>
          <Button onClick={handleSave} style={{ padding: '12px 18px' }}>Save</Button>
        </div>
      </Modal>
    </div>
  );
}
