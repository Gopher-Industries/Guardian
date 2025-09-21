import dotenv from 'dotenv';
dotenv.config();

import { 
    getPatients, addPatient as addPatientApi, updatePatient as updatePatientApi, deletePatientApi,
    getStaff, addStaff as addStaffApi, updateStaff as updateStaffApi, deleteStaffApi,
    getAssignments, addAssignment as addAssignmentApi, updateAssignment as updateAssignmentApi, deleteAssignmentApi
} from './api.js';

let patients = [];
let staff = [];
let assignments = [];

async function loadData() {
    try {
        const [patientsRes, staffRes, assignmentsRes] = await Promise.all([
            getPatients(),
            getStaff(),
            getAssignments()
        ]);

        patients = patientsRes.data;
        staff = staffRes.data;
        assignments = assignmentsRes.data;

        updateDashboard();
        updatePatientsTable();
        updateStaffTable();
        updateAssignmentsTable();
    } catch (err) {
        console.error('Error loading data:', err);
    }
}

document.addEventListener('DOMContentLoaded', loadData);

// Data Storage
patients = [
    { id: 1, name: "John Doe", age: 45, condition: "Cardiac Care", priority: "urgent", status: "unassigned", department: "Cardiology", notes: "" },
    { id: 2, name: "Maria Wilson", age: 62, condition: "Post-Surgery", priority: "medium", status: "unassigned", department: "Surgery", notes: "" },
    { id: 3, name: "Robert Taylor", age: 38, condition: "Orthopedic", priority: "low", status: "unassigned", department: "Orthopedics", notes: "" },
    { id: 4, name: "Alice Davis", age: 55, condition: "Cardiac Surgery", priority: "high", status: "assigned", department: "Cardiology", notes: "" },
    { id: 5, name: "Brian Chen", age: 32, condition: "Post-Op Recovery", priority: "medium", status: "assigned", department: "Surgery", notes: "" }
];

staff = [
    { id: 1, name: "Dr. Martinez", role: "doctor", department: "Cardiology", maxPatients: 4, status: "available" },
    { id: 2, name: "Nurse Smith", role: "nurse", department: "ICU", maxPatients: 5, status: "available" },
    { id: 3, name: "Nurse Wilson", role: "nurse", department: "Surgery", maxPatients: 5, status: "busy" },
    { id: 4, name: "Dr. Johnson", role: "doctor", department: "Cardiology", maxPatients: 4, status: "available" },
    { id: 5, name: "Dr. Lee", role: "doctor", department: "Orthopedics", maxPatients: 4, status: "available" }
];

assignments = [
    { id: 1, patientId: 4, staffId: 1, status: "active", notes: "Regular monitoring required", assignedAt: new Date('2024-01-15') },
    { id: 2, patientId: 5, staffId: 3, status: "active", notes: "Post-operative care", assignedAt: new Date('2024-01-14') }
];

let currentEditId = null;
let currentEditType = null;

// Utility Functions
function generateId(array) {
    return array.length > 0 ? Math.max(...array.map(item => item.id)) + 1 : 1;
}

function getPatientById(id) {
    return patients.find(p => p.id === parseInt(id));
}

function getStaffById(id) {
    return staff.find(s => s.id === parseInt(id));
}

function getInitials(name) {
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
}

function formatDate(date) {
    return new Date(date).toLocaleDateString();
}

// Navigation
function showPage(pageId) {
    // this Hide all pages
    document.querySelectorAll('.page').forEach(page => {
        page.classList.remove('active');
    });
    
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });
    
    document.getElementById(pageId).classList.add('active');
   
    document.querySelector(`[data-page="${pageId}"]`).classList.add('active');
    
    if (pageId === 'dashboard') {
        updateDashboard();
    } else if (pageId === 'patients') {
        updatePatientsTable();
    } else if (pageId === 'staff') {
        updateStaffTable();
    } else if (pageId === 'assignments') {
        updateAssignmentsTable();
    }
}

// Dashboard Functions
function updateDashboard() {
    const totalPatients = patients.length;
    const activeStaff = staff.filter(s => s.status === 'available').length;
    const activeAssignments = assignments.filter(a => a.status === 'active').length;
    const unassignedPatients = patients.filter(p => p.status === 'unassigned').length;
    
    document.getElementById('total-patients').textContent = totalPatients;
    document.getElementById('active-staff').textContent = activeStaff;
    document.getElementById('active-assignments').textContent = activeAssignments;
    document.getElementById('unassigned-patients').textContent = unassignedPatients;
    
    updateUnassignedList();
    updateAvailableStaffList();
}

