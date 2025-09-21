import axios from 'axios';

const API_BASE = 'http://localhost:5000/api/v1';

//Patients 
export const getPatients = () => axios.get(`${API_BASE}/patients`);
export const addPatient = (data) => axios.post(`${API_BASE}/patients`, data);
export const updatePatient = (id, data) => axios.put(`${API_BASE}/patients/${id}`, data);
export const deletePatient = (id) => axios.delete(`${API_BASE}/patients/${id}`);

// Caretakers
export const getCaretakers = () => axios.get(`${API_BASE}/caretaker`);
export const addCaretaker = (data) => axios.post(`${API_BASE}/caretaker`, data);
export const updateCaretaker = (id, data) => axios.put(`${API_BASE}/caretaker/${id}`, data);
export const deleteCaretaker = (id) => axios.delete(`${API_BASE}/caretaker/${id}`);

// Nurses
export const getNurses = () => axios.get(`${API_BASE}/nurse`);
export const addNurse = (data) => axios.post(`${API_BASE}/nurse`, data);
export const updateNurse = (id, data) => axios.put(`${API_BASE}/nurse/${id}`, data);
export const deleteNurse = (id) => axios.delete(`${API_BASE}/nurse/${id}`);

//Alerts
export const getAlerts = () => axios.get(`${API_BASE}/alerts`);
export const addAlert = (data) => axios.post(`${API_BASE}/alerts`, data);
export const updateAlert = (id, data) => axios.put(`${API_BASE}/alerts/${id}`, data);
export const deleteAlert = (id) => axios.delete(`${API_BASE}/alerts/${id}`);

// Notifications 
export const getNotifications = () => axios.get(`${API_BASE}/notifications`);
export const addNotification = (data) => axios.post(`${API_BASE}/notifications`, data);
export const updateNotification = (id, data) => axios.put(`${API_BASE}/notifications/${id}`, data);
export const deleteNotification = (id) => axios.delete(`${API_BASE}/notifications/${id}`);

// Doctors 
export const getDoctors = () => axios.get(`${API_BASE}/doctors`);
export const addDoctor = (data) => axios.post(`${API_BASE}/doctors`, data);
export const updateDoctor = (id, data) => axios.put(`${API_BASE}/doctors/${id}`, data);
export const deleteDoctor = (id) => axios.delete(`${API_BASE}/doctors/${id}`);

//Authentication
export const loginUser = (data) => axios.post(`${API_BASE}/auth/login`, data);
export const registerUser = (data) => axios.post(`${API_BASE}/auth/register`, data);
