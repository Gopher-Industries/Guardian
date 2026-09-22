import { useState, useEffect, useCallback } from 'react';
import { TicketPlus, Pencil } from 'lucide-react';
import DataTable from '../components/common/DataTable';
import Modal from '../components/common/Modal';
import Toast from '../components/common/Toast';
import Button from '../components/common/Button';
import InputField from '../components/common/InputField';
import Dropdown from '../components/common/Dropdown';
import {
  // getSupportTickets,
  createSupportTicket,
  updateSupportTicket,
} from '../services/supportTicketService';

// TODO: remove sample data when backend is ready
const SAMPLE_TICKETS = [
  { _id: 'TKT-001', title: 'Login page not loading', issueType: 'technical', status: 'open', priority: 'high', createdAt: '2026-04-01', assignedTo: { fullname: 'Alice Johnson' }, description: 'Users are unable to load the login page on Safari.' },
  { _id: 'TKT-002', title: 'Incorrect billing amount', issueType: 'billing', status: 'in_progress', priority: 'critical', createdAt: '2026-04-02', assignedTo: { fullname: 'Bob Smith' }, description: 'Patient was charged twice for the same service.' },
  { _id: 'TKT-003', title: 'Dashboard charts not rendering', issueType: 'technical', status: 'resolved', priority: 'medium', createdAt: '2026-04-03', assignedTo: null, description: '' },
  { _id: 'TKT-004', title: 'Add export to CSV feature', issueType: 'feature_request', status: 'open', priority: 'low', createdAt: '2026-04-04', assignedTo: { fullname: 'Carol White' }, description: 'Ability to export patient records to CSV.' },
  { _id: 'TKT-005', title: 'Password reset email not received', issueType: 'technical', status: 'closed', priority: 'high', createdAt: '2026-04-05', assignedTo: { fullname: 'David Lee' }, description: '' },
  { _id: 'TKT-006', title: 'General enquiry about system limits', issueType: 'general', status: 'open', priority: 'low', createdAt: '2026-04-06', assignedTo: null, description: 'How many users can be active at the same time?' },
  { _id: 'TKT-007', title: 'Nurse roster not saving changes', issueType: 'technical', status: 'in_progress', priority: 'high', createdAt: '2026-04-07', assignedTo: { fullname: 'Emma Davis' }, description: 'Changes to roster are lost after page refresh.' },
  { _id: 'TKT-008', title: 'Subscription renewal failed', issueType: 'billing', status: 'open', priority: 'critical', createdAt: '2026-04-08', assignedTo: { fullname: 'Frank Moore' }, description: '' },
  { _id: 'TKT-009', title: 'Dark mode request', issueType: 'feature_request', status: 'closed', priority: 'low', createdAt: '2026-04-09', assignedTo: null, description: 'Would like a dark mode option in settings.' },
  { _id: 'TKT-010', title: 'Patient records missing after migration', issueType: 'technical', status: 'in_progress', priority: 'critical', createdAt: '2026-04-10', assignedTo: { fullname: 'Grace Kim' }, description: 'Several patient records were not migrated.' },
  { _id: 'TKT-011', title: 'Invoice PDF download broken', issueType: 'billing', status: 'resolved', priority: 'medium', createdAt: '2026-04-11', assignedTo: { fullname: 'Henry Clark' }, description: 'PDF download returns a 404 error.' },
  { _id: 'TKT-012', title: 'Add bulk user import', issueType: 'feature_request', status: 'open', priority: 'medium', createdAt: '2026-04-12', assignedTo: null, description: 'Support CSV upload to add multiple users at once.' },
  { _id: 'TKT-013', title: 'MFA not working for some accounts', issueType: 'technical', status: 'in_progress', priority: 'high', createdAt: '2026-04-13', assignedTo: { fullname: 'Isla Turner' }, description: '' },
  { _id: 'TKT-014', title: 'Billing cycle dates incorrect', issueType: 'billing', status: 'open', priority: 'medium', createdAt: '2026-04-14', assignedTo: null, description: 'Billing date shows previous month.' },
  { _id: 'TKT-015', title: 'Support for SSO integration', issueType: 'feature_request', status: 'resolved', priority: 'high', createdAt: '2026-04-15', assignedTo: { fullname: 'Jack Wilson' }, description: 'Request to integrate with Okta SSO.' },
  { _id: 'TKT-016', title: 'Notification emails going to spam', issueType: 'technical', status: 'open', priority: 'medium', createdAt: '2026-04-16', assignedTo: { fullname: 'Karen Hall' }, description: '' },
  { _id: 'TKT-017', title: 'Account access for new admin', issueType: 'general', status: 'closed', priority: 'low', createdAt: '2026-04-17', assignedTo: null, description: 'How do I grant admin access to a new user?' },
  { _id: 'TKT-018', title: 'Reports page timeout error', issueType: 'technical', status: 'in_progress', priority: 'high', createdAt: '2026-04-18', assignedTo: { fullname: 'Liam Young' }, description: 'Report generation times out for large date ranges.' },
  { _id: 'TKT-019', title: 'Refund request for duplicate charge', issueType: 'billing', status: 'resolved', priority: 'critical', createdAt: '2026-04-19', assignedTo: { fullname: 'Mia Scott' }, description: 'Customer was charged twice in March.' },
  { _id: 'TKT-020', title: 'Mobile responsive layout issues', issueType: 'technical', status: 'open', priority: 'medium', createdAt: '2026-04-20', assignedTo: { fullname: 'Noah Adams' }, description: 'Several panels overflow on small screens.' },
];

