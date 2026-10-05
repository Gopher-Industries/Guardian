import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3000/api';

const subscriptionAPI = axios.create({
  baseURL: `${API_BASE_URL}/subscription`,
});

subscriptionAPI.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

const mockInvoices = [
  {
    id: '1',
    invoiceNumber: 'INV-2024-001',
    description: 'Monthly subscription - Guardian Monitor',
    amount: 1500.0,
    issueDate: '2024-01-05',
    dueDate: '2024-02-05',
    status: 'Paid',
    organizationName: 'Guardian Health Org',
    paidDate: '2024-01-28',
  },
  {
    id: '2',
    invoiceNumber: 'INV-2024-002',
    description: 'Monthly subscription - Guardian Monitor',
    amount: 1500.0,
    issueDate: '2024-02-05',
    dueDate: '2024-03-05',
    status: 'Paid',
    organizationName: 'Guardian Health Org',
    paidDate: '2024-02-25',
  },
  {
    id: '3',
    invoiceNumber: 'INV-2024-003',
    description: 'Monthly subscription - Guardian Monitor',
    amount: 1500.0,
    issueDate: '2024-03-05',
    dueDate: '2024-04-05',
    status: 'Paid',
    organizationName: 'Guardian Health Org',
    paidDate: '2024-03-30',
  },
  {
    id: '4',
    invoiceNumber: 'INV-2024-004',
    description: 'Monthly subscription - Guardian Monitor',
    amount: 1500.0,
    issueDate: '2024-04-05',
    dueDate: '2024-05-05',
    status: 'Paid',
    organizationName: 'Guardian Health Org',
    paidDate: '2024-04-28',
  },
  {
    id: '5',
    invoiceNumber: 'INV-2024-005',
    description: 'Monthly subscription - Guardian Monitor',
    amount: 1500.0,
    issueDate: '2024-05-05',
    dueDate: '2024-06-05',
    status: 'Paid',
    organizationName: 'Guardian Health Org',
    paidDate: '2024-05-30',
  },
  {
    id: '6',
    invoiceNumber: 'INV-2024-006',
    description: 'Monthly subscription - Guardian Monitor',
    amount: 1500.0,
    issueDate: '2024-06-05',
    dueDate: '2024-07-05',
    status: 'Paid',
    organizationName: 'Guardian Health Org',
    paidDate: '2024-06-28',
  },
  {
    id: '7',
    invoiceNumber: 'INV-2024-007',
    description: 'Monthly subscription - Guardian Monitor',
    amount: 1500.0,
    issueDate: '2024-07-05',
    dueDate: '2024-08-05',
    status: 'Pending',
    organizationName: 'Guardian Health Org',
  },
  {
    id: '8',
    invoiceNumber: 'INV-2024-008',
    description: 'API usage charges - Additional requests',
    amount: 250.0,
    issueDate: '2024-07-10',
    dueDate: '2024-08-10',
    status: 'Pending',
    organizationName: 'Guardian Health Org',
  },
  {
    id: '9',
    invoiceNumber: 'INV-2024-009',
    description: 'Monthly subscription - Guardian Monitor',
    amount: 1500.0,
    issueDate: '2024-08-05',
    dueDate: '2024-09-05',
    status: 'Overdue',
    organizationName: 'Guardian Health Org',
  },
  {
    id: '10',
    invoiceNumber: 'INV-2024-010',
    description: 'Support package upgrade',
    amount: 500.0,
    issueDate: '2024-08-15',
    dueDate: '2024-09-15',
    status: 'Pending',
    organizationName: 'Guardian Health Org',
  },
  {
    id: '11',
    invoiceNumber: 'INV-2024-011',
    description: 'Custom development - Report module',
    amount: 2000.0,
    issueDate: '2024-08-20',
    dueDate: '2024-09-20',
    status: 'Pending',
    organizationName: 'Guardian Health Org',
  },
];