function updateUnassignedList() {
    const unassignedList = document.getElementById('unassigned-list');
    const unassignedPatients = patients.filter(p => p.status === 'unassigned');
    
    unassignedList.innerHTML = '';
    
    unassignedPatients.forEach(patient => {
        const item = document.createElement('div');
        item.className = 'patient-item';
        item.innerHTML = `
            <div class="patient-info">
                <div class="avatar">${getInitials(patient.name)}</div>
                <div class="item-details">
                    <div class="item-name">${patient.name}</div>
                    <div class="item-subtitle">${patient.condition}</div>
                </div>
            </div>
            <span class="badge badge-${patient.priority}">${patient.priority}</span>
        `;
        unassignedList.appendChild(item);
    });
}

function updateAvailableStaffList() {
    const availableList = document.getElementById('available-staff-list');
    const availableStaff = staff.filter(s => s.status === 'available');
    
    availableList.innerHTML = '';
    
    availableStaff.forEach(staffMember => {
        const currentAssignments = assignments.filter(a => a.staffId === staffMember.id && a.status === 'active').length;
        
        if (currentAssignments < staffMember.maxPatients) {
            const item = document.createElement('div');
            item.className = 'staff-item';
            item.innerHTML = `
                <div class="staff-info">
                    <div class="avatar">👨‍⚕️</div>
                    <div class="item-details">
                        <div class="item-name">${staffMember.name}</div>
                        <div class="item-subtitle">${staffMember.department}</div>
                    </div>
                </div>
                <div class="item-subtitle">${staffMember.maxPatients} max</div>
            `;
            availableList.appendChild(item);
        }
    });
}

