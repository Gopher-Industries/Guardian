import { useState, useEffect, useCallback } from 'react';
import { TicketPlus, Pencil } from 'lucide-react';
import DataTable from '../components/common/DataTable';
import Modal from '../components/common/Modal';
import Toast from '../components/common/Toast';
import Button from '../components/common/Button';
import InputField from '../components/common/InputField';
import Dropdown from '../components/common/Dropdown';
import {
  getSupportTickets,
  createSupportTicket,
  updateSupportTicket,
} from '../services/supportTicketService';
import {
  TICKET_ISSUE_TYPE_OPTIONS,
  TICKET_PRIORITY_OPTIONS,
  TICKET_STATUS_OPTIONS,
} from '../utils/constants';

const emptyCreateForm = {
  subject: '',
  description: '',
  issue_type: '',
  priority: '',
};
const emptyCreateErrors = {
  subject: '',
  description: '',
  issue_type: '',
  priority: '',
};
const emptyEditForm = {
  subject: '',
  description: '',
  issue_type: '',
  priority: '',
  status: '',
  adminResponse: '',
};
const emptyEditErrors = {
  subject: '',
  description: '',
  issue_type: '',
  priority: '',
  status: '',
};

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

function pickFirst(...values) {
  for (const value of values) {
    if (value !== undefined && value !== null && value !== '') return value;
  }
  return undefined;
}

function normalizeOptionValue(value, options) {
  if (value === undefined || value === null || value === '') return '';
  const str = String(value).trim();
  const exact = options.find((o) => o.value === str);
  if (exact) return exact.value;

  const lower = str.toLowerCase();
  const byValue = options.find((o) => o.value.toLowerCase() === lower);
  if (byValue) return byValue.value;

  const byLabel = options.find((o) => o.label.toLowerCase() === lower);
  if (byLabel) return byLabel.value;

  const underscored = lower.replace(/[\s-]+/g, '_');
  return options.find((o) => o.value === underscored)?.value ?? str;
}

function extractTicketsResponse(data) {
  if (Array.isArray(data)) {
    return { tickets: data, total: data.length };
  }

  const nested = data?.data;
  const tickets =
    pickFirst(
      Array.isArray(data?.tickets) ? data.tickets : undefined,
      Array.isArray(nested) ? nested : undefined,
      Array.isArray(nested?.tickets) ? nested.tickets : undefined,
    ) ?? [];

  const total =
    pickFirst(
      data?.total,
      data?.pagination?.total,
      nested?.total,
      nested?.pagination?.total,
      Array.isArray(tickets) ? tickets.length : undefined,
    ) ?? 0;

  return { tickets, total };
}

function formatTickets(raw) {
  return raw.map((t) => {
    const issueTypeRaw = normalizeOptionValue(
      pickFirst(t.issue_type, t.issueType, t.type, t.category),
      TICKET_ISSUE_TYPE_OPTIONS,
    );
    const priority = normalizeOptionValue(
      pickFirst(t.priority, t.Priority),
      TICKET_PRIORITY_OPTIONS,
    );
    const status = normalizeOptionValue(
      pickFirst(t.status, t.Status),
      TICKET_STATUS_OPTIONS,
    );
    const user = t.user;
    const submittedBy =
      typeof user === 'string'
        ? user
        : pickFirst(user?.fullname, user?.name, user?.email) ?? '-';
    const createdRaw = pickFirst(t.created_at, t.createdAt, t.created);

    return {
      id: pickFirst(t._id, t.id),
      subject: pickFirst(t.subject, t.title) ?? '-',
      description: pickFirst(t.description, t.details) ?? '',
      issueType:
        TICKET_ISSUE_TYPE_OPTIONS.find((o) => o.value === issueTypeRaw)?.label ??
        issueTypeRaw ??
        '-',
      issueTypeRaw,
      status,
      priority,
      adminResponse: t.adminResponse ?? '',
      createdAt: createdRaw ? new Date(createdRaw).toLocaleDateString() : '-',
      submittedBy,
    };
  });
}

function getErrorMessage(err, fallback) {
  return (
    err?.response?.data?.message ||
    err?.response?.data?.error ||
    err?.message ||
    fallback
  );
}