const mockPaymentHistory = [
  {
    id: '1',
    invoiceNumber: 'INV-2024-006',
    description: 'Monthly subscription payment',
    amount: 1500.0,
    date: '2024-06-28',
    status: 'Paid',
    paymentMethod: 'Credit Card',
    transactionId: 'TXN-2024-006001',
  },
  {
    id: '2',
    invoiceNumber: 'INV-2024-005',
    description: 'Monthly subscription payment',
    amount: 1500.0,
    date: '2024-05-30',
    status: 'Paid',
    paymentMethod: 'Bank Transfer',
    transactionId: 'TXN-2024-005001',
  },
  {
    id: '3',
    invoiceNumber: 'INV-2024-004',
    description: 'Monthly subscription payment',
    amount: 1500.0,
    date: '2024-04-28',
    status: 'Paid',
    paymentMethod: 'Credit Card',
    transactionId: 'TXN-2024-004001',
  },
  {
    id: '4',
    invoiceNumber: 'INV-2024-003',
    description: 'Monthly subscription payment',
    amount: 1500.0,
    date: '2024-03-30',
    status: 'Paid',
    paymentMethod: 'Bank Transfer',
    transactionId: 'TXN-2024-003001',
  },
  {
    id: '5',
    invoiceNumber: 'INV-2024-002',
    description: 'Monthly subscription payment',
    amount: 1500.0,
    date: '2024-02-25',
    status: 'Paid',
    paymentMethod: 'Credit Card',
    transactionId: 'TXN-2024-002001',
  },
  {
    id: '6',
    invoiceNumber: 'INV-2024-001',
    description: 'Monthly subscription payment',
    amount: 1500.0,
    date: '2024-01-28',
    status: 'Paid',
    paymentMethod: 'Credit Card',
    transactionId: 'TXN-2024-001001',
  },
  {
    id: '7',
    invoiceNumber: 'INV-2023-012',
    description: 'December subscription payment',
    amount: 1500.0,
    date: '2023-12-28',
    status: 'Paid',
    paymentMethod: 'Bank Transfer',
    transactionId: 'TXN-2023-012001',
  },
  {
    id: '8',
    invoiceNumber: 'INV-2023-011',
    description: 'November subscription payment',
    amount: 1500.0,
    date: '2023-11-25',
    status: 'Paid',
    paymentMethod: 'Credit Card',
    transactionId: 'TXN-2023-011001',
  },
];

export const getInvoices = async (filters = {}) => {
  try {
    // Uncomment the line below when backend is ready
    // const response = await billingAPI.get('/invoices', { params: filters });
    // return response.data;

    // Mock implementation
    return mockInvoices;
  } catch (error) {
    console.error('Error fetching invoices:', error);
    throw error;
  }
};

export const getInvoiceById = async (invoiceId) => {
  try {
    // Uncomment the line below when backend is ready
    // const response = await billingAPI.get(`/invoices/${invoiceId}`);
    // return response.data;

    // Mock implementation
    return mockInvoices.find((inv) => inv.id === invoiceId);
  } catch (error) {
    console.error('Error fetching invoice:', error);
    throw error;
  }
};

export const getPaymentHistory = async (filters = {}) => {
  try {
    // Uncomment the line below when backend is ready
    // const response = await billingAPI.get('/payments', { params: filters });
    // return response.data;

    // Mock implementation
    return mockPaymentHistory;
  } catch (error) {
    console.error('Error fetching payment history:', error);
    throw error;
  }
};

export const downloadInvoice = async (invoiceId) => {
  try {
    // Uncomment the line below when backend is ready
    // const response = await billingAPI.get(`/invoices/${invoiceId}/download`, {
    //   responseType: 'blob',
    // });
    // return response.data;

    // Mock implementation
    alert(`Download invoice ${invoiceId} functionality to be implemented with backend`);
    return null;
  } catch (error) {
    console.error('Error downloading invoice:', error);
    throw error;
  }
};

export const getBillingStats = async () => {
  try {
    // Uncomment the line below when backend is ready
    // const response = await billingAPI.get('/stats');
    // return response.data;

    // Mock implementation
    return {
      totalInvoices: mockInvoices.length,
      totalRevenue: mockInvoices.reduce((sum, inv) => sum + inv.amount, 0),
      paidInvoices: mockInvoices.filter((inv) => inv.status === 'Paid').length,
      pendingAmount: mockInvoices
        .filter((inv) => inv.status === 'Pending')
        .reduce((sum, inv) => sum + inv.amount, 0),
      overdueAmount: mockInvoices
        .filter((inv) => inv.status === 'Overdue')
        .reduce((sum, inv) => sum + inv.amount, 0),
    };
  } catch (error) {
    console.error('Error fetching billing stats:', error);
    throw error;
  }
};

export const updateInvoiceStatus = async (invoiceId, status) => {
  try {
    // Uncomment the line below when backend is ready
    // const response = await billingAPI.patch(`/invoices/${invoiceId}`, { status });
    // return response.data;

    // Mock implementation
    alert(`Update invoice ${invoiceId} status to ${status} - functionality to be implemented with backend`);
    return null;
  } catch (error) {
    console.error('Error updating invoice status:', error);
    throw error;
  }
};

export const recordPayment = async (invoiceId, paymentData) => {
  try {
    // Uncomment the line below when backend is ready
    // const response = await billingAPI.post(`/invoices/${invoiceId}/payment`, paymentData);
    // return response.data;

    // Mock implementation
    alert(`Record payment for invoice ${invoiceId} - functionality to be implemented with backend`);
    return null;
  } catch (error) {
    console.error('Error recording payment:', error);
    throw error;
  }
};

export default subscriptionAPI;
