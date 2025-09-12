import React from 'react';
import './App.css';
import { useNavigate } from 'react-router-dom';

const Staff = () => {
  const navigate = useNavigate();

  const staffMembers = [
    { id: 1, name: 'Nurse 1', role: 'Nurse', shift: 'Morning' },
    { id: 2, name: 'Doctor 2', role: 'Doctor', shift: 'Evening' },
    { id: 3, name: 'Caretaker 3', role: 'Caretaker', shift: 'Night' },
    { id: 4, name: 'Doctor 4', role: 'Doctor', shift: 'Morning' },
  ];

  const handleLogout = () => {
    localStorage.removeItem('isAuthenticated');
    navigate('/');
  };

  return (
    <div className="main-content">
      <button onClick={handleLogout} className="logout-button">
        Logout
      </button>

      <h2>Staff List</h2>

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Role</th>
            <th>Shift</th>
          </tr>
        </thead>
        <tbody>
          {staffMembers.map((staff) => (
            <tr key={staff.id}>
              <td>{staff.id}</td>
              <td>{staff.name}</td>
              <td>{staff.role}</td>
              <td>{staff.shift}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Staff;
