import React from 'react';
import './App.css'; // reuse same styles
import { useNavigate } from 'react-router-dom';

const Patients = () => {
  const navigate = useNavigate();

  const patients = [
    { id: 1, name: 'Patient A', age: 78, condition: 'Diabetes' },
    { id: 2, name: 'Patient B', age: 85, condition: 'Dementia' },
    { id: 3, name: 'Patient C', age: 72, condition: 'Hypertension' },
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

      <h2>Patient List</h2>

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Age</th>
            <th>Condition</th>
          </tr>
        </thead>
        <tbody>
          {patients.map((patient) => (
            <tr key={patient.id}>
              <td>{patient.id}</td>
              <td>{patient.name}</td>
              <td>{patient.age}</td>
              <td>{patient.condition}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Patients;
