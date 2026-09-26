import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3000/api';

const patientBillingAPI = axios.create({
  baseURL: `${API_BASE_URL}/patient-billing`,
});

patientBillingAPI.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// MOCK DATA - Patient Appointments
const mockAppointments = [
  {
    id: 'APT-001',
    patientId: 'PAT-001',
    patientName: 'John Smith',
    patientEmail: 'john.smith@example.com',
    patientPhone: '555-0101',
    doctorName: 'Dr. Sarah Johnson',
    appointmentDate: '2024-09-10',
    appointmentTime: '10:00 AM',
    serviceType: 'General Checkup',
    cost: 150.00,
    status: 'Completed',
    paymentStatus: 'Paid',
    invoiceNumber: 'INV-2024-001',
  },
  {
    id: 'APT-002',
    patientId: 'PAT-002',
    patientName: 'Jane Doe',
    patientEmail: 'jane.doe@example.com',
    patientPhone: '555-0102',
    doctorName: 'Dr. Michael Chen',
    appointmentDate: '2024-09-12',
    appointmentTime: '2:30 PM',
    serviceType: 'Follow-up Consultation',
    cost: 200.00,
    status: 'Completed',
    paymentStatus: 'Pending',
    invoiceNumber: 'INV-2024-002',
  },
  {
    id: 'APT-003',
    patientId: 'PAT-003',
    patientName: 'Robert Wilson',
    patientEmail: 'robert.wilson@example.com',
    patientPhone: '555-0103',
    doctorName: 'Dr. Sarah Johnson',
    appointmentDate: '2024-09-15',
    appointmentTime: '11:00 AM',
    serviceType: 'Dental Cleaning',
    cost: 100.00,
    status: 'Completed',
    paymentStatus: 'Pending',
    invoiceNumber: 'INV-2024-003',
  },
  {
    id: 'APT-004',
    patientId: 'PAT-004',
    patientName: 'Emily Davis',
    patientEmail: 'emily.davis@example.com',
    patientPhone: '555-0104',
    doctorName: 'Dr. Emily White',
    appointmentDate: '2024-09-08',
    appointmentTime: '9:00 AM',
    serviceType: 'Lab Work',
    cost: 75.00,
    status: 'Completed',
    paymentStatus: 'Paid',
    invoiceNumber: 'INV-2024-004',
  },
  {
    id: 'APT-005',
    patientId: 'PAT-005',
    patientName: 'David Brown',
    patientEmail: 'david.brown@example.com',
    patientPhone: '555-0105',
    doctorName: 'Dr. James Miller',
    appointmentDate: '2024-09-05',
    appointmentTime: '3:00 PM',
    serviceType: 'Consultation',
    cost: 250.00,
    status: 'Completed',
    paymentStatus: 'Overdue',
    invoiceNumber: 'INV-2024-005',
  },
  {
    id: 'APT-006',
    patientId: 'PAT-006',
    patientName: 'Lisa Anderson',
    patientEmail: 'lisa.anderson@example.com',
    patientPhone: '555-0106',
    doctorName: 'Dr. Sarah Johnson',
    appointmentDate: '2024-09-18',
    appointmentTime: '1:00 PM',
    serviceType: 'Annual Physical',
    cost: 300.00,
    status: 'Scheduled',
    paymentStatus: 'Pending',
    invoiceNumber: 'INV-2024-006',
  },
  {
    id: 'APT-007',
    patientId: 'PAT-007',
    patientName: 'Michael Johnson',
    patientEmail: 'michael.j@example.com',
    patientPhone: '555-0107',
    doctorName: 'Dr. Michael Chen',
    appointmentDate: '2024-09-14',
    appointmentTime: '4:00 PM',
    serviceType: 'Blood Pressure Check',
    cost: 50.00,
    status: 'Completed',
    paymentStatus: 'Paid',
    invoiceNumber: 'INV-2024-007',
  },
  {
    id: 'APT-008',
    patientId: 'PAT-008',
    patientName: 'Sarah Lee',
    patientEmail: 'sarah.lee@example.com',
    patientPhone: '555-0108',
    doctorName: 'Dr. Emily White',
    appointmentDate: '2024-09-16',
    appointmentTime: '10:30 AM',
    serviceType: 'Eye Exam',
    cost: 120.00,
    status: 'Completed',
    paymentStatus: 'Pending',
    invoiceNumber: 'INV-2024-008',
  },
  {
    id: 'APT-009',
    patientId: 'PAT-009',
    patientName: 'Thomas Martinez',
    patientEmail: 'thomas.m@example.com',
    patientPhone: '555-0109',
    doctorName: 'Dr. James Miller',
    appointmentDate: '2024-09-20',
    appointmentTime: '2:00 PM',
    serviceType: 'Physical Therapy',
    cost: 180.00,
    status: 'Scheduled',
    paymentStatus: 'Pending',
    invoiceNumber: 'INV-2024-009',
  },
  {
    id: 'APT-010',
    patientId: 'PAT-010',
    patientName: 'Jennifer Taylor',
    patientEmail: 'jennifer.t@example.com',
    patientPhone: '555-0110',
    doctorName: 'Dr. Sarah Johnson',
    appointmentDate: '2024-09-11',
    appointmentTime: '11:30 AM',
    serviceType: 'Vaccination',
    cost: 85.00,
    status: 'Completed',
    paymentStatus: 'Paid',
    invoiceNumber: 'INV-2024-010',
  },
];