export default function SupportTicketPage() {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [totalRows, setTotalRows] = useState(0);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [priorityFilter, setPriorityFilter] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const [createOpen, setCreateOpen] = useState(false);
  const [createForm, setCreateForm] = useState(emptyCreateForm);
  const [createErrors, setCreateErrors] = useState(emptyCreateErrors);
  const [createLoading, setCreateLoading] = useState(false);
  const [createSubmitError, setCreateSubmitError] = useState('');

  const [selectedTicket, setSelectedTicket] = useState(null);
  const [editForm, setEditForm] = useState(emptyEditForm);
  const [editErrors, setEditErrors] = useState(emptyEditErrors);
  const [editLoading, setEditLoading] = useState(false);
  const [editSubmitError, setEditSubmitError] = useState('');

  const [successOpen, setSuccessOpen] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  const fetchTickets = useCallback(async () => {
    setLoading(true);
    setErrorMessage('');
    try {
      const data = await getSupportTickets({
        page,
        limit: 10,
        search,
        status: statusFilter,
        priority: priorityFilter,
      });
      const { tickets: rawTickets, total } = extractTicketsResponse(data);
      setTickets(formatTickets(rawTickets));
      setTotalRows(total);
    } catch (err) {
      console.error('Failed to load support tickets:', err);
      setTickets([]);
      setTotalRows(0);
      setErrorMessage(getErrorMessage(err, 'Failed to load support tickets.'));
    } finally {
      setLoading(false);
    }
  }, [page, search, statusFilter, priorityFilter]);

  useEffect(() => {
    fetchTickets();
  }, [fetchTickets]);

  function validateCreate(fields) {
    const errs = { ...emptyCreateErrors };
    if (!fields.subject.trim()) errs.subject = 'Subject is required.';
    if (!fields.description.trim()) errs.description = 'Description is required.';
    if (!fields.issue_type) errs.issue_type = 'Issue type is required.';
    if (!fields.priority) errs.priority = 'Priority is required.';
    return errs;
  }

  function handleCreateChange(e) {
    const { name, value } = e.target;
    setCreateForm((prev) => ({ ...prev, [name]: value }));
    setCreateErrors((prev) => ({ ...prev, [name]: '' }));
    setCreateSubmitError('');
  }

  function handleCreateClose() {
    setCreateOpen(false);
    setCreateForm(emptyCreateForm);
    setCreateErrors(emptyCreateErrors);
    setCreateSubmitError('');
  }

  async function handleCreateSave() {
    const errs = validateCreate(createForm);
    if (Object.values(errs).some(Boolean)) {
      setCreateErrors(errs);
      return;
    }
    setCreateLoading(true);
    setCreateSubmitError('');
    try {
      await createSupportTicket({
        subject: createForm.subject.trim(),
        description: createForm.description.trim(),
        issue_type: createForm.issue_type,
        priority: createForm.priority,
      });
      handleCreateClose();
      setSuccessMessage('Support ticket has been successfully created.');
      setSuccessOpen(true);
      fetchTickets();
    } catch (err) {
      console.error('Failed to create support ticket:', err);
      setCreateSubmitError(getErrorMessage(err, 'Failed to create support ticket.'));
    } finally {
      setCreateLoading(false);
    }
  }

  function openEdit(row) {
    setSelectedTicket(row);
    setEditForm({
      subject: row.subject === '-' ? '' : row.subject,
      description: row.description,
      issue_type: row.issueTypeRaw,
      priority: row.priority,
      status: row.status,
      adminResponse: row.adminResponse ?? '',
    });
    setEditErrors(emptyEditErrors);
    setEditSubmitError('');
  }

  function validateEdit(fields) {
    const errs = { ...emptyEditErrors };
    if (!fields.subject.trim()) errs.subject = 'Subject is required.';
    if (!fields.description.trim()) errs.description = 'Description is required.';
    // issue_type / priority not returned by API yet — required on create only
    if (!fields.status) errs.status = 'Status is required.';
    return errs;
  }

  function handleEditChange(e) {
    const { name, value } = e.target;
    setEditForm((prev) => ({ ...prev, [name]: value }));
    setEditErrors((prev) => ({ ...prev, [name]: '' }));
    setEditSubmitError('');
  }

  function handleEditClose() {
    setSelectedTicket(null);
    setEditForm(emptyEditForm);
    setEditErrors(emptyEditErrors);
    setEditSubmitError('');
  }

  async function handleEditSave() {
    const errs = validateEdit(editForm);
    if (Object.values(errs).some(Boolean)) {
      setEditErrors(errs);
      return;
    }
    setEditLoading(true);
    setEditSubmitError('');
    try {
      await updateSupportTicket(selectedTicket.id, {
        subject: editForm.subject.trim(),
        description: editForm.description.trim(),
        issue_type: editForm.issue_type,
        priority: editForm.priority,
        status: editForm.status,
        adminResponse: editForm.adminResponse.trim(),
      });
      handleEditClose();
      setSuccessMessage('Support ticket has been successfully updated.');
      setSuccessOpen(true);
      fetchTickets();
    } catch (err) {
      console.error('Failed to update support ticket:', err);
      setEditSubmitError(getErrorMessage(err, 'Failed to update support ticket.'));
    } finally {
      setEditLoading(false);
    }
  }

  const columns = [
    {
      name: 'Subject',
      selector: (row) => row.subject,
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
      name: 'Submitted By',
      selector: (row) => row.submittedBy,
    },
    {
      name: 'Created',
      selector: (row) => row.createdAt,
      sortable: true,
    },
    {
      name: 'Actions',
      cell: (row) => (
        <button
          className='btn-deactivate'
          onClick={() => openEdit(row)}
          style={{ display: 'inline-flex', alignItems: 'center', gap: '4px' }}
          type='button'
        >
          <Pencil size={13} />
          Edit
        </button>
      ),
    },
  ];

  return (
    <div className='panel'>
      <div className='ticket-page-header'>
        <h3 style={{ margin: 0 }}>Support Tickets</h3>
        <Button onClick={() => setCreateOpen(true)}>
          <TicketPlus size={18} />
          Create Ticket
        </Button>
      </div>

      {errorMessage ? (
        <p className='ticket-error-message' role='alert'>
          {errorMessage}
        </p>
      ) : null}

      <div className='ticket-toolbar'>
        <div className='ticket-toolbar-search'>
          <InputField
            label='Search'
            name='search'
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPage(1);
            }}
            placeholder='Search by subject...'
          />
        </div>
        <div className='ticket-toolbar-filters'>
          <div className='ticket-toolbar-filter'>
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
          <div className='ticket-toolbar-filter'>
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

      <Modal
        open={createOpen}
        onClose={handleCreateClose}
        title='Create Support Ticket'
        className='ticket-modal'
      >
        {createSubmitError ? (
          <p className='ticket-error-message' role='alert'>
            {createSubmitError}
          </p>
        ) : null}
        <InputField
          label='Subject'
          name='subject'
          value={createForm.subject}
          onChange={handleCreateChange}
          placeholder='Brief summary of the issue'
          error={createErrors.subject}
        />
        <div className='ticket-modal-row'>
          <Dropdown
            label='Issue Type'
            name='issue_type'
            value={createForm.issue_type}
            onChange={handleCreateChange}
            options={TICKET_ISSUE_TYPE_OPTIONS}
            placeholder='Select issue type'
            error={createErrors.issue_type}
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
        </div>
        <label className='field'>
          <span className='field-label'>Description</span>
          <textarea
            className={`field-input ticket-description${createErrors.description ? ' field-input--error' : ''}`}
            name='description'
            value={createForm.description}
            onChange={handleCreateChange}
            placeholder='Describe the issue in detail'
            rows={4}
          />
          {createErrors.description ? (
            <span className='field-error'>{createErrors.description}</span>
          ) : null}
        </label>
        <div className='modal-footer'>
          <button
            className='btn-secondary'
            style={{ padding: '12px 18px' }}
            onClick={handleCreateClose}
            type='button'
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
        className='ticket-modal'
      >
        {editSubmitError ? (
          <p className='ticket-error-message' role='alert'>
            {editSubmitError}
          </p>
        ) : null}
        <InputField
          label='Subject'
          name='subject'
          value={editForm.subject}
          onChange={handleEditChange}
          placeholder='Brief summary of the issue'
          error={editErrors.subject}
        />
        <div className='ticket-modal-row'>
          <Dropdown
            label='Issue Type'
            name='issue_type'
            value={editForm.issue_type}
            onChange={handleEditChange}
            options={TICKET_ISSUE_TYPE_OPTIONS}
            placeholder='Select issue type'
            error={editErrors.issue_type}
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
        </div>
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
            className={`field-input ticket-description${editErrors.description ? ' field-input--error' : ''}`}
            name='description'
            value={editForm.description}
            onChange={handleEditChange}
            placeholder='Describe the issue in detail'
            rows={4}
          />
          {editErrors.description ? (
            <span className='field-error'>{editErrors.description}</span>
          ) : null}
        </label>
        <label className='field'>
          <span className='field-label'>Admin Response</span>
          <textarea
            className='field-input ticket-description'
            name='adminResponse'
            value={editForm.adminResponse}
            onChange={handleEditChange}
            placeholder='Add a response for this ticket'
            rows={3}
          />
        </label>
        <div className='modal-footer'>
          <button
            className='btn-secondary'
            style={{ padding: '12px 18px' }}
            onClick={handleEditClose}
            type='button'
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
