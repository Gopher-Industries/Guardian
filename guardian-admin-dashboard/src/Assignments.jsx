import React, { useState } from 'react';

const Assignments = () => {
  const [selectedPatient, setSelectedPatient] = useState('');
  const [selectedStaff, setSelectedStaff] = useState('');
  const [notes, setNotes] = useState('');
  const [assignments, setAssignments] = useState([
    { id: 1, patient: 'Patient A', staff: 'Nurse Emma', notes: 'Checkup daily' },
    { id: 2, patient: 'Patient B', staff: 'Doctor James', notes: 'Blood test' },
    { id: 3, patient: 'Patient C', staff: 'Nurse Liam', notes: 'Vitals monitoring' },
  ]);
  const [successMessage, setSuccessMessage] = useState('');

  const handleCreateAssignment = () => {
    if (!selectedPatient || !selectedStaff) return;

    const newAssignment = {
      id: assignments.length + 1,
      patient: selectedPatient,
      staff: selectedStaff,
      notes: notes,
    };

    setAssignments([...assignments, newAssignment]);
    setSelectedPatient('');
    setSelectedStaff('');
    setNotes('');
    setSuccessMessage('Assignment created successfully!');

    setTimeout(() => setSuccessMessage(''), 3000);
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Assignments</h1>

      {/* Success message */}
      {successMessage && (
        <div className="mb-4 p-3 text-green-700 bg-green-100 rounded">
          {successMessage}
        </div>
      )}

      {/* Form */}
      <div className="bg-white shadow-md rounded-lg p-6 mb-6 w-full max-w-xl">
        <h2 className="text-lg font-semibold mb-4">Create New Assignment</h2>

        <select
          value={selectedPatient}
          onChange={(e) => setSelectedPatient(e.target.value)}
          className="w-full p-2 mb-3 border border-gray-300 rounded"
        >
          <option value="">Select Patient</option>
          <option value="Patient A">Patient A</option>
          <option value="Patient B">Patient B</option>
          <option value="Patient C">Patient C</option>
        </select>

        <select
          value={selectedStaff}
          onChange={(e) => setSelectedStaff(e.target.value)}
          className="w-full p-2 mb-3 border border-gray-300 rounded"
        >
          <option value="">Select Staff</option>
          <option value="Nurse Emma">Nurse Emma</option>
          <option value="Nurse Liam">Nurse Liam</option>
          <option value="Doctor James">Doctor James</option>
        </select>

        <input
          type="text"
          placeholder="Notes (optional)"
          value={notes}
          onChange={(e) => setNotes(e.target.value)}
          className="w-full p-2 mb-4 border border-gray-300 rounded"
        />

        <button
          onClick={handleCreateAssignment}
          disabled={!selectedPatient || !selectedStaff}
          className={`w-full p-2 rounded font-semibold text-white ${!selectedPatient || !selectedStaff
              ? 'bg-gray-400 cursor-not-allowed'
              : 'bg-green-600 hover:bg-green-700'
            }`}
        >
          Create Assignment
        </button>
      </div>

      {/* Table */}
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white shadow-md rounded-lg overflow-hidden">
          <thead className="bg-gray-100 text-gray-700">
            <tr>
              <th className="px-4 py-2 text-left">ID</th>
              <th className="px-4 py-2 text-left">Patient</th>
              <th className="px-4 py-2 text-left">Staff</th>
              <th className="px-4 py-2 text-left">Notes</th>
            </tr>
          </thead>
          <tbody>
            {assignments.map((a) => (
              <tr key={a.id} className="border-t">
                <td className="px-4 py-2">{a.id}</td>
                <td className="px-4 py-2">{a.patient}</td>
                <td className="px-4 py-2">{a.staff}</td>
                <td className="px-4 py-2">{a.notes}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Assignments;