import {
  TICKET_ISSUE_TYPE_OPTIONS,
  TICKET_PRIORITY_OPTIONS,
  TICKET_STATUS_OPTIONS,
} from '../utils/constants';

const emptyCreateForm = { title: '', issueType: '', priority: '', description: '' };
const emptyCreateErrors = { title: '', issueType: '', priority: '' };
const emptyEditForm = { title: '', issueType: '', priority: '', status: '', description: '' };
const emptyEditErrors = { title: '', issueType: '', priority: '', status: '' };

const STATUS_FILTER_OPTIONS = [{ value: '', label: 'All Statuses' }, ...TICKET_STATUS_OPTIONS];
const PRIORITY_FILTER_OPTIONS = [{ value: '', label: 'All Priorities' }, ...TICKET_PRIORITY_OPTIONS];

const STATUS_COLORS = {
  open: { background: '#e8f4fd', color: '#1a6fa8' },
  in_progress: { background: '#fff3e0', color: '#b45309' },
  resolved: { background: '#e6f4ea', color: '#2e7d32' },
  closed: { background: '#f3f4f6', color: '#6b7280' },
};

const PRIORITY_COLORS = {
  low: { background: '#e6f4ea', color: '#2e7d32' },
  medium: { background: '#fff3e0', color: '#b45309' },
  high: { background: '#fde8e8', color: '#b91c1c' },
  critical: { background: '#fce7f3', color: '#9d174d' },
};

function StatusBadge({ value }) {
  const style = STATUS_COLORS[value] ?? {};
  const label =
    TICKET_STATUS_OPTIONS.find((o) => o.value === value)?.label ?? value ?? '-';
  return (
    <span
      style={{
        display: 'inline-block',
        padding: '3px 10px',
        borderRadius: '12px',
        fontSize: '12px',
        fontWeight: 500,
        ...style,
      }}
    >
      {label}
    </span>
  );
}

function PriorityBadge({ value }) {
  const style = PRIORITY_COLORS[value] ?? {};
  const label =
    TICKET_PRIORITY_OPTIONS.find((o) => o.value === value)?.label ?? value ?? '-';
  return (
    <span
      style={{
        display: 'inline-block',
        padding: '3px 10px',
        borderRadius: '12px',
        fontSize: '12px',
        fontWeight: 500,
        ...style,
      }}
    >
      {label}
    </span>
  );
}

function formatTickets(raw) {
  return raw.map((t) => ({
    id: t._id,
    title: t.title ?? '-',
    issueType:
      TICKET_ISSUE_TYPE_OPTIONS.find((o) => o.value === t.issueType)?.label ??
      t.issueType ??
      '-',
    issueTypeRaw: t.issueType ?? '',
    status: t.status ?? '',
    priority: t.priority ?? '',
    createdAt: t.createdAt ? new Date(t.createdAt).toLocaleDateString() : '-',
    assignedTo: t.assignedTo?.fullname ?? '-',
    description: t.description ?? '',
  }));
}

