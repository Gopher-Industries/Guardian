import React, { useState, useEffect } from "react";
import {
  AlertCircle,
  Loader,
  DollarSign,
  Calendar,
  User,
  X,
  Download,
  CreditCard,
  ArrowRight,
} from "lucide-react";
import {
  getPatientAppointments,
  getPaymentHistory,
} from "../services/patientBillingService";
import "./PatientBilling.css";

const PatientBilling = () => {
  const [appointments, setAppointments] = useState([]);
  const [payments, setPayments] = useState([]);
  const [stats, setStats] = useState({
    totalPatients: 0,
    totalBilled: 0,
    totalPaid: 0,
    totalPending: 0,
  });
  const [statusFilter, setStatusFilter] = useState("all");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState("existing");
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [showInvoiceModal, setShowInvoiceModal] = useState(false);
  const [selectedAppointment, setSelectedAppointment] = useState(null);

  // New Customer Payment Form State
  const [newCustomerForm, setNewCustomerForm] = useState({
    fullName: "",
    email: "",
    phone: "",
    address: "",
    serviceType: "",
    serviceDescription: "",
    amount: "",
  });

  const [paymentStep, setPaymentStep] = useState(1);
  const [paymentDetails, setPaymentDetails] = useState({
    cardholderName: "",
    cardNumber: "",
    expiryDate: "",
    cvv: "",
    paymentMethod: "credit-card",
  });

  const [paymentErrors, setPaymentErrors] = useState({});
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const appointmentsData = await getPatientAppointments();
      const paymentsData = await getPaymentHistory();

      setAppointments(appointmentsData);
      setPayments(paymentsData);

      const uniquePatients = new Set(appointmentsData.map((apt) => apt.patientId));
      const totalBilled = appointmentsData.reduce((sum, apt) => sum + apt.cost, 0);
      const totalPaid = appointmentsData
        .filter((apt) => apt.paymentStatus === "Paid")
        .reduce((sum, apt) => sum + apt.cost, 0);
      const totalPending = totalBilled - totalPaid;

      setStats({
        totalPatients: uniquePatients.size,
        totalBilled,
        totalPaid,
        totalPending,
      });
      setError(null);
    } catch (err) {
      setError("Failed to load data");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const filteredAppointments = appointments.filter((apt) => {
    if (statusFilter === "all") return true;
    return apt.paymentStatus.toLowerCase() === statusFilter.toLowerCase();
  });

  // New Customer Form Handlers
  const handleNewCustomerChange = (e) => {
    const { name, value } = e.target;
    setNewCustomerForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handlePaymentDetailsChange = (e) => {
    const { name, value } = e.target;
    setPaymentDetails((prev) => ({
      ...prev,
      [name]: value,
    }));
    // Clear error for this field
    if (paymentErrors[name]) {
      setPaymentErrors((prev) => ({
        ...prev,
        [name]: "",
      }));
    }
  };

  const validateCustomerDetails = () => {
    const errors = {};
    if (!newCustomerForm.fullName.trim()) errors.fullName = "Name is required";
    if (!newCustomerForm.email.trim()) errors.email = "Email is required";
    if (!newCustomerForm.phone.trim()) errors.phone = "Phone is required";
    if (!newCustomerForm.serviceType.trim()) errors.serviceType = "Service type is required";
    if (!newCustomerForm.amount || isNaN(newCustomerForm.amount) || parseFloat(newCustomerForm.amount) <= 0) {
      errors.amount = "Valid amount is required";
    }
    return errors;
  };

  const handleProceedToPayment = () => {
    const errors = validateCustomerDetails();
    if (Object.keys(errors).length === 0) {
      setPaymentErrors({});
      setPaymentStep(2);
    } else {
      setPaymentErrors(errors);
    }
  };

  const validatePaymentDetails = () => {
    const errors = {};
    if (!paymentDetails.cardholderName.trim()) {
      errors.cardholderName = "Cardholder name is required";
    }
    if (!paymentDetails.cardNumber.trim()) {
      errors.cardNumber = "Card number is required";
    } else if (paymentDetails.cardNumber.replace(/\s/g, "").length !== 16) {
      errors.cardNumber = "Card number must be 16 digits";
    }
    if (!paymentDetails.expiryDate.trim()) {
      errors.expiryDate = "Expiry date is required";
    }
    if (!paymentDetails.cvv.trim()) {
      errors.cvv = "CVV is required";
    } else if (paymentDetails.cvv.length !== 3) {
      errors.cvv = "CVV must be 3 digits";
    }
    return errors;
  };

  const handleProcessPayment = () => {
    const errors = validatePaymentDetails();
    if (Object.keys(errors).length === 0) {
      // Payment successful - create new payment object
      const today = new Date().toISOString().split('T')[0];
      const newPayment = {
        id: `PAY-${Date.now()}`, // Unique ID based on timestamp
        patientName: newCustomerForm.fullName,
        patientId: `PAT-NEW-${Date.now()}`, // New customer ID
        appointmentDate: today,
        amount: parseFloat(newCustomerForm.amount),
        paymentDate: today,
        paymentMethod: paymentDetails.paymentMethod === 'credit-card' ? 'Credit Card' : 
                       paymentDetails.paymentMethod === 'debit-card' ? 'Debit Card' : 'Bank Transfer',
        transactionId: `TXN-NC-${Date.now()}`, // New customer transaction ID
        status: 'Completed',
      };

      // Add to payments array (so it shows in payment records)
      setPayments((prevPayments) => [newPayment, ...prevPayments]);

      // Update stats
      setStats((prevStats) => ({
        totalPatients: prevStats.totalPatients + 1,
        totalBilled: prevStats.totalBilled + parseFloat(newCustomerForm.amount),
        totalPaid: prevStats.totalPaid + parseFloat(newCustomerForm.amount),
        totalPending: prevStats.totalPending,
      }));

      // Show success message
      setPaymentErrors({});
      setSuccessMessage(
        `Payment of $${parseFloat(newCustomerForm.amount).toFixed(2)} received from ${newCustomerForm.fullName}!`
      );
      
      setTimeout(() => {
        resetNewCustomerForm();
        // Auto-switch to existing patients tab to show the new payment
        setActiveTab("existing");
      }, 2000);
    } else {
      setPaymentErrors(errors);
    }
  };

  const resetNewCustomerForm = () => {
    setNewCustomerForm({
      fullName: "",
      email: "",
      phone: "",
      address: "",
      serviceType: "",
      serviceDescription: "",
      amount: "",
    });
    setPaymentDetails({
      cardholderName: "",
      cardNumber: "",
      expiryDate: "",
      cvv: "",
      paymentMethod: "credit-card",
    });
    setPaymentStep(1);
    setSuccessMessage("");
    setPaymentErrors({});
  };

  const openPaymentModal = (appointment) => {
    setSelectedAppointment(appointment);
    setShowPaymentModal(true);
  };

  const closePaymentModal = () => {
    setShowPaymentModal(false);
    setSelectedAppointment(null);
  };

  const openInvoiceModal = (appointment) => {
    setSelectedAppointment(appointment);
    setShowInvoiceModal(true);
  };

  const closeInvoiceModal = () => {
    setShowInvoiceModal(false);
    setSelectedAppointment(null);
  };

  if (loading) {
    return (
      <div className="billing-container">
        <Loader className="spinner" />
      </div>
    );
  }

  return (
    <div className="billing-container">
      {error && (
        <div className="error-banner">
          <AlertCircle size={20} />
          <span>{error}</span>
        </div>
      )}

      {/* Page Header */}
      <div className="page-header">
        <h1>Patient Billing & Payments</h1>
        <p>Manage existing appointments or accept payment from new customers</p>
      </div>

      {/* Summary Cards */}
      <div className="summary-cards">
        <div className="card">
          <div className="card-icon">
            <User size={24} />
          </div>
          <div className="card-content">
            <p className="label">Total Patients</p>
            <p className="value">{stats.totalPatients}</p>
          </div>
        </div>
        <div className="card">
          <div className="card-icon">
            <DollarSign size={24} />
          </div>
          <div className="card-content">
            <p className="label">Total Billed</p>
            <p className="value">${stats.totalBilled.toFixed(2)}</p>
          </div>
        </div>
        <div className="card">
          <div className="card-icon success">
            <DollarSign size={24} />
          </div>
          <div className="card-content">
            <p className="label">Total Paid</p>
            <p className="value">${stats.totalPaid.toFixed(2)}</p>
          </div>
        </div>
        <div className="card">
          <div className="card-icon warning">
            <AlertCircle size={24} />
          </div>
          <div className="card-content">
            <p className="label">Pending Amount</p>
            <p className="value">${stats.totalPending.toFixed(2)}</p>
          </div>
        </div>
      </div>

      {/* Tabs for Existing vs New Customer */}
      <div className="tabs-container">
        <button
          className={`tab-button ${activeTab === "existing" ? "active" : ""}`}
          onClick={() => setActiveTab("existing")}
        >
          <Calendar size={18} />
          Existing Patients
        </button>
        <button
          className={`tab-button ${activeTab === "new" ? "active" : ""}`}
          onClick={() => setActiveTab("new")}
        >
          <CreditCard size={18} />
          New Customer Payment
        </button>
      </div>

      {/* EXISTING PATIENTS TAB */}
      {activeTab === "existing" && (
        <>
          {/* Filter Section */}
          <div className="filter-section">
            <label>Filter by Payment Status:</label>
            <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
              <option value="all">All Appointments</option>
              <option value="paid">Paid</option>
              <option value="pending">Pending</option>
              <option value="overdue">Overdue</option>
            </select>
          </div>

          {/* Appointments Table */}
          <div className="table-wrapper">
            <h2>Patient Appointments</h2>
            <div className="table-container">
              <table className="appointments-table">
                <thead>
                  <tr>
                    <th>Patient Name</th>
                    <th>Email</th>
                    <th>Doctor</th>
                    <th>Appointment Date</th>
                    <th>Service</th>
                    <th>Cost</th>
                    <th>Payment Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredAppointments.length > 0 ? (
                    filteredAppointments.map((apt) => (
                      <tr key={apt.id}>
                        <td className="patient-name">{apt.patientName}</td>
                        <td>{apt.patientEmail}</td>
                        <td>{apt.doctorName}</td>
                        <td>{apt.appointmentDate}</td>
                        <td>{apt.serviceType}</td>
                        <td className="amount">${apt.cost.toFixed(2)}</td>
                        <td>
                          <span className={`payment-status ${apt.paymentStatus.toLowerCase()}`}>
                            {apt.paymentStatus}
                          </span>
                        </td>
                        <td className="actions">
                          {apt.paymentStatus !== "Paid" && (
                            <button
                              className="btn-pay"
                              onClick={() => openPaymentModal(apt)}
                              title="Process Payment"
                            >
                              Pay
                            </button>
                          )}
                          <button
                            className="btn-invoice"
                            onClick={() => openInvoiceModal(apt)}
                            title="View Invoice"
                          >
                            <Download size={16} />
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="8" className="no-data">
                        No appointments found
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          {/* Payment History */}
          <div className="payment-history-wrapper">
            <h2>Payment Records</h2>
            <div className="payments-grid">
              {payments.slice(0, 6).map((payment) => (
                <div key={payment.id} className="payment-card">
                  <div className="payment-header">
                    <h4>{payment.patientName}</h4>
                    <span className="status-badge">Paid</span>
                  </div>
                  <div className="payment-body">
                    <p>
                      <strong>Amount:</strong> ${payment.amount.toFixed(2)}
                    </p>
                    <p>
                      <strong>Date:</strong> {payment.paymentDate}
                    </p>
                    <p>
                      <strong>Method:</strong> {payment.paymentMethod}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </>
      )}

      {/* NEW CUSTOMER PAYMENT TAB */}
      {activeTab === "new" && (
        <div className="new-customer-section">
          {successMessage && (
            <div className="success-banner">
              <AlertCircle size={20} style={{ color: "#10b981" }} />
              <span>{successMessage}</span>
            </div>
          )}

          {paymentStep === 1 ? (
            // Step 1: Customer Details
            <div className="form-card">
              <h2>New Customer - Payment Details</h2>
              <form className="customer-form">
                <div className="form-row">
                  <div className="form-group">
                    <label>Full Name *</label>
                    <input
                      type="text"
                      name="fullName"
                      placeholder="Enter customer name"
                      value={newCustomerForm.fullName}
                      onChange={handleNewCustomerChange}
                      className={paymentErrors.fullName ? "error" : ""}
                    />
                    {paymentErrors.fullName && (
                      <span className="error-text">{paymentErrors.fullName}</span>
                    )}
                  </div>
                  <div className="form-group">
                    <label>Email *</label>
                    <input
                      type="email"
                      name="email"
                      placeholder="Enter email address"
                      value={newCustomerForm.email}
                      onChange={handleNewCustomerChange}
                      className={paymentErrors.email ? "error" : ""}
                    />
                    {paymentErrors.email && (
                      <span className="error-text">{paymentErrors.email}</span>
                    )}
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>Phone Number *</label>
                    <input
                      type="tel"
                      name="phone"
                      placeholder="Enter phone number"
                      value={newCustomerForm.phone}
                      onChange={handleNewCustomerChange}
                      className={paymentErrors.phone ? "error" : ""}
                    />
                    {paymentErrors.phone && (
                      <span className="error-text">{paymentErrors.phone}</span>
                    )}
                  </div>
                  <div className="form-group">
                    <label>Address</label>
                    <input
                      type="text"
                      name="address"
                      placeholder="Enter address (optional)"
                      value={newCustomerForm.address}
                      onChange={handleNewCustomerChange}
                    />
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>Service Type *</label>
                    <select
                      name="serviceType"
                      value={newCustomerForm.serviceType}
                      onChange={handleNewCustomerChange}
                      className={paymentErrors.serviceType ? "error" : ""}
                    >
                      <option value="">Select a service</option>
                      <option value="Consultation">Consultation</option>
                      <option value="Treatment">Treatment</option>
                      <option value="Surgery">Surgery</option>
                      <option value="Follow-up">Follow-up</option>
                      <option value="Other">Other</option>
                    </select>
                    {paymentErrors.serviceType && (
                      <span className="error-text">{paymentErrors.serviceType}</span>
                    )}
                  </div>
                  <div className="form-group">
                    <label>Amount ($) *</label>
                    <input
                      type="number"
                      name="amount"
                      placeholder="Enter amount"
                      value={newCustomerForm.amount}
                      onChange={handleNewCustomerChange}
                      min="0"
                      step="0.01"
                      className={paymentErrors.amount ? "error" : ""}
                    />
                    {paymentErrors.amount && (
                      <span className="error-text">{paymentErrors.amount}</span>
                    )}
                  </div>
                </div>

                <div className="form-group full-width">
                  <label>Service Description</label>
                  <textarea
                    name="serviceDescription"
                    placeholder="Enter service description (optional)"
                    value={newCustomerForm.serviceDescription}
                    onChange={handleNewCustomerChange}
                    rows="3"
                  />
                </div>

                <div className="form-actions">
                  <button
                    type="button"
                    className="btn-cancel"
                    onClick={resetNewCustomerForm}
                  >
                    Clear Form
                  </button>
                  <button
                    type="button"
                    className="btn-submit"
                    onClick={handleProceedToPayment}
                  >
                    <span>Proceed to Payment</span>
                    <ArrowRight size={18} />
                  </button>
                </div>
              </form>
            </div>
          ) : (
            // Step 2: Payment Details
            <div className="form-card">
              <div className="payment-summary-header">
                <button
                  className="btn-back"
                  onClick={() => setPaymentStep(1)}
                  title="Go back"
                >
                  ← Back
                </button>
                <h2>Enter Payment Details</h2>
              </div>

              {/* Order Summary */}
              <div className="order-summary">
                <h3>Order Summary</h3>
                <div className="summary-item">
                  <span>Customer:</span>
                  <strong>{newCustomerForm.fullName}</strong>
                </div>
                <div className="summary-item">
                  <span>Email:</span>
                  <strong>{newCustomerForm.email}</strong>
                </div>
                <div className="summary-item">
                  <span>Service:</span>
                  <strong>{newCustomerForm.serviceType}</strong>
                </div>
                <div className="summary-item total">
                  <span>Total Amount:</span>
                  <strong>${parseFloat(newCustomerForm.amount).toFixed(2)}</strong>
                </div>
              </div>

              <form className="payment-form">
                <div className="form-group">
                  <label>Cardholder Name *</label>
                  <input
                    type="text"
                    name="cardholderName"
                    placeholder="Name on card"
                    value={paymentDetails.cardholderName}
                    onChange={handlePaymentDetailsChange}
                    className={paymentErrors.cardholderName ? "error" : ""}
                  />
                  {paymentErrors.cardholderName && (
                    <span className="error-text">{paymentErrors.cardholderName}</span>
                  )}
                </div>

                <div className="form-group">
                  <label>Card Number *</label>
                  <input
                    type="text"
                    name="cardNumber"
                    placeholder="1234 5678 9012 3456"
                    value={paymentDetails.cardNumber}
                    onChange={handlePaymentDetailsChange}
                    maxLength="16"
                    className={paymentErrors.cardNumber ? "error" : ""}
                  />
                  {paymentErrors.cardNumber && (
                    <span className="error-text">{paymentErrors.cardNumber}</span>
                  )}
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>Expiry Date (MM/YY) *</label>
                    <input
                      type="text"
                      name="expiryDate"
                      placeholder="MM/YY"
                      value={paymentDetails.expiryDate}
                      onChange={handlePaymentDetailsChange}
                      maxLength="5"
                      className={paymentErrors.expiryDate ? "error" : ""}
                    />
                    {paymentErrors.expiryDate && (
                      <span className="error-text">{paymentErrors.expiryDate}</span>
                    )}
                  </div>
                  <div className="form-group">
                    <label>CVV *</label>
                    <input
                      type="text"
                      name="cvv"
                      placeholder="123"
                      value={paymentDetails.cvv}
                      onChange={handlePaymentDetailsChange}
                      maxLength="3"
                      className={paymentErrors.cvv ? "error" : ""}
                    />
                    {paymentErrors.cvv && (
                      <span className="error-text">{paymentErrors.cvv}</span>
                    )}
                  </div>
                </div>

                <div className="form-group">
                  <label>Payment Method *</label>
                  <select
                    name="paymentMethod"
                    value={paymentDetails.paymentMethod}
                    onChange={handlePaymentDetailsChange}
                  >
                    <option value="credit-card">Credit Card</option>
                    <option value="debit-card">Debit Card</option>
                    <option value="bank-transfer">Bank Transfer</option>
                  </select>
                </div>

                <div className="form-actions">
                  <button
                    type="button"
                    className="btn-cancel"
                    onClick={() => setPaymentStep(1)}
                  >
                    Back
                  </button>
                  <button
                    type="button"
                    className="btn-submit btn-success"
                    onClick={handleProcessPayment}
                  >
                    <CreditCard size={18} />
                    <span>Process Payment</span>
                  </button>
                </div>
              </form>
            </div>
          )}
        </div>
      )}

      {/* Payment Modal - for existing patients */}
      {showPaymentModal && selectedAppointment && (
        <div className="modal-overlay" onClick={closePaymentModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Payment Form</h2>
              <button className="close-btn" onClick={closePaymentModal}>
                <X size={24} />
              </button>
            </div>

            <div className="modal-body">
              <div className="appointment-summary">
                <h3>Appointment Details</h3>
                <div className="summary-row">
                  <span>Patient:</span>
                  <strong>{selectedAppointment.patientName}</strong>
                </div>
                <div className="summary-row">
                  <span>Doctor:</span>
                  <strong>{selectedAppointment.doctorName}</strong>
                </div>
                <div className="summary-row">
                  <span>Service:</span>
                  <strong>{selectedAppointment.serviceType}</strong>
                </div>
                <div className="summary-row">
                  <span>Date:</span>
                  <strong>{selectedAppointment.appointmentDate}</strong>
                </div>
                <div className="summary-row amount-row">
                  <span>Amount Due:</span>
                  <strong>${selectedAppointment.cost.toFixed(2)}</strong>
                </div>
              </div>

              <form className="payment-form">
                <div className="form-group">
                  <label>Cardholder Name</label>
                  <input
                    type="text"
                    placeholder={selectedAppointment.patientName}
                    disabled
                  />
                </div>

                <div className="form-group">
                  <label>Card Number</label>
                  <input
                    type="text"
                    placeholder="•••• •••• •••• 4242"
                    disabled
                  />
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>Expiry (MM/YY)</label>
                    <input type="text" placeholder="••/••" disabled />
                  </div>
                  <div className="form-group">
                    <label>CVV</label>
                    <input type="text" placeholder="•••" disabled />
                  </div>
                </div>

                <div className="form-group">
                  <label>Payment Method</label>
                  <select disabled>
                    <option>Credit Card</option>
                    <option>Debit Card</option>
                    <option>Bank Transfer</option>
                  </select>
                </div>

                <div className="form-group">
                  <label>Amount</label>
                  <input
                    type="text"
                    value={`$${selectedAppointment.cost.toFixed(2)}`}
                    disabled
                  />
                </div>

                <div className="info-notice">
                  <AlertCircle size={16} />
                  <p>Payment processing system ready for integration</p>
                </div>
              </form>

              <div className="form-actions">
                <button className="btn-cancel" onClick={closePaymentModal}>
                  Close
                </button>
                <button className="btn-submit" disabled>
                  Payment Ready (Not Integrated)
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Invoice Modal */}
      {showInvoiceModal && selectedAppointment && (
        <div className="modal-overlay" onClick={closeInvoiceModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Invoice</h2>
              <button className="close-btn" onClick={closeInvoiceModal}>
                <X size={24} />
              </button>
            </div>

            <div className="modal-body">
              <div className="invoice-preview">
                <div className="invoice-header">
                  <h3>INVOICE</h3>
                  <p className="invoice-number">
                    #{selectedAppointment.invoiceNumber}
                  </p>
                </div>

                <div className="invoice-details">
                  <div className="invoice-section">
                    <h4>Bill To:</h4>
                    <p>{selectedAppointment.patientName}</p>
                    <p>{selectedAppointment.patientEmail}</p>
                    <p>{selectedAppointment.patientPhone}</p>
                  </div>

                  <div className="invoice-section">
                    <h4>Service Details:</h4>
                    <table className="invoice-table">
                      <tbody>
                        <tr>
                          <td>Doctor:</td>
                          <td>{selectedAppointment.doctorName}</td>
                        </tr>
                        <tr>
                          <td>Service:</td>
                          <td>{selectedAppointment.serviceType}</td>
                        </tr>
                        <tr>
                          <td>Date:</td>
                          <td>{selectedAppointment.appointmentDate}</td>
                        </tr>
                        <tr>
                          <td>Amount:</td>
                          <td>${selectedAppointment.cost.toFixed(2)}</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>

                <div className="invoice-footer">
                  <p>Thank you for your business!</p>
                </div>
              </div>

              <div className="form-actions">
                <button className="btn-cancel" onClick={closeInvoiceModal}>
                  Close
                </button>
                <button className="btn-submit" disabled>
                  Download (UI Demo)
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PatientBilling;
