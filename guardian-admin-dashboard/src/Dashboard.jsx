// src/Dashboard.jsx
import React from 'react';
import './App.css';

const Dashboard = () => {
  return (
    <div className="dashboard">
      <aside className="sidebar">
        <h2>Guardian<br />Admin Panel</h2>
        <nav>
          <button>Dashboard</button>
          <button>Patients</button>
          <button>Staff</button>
          <button>Assignments</button>
        </nav>
      </aside>

      <main className="main-content">
        <h2>Dashboard Overview</h2>

        <div className="card-grid">
          <div className="card">Total Patients: 5</div>
          <div className="card">Active Staff: 4</div>
          <div className="card">Assignments: 3</div>
          <div className="card">Unassigned: 2</div>
        </div>

        <button className="create-button">+ New Assignment</button>

        <div className="assignment-form">
          <h3>Create New Assignment</h3>
          <select>
            <option>Choose a patient...</option>
            <option>Patient A</option>
            <option>Patient B</option>
          </select>
          <select>
            <option>Choose staff member...</option>
            <option>Dr. John</option>
            <option>Nurse Emma</option>
          </select>
          <input placeholder="Notes (optional)" />
          <button>Create Assignment</button>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;