export default function SupportTicketPage() {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [totalRows, setTotalRows] = useState(0);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [priorityFilter, setPriorityFilter] = useState('');

  const [createOpen, setCreateOpen] = useState(false);
  const [createForm, setCreateForm] = useState(emptyCreateForm);
  const [createErrors, setCreateErrors] = useState(emptyCreateErrors);
  const [createLoading, setCreateLoading] = useState(false);

  const [selectedTicket, setSelectedTicket] = useState(null);
  const [editForm, setEditForm] = useState(emptyEditForm);
  const [editErrors, setEditErrors] = useState(emptyEditErrors);
  const [editLoading, setEditLoading] = useState(false);

  const [successOpen, setSuccessOpen] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  const fetchTickets = useCallback(() => {
    setLoading(true);
    // TODO: replace with API call when backend is ready
    // const data = await getSupportTickets({ page, limit: 10, search, status: statusFilter, priority: priorityFilter });
    // setTickets(formatTickets(data.tickets ?? data.data ?? []));
    // setTotalRows(data.pagination?.total ?? 0);
    let filtered = formatTickets(SAMPLE_TICKETS);
    if (search) filtered = filtered.filter((t) => t.title.toLowerCase().includes(search.toLowerCase()));
    if (statusFilter) filtered = filtered.filter((t) => t.status === statusFilter);
    if (priorityFilter) filtered = filtered.filter((t) => t.priority === priorityFilter);
    const start = (page - 1) * 10;
    setTotalRows(filtered.length);
    setTickets(filtered.slice(start, start + 10));
    setLoading(false);
  }, [page, search, statusFilter, priorityFilter]);

  useEffect(() => {
    fetchTickets();
  }, [fetchTickets]);

  function validateCreate(fields) {
    const errs = { ...emptyCreateErrors };
    if (!fields.title.trim()) errs.title = 'Title is required.';
    if (!fields.issueType) errs.issueType = 'Issue type is required.';
    if (!fields.priority) errs.priority = 'Priority is required.';
    return errs;
  }

  function handleCreateChange(e) {
    const { name, value } = e.target;
    setCreateForm((prev) => ({ ...prev, [name]: value }));
    setCreateErrors((prev) => ({ ...prev, [name]: '' }));
  }

  function handleCreateClose() {
    setCreateOpen(false);
    setCreateForm(emptyCreateForm);
    setCreateErrors(emptyCreateErrors);
  }

  async function handleCreateSave() {
    const errs = validateCreate(createForm);
    if (Object.values(errs).some(Boolean)) {
      setCreateErrors(errs);
      return;
    }
    setCreateLoading(true);
    try {
      await createSupportTicket({
        title: createForm.title.trim(),
        issueType: createForm.issueType,
        priority: createForm.priority,
        ...(createForm.description.trim() && {
          description: createForm.description.trim(),
        }),
      });
      handleCreateClose();
      setSuccessMessage('Support ticket has been successfully created.');
      setSuccessOpen(true);
      fetchTickets();
    } catch (err) {
      console.error('Failed to create support ticket:', err);
    } finally {
      setCreateLoading(false);
    }
  }

  function openEdit(row) {
    setSelectedTicket(row);
    setEditForm({
      title: row.title === '-' ? '' : row.title,
      issueType: row.issueTypeRaw,
      priority: row.priority,
      status: row.status,
      description: row.description,
    });
    setEditErrors(emptyEditErrors);
  }

  function validateEdit(fields) {
    const errs = { ...emptyEditErrors };
    if (!fields.title.trim()) errs.title = 'Title is required.';
    if (!fields.issueType) errs.issueType = 'Issue type is required.';
    if (!fields.priority) errs.priority = 'Priority is required.';
    if (!fields.status) errs.status = 'Status is required.';
    return errs;
  }

  function handleEditChange(e) {
    const { name, value } = e.target;
    setEditForm((prev) => ({ ...prev, [name]: value }));
    setEditErrors((prev) => ({ ...prev, [name]: '' }));
  }

  function handleEditClose() {
    setSelectedTicket(null);
    setEditForm(emptyEditForm);
    setEditErrors(emptyEditErrors);
  }

  async function handleEditSave() {
    const errs = validateEdit(editForm);
    if (Object.values(errs).some(Boolean)) {
      setEditErrors(errs);
      return;
    }
    setEditLoading(true);
    try {
      await updateSupportTicket(selectedTicket.id, {
        title: editForm.title.trim(),
        issueType: editForm.issueType,
        priority: editForm.priority,
        status: editForm.status,
        ...(editForm.description.trim() && {
          description: editForm.description.trim(),
        }),
      });
      handleEditClose();
      setSuccessMessage('Support ticket has been successfully updated.');
      setSuccessOpen(true);
      fetchTickets();
    } catch (err) {
      console.error('Failed to update support ticket:', err);
    } finally {
      setEditLoading(false);
    }
  }

  const columns = [
    {
      name: 'Title',
      selector: (row) => row.title,
      sortable: true,
      grow: 2,
    },
    {
      name: 'Issue Type',
      selector: (row) => row.issueType,
      sortable: true,
    },
    {
      name: 'Status',
      cell: (row) => <StatusBadge value={row.status} />,
      sortable: true,
    },
    {
      name: 'Priority',
      cell: (row) => <PriorityBadge value={row.priority} />,
      sortable: true,
    },
    {
      name: 'Created',
      selector: (row) => row.createdAt,
      sortable: true,
    },
    {
      name: 'Assigned To',
      selector: (row) => row.assignedTo,
    },
    {
      name: 'Actions',
      cell: (row) => (
        <button
          className='btn-deactivate'
          onClick={() => openEdit(row)}
          style={{ display: 'inline-flex', alignItems: 'center', gap: '4px' }}
        >
          <Pencil size={13} />
          Edit
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
        <h3 style={{ margin: 0 }}>Support Tickets</h3>
        <Button onClick={() => setCreateOpen(true)}>
          <TicketPlus size={18} />
          Create Ticket
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
            placeholder='Search by title...'
          />
        </div>
        <div style={{ display: 'flex', gap: '16px', alignItems: 'flex-end' }}>
          <div style={{ width: '180px' }}>
            <Dropdown
              label='Filter by Status'
              name='statusFilter'
              value={statusFilter}
              onChange={(e) => {
                setStatusFilter(e.target.value);
                setPage(1);
              }}
              options={STATUS_FILTER_OPTIONS}
            />
          </div>
          <div style={{ width: '180px' }}>
            <Dropdown
              label='Filter by Priority'
              name='priorityFilter'
              value={priorityFilter}
              onChange={(e) => {
                setPriorityFilter(e.target.value);
                setPage(1);
              }}
              options={PRIORITY_FILTER_OPTIONS}
            />
          </div>
        </div>
      </div>

      <DataTable
        columns={columns}
        data={tickets}
        loading={loading}
        loadingMessage='Loading support tickets...'
        totalRows={totalRows}
        onChangePage={(newPage) => setPage(newPage)}
      />

      <Modal open={createOpen} onClose={handleCreateClose} title='Create Support Ticket'>
        <InputField
          label='Title'
          name='title'
          value={createForm.title}
          onChange={handleCreateChange}
          placeholder='Brief summary of the issue'
          error={createErrors.title}
        />
        <Dropdown
          label='Issue Type'
          name='issueType'
          value={createForm.issueType}
          onChange={handleCreateChange}
          options={TICKET_ISSUE_TYPE_OPTIONS}
          placeholder='Select issue type'
          error={createErrors.issueType}
        />
        <Dropdown
          label='Priority'
          name='priority'
          value={createForm.priority}
          onChange={handleCreateChange}
          options={TICKET_PRIORITY_OPTIONS}
          placeholder='Select priority'
          error={createErrors.priority}
        />
        <label className='field'>
          <span className='field-label'>Description</span>
          <textarea
            className='field-input'
            name='description'
            value={createForm.description}
            onChange={handleCreateChange}
            placeholder='Optional: describe the issue in detail'
            rows={4}
            style={{ resize: 'vertical', minHeight: '80px' }}
          />
        </label>
        <div className='modal-footer'>
          <button
            className='btn-secondary'
            style={{ padding: '12px 18px' }}
            onClick={handleCreateClose}
          >
            Cancel
          </button>
          <Button
            onClick={handleCreateSave}
            disabled={createLoading}
            style={{ padding: '12px 18px' }}
          >
            {createLoading ? 'Saving...' : 'Save'}
          </Button>
        </div>
      </Modal>

      <Modal
        open={selectedTicket !== null}
        onClose={handleEditClose}
        title='Edit Support Ticket'
      >
        <InputField
          label='Title'
          name='title'
          value={editForm.title}
          onChange={handleEditChange}
          placeholder='Brief summary of the issue'
          error={editErrors.title}
        />
        <Dropdown
          label='Issue Type'
          name='issueType'
          value={editForm.issueType}
          onChange={handleEditChange}
          options={TICKET_ISSUE_TYPE_OPTIONS}
          placeholder='Select issue type'
          error={editErrors.issueType}
        />
        <Dropdown
          label='Priority'
          name='priority'
          value={editForm.priority}
          onChange={handleEditChange}
          options={TICKET_PRIORITY_OPTIONS}
          placeholder='Select priority'
          error={editErrors.priority}
        />
        <Dropdown
          label='Status'
          name='status'
          value={editForm.status}
          onChange={handleEditChange}
          options={TICKET_STATUS_OPTIONS}
          placeholder='Select status'
          error={editErrors.status}
        />
        <label className='field'>
          <span className='field-label'>Description</span>
          <textarea
            className='field-input'
            name='description'
            value={editForm.description}
            onChange={handleEditChange}
            placeholder='Optional: describe the issue in detail'
            rows={4}
            style={{ resize: 'vertical', minHeight: '80px' }}
          />
        </label>
        <div className='modal-footer'>
          <button
            className='btn-secondary'
            style={{ padding: '12px 18px' }}
            onClick={handleEditClose}
          >
            Cancel
          </button>
          <Button
            onClick={handleEditSave}
            disabled={editLoading}
            style={{ padding: '12px 18px' }}
          >
            {editLoading ? 'Saving...' : 'Update'}
          </Button>
        </div>
      </Modal>

      <Toast
        open={successOpen}
        variant='success'
        title='Success'
        message={successMessage}
        confirmLabel='OK'
        onConfirm={() => setSuccessOpen(false)}
      />
    </div>
  );
}
