import React, { useState, useEffect } from "react";
import "./admin.css";

const Admin = () => {
  const [activePage, setActivePage] = useState("dashboard");
  const [staffType, setStaffType] = useState("nurses");
  const [nurses, setNurses] = useState([]);
  const [caretakers, setCaretakers] = useState([]);
  const [patients, setPatients] = useState([]);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  // Add Patient / Staff form state
  const [showPatientForm, setShowPatientForm] = useState(false);
  const [showStaffForm, setShowStaffForm] = useState(false);
  const [formError, setFormError] = useState("");

  // Patient fields
  const [newPatientName, setNewPatientName] = useState("");
  const [newPatientGender, setNewPatientGender] = useState("male");
  const [newPatientDOB, setNewPatientDOB] = useState("");
  const [newPatientCaretakerID, setNewPatientCaretakerID] = useState("");

  // Staff fields
  const [newStaffName, setNewStaffName] = useState("");
  const [newStaffEmail, setNewStaffEmail] = useState("");
  const [newStaffRole, setNewStaffRole] = useState("nurse"); // backend still needs it

  // Check token
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) setIsLoggedIn(true);
  }, []);

  // Fetch data
  useEffect(() => {
    if (!isLoggedIn) return;
    const token = localStorage.getItem("token");

    const fetchData = async () => {
      try {
        const nurseRes = await fetch("/api/v1/nurse/all", {
          headers: { Authorization: `Bearer ${token}` },
        });
        const nurseData = await nurseRes.json();
        setNurses(nurseData.nurses || []);

        const caretakerRes = await fetch("/api/v1/caretaker", {
          headers: { Authorization: `Bearer ${token}` },
        });
        const caretakerData = await caretakerRes.json();
        const filteredCaretakers = (caretakerData.data || []).filter(
          (u) => u.role?.name === "caretaker"
        );
        setCaretakers(filteredCaretakers);

        const patientRes = await fetch("/api/v1/patients", {
          headers: { Authorization: `Bearer ${token}` },
        });
        const patientData = await patientRes.json();
        setPatients(patientData.data || []);
      } catch (err) {
        console.error("Error fetching data:", err);
      }
    };

    fetchData();
  }, [activePage, staffType, isLoggedIn]);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const res = await fetch("/api/v1/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });
      const data = await res.json();
      if (!res.ok) return setError(data.message || "Invalid credentials");
      localStorage.setItem("token", data.token);
      setIsLoggedIn(true);
      setActivePage("dashboard");
    } catch (err) {
      console.error(err);
      setError("Login failed. Please check your email and password.");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    setIsLoggedIn(false);
  };

  // Add Patient
  const handleAddPatient = async (e) => {
    e.preventDefault();
    setFormError("");
    const token = localStorage.getItem("token");

    try {
      const res = await fetch("/api/v1/patients/add", {
        method: "POST",
        headers: { 
          "Content-Type": "application/json", 
          Authorization: `Bearer ${token}` 
        },
        body: JSON.stringify({
          fullname: newPatientName,
          gender: newPatientGender,
          dateOfBirth: newPatientDOB,
          caretakerID: newPatientCaretakerID,
        }),
      });

      const data = await res.json();
      if (!res.ok) return setFormError(data.message || "Failed to add patient");

      setPatients([...patients, data]);
      setShowPatientForm(false);
      setNewPatientName("");
      setNewPatientGender("male");
      setNewPatientDOB("");
      setNewPatientCaretakerID("");
    } catch (err) {
      console.error(err);
      setFormError("Error adding patient.");
    }
  };

  // Add Staff
  const handleAddStaff = async (e) => {
    e.preventDefault();
    setFormError("");
    const token = localStorage.getItem("token");

    try {
      const res = await fetch("/api/v1/staff", {
        method: "POST",
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
        body: JSON.stringify({
          fullname: newStaffName,
          email: newStaffEmail,
          role: { name: newStaffRole },
        }),
      });
      const data = await res.json();
      if (!res.ok) return setFormError(data.message || "Failed to add staff");

      if (newStaffRole === "nurse") setNurses([...nurses, data]);
      else setCaretakers([...caretakers, data]);

      setShowStaffForm(false);
      setNewStaffName("");
      setNewStaffEmail("");
      setNewStaffRole("nurse");
    } catch (err) {
      console.error(err);
      setFormError("Error adding staff.");
    }
  };

  if (!isLoggedIn) {
    return (
      <div className="login-page">
        <div className="login-card">
          <div className="login-logo">
            <img 
              src="/logo.png" 
              alt="Guardian Monitor Logo" 
              style={{ width: "80px", height: "80px", borderRadius: "8px" }} 
            />
          </div>
          <div className="login-header">
            <h1 className="app-title">Guardian Monitor</h1>
            <p className="app-subtitle">Sign in to manage your healthcare system</p>
          </div>
          <form onSubmit={handleLogin} className="login-form">
            <input type="email" placeholder="Email address" value={email} onChange={(e) => setEmail(e.target.value)} required />
            <input type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} required />
            {error && <p className="error">{error}</p>}
            <button type="submit" className="btn-login">Log In</button>
          </form>
        </div>
      </div>
    );
  }

  const renderPage = () => {
    switch (activePage) {
      case "dashboard":
        return (
          <div className="page">
            <div className="page-header">
              <h2>Dashboard</h2>
              <p>Patient-Staff Overview</p>
            </div>
            <div className="stats-grid">
              <div className="stat-card">
                <p className="stat-label">Total Patients</p>
                <p className="stat-value">{patients.length}</p>
              </div>
              <div className="stat-card">
                <p className="stat-label">Total Nurses</p>
                <p className="stat-value">{nurses.length}</p>
              </div>
              <div className="stat-card">
                <p className="stat-label">Total Caretakers</p>
                <p className="stat-value">{caretakers.length}</p>
              </div>
            </div>
          </div>
        );

      case "patients":
        return (
          <div className="page">
            <div className="page-header">
              <h2>Patients</h2>
              <p>Manage patient information</p>
              <button className="btn btn-primary" onClick={() => setShowPatientForm(true)}>Add Patient</button>
            </div>

            {showPatientForm && (
              <div className="modal-overlay">
                <div className="modal">
                  <button className="close-btn" onClick={() => setShowPatientForm(false)}>×</button>
                  <h3>Add New Patient</h3>
                  <form className="modal-form" onSubmit={handleAddPatient}>
                    <div className="form-group">
                      <label>Full Name</label>
                      <input type="text" value={newPatientName} onChange={(e) => setNewPatientName(e.target.value)} required />
                    </div>
                    <div className="form-group">
                      <label>Gender</label>
                      <select value={newPatientGender} onChange={(e) => setNewPatientGender(e.target.value)}>
                        <option value="male">Male</option>
                        <option value="female">Female</option>
                        <option value="other">Other</option>
                      </select>
                    </div>
                    <div className="form-group">
                      <label>Date of Birth</label>
                      <input type="date" value={newPatientDOB} onChange={(e) => setNewPatientDOB(e.target.value)} required />
                    </div>
                    <div className="form-group">
                      <label>Caretaker</label>
                      <select value={newPatientCaretakerID} onChange={(e) => setNewPatientCaretakerID(e.target.value)} required>
                        <option value="">Select Caretaker</option>
                        {caretakers.map((c) => (
                          <option key={c._id} value={c._id}>{c.fullname}</option>
                        ))}
                      </select>
                    </div>
                    {formError && <p className="error">{formError}</p>}
                    <button type="submit" className="btn-submit">Save Patient</button>
                  </form>
                </div>
              </div>
            )}

            <div className="table-container">
              {patients.length > 0 ? (
                <table className="table">
                  <thead>
                    <tr>
                      <th>Full Name</th>
                      <th>Gender</th>
                      <th>Date of Birth</th>
                      <th>Caretaker</th>
                    </tr>
                  </thead>
                  <tbody>
                    {patients.map((patient) => (
                      <tr key={patient._id}>
                        <td>{patient.fullname}</td>
                        <td>{patient.gender}</td>
                        <td>{patient.dateOfBirth}</td>
                        <td>{patient.caretaker?.fullname || "N/A"}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : <p>No patients found.</p>}
            </div>
          </div>
        );

      case "staff":
        const currentStaff = staffType === "nurses" ? nurses : caretakers;
        return (
          <div className="page">
            <div className="page-header">
              <h2>Staff</h2>
              <p>Manage healthcare staff and their assignments</p>
              <button className="btn btn-primary" onClick={() => setShowStaffForm(true)}>Add Staff</button>
            </div>

            {showStaffForm && (
              <div className="modal-overlay">
                <div className="modal">
                  <button className="close-btn" onClick={() => setShowStaffForm(false)}>×</button>
                  <h3>Add New Staff</h3>
                  <form className="modal-form" onSubmit={handleAddStaff}>
                    <div className="form-group">
                      <label>Full Name</label>
                      <input type="text" value={newStaffName} onChange={(e) => setNewStaffName(e.target.value)} required />
                    </div>
                    <div className="form-group">
                      <label>Email</label>
                      <input type="email" value={newStaffEmail} onChange={(e) => setNewStaffEmail(e.target.value)} required />
                    </div>
                    {formError && <p className="error">{formError}</p>}
                    <button type="submit" className="btn-submit">Save Staff</button>
                  </form>
                </div>
              </div>
            )}

            <div className="staff-switch">
              <button className={`btn ${staffType === "nurses" ? "btn-primary" : "btn-secondary"}`} onClick={() => setStaffType("nurses")}>Nurses</button>
              <button className={`btn ${staffType === "caretakers" ? "btn-primary" : "btn-secondary"}`} onClick={() => setStaffType("caretakers")}>Caretakers</button>
            </div>

            <div className="table-container">
              {currentStaff.length > 0 ? (
                <table className="table">
                  <thead>
                    <tr>
                      <th>Full Name</th>
                      <th>User ID</th>
                      <th>Email</th>
                    </tr>
                  </thead>
                  <tbody>
                    {currentStaff.map((staffMember) => (
                      <tr key={staffMember._id}>
                        <td>{staffMember.fullname}</td>
                        <td>{staffMember._id}</td>
                        <td>{staffMember.email}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : <p>No {staffType} found.</p>}
            </div>
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <div className="container">
      <header className="header">
        <div className="logo">
          <img 
            src="/logo.png" 
            alt="Guardian Monitor Logo" 
            style={{ width: "80px", height: "80px", borderRadius: "8px" }} 
          />
          <div className="logo-text">
            <h1>Guardian Monitor</h1>
            <p>Assignment System</p>
          </div>
        </div>
        <div className="header-right">
          <button className="btn btn-secondary" onClick={handleLogout}>Logout</button>
        </div>
      </header>

      <nav className="sidebar">
        <ul className="nav-menu">
          <li>
            <button className={`nav-link ${activePage === "dashboard" ? "active" : ""}`} onClick={() => setActivePage("dashboard")}>Dashboard</button>
          </li>
          <li>
            <button className={`nav-link ${activePage === "patients" ? "active" : ""}`} onClick={() => setActivePage("patients")}>Patients</button>
          </li>
          <li>
            <button className={`nav-link ${activePage === "staff" ? "active" : ""}`} onClick={() => setActivePage("staff")}>Staff</button>
          </li>
        </ul>
      </nav>

      <main className="main-content">{renderPage()}</main>
    </div>
  );
};

export default Admin;
