import { useState, useEffect, useCallback } from 'react';
import { TicketPlus, Pencil, Plus } from 'lucide-react';
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
  addSupportTicketAction,
} from '../services/supportTicketService';
import { TICKET_STATUS_OPTIONS } from '../utils/constants';

const emptyCreateForm = {
  subject: '',
  description: '',
};
const emptyCreateErrors = {
  subject: '',
  description: '',
};
const emptyEditForm = {
  status: '',
  adminResponse: '',
};
const emptyEditErrors = {
  status: '',
};
const emptyActivityForm = {
  actionTaken: '',
  outcome: '',
  recommendation: '',
  notes: '',
};
const emptyActivityErrors = {
  actionTaken: '',
  outcome: '',
  recommendation: '',
  notes: '',
};

const STATUS_FILTER_OPTIONS = [{ value: '', label: 'All Statuses' }, ...TICKET_STATUS_OPTIONS];

const STATUS_COLORS = {
  open: { background: '#e8f4fd', color: '#1a6fa8' },
  in_progress: { background: '#fff3e0', color: '#b45309' },
  resolved: { background: '#e6f4ea', color: '#2e7d32' },
  closed: { background: '#f3f4f6', color: '#6b7280' },
};

const DATE_TIME_OPTIONS = {
  day: '2-digit',
  month: 'short',
  year: 'numeric',
  hour: '2-digit',
  minute: '2-digit',
};

function StatusBadge({ value }) {
  const style = STATUS_COLORS[value] ?? {};
  const label =
    TICKET_STATUS_OPTIONS.find((o) => o.value === value)?.label ?? value ?? '—';
  return (
    <span className='ticket-status-badge' style={style}>
      {label}
    </span>
  );
}

function formatDateTime(value) {
  if (!value) return '—';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '—';
  return date.toLocaleString(undefined, DATE_TIME_OPTIONS);
}

function normalizeStatus(value) {
  if (!value) return '';
  const str = String(value).trim();
  const exact = TICKET_STATUS_OPTIONS.find((o) => o.value === str);
  if (exact) return exact.value;

  const lower = str.toLowerCase().replace(/[\s-]+/g, '_');
  return TICKET_STATUS_OPTIONS.find((o) => o.value === lower)?.value ?? str;
}

function formatTickets(raw) {
  return raw.map((ticket) => {
    const user = ticket.user ?? {};
    const submittedBy = user.fullname || user.email || '—';
    const userId = user._id || user.id || '';
    const actions = Array.isArray(ticket.actions)
      ? [...ticket.actions].sort(
          (a, b) => new Date(b.dateTime) - new Date(a.dateTime),
        )
      : [];

    return {
      id: ticket._id,
      subject: ticket.subject || '—',
      description: ticket.description || '—',
      submittedBy,
      userId,
      status: normalizeStatus(ticket.status),
      adminResponse: ticket.adminResponse || '',
      createdAt: ticket.created_at,
      updatedAt: ticket.updated_at,
      actions,
    };
  });
}

function actionActorName(action, ticket) {
  if (action.userId && ticket.userId && action.userId === ticket.userId) {
    return ticket.submittedBy;
  }
  return action.userId || '—';
}

