import React, { useState } from 'react';
import './admin.css'

// Types
interface Patient {
  id: number;
  name: string;
  age: number;
  condition: string;
  priority: 'low' | 'medium' | 'high' | 'urgent';
  status: 'unassigned' | 'assigned';
  department: string;
  notes?: string;
}

interface Staff {
  id: number;
  name: string;
  role: 'doctor' | 'nurse' | 'caretaker';
  department: string;
  maxPatients: number;
  status: 'available' | 'busy' | 'unavailable';
}

interface Assignment {
  id: number;
  patientId: number;
  staffId: number;
  status: 'active';
  notes?: string;
  assignedAt: Date;
}

// Initial Data
const initialPatients: Patient[] = [
  { id: 1, name: "John Doe", age: 45, condition: "Cardiac Care", priority: "urgent", status: "unassigned", department: "Cardiology" },
  { id: 2, name: "Maria Wilson", age: 62, condition: "Post-Surgery", priority: "medium", status: "unassigned", department: "Surgery" },
  { id: 3, name: "Robert Taylor", age: 38, condition: "Orthopedic", priority: "low", status: "unassigned", department: "Orthopedics" },
  { id: 4, name: "Alice Davis", age: 55, condition: "Cardiac Surgery", priority: "high", status: "assigned", department: "Cardiology" },
  { id: 5, name: "Brian Chen", age: 32, condition: "Post-Op Recovery", priority: "medium", status: "assigned", department: "Surgery" }
];

const initialStaff: Staff[] = [
  { id: 1, name: "Dr. Martinez", role: "doctor", department: "Cardiology", maxPatients: 4, status: "available" },
  { id: 2, name: "Nurse Smith", role: "nurse", department: "ICU", maxPatients: 5, status: "available" },
  { id: 3, name: "Nurse Wilson", role: "nurse", department: "Surgery", maxPatients: 5, status: "busy" },
  { id: 4, name: "Dr. Johnson", role: "doctor", department: "Cardiology", maxPatients: 4, status: "available" },
  { id: 5, name: "Dr. Lee", role: "doctor", department: "Orthopedics", maxPatients: 4, status: "available" }
];

const initialAssignments: Assignment[] = [
  { id: 1, patientId: 4, staffId: 1, status: "active", notes: "Regular monitoring required", assignedAt: new Date('2024-01-15') },
  { id: 2, patientId: 5, staffId: 3, status: "active", notes: "Post-operative care", assignedAt: new Date('2024-01-14') }
];

// Helper Functions
const getInitials = (name: string) => name.split(' ').map(n => n[0]).join('').toUpperCase();
const formatDate = (date: Date) => new Date(date).toLocaleDateString();

