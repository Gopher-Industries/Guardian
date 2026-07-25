import { useState } from "react";
import "./ReportsPage.css";

import {
  LineChart,
  Line,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

import { jsPDF } from "jspdf";
import autoTable from "jspdf-autotable";

const reportData = [
  { month: "Jan", reports: 120 },
  { month: "Feb", reports: 145 },
  { month: "Mar", reports: 160 },
  { month: "Apr", reports: 190 },
  { month: "May", reports: 175 },
  { month: "Jun", reports: 210 },
  { month: "Jul", reports: 248 },
];

const staffData = [
  { name: "Dr. Smith", completed: 42 },
  { name: "Nurse Lee", completed: 36 },
  { name: "Dr. Patel", completed: 31 },
  { name: "Nurse Brown", completed: 27 },
  { name: "Admin Team", completed: 22 },
];

const reports = [
  {
    id: "REP-001",
    name: "Patient Intake Summary",
    department: "Emergency",
    role: "Doctor",
    date: "24 Jul 2026",
    filterDate: "2026-07-24",
    status: "Completed",
  },
  {
    id: "REP-002",
    name: "Task Completion Report",
    department: "ICU",
    role: "Nurse",
    date: "23 Jul 2026",
    filterDate: "2026-07-23",
    status: "Pending",
  },
  {
    id: "REP-003",
    name: "Critical Alert Summary",
    department: "Cardiology",
    role: "Doctor",
    date: "22 Jul 2026",
    filterDate: "2026-07-22",
    status: "Completed",
  },
  {
    id: "REP-004",
    name: "Average Resolution Time",
    department: "Pediatrics",
    role: "Admin",
    date: "21 Jul 2026",
    filterDate: "2026-07-21",
    status: "In Review",
  },
];

export default function ReportsPage() {
  // FILTER STATES
  const [selectedDepartment, setSelectedDepartment] = useState("all");
  const [appliedDepartment, setAppliedDepartment] = useState("all");

  const [selectedRole, setSelectedRole] = useState("all");
  const [appliedRole, setAppliedRole] = useState("all");

  const [selectedDate, setSelectedDate] = useState("");
  const [appliedDate, setAppliedDate] = useState("");

  // SORT STATE
  const [sortOption, setSortOption] = useState("Newest");

  // APPLY FILTERS
  const handleApplyFilters = () => {
    setAppliedDepartment(selectedDepartment);
    setAppliedRole(selectedRole);
    setAppliedDate(selectedDate);
  };

  // FILTER REPORTS
  const filteredReports = reports.filter((report) => {
    const departmentMatches =
      appliedDepartment === "all" ||
      report.department === appliedDepartment;

    const roleMatches =
      appliedRole === "all" || report.role === appliedRole;

    const dateMatches =
      appliedDate === "" || report.filterDate === appliedDate;

    return departmentMatches && roleMatches && dateMatches;
  });

  // SORT REPORTS
  const sortedReports = [...filteredReports].sort((a, b) => {
    if (sortOption === "Newest") {
      return new Date(b.filterDate) - new Date(a.filterDate);
    }

    if (sortOption === "Oldest") {
      return new Date(a.filterDate) - new Date(b.filterDate);
    }

    if (sortOption === "Status") {
      return a.status.localeCompare(b.status);
    }

    if (sortOption === "Department") {
      return a.department.localeCompare(b.department);
    }

    return 0;
  });

  // STATUS COLOUR CLASS
  const getStatusClass = (status) => {
    if (status === "Completed") {
      return "completed";
    }

    if (status === "Pending") {
      return "pending";
    }

    return "review";
  };

  // EXPORT CSV
  const handleExportCSV = () => {
    const headers = [
      "Report ID",
      "Report Name",
      "Department",
      "Role",
      "Date",
      "Status",
    ];

    const rows = sortedReports.map((report) => [
      report.id,
      report.name,
      report.department,
      report.role,
      report.date,
      report.status,
    ]);

    const csvContent = [headers, ...rows]
      .map((row) =>
        row
          .map((value) => `"${String(value).replace(/"/g, '""')}"`)
          .join(",")
      )
      .join("\n");

    const blob = new Blob([csvContent], {
      type: "text/csv;charset=utf-8;",
    });

    const url = URL.createObjectURL(blob);

    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", "guardian-reports.csv");

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    URL.revokeObjectURL(url);
  };

  // EXPORT ALL FILTERED REPORTS AS PDF
  const handleExportPDF = () => {
    const doc = new jsPDF();

    doc.setFontSize(18);
    doc.text("Guardian Admin - Reports", 14, 20);

    doc.setFontSize(10);
    doc.text("Generated report data", 14, 28);

    autoTable(doc, {
      startY: 35,

      head: [
        [
          "Report ID",
          "Report Name",
          "Department",
          "Role",
          "Date",
          "Status",
        ],
      ],

      body: sortedReports.map((report) => [
        report.id,
        report.name,
        report.department,
        report.role,
        report.date,
        report.status,
      ]),

      styles: {
        fontSize: 9,
      },

      headStyles: {
        fillColor: [47, 128, 237],
      },
    });

    doc.save("guardian-reports.pdf");
  };

  // DOWNLOAD ONE INDIVIDUAL REPORT
  const handleDownloadReport = (report) => {
    const doc = new jsPDF();

    doc.setFontSize(18);
    doc.text("Guardian Admin - Report", 14, 20);

    doc.setFontSize(10);
    doc.text("Individual report details", 14, 28);

    autoTable(doc, {
      startY: 35,

      head: [["Field", "Details"]],

      body: [
        ["Report ID", report.id],
        ["Report Name", report.name],
        ["Department", report.department],
        ["Role", report.role],
        ["Date", report.date],
        ["Status", report.status],
      ],

      styles: {
        fontSize: 10,
      },

      headStyles: {
        fillColor: [47, 128, 237],
      },
    });

    doc.save(`${report.id}.pdf`);
  };

  return (
    <div className="reports-page">
      {/* HEADER */}
      <section className="reports-header">
        <div>
          <p className="reports-eyebrow">Analytics & Reporting</p>

          <h1>Reports</h1>

          <p className="reports-subtitle">
            View key performance insights, filter report data, and export
            results.
          </p>
        </div>

        <div className="reports-actions">
          <button
            className="secondary-btn"
            onClick={handleExportPDF}
          >
            Export PDF
          </button>

          <button
            className="primary-btn"
            onClick={handleExportCSV}
          >
            Export CSV
          </button>
        </div>
      </section>

      {/* FILTERS */}
      <section className="reports-filters">
        <input
          type="date"
          className="filter-input"
          value={selectedDate}
          onChange={(event) => setSelectedDate(event.target.value)}
        />

        <select
          className="filter-input"
          value={selectedDepartment}
          onChange={(event) =>
            setSelectedDepartment(event.target.value)
          }
        >
          <option value="all">All Departments</option>
          <option value="Emergency">Emergency</option>
          <option value="ICU">ICU</option>
          <option value="Cardiology">Cardiology</option>
          <option value="Pediatrics">Pediatrics</option>
        </select>

        <select
          className="filter-input"
          value={selectedRole}
          onChange={(event) => setSelectedRole(event.target.value)}
        >
          <option value="all">All Roles</option>
          <option value="Doctor">Doctor</option>
          <option value="Nurse">Nurse</option>
          <option value="Admin">Admin</option>
        </select>

        <button
          className="primary-btn"
          onClick={handleApplyFilters}
        >
          Apply Filters
        </button>
      </section>

      {/* SUMMARY CARDS */}
      <section className="reports-summary">
        <div className="summary-card">
          <h3>Total Reports</h3>
          <h2>248</h2>
          <p>Generated this month</p>
        </div>

        <div className="summary-card">
          <h3>Pending Reviews</h3>
          <h2>18</h2>
          <p>Awaiting approval</p>
        </div>

        <div className="summary-card">
          <h3>Critical Alerts</h3>
          <h2>6</h2>
          <p>Require attention</p>
        </div>

        <div className="summary-card">
          <h3>Exported Reports</h3>
          <h2>91</h2>
          <p>Last 30 days</p>
        </div>
      </section>

      {/* RECENT REPORTS TABLE */}
      <section className="reports-table-section">
        <div className="table-header">
          <div>
            <h2>Recent Reports</h2>

            <p>
              Latest generated reports and their current status.
            </p>
          </div>

          <div className="table-sort">
            <label htmlFor="report-sort">Sort by</label>

            <select
              id="report-sort"
              value={sortOption}
              onChange={(event) =>
                setSortOption(event.target.value)
              }
            >
              <option value="Newest">Newest</option>
              <option value="Oldest">Oldest</option>
              <option value="Status">Status</option>
              <option value="Department">Department</option>
            </select>
          </div>
        </div>

        <div className="reports-table-wrapper">
          <table className="reports-table">
            <thead>
              <tr>
                <th>Report ID</th>
                <th>Report Name</th>
                <th>Department</th>
                <th>Role</th>
                <th>Date</th>
                <th>Status</th>
                <th>Export</th>
              </tr>
            </thead>

            <tbody>
              {sortedReports.length > 0 ? (
                sortedReports.map((report) => (
                  <tr key={report.id}>
                    <td>{report.id}</td>

                    <td>{report.name}</td>

                    <td>{report.department}</td>

                    <td>{report.role}</td>

                    <td>{report.date}</td>

                    <td>
                      <span
                        className={`status-badge ${getStatusClass(
                          report.status
                        )}`}
                      >
                        {report.status}
                      </span>
                    </td>

                    <td>
                      <button
                        className="table-export-btn"
                        onClick={() =>
                          handleDownloadReport(report)
                        }
                      >
                        Download
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="7">
                    No reports found for the selected filters.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* PAGINATION */}
        <div className="table-pagination">
          <span>
            Showing {sortedReports.length === 0 ? 0 : 1}–
            {sortedReports.length} of {sortedReports.length} reports
          </span>

          <div className="pagination-buttons">
            <button disabled>Previous</button>

            <button className="active-page">1</button>

            <button disabled>Next</button>
          </div>
        </div>
      </section>

      {/* MONTHLY REPORTS CHART */}
      <section className="reports-chart">
        <h2>Monthly Reports</h2>

        <div className="chart-container">
          <ResponsiveContainer width="100%" height={320}>
            <LineChart data={reportData}>
              <CartesianGrid strokeDasharray="3 3" />

              <XAxis dataKey="month" />

              <YAxis />

              <Tooltip />

              <Line
                type="monotone"
                dataKey="reports"
                stroke="#2f80ed"
                strokeWidth={3}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </section>

      {/* STAFF COMPLETION CHART */}
      <section className="reports-chart">
        <h2>Task Completion by Staff</h2>

        <div className="chart-container">
          <ResponsiveContainer width="100%" height={320}>
            <BarChart
              data={staffData}
              margin={{
                top: 10,
                right: 10,
                left: 0,
                bottom: 20,
              }}
            >
              <CartesianGrid strokeDasharray="3 3" />

              <XAxis
                dataKey="name"
                angle={-25}
                textAnchor="end"
                height={70}
                interval={0}
              />

              <YAxis />

              <Tooltip />

              <Bar
                dataKey="completed"
                fill="#2f80ed"
                radius={[6, 6, 0, 0]}
              />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </section>
    </div>
  );
}