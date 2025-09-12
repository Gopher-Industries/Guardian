import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "./index.css";

const Sidebar = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  return (
    <aside className="sidebar">
      <h2>
        Guardian<br />Admin Portal
      </h2>
      <nav>
        <button
          className={isActive("/dashboard") ? "active" : ""}
          onClick={() => navigate("/dashboard")}
        >
          Dashboard
        </button>
        <button
          className={isActive("/patients") ? "active" : ""}
          onClick={() => navigate("/patients")}
        >
          Patients
        </button>
        <button
          className={isActive("/staff") ? "active" : ""}
          onClick={() => navigate("/staff")}
        >
          Staff
        </button>
        <button
          className={isActive("/assignments") ? "active" : ""}
          onClick={() => navigate("/assignments")}
        >
          Assignments
        </button>
      </nav>
    </aside>
  );
};

export default Sidebar;