function App() {
  const [patients, setPatients] = useState<Patient[]>(initialPatients);
  const [staff, setStaff] = useState<Staff[]>(initialStaff);
  const [assignments, setAssignments] = useState<Assignment[]>(initialAssignments);
  const [activePage, setActivePage] = useState<string>('dashboard');

  const [searchPatient, setSearchPatient] = useState('');
  const [searchStaff, setSearchStaff] = useState('');
  const [searchAssignment, setSearchAssignment] = useState('');

  const [showPatientModal, setShowPatientModal] = useState(false);
  const [showStaffModal, setShowStaffModal] = useState(false);
  const [showAssignmentModal, setShowAssignmentModal] = useState(false);

  // Dashboard Stats
  const totalPatients = patients.length;
  const activeStaff = staff.filter(s => s.status === 'available').length;
  const activeAssignments = assignments.filter(a => a.status === 'active').length;
  const unassignedPatients = patients.filter(p => p.status === 'unassigned').length;

  // CRUD Operations
  const addPatient = (patient: Patient) => setPatients([...patients, patient]);
  const addStaff = (staffMember: Staff) => setStaff([...staff, staffMember]);
  const addAssignment = (assignment: Assignment) => setAssignments([...assignments, assignment]);

  // Filtered Lists
  const filteredPatients = patients.filter(p => p.name.toLowerCase().includes(searchPatient.toLowerCase()) || p.condition.toLowerCase().includes(searchPatient.toLowerCase()));
  const filteredStaff = staff.filter(s => s.name.toLowerCase().includes(searchStaff.toLowerCase()));
  const filteredAssignments = assignments.filter(a => {
    const patient = patients.find(p => p.id === a.patientId);
    const staffMember = staff.find(s => s.id === a.staffId);
    return (patient?.name.toLowerCase().includes(searchAssignment.toLowerCase()) || staffMember?.name.toLowerCase().includes(searchAssignment.toLowerCase()));
  });

  // JSX
  return (
    <>
    
    <div className="container">
      {/* Header */}
      <header className="header">
        <div className="logo">
          <div className="logo-icon"></div>
          <div className="logo-text">
            <h1>HealthCare</h1>
            <p>Assignment System</p>
          </div>
        </div>
      </header>

      {/* Sidebar */}
      <nav className="sidebar">
        <ul className="nav-menu">
          {['dashboard','patients','staff','assignments'].map(page => (
            <li key={page}>
              <button className={`nav-link ${activePage===page?'active':''}`} onClick={()=>setActivePage(page)}>
                {page==='dashboard'?' Dashboard':''}
                {page==='patients'?' Patients':''}
                {page==='staff'?' Staff':''}
                {page==='assignments'?'Assignments':''}
              </button>
            </li>
          ))}
        </ul>
      </nav>

      {/* Main Content */}
      <main className="main-content">
        {/* Dashboard */}
        {activePage==='dashboard' && (
          <div className="page active">
            <div className="page-header">
              <h2>Dashboard</h2>
              <p>Patient-Staff Assignment Overview</p>
            </div>
            <div className="stats-grid">
              <div className="stat-card"><div className="stat-content"><div className="stat-info"><p className="stat-label">Total Patients</p><p className="stat-value">{totalPatients}</p></div><div className="stat-icon">👥</div></div></div>
              <div className="stat-card"><div className="stat-content"><div className="stat-info"><p className="stat-label">Active Staff</p><p className="stat-value">{activeStaff}</p></div><div className="stat-icon">👨‍⚕️</div></div></div>
              <div className="stat-card"><div className="stat-content"><div className="stat-info"><p className="stat-label">Active Assignments</p><p className="stat-value">{activeAssignments}</p></div><div className="stat-icon">📋</div></div></div>
              <div className="stat-card"><div className="stat-content"><div className="stat-info"><p className="stat-label">Unassigned Patients</p><p className="stat-value">{unassignedPatients}</p></div><div className="stat-icon">⚠️</div></div></div>
            </div>
          </div>
        )}

        {/* Patients Page */}
        {activePage==='patients' && (
          <div className="page active">
            <div className="page-header"><h2>Patients</h2><p>Manage patient information and assignments</p></div>
            <div className="table-container">
              <div className="table-header">
                <h3>All Patients</h3>
                <div className="table-actions">
                  <input type="text" placeholder="Search patients..." className="search-input" value={searchPatient} onChange={(e)=>setSearchPatient(e.target.value)} />
                  <button className="btn btn-primary" onClick={()=>setShowPatientModal(true)}>+ Add Patient</button>
                </div>
              </div>
              <div className="table-wrapper">
                <table className="data-table">
                  <thead>
                    <tr><th>Patient</th><th>Age</th><th>Condition</th><th>Department</th><th>Priority</th><th>Status</th></tr>
                  </thead>
                  <tbody>
                    {filteredPatients.map(p=>(
                      <tr key={p.id}>
                        <td>{p.name}</td>
                        <td>{p.age}</td>
                        <td>{p.condition}</td>
                        <td>{p.department}</td>
                        <td>{p.priority}</td>
                        <td>{p.status}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* Staff Page */}
        {activePage==='staff' && (
          <div className="page active">
            <div className="page-header"><h2>Staff</h2><p>Manage healthcare staff and their assignments</p></div>
          </div>
        )}

        {/* Assignments Page */}
        {activePage==='assignments' && (
          <div className="page active">
            <div className="page-header"><h2>Assignments</h2><p>Manage patient-staff assignments</p></div>
          </div>
        )}
      </main>
    </div>
    </>
  );
}

export default App;