function TicketActionsExpand({ data, onAddActivity }) {
  const actions = data?.actions ?? [];

  return (
    <div className='ticket-actions-expand'>
      <div className='ticket-actions-header'>
        <h4 className='ticket-actions-heading'>Activity</h4>
        <button
          className='ticket-add-activity-btn'
          type='button'
          onClick={(e) => {
            e.stopPropagation();
            onAddActivity?.(data);
          }}
        >
          <Plus size={14} />
          Add activity
        </button>
      </div>
      {!actions.length ? (
        <p className='ticket-actions-empty'>No activity recorded yet.</p>
      ) : (
        <ul className='ticket-actions-list'>
          {actions.map((action) => (
            <li
              key={action._id || `${action.userId}-${action.dateTime}`}
              className='ticket-action-item'
            >
              <div className='ticket-action-meta'>
                <span className='ticket-action-time'>{formatDateTime(action.dateTime)}</span>
                <span className='ticket-action-actor'>{actionActorName(action, data)}</span>
              </div>
              <dl className='ticket-action-fields'>
                <div>
                  <dt>Action taken</dt>
                  <dd>{action.actionTaken || '—'}</dd>
                </div>
                <div>
                  <dt>Outcome</dt>
                  <dd>{action.outcome || '—'}</dd>
                </div>
                <div>
                  <dt>Recommendation</dt>
                  <dd>{action.recommendation || '—'}</dd>
                </div>
                <div>
                  <dt>Notes</dt>
                  <dd>{action.notes || '—'}</dd>
                </div>
              </dl>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
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

  const [activityTicket, setActivityTicket] = useState(null);
  const [activityForm, setActivityForm] = useState(emptyActivityForm);
  const [activityErrors, setActivityErrors] = useState(emptyActivityErrors);
  const [activityLoading, setActivityLoading] = useState(false);
  const [activitySubmitError, setActivitySubmitError] = useState('');

  const fetchTickets = useCallback(async () => {
    setLoading(true);
    setErrorMessage('');
    try {
      const { tickets: rawTickets, total } = await getSupportTickets({
        page,
        limit: 10,
        search,
        status: statusFilter,
      });
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
  }, [page, search, statusFilter]);

  useEffect(() => {
    fetchTickets();
  }, [fetchTickets]);

  function validateCreate(fields) {
    const errs = { ...emptyCreateErrors };
    if (!fields.subject.trim()) errs.subject = 'Subject is required.';
    if (!fields.description.trim()) errs.description = 'Description is required.';
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
      status: row.status,
      adminResponse: row.adminResponse ?? '',
    });
    setEditErrors(emptyEditErrors);
    setEditSubmitError('');
  }

  function validateEdit(fields) {
    const errs = { ...emptyEditErrors };
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

  function openAddActivity(ticket) {
    setActivityTicket(ticket);
    setActivityForm(emptyActivityForm);
    setActivityErrors(emptyActivityErrors);
    setActivitySubmitError('');
  }

  function handleActivityChange(e) {
    const { name, value } = e.target;
    setActivityForm((prev) => ({ ...prev, [name]: value }));
    setActivityErrors((prev) => ({ ...prev, [name]: '' }));
    setActivitySubmitError('');
  }

  function handleActivityClose() {
    setActivityTicket(null);
    setActivityForm(emptyActivityForm);
    setActivityErrors(emptyActivityErrors);
    setActivitySubmitError('');
  }

  function validateActivity(fields) {
    const errs = { ...emptyActivityErrors };
    if (!fields.actionTaken.trim()) errs.actionTaken = 'Action taken is required.';
    if (!fields.outcome.trim()) errs.outcome = 'Outcome is required.';
    if (!fields.recommendation.trim()) errs.recommendation = 'Recommendation is required.';
    if (!fields.notes.trim()) errs.notes = 'Notes are required.';
    return errs;
  }

  async function handleActivitySave() {
    const errs = validateActivity(activityForm);
    if (Object.values(errs).some(Boolean)) {
      setActivityErrors(errs);
      return;
    }
    setActivityLoading(true);
    setActivitySubmitError('');
    try {
      await addSupportTicketAction(activityTicket.id, {
        actionTaken: activityForm.actionTaken.trim(),
        outcome: activityForm.outcome.trim(),
        recommendation: activityForm.recommendation.trim(),
        notes: activityForm.notes.trim(),
      });
      handleActivityClose();
      setSuccessMessage('Activity has been added to the ticket.');
      setSuccessOpen(true);
      fetchTickets();
    } catch (err) {
      console.error('Failed to add ticket activity:', err);
      const status = err?.response?.status;
      setActivitySubmitError(
        status === 404
          ? 'The API does not have an add-activity route yet. Status and admin comments can still be saved from Edit.'
          : getErrorMessage(err, 'Failed to add activity to this ticket.'),
      );
    } finally {
      setActivityLoading(false);
    }
  }

  const columns = [
    {
      name: 'Description',
      grow: 2,
      minWidth: '180px',
      cell: (row) => (
        <div className='ticket-description-cell'>
          <span className='ticket-subject-line'>{row.subject}</span>
          <span className='ticket-description-line'>{row.description}</span>
        </div>
      ),
    },
    {
      name: 'Submitted by',
      selector: (row) => row.submittedBy,
      minWidth: '110px',
      wrap: true,
    },
    {
      name: 'Submitted',
      selector: (row) => formatDateTime(row.createdAt),
      sortable: true,
      minWidth: '140px',
      wrap: true,
    },
    {
      name: 'Status',
      cell: (row) => <StatusBadge value={row.status} />,
      sortable: true,
      minWidth: '110px',
    },
    {
      name: 'Admin comments',
      minWidth: '140px',
      grow: 1,
      wrap: true,
      cell: (row) => (
        <span className='ticket-admin-comment'>
          {row.adminResponse.trim() ? row.adminResponse : '—'}
        </span>
      ),
    },
    {
      name: 'Last updated',
      selector: (row) => formatDateTime(row.updatedAt),
      sortable: true,
      minWidth: '140px',
      wrap: true,
    },
    {
      name: 'Edit',
      width: '118px',
      minWidth: '118px',
      wrap: false,
      ignoreRowClick: true,
      button: true,
      style: {
        paddingTop: '10px',
        paddingBottom: '10px',
        paddingLeft: '8px',
        paddingRight: '12px',
      },
      cell: (row) => (
        <div className='ticket-edit-cell'>
          <button
            className='btn-deactivate ticket-edit-btn'
            onClick={(e) => {
              e.stopPropagation();
              openEdit(row);
            }}
            type='button'
          >
            <Pencil size={13} />
            Edit
          </button>
        </div>
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
        </div>
      </div>

      <div className='ticket-table-wrap'>
        <DataTable
          columns={columns}
          data={tickets}
          loading={loading}
          loadingMessage='Loading support tickets...'
          totalRows={totalRows}
          onChangePage={(newPage) => setPage(newPage)}
          expandableRows
          expandableRowsComponent={(rowProps) => (
            <TicketActionsExpand {...rowProps} onAddActivity={openAddActivity} />
          )}
          expandOnRowClicked
          expandableRowsHideExpander={false}
        />
      </div>

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
        {selectedTicket ? (
          <div className='ticket-readonly-block'>
            <p className='ticket-readonly-text'>
              <strong>{selectedTicket.subject}</strong>
            </p>
            <p className='ticket-readonly-text'>{selectedTicket.description}</p>
          </div>
        ) : null}
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
          <span className='field-label'>Admin comments</span>
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

      <Modal
        open={activityTicket !== null}
        onClose={handleActivityClose}
        title='Add activity'
        className='ticket-modal'
      >
        {activitySubmitError ? (
          <p className='ticket-error-message' role='alert'>
            {activitySubmitError}
          </p>
        ) : null}
        {activityTicket ? (
          <div className='ticket-readonly-block'>
            <p className='ticket-readonly-text'>
              <strong>{activityTicket.subject}</strong>
            </p>
            <p className='ticket-readonly-text'>{activityTicket.description}</p>
          </div>
        ) : null}
        <label className='field'>
          <span className='field-label'>Action taken</span>
          <textarea
            className={`field-input ticket-description${activityErrors.actionTaken ? ' field-input--error' : ''}`}
            name='actionTaken'
            value={activityForm.actionTaken}
            onChange={handleActivityChange}
            placeholder='What action did you take?'
            rows={2}
          />
          {activityErrors.actionTaken ? (
            <span className='field-error'>{activityErrors.actionTaken}</span>
          ) : null}
        </label>
        <label className='field'>
          <span className='field-label'>Outcome</span>
          <textarea
            className={`field-input ticket-description${activityErrors.outcome ? ' field-input--error' : ''}`}
            name='outcome'
            value={activityForm.outcome}
            onChange={handleActivityChange}
            placeholder='What was the result?'
            rows={2}
          />
          {activityErrors.outcome ? (
            <span className='field-error'>{activityErrors.outcome}</span>
          ) : null}
        </label>
        <label className='field'>
          <span className='field-label'>Recommendation</span>
          <textarea
            className={`field-input ticket-description${activityErrors.recommendation ? ' field-input--error' : ''}`}
            name='recommendation'
            value={activityForm.recommendation}
            onChange={handleActivityChange}
            placeholder='What do you recommend next?'
            rows={2}
          />
          {activityErrors.recommendation ? (
            <span className='field-error'>{activityErrors.recommendation}</span>
          ) : null}
        </label>
        <label className='field'>
          <span className='field-label'>Notes</span>
          <textarea
            className={`field-input ticket-description${activityErrors.notes ? ' field-input--error' : ''}`}
            name='notes'
            value={activityForm.notes}
            onChange={handleActivityChange}
            placeholder='Any additional notes'
            rows={2}
          />
          {activityErrors.notes ? (
            <span className='field-error'>{activityErrors.notes}</span>
          ) : null}
        </label>
        <div className='modal-footer'>
          <button
            className='btn-secondary'
            style={{ padding: '12px 18px' }}
            onClick={handleActivityClose}
            type='button'
          >
            Cancel
          </button>
          <Button
            onClick={handleActivitySave}
            disabled={activityLoading}
            style={{ padding: '12px 18px' }}
          >
            {activityLoading ? 'Saving...' : 'Add activity'}
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
