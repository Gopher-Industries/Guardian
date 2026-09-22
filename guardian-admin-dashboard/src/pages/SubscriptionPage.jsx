import { useEffect, useState } from 'react';
import {
  FileText,
  Download,
  Eye,
  Loader,
  AlertCircle,
  CheckCircle2,
  Clock,
  XCircle,
  Filter,
} from 'lucide-react';
import { getInvoices, getPaymentHistory } from "../services/subscriptionService";
import "./SubscriptionPage.css";

const SubscriptionPage = () => {
  const [invoices, setInvoices] = useState([]);
  const [paymentHistory, setPaymentHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedInvoice, setSelectedInvoice] = useState(null);
  const [invoiceDetailOpen, setInvoiceDetailOpen] = useState(false);
  const [statusFilter, setStatusFilter] = useState('all');
  const [invoicePage, setInvoicePage] = useState(1);
  const [paymentPage, setPaymentPage] = useState(1);

  const itemsPerPage = 10;

  useEffect(() => {
    loadBillingData();
  }, []);

  const loadBillingData = async () => {
    try {
      setLoading(true);
      setError('');

      const invoiceData = await getInvoices();
      const paymentData = await getPaymentHistory();

      setInvoices(invoiceData || []);
      setPaymentHistory(paymentData || []);
    } catch (err) {
      console.error('Load billing data error:', err);
      setError(
        err?.response?.data?.message ||
          err?.message ||
          'Failed to load billing data.'
      );
    } finally {
      setLoading(false);
    }
  };

  const getStatusIcon = (status) => {
    switch (status.toLowerCase()) {
      case 'paid':
        return <CheckCircle2 size={16} className="status-icon paid" />;
      case 'pending':
        return <Clock size={16} className="status-icon pending" />;
      case 'overdue':
        return <XCircle size={16} className="status-icon overdue" />;
      default:
        return <FileText size={16} />;
    }
  };

  const getStatusBadge = (status) => {
    const statusClass = `status-badge status-${status.toLowerCase()}`;
    return <span className={statusClass}>{status}</span>;
  };

  const filteredInvoices = invoices.filter((inv) => {
    if (statusFilter === 'all') return true;
    return inv.status.toLowerCase() === statusFilter.toLowerCase();
  });

  const paginatedInvoices = filteredInvoices.slice(
    (invoicePage - 1) * itemsPerPage,
    invoicePage * itemsPerPage
  );

  const paginatedPayments = paymentHistory.slice(
    (paymentPage - 1) * itemsPerPage,
    paymentPage * itemsPerPage
  );

  const totalInvoices = invoices.length;
  const totalRevenue = invoices.reduce((sum, inv) => sum + (inv.amount || 0), 0);
  const paidInvoices = invoices.filter(
    (inv) => inv.status.toLowerCase() === 'paid'
  ).length;
  const pendingAmount = invoices
    .filter((inv) => inv.status.toLowerCase() === 'pending')
    .reduce((sum, inv) => sum + (inv.amount || 0), 0);

  const totalInvoicePages = Math.ceil(filteredInvoices.length / itemsPerPage);
  const totalPaymentPages = Math.ceil(paymentHistory.length / itemsPerPage);

  return (
    <section className="billing-page">
      <div className="billing-header">
        <div>
          <p className="billing-eyebrow">Guardian Monitor Admin</p>
          <h1>Billing & Invoices</h1>
          <p className="billing-subtitle">
            Manage invoices, track payments, and view billing history.
          </p>
        </div>
      </div>

      {error && (
        <div className="billing-error-banner">
          <AlertCircle size={18} />
          <span>{error}</span>
          <button onClick={loadBillingData} className="billing-retry-btn">
            Retry
          </button>
        </div>
      )}

      {loading ? (
        <div className="billing-loading">
          <Loader size={32} className="spinner" />
          <p>Loading billing data...</p>
        </div>
      ) : (
        <>
          <div className="billing-summary-grid">
            <div className="billing-summary-card">
              <div className="summary-card-header">
                <span className="summary-label">Total Invoices</span>
                <FileText size={20} className="summary-icon" />
              </div>
              <div className="summary-value">{totalInvoices}</div>
              <p className="summary-meta">All time</p>
            </div>

            <div className="billing-summary-card">
              <div className="summary-card-header">
                <span className="summary-label">Total Revenue</span>
                <CheckCircle2 size={20} className="summary-icon paid" />
              </div>
              <div className="summary-value">${totalRevenue.toFixed(2)}</div>
              <p className="summary-meta">{paidInvoices} invoices paid</p>
            </div>

            <div className="billing-summary-card">
              <div className="summary-card-header">
                <span className="summary-label">Pending Amount</span>
                <Clock size={20} className="summary-icon pending" />
              </div>
              <div className="summary-value">${pendingAmount.toFixed(2)}</div>
              <p className="summary-meta">
                {invoices.filter((i) => i.status.toLowerCase() === 'pending')
                  .length || 0}{' '}
                pending
              </p>
            </div>
          </div>

          <div className="billing-sections">
            <div className="billing-section">
              <div className="section-header">
                <div>
                  <h2>Invoices</h2>
                  <p>View and manage all invoices</p>
                </div>

                <div className="section-controls">
                  <div className="filter-group">
                    <Filter size={16} />
                    <select
                      value={statusFilter}
                      onChange={(e) => {
                        setStatusFilter(e.target.value);
                        setInvoicePage(1);
                      }}
                      className="status-filter"
                    >
                      <option value="all">All Status</option>
                      <option value="paid">Paid</option>
                      <option value="pending">Pending</option>
                      <option value="overdue">Overdue</option>
                    </select>
                  </div>
                </div>
              </div>

              {paginatedInvoices.length > 0 ? (
                <>
                  <div className="invoices-table">
                    <table>
                      <thead>
                        <tr>
                          <th>Invoice ID</th>
                          <th>Description</th>
                          <th>Amount</th>
                          <th>Date</th>
                          <th>Due Date</th>
                          <th>Status</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {paginatedInvoices.map((invoice) => (
                          <tr key={invoice.id}>
                            <td>
                              <span className="invoice-id">
                                {invoice.invoiceNumber}
                              </span>
                            </td>
                            <td>{invoice.description}</td>
                            <td className="amount">
                              ${invoice.amount.toFixed(2)}
                            </td>
                            <td>
                              {new Date(invoice.issueDate).toLocaleDateString()}
                            </td>
                            <td>
                              {new Date(invoice.dueDate).toLocaleDateString()}
                            </td>
                            <td>
                              <div className="status-cell">
                                {getStatusIcon(invoice.status)}
                                {getStatusBadge(invoice.status)}
                              </div>
                            </td>
                            <td>
                              <div className="table-actions">
                                <button
                                  className="action-btn view-btn"
                                  title="View Invoice"
                                  onClick={() => {
                                    setSelectedInvoice(invoice);
                                    setInvoiceDetailOpen(true);
                                  }}
                                >
                                  <Eye size={16} />
                                </button>
                                <button
                                  className="action-btn download-btn"
                                  title="Download Invoice"
                                >
                                  <Download size={16} />
                                </button>
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  {totalInvoicePages > 1 && (
                    <div className="pagination">
                      <button
                        onClick={() =>
                          setInvoicePage((p) => Math.max(1, p - 1))
                        }
                        disabled={invoicePage === 1}
                      >
                        Previous
                      </button>
                      <span>
                        Page {invoicePage} of {totalInvoicePages}
                      </span>
                      <button
                        onClick={() =>
                          setInvoicePage((p) =>
                            Math.min(totalInvoicePages, p + 1)
                          )
                        }
                        disabled={invoicePage === totalInvoicePages}
                      >
                        Next
                      </button>
                    </div>
                  )}
                </>
              ) : (
                <div className="empty-state">
                  <FileText size={48} />
                  <p>No invoices found</p>
                </div>
              )}
            </div>

            <div className="billing-section">
              <div className="section-header">
                <div>
                  <h2>Payment History</h2>
                  <p>Recent payments and transactions</p>
                </div>
              </div>

              {paginatedPayments.length > 0 ? (
                <>
                  <div className="payment-history-list">
                    {paginatedPayments.map((payment) => (
                      <div key={payment.id} className="payment-item">
                        <div className="payment-left">
                          <div className="payment-icon-wrapper">
                            {getStatusIcon(payment.status)}
                          </div>
                          <div className="payment-details">
                            <h4>{payment.invoiceNumber}</h4>
                            <p>{payment.description}</p>
                          </div>
                        </div>

                        <div className="payment-right">
                          <div className="payment-amount">
                            ${payment.amount.toFixed(2)}
                          </div>
                          <div className="payment-date">
                            {new Date(payment.date).toLocaleDateString()}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>

                  {totalPaymentPages > 1 && (
                    <div className="pagination">
                      <button
                        onClick={() => setPaymentPage((p) => Math.max(1, p - 1))}
                        disabled={paymentPage === 1}
                      >
                        Previous
                      </button>
                      <span>
                        Page {paymentPage} of {totalPaymentPages}
                      </span>
                      <button
                        onClick={() =>
                          setPaymentPage((p) =>
                            Math.min(totalPaymentPages, p + 1)
                          )
                        }
                        disabled={paymentPage === totalPaymentPages}
                      >
                        Next
                      </button>
                    </div>
                  )}
                </>
              ) : (
                <div className="empty-state">
                  <CheckCircle2 size={48} />
                  <p>No payment history</p>
                </div>
              )}
            </div>
          </div>

          {invoiceDetailOpen && selectedInvoice && (
            <div className="modal-backdrop" onClick={() => setInvoiceDetailOpen(false)}>
              <div className="invoice-modal" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                  <h3>Invoice Details</h3>
                  <button
                    className="modal-close"
                    onClick={() => setInvoiceDetailOpen(false)}
                  >
                    ×
                  </button>
                </div>

                <div className="modal-body">
                  <div className="invoice-detail-grid">
                    <div>
                      <span className="detail-label">Invoice Number</span>
                      <p>{selectedInvoice.invoiceNumber}</p>
                    </div>
                    <div>
                      <span className="detail-label">Status</span>
                      <div>{getStatusBadge(selectedInvoice.status)}</div>
                    </div>
                    <div>
                      <span className="detail-label">Amount</span>
                      <p className="detail-amount">
                        ${selectedInvoice.amount.toFixed(2)}
                      </p>
                    </div>
                    <div>
                      <span className="detail-label">Issue Date</span>
                      <p>
                        {new Date(selectedInvoice.issueDate).toLocaleDateString()}
                      </p>
                    </div>
                    <div>
                      <span className="detail-label">Due Date</span>
                      <p>
                        {new Date(selectedInvoice.dueDate).toLocaleDateString()}
                      </p>
                    </div>
                    <div>
                      <span className="detail-label">Description</span>
                      <p>{selectedInvoice.description}</p>
                    </div>
                  </div>
                </div>

                <div className="modal-footer">
                  <button
                    className="modal-btn secondary"
                    onClick={() => setInvoiceDetailOpen(false)}
                  >
                    Close
                  </button>
                  <button className="modal-btn primary">
                    <Download size={16} />
                    Download PDF
                  </button>
                </div>
              </div>
            </div>
          )}
        </>
      )}
    </section>
  );
};

export default SubscriptionPage;