// MOCK DATA - Payment History
const mockPaymentHistory = [
  {
    id: 'PAY-001',
    patientName: 'John Smith',
    patientId: 'PAT-001',
    appointmentDate: '2024-09-10',
    amount: 150.00,
    paymentDate: '2024-09-10',
    paymentMethod: 'Credit Card',
    transactionId: 'TXN-PA-001',
    status: 'Completed',
  },
  {
    id: 'PAY-002',
    patientName: 'Emily Davis',
    patientId: 'PAT-004',
    appointmentDate: '2024-09-08',
    amount: 75.00,
    paymentDate: '2024-09-09',
    paymentMethod: 'Debit Card',
    transactionId: 'TXN-PA-002',
    status: 'Completed',
  },
  {
    id: 'PAY-003',
    patientName: 'Michael Johnson',
    patientId: 'PAT-007',
    appointmentDate: '2024-09-14',
    amount: 50.00,
    paymentDate: '2024-09-14',
    paymentMethod: 'Credit Card',
    transactionId: 'TXN-PA-003',
    status: 'Completed',
  },
  {
    id: 'PAY-004',
    patientName: 'Jennifer Taylor',
    patientId: 'PAT-010',
    appointmentDate: '2024-09-11',
    amount: 85.00,
    paymentDate: '2024-09-11',
    paymentMethod: 'Bank Transfer',
    transactionId: 'TXN-PA-004',
    status: 'Completed',
  },
  {
    id: 'PAY-005',
    patientName: 'Robert Wilson',
    patientId: 'PAT-003',
    appointmentDate: '2024-08-28',
    amount: 100.00,
    paymentDate: '2024-08-29',
    paymentMethod: 'Credit Card',
    transactionId: 'TXN-PA-005',
    status: 'Completed',
  },
  {
    id: 'PAY-006',
    patientName: 'Sarah Lee',
    patientId: 'PAT-008',
    appointmentDate: '2024-08-25',
    amount: 200.00,
    paymentDate: '2024-08-26',
    paymentMethod: 'Debit Card',
    transactionId: 'TXN-PA-006',
    status: 'Completed',
  },
];

/**
 * Get all patient appointments
 */
export const getPatientAppointments = async (filters = {}) => {
  try {
    // PRODUCTION: Connect to backend API
    // const response = await patientBillingAPI.get('/appointments', { params: filters });
    // return response.data;

    // Return mock data
    return mockAppointments;
  } catch (error) {
    console.error('Error fetching appointments:', error);
    throw error;
  }
};

/**
 * Get payment history
 */
export const getPaymentHistory = async (filters = {}) => {
  try {
    // PRODUCTION: Connect to backend API
    // const response = await patientBillingAPI.get('/payments', { params: filters });
    // return response.data;

    // Return mock data
    return mockPaymentHistory;
  } catch (error) {
    console.error('Error fetching payments:', error);
    throw error;
  }
};

export default patientBillingAPI;