// Patients Functions
function updatePatientsTable() {
    const tbody = document.getElementById('patients-table-body');
    const searchTerm = document.getElementById('patient-search').value.toLowerCase();
    
    const filteredPatients = patients.filter(patient => 
        patient.name.toLowerCase().includes(searchTerm) ||
        patient.condition.toLowerCase().includes(searchTerm)
    );
    
    tbody.innerHTML = '';
    
    filteredPatients.forEach(patient => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>
                <div class="patient-cell">
                    <div class="avatar">${getInitials(patient.name)}</div>
                    <div class="patient-details">
                        <div class="patient-name">${patient.name}</div>
                        <div class="patient-id">ID: ${patient.id}</div>
                    </div>
                </div>
            </td>
            <td>${patient.age}</td>
            <td>${patient.condition}</td>
            <td>${patient.department || 'N/A'}</td>
            <td><span class="badge badge-${patient.priority}">${patient.priority}</span></td>
            <td><span class="badge badge-${patient.status}">${patient.status}</span></td>
            <td>
                <button class="btn btn-ghost btn-sm" onclick="editPatient(${patient.id})">✏️</button>
                <button class="btn btn-ghost btn-sm" onclick="deletePatient(${patient.id})">🗑️</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Staff Functions
function updateStaffTable() {
    const tbody = document.getElementById('staff-table-body');
    const searchTerm = document.getElementById('staff-search').value.toLowerCase();
    
    const filteredStaff = staff.filter(staffMember => 
        staffMember.name.toLowerCase().includes(searchTerm) ||
        staffMember.department.toLowerCase().includes(searchTerm) ||
        staffMember.role.toLowerCase().includes(searchTerm)
    );
    
    tbody.innerHTML = '';
    
    filteredStaff.forEach(staffMember => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>
                <div class="patient-cell">
                    <div class="avatar">${getInitials(staffMember.name)}</div>
                    <div class="patient-details">
                        <div class="patient-name">${staffMember.name}</div>
                        <div class="patient-id">ID: ${staffMember.id}</div>
                    </div>
                </div>
            </td>
            <td><span class="badge badge-${staffMember.role}">${staffMember.role}</span></td>
            <td>${staffMember.department}</td>
            <td>${staffMember.maxPatients}</td>
            <td><span class="badge badge-${staffMember.status}">${staffMember.status}</span></td>
            <td>
                <button class="btn btn-ghost btn-sm" onclick="editStaff(${staffMember.id})">✏️</button>
                <button class="btn btn-ghost btn-sm" onclick="deleteStaff(${staffMember.id})">🗑️</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Assignment Functions
function updateAssignmentsTable() {
    const tbody = document.getElementById('assignments-table-body');
    const searchTerm = document.getElementById('assignment-search').value.toLowerCase();
    
    const assignmentsWithDetails = assignments.map(assignment => {
        const patient = getPatientById(assignment.patientId);
        const staffMember = getStaffById(assignment.staffId);
        return { ...assignment, patient, staff: staffMember };
    }).filter(assignment => assignment.patient && assignment.staff);
    
    const filteredAssignments = assignmentsWithDetails.filter(assignment => 
        assignment.patient.name.toLowerCase().includes(searchTerm) ||
        assignment.staff.name.toLowerCase().includes(searchTerm) ||
        assignment.patient.condition.toLowerCase().includes(searchTerm)
    );
    
    tbody.innerHTML = '';
    
    filteredAssignments.forEach(assignment => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>
                <div class="patient-cell">
                    <div class="avatar">${getInitials(assignment.patient.name)}</div>
                    <div class="patient-details">
                        <div class="patient-name">${assignment.patient.name}</div>
                        <div class="patient-id">Age: ${assignment.patient.age}</div>
                    </div>
                </div>
            </td>
            <td>${assignment.patient.condition}</td>
            <td>
                <div class="patient-details">
                    <div class="patient-name">${assignment.staff.name}</div>
                    <div class="patient-id">${assignment.staff.role}</div>
                </div>
            </td>
            <td>${assignment.staff.department}</td>
            <td><span class="badge badge-${assignment.patient.priority}">${assignment.patient.priority}</span></td>
            <td><span class="badge badge-${assignment.status}">${assignment.status}</span></td>
            <td>${formatDate(assignment.assignedAt)}</td>
            <td>
                <button class="btn btn-ghost btn-sm" onclick="editAssignment(${assignment.id})">✏️</button>
                <button class="btn btn-ghost btn-sm" onclick="deleteAssignment(${assignment.id})">🗑️</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Modal Functions
function openModal(modalId) {
    document.getElementById(modalId).classList.add('active');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
    currentEditId = null;
    currentEditType = null;
    
    // this part Reset forms
    if (modalId === 'patient-modal') {
        document.getElementById('patient-form').reset();
        document.getElementById('patient-modal-title').textContent = 'Add New Patient';
    } else if (modalId === 'staff-modal') {
        document.getElementById('staff-form').reset();
        document.getElementById('staff-modal-title').textContent = 'Add New Staff Member';
    } else if (modalId === 'assignment-modal') {
        document.getElementById('assignment-form').reset();
        document.getElementById('assignment-modal-title').textContent = 'Create New Assignment';
    }
}

function openPatientModal() {
    updateAssignmentSelects();
    openModal('patient-modal');
}

function openStaffModal() {
    openModal('staff-modal');
}

function openAssignmentModal() {
    updateAssignmentSelects();
    openModal('assignment-modal');
}

function updateAssignmentSelects() {
    const patientSelect = document.getElementById('assignment-patient');
    const staffSelect = document.getElementById('assignment-staff');
    
    // Update patient select
    patientSelect.innerHTML = '<option value="">Choose a patient...</option>';
    const unassignedPatients = patients.filter(p => p.status === 'unassigned');
    unassignedPatients.forEach(patient => {
        const option = document.createElement('option');
        option.value = patient.id;
        option.textContent = `${patient.name} - ${patient.condition}`;
        patientSelect.appendChild(option);
    });
    
    // Update staf select
    staffSelect.innerHTML = '<option value="">Choose staff member...</option>';
    const availableStaff = staff.filter(s => {
        const currentAssignments = assignments.filter(a => a.staffId === s.id && a.status === 'active').length;
        return s.status === 'available' && currentAssignments < s.maxPatients;
    });
    availableStaff.forEach(staffMember => {
        const option = document.createElement('option');
        option.value = staffMember.id;
        option.textContent = `${staffMember.name} - ${staffMember.department} (${staffMember.role})`;
        staffSelect.appendChild(option);
    });
}

// CRUD Function
function editPatient(id) {
    const patient = getPatientById(id);
    if (!patient) return;
    
    currentEditId = id;
    currentEditType = 'patient';
    
    document.getElementById('patient-name').value = patient.name;
    document.getElementById('patient-age').value = patient.age;
    document.getElementById('patient-condition').value = patient.condition;
    document.getElementById('patient-department').value = patient.department || '';
    document.getElementById('patient-priority').value = patient.priority;
    document.getElementById('patient-notes').value = patient.notes || '';
    
    document.getElementById('patient-modal-title').textContent = 'Edit Patient';
    openModal('patient-modal');
}

async function deletePatient(id) {
    if (confirm('Are you sure you want to delete this patient?')) {
        try {
            await deletePatientApi(id); // API call
            await loadData(); // reloads updated data
        } catch (err) {
            console.error('Error deleting patient:', err);
        }
    }
}


function editStaff(id) {
    const staffMember = getStaffById(id);
    if (!staffMember) return;
    
    currentEditId = id;
    currentEditType = 'staff';
    
    document.getElementById('staff-name').value = staffMember.name;
    document.getElementById('staff-role').value = staffMember.role;
    document.getElementById('staff-department').value = staffMember.department;
    document.getElementById('staff-max-patients').value = staffMember.maxPatients;
    document.getElementById('staff-status').value = staffMember.status;
    
    document.getElementById('staff-modal-title').textContent = 'Edit Staff Member';
    openModal('staff-modal');
}

async function deleteStaff(id) {
    if (confirm('Are you sure you want to delete this staff member?')) {
        try {
            await deleteStaffApi(id);  // API call
            await loadData();
        } catch (err) {
            console.error('Error deleting staff:', err);
        }
    }
}

function editAssignment(id) {
    const assignment = assignments.find(a => a.id === id);
    if (!assignment) return;
    
    currentEditId = id;
    currentEditType = 'assignment';
    
    updateAssignmentSelects();
    
    // this part adds current patient and staff to selects if not already there
    const patient = getPatientById(assignment.patientId);
    const staffMember = getStaffById(assignment.staffId);
    
    if (patient) {
        const patientSelect = document.getElementById('assignment-patient');
        const existingOption = patientSelect.querySelector(`option[value="${patient.id}"]`);
        if (!existingOption) {
            const option = document.createElement('option');
            option.value = patient.id;
            option.textContent = `${patient.name} - ${patient.condition} [Current]`;
            patientSelect.appendChild(option);
        }
        patientSelect.value = assignment.patientId;
    }
    
    if (staffMember) {
        const staffSelect = document.getElementById('assignment-staff');
        const existingOption = staffSelect.querySelector(`option[value="${staffMember.id}"]`);
        if (!existingOption) {
            const option = document.createElement('option');
            option.value = staffMember.id;
            option.textContent = `${staffMember.name} - ${staffMember.department} (${staffMember.role}) [Current]`;
            staffSelect.appendChild(option);
        }
        staffSelect.value = assignment.staffId;
    }
    
    document.getElementById('assignment-notes').value = assignment.notes || '';
    
    document.getElementById('assignment-modal-title').textContent = 'Edit Assignment';
    openModal('assignment-modal');
}

async function deleteAssignment(id) {
    if (confirm('Are you sure you want to remove this assignment?')) {
        try {
            await deleteAssignmentApi(id); // API call
            await loadData();
        } catch (err) {
            console.error('Error deleting assignment:', err);
        }
    }
}

// Form Handlers
document.getElementById('patient-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const patientData = {
        name: formData.get('name'),
        age: parseInt(formData.get('age')),
        condition: formData.get('condition'),
        department: formData.get('department'),
        priority: formData.get('priority'),
        notes: formData.get('notes'),
        status: 'unassigned'
    };
    
    try {
        if (currentEditType === 'patient' && currentEditId) {
            await updatePatientApi(currentEditId, patientData); // API update
        } else {
            await addPatientApi(patientData); // API add
        }
        closeModal('patient-modal');
        await loadData(); // reload updated data
    } catch (err) {
        console.error('Error saving patient:', err);
    }
});
document.getElementById('staff-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const staffData = {
        name: formData.get('name'),
        role: formData.get('role'),
        department: formData.get('department'),
        maxPatients: parseInt(formData.get('maxPatients')),
        status: formData.get('status')
    };
    
    try {
        if (currentEditType === 'staff' && currentEditId) {
            await updateStaffApi(currentEditId, staffData); // API update
        } else {
            await addStaffApi(staffData); // API add
        }
        closeModal('staff-modal');
        await loadData();
    } catch (err) {
        console.error('Error saving staff:', err);
    }
});


document.getElementById('assignment-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const assignmentData = {
        patientId: parseInt(formData.get('patientId')),
        staffId: parseInt(formData.get('staffId')),
        notes: formData.get('notes'),
        status: 'active',
        assignedAt: new Date()
    };
    
    try {
        if (currentEditType === 'assignment' && currentEditId) {
            await updateAssignmentApi(currentEditId, assignmentData); // API update
        } else {
            await addAssignmentApi(assignmentData); // API add
        }
        closeModal('assignment-modal');
        await loadData();
    } catch (err) {
        console.error('Error saving assignment:', err);
    }
});
// Search Functions
document.getElementById('patient-search').addEventListener('input', updatePatientsTable);
document.getElementById('staff-search').addEventListener('input', updateStaffTable);
document.getElementById('assignment-search').addEventListener('input', updateAssignmentsTable);

// Event Listeners
document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', function(e) {
        e.preventDefault();
        const pageId = this.getAttribute('data-page');
        showPage(pageId);
    });
});

// Close modal when clicking outside
window.addEventListener('click', function(e) {
    if (e.target.classList.contains('modal')) {
        e.target.classList.remove('active');
        currentEditId = null;
        currentEditType = null;
    }
});

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    showPage('dashboard');
});