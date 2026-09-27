import { useState, useEffect } from "react";
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
  Legend,
  ResponsiveContainer,
} from "recharts";

import { jsPDF } from "jspdf";
import autoTable from "jspdf-autotable";


import {
  getReportsList,
  getReportsSummary,
  getAdmissionsTrend,
  getStaffCompletion,
  getTasksOverview,
  getAppointmentsSummary,
  getAppointmentsTrend,
  getBillingOverview,
  getSupportTicketsOverview,
  getDoctorCaseload,
  getRosterCoverage,
  getPolypharmacyRisk,
  getStaffingRequirement,
  getReportsRiskData,
  notifyFlaggedPatients,
  getPatientRiskTrajectory,
} from "../services/reportsService";

function riskSummaryToReportRow(summary, index) {
  const statusLabel = summary.riskLevel === "flagged" ? "Flagged" : "Watch";

  return {
    id: `RISK-${String(index + 1).padStart(3, "0")}`,
    name: `Patient Risk Summary — ${summary.patientName}`,
    department: summary.department,
    role: "System",
    date: summary.date,
    filterDate: "2026-08-25",
    status: statusLabel,
    reasons: summary.reasons, 
    source: summary.source, 
  };
}

function SourceBadge({ source }) {
  if (!source) return null;

  const isLive = source === "live";

  return (
    <span className={`source-tag ${isLive ? "live" : "mock"}`}>
      {isLive ? "Live" : "Demo data"}
    </span>
  );
}

export default function ReportsPage() {
  const [selectedDepartment, setSelectedDepartment] = useState("all");
  const [appliedDepartment, setAppliedDepartment] = useState("all");

  const [selectedRole, setSelectedRole] = useState("all");
  const [appliedRole, setAppliedRole] = useState("all");

  const [selectedDate, setSelectedDate] = useState("");
  const [appliedDate, setAppliedDate] = useState("");

  const [sortOption, setSortOption] = useState("Newest");

  const [activeTab, setActiveTab] = useState("reports");

  const [baseReports, setBaseReports] = useState([]);
  const [reportsSummary, setReportsSummary] = useState(null);
  const [admissionsData, setAdmissionsData] = useState([]);
  const [staffData, setStaffData] = useState([]);
  const [tasksOverview, setTasksOverview] = useState(null);
  const [outstandingPage, setOutstandingPage] = useState(1);
  const OUTSTANDING_PAGE_SIZE = 8;
  const [appointmentsSummary, setAppointmentsSummary] = useState(null);
  const [appointmentsTrend, setAppointmentsTrend] = useState([]);
  const [billingOverview, setBillingOverview] = useState(null);
  const [supportTicketsOverview, setSupportTicketsOverview] = useState(null);
  const [doctorCaseload, setDoctorCaseload] = useState([]);
  const [rosterCoverage, setRosterCoverage] = useState(null);
  const [polypharmacyRisk, setPolypharmacyRisk] = useState(null);
  const [staffingRequirement, setStaffingRequirement] = useState([]);
  const [riskOverview, setRiskOverview] = useState(null);
  const [riskReports, setRiskReports] = useState([]);
  const [pageLoading, setPageLoading] = useState(true);
  const [liveTrajectoryCandidates, setLiveTrajectoryCandidates] = useState([]);
  const [selectedTrajectoryPatientId, setSelectedTrajectoryPatientId] = useState(null);
  const [trajectoryData, setTrajectoryData] = useState([]);
  const [trajectoryPatientName, setTrajectoryPatientName] = useState(null);
  const [trajectorySource, setTrajectorySource] = useState(null);
  const [trajectoryLoading, setTrajectoryLoading] = useState(false);

  function loadTrajectory(candidate) {
    setTrajectoryLoading(true);
    getPatientRiskTrajectory(candidate.patientId)
      .then((trajectory) => {
        setTrajectoryData(
          trajectory.map((point, i) => ({
            reading: `#${i + 1}`,
            reasonCount: point.reasonCount,
          }))
        );
        setTrajectoryPatientName(candidate.patientName);
        setTrajectorySource("live");
      })
      .catch(() => {
        setTrajectorySource(null); 
      })
      .finally(() => {
        setTrajectoryLoading(false);
      });
  }

  const [sources, setSources] = useState({
    reports: null,
    summary: null,
    admissions: null,
    staff: null,
    tasks: null,
    appointmentsSummary: null,
    appointmentsTrend: null,
    billing: null,
    supportTickets: null,
    doctorCaseload: null,
    rosterCoverage: null,
    polypharmacy: null,
    staffingRequirement: null,
    risk: null,
  });

  useEffect(() => {
    let isMounted = true;

    Promise.all([
      getReportsList(),
      getReportsSummary(),
      getAdmissionsTrend(),
      getStaffCompletion(),
      getTasksOverview(),
      getAppointmentsSummary(),
      getAppointmentsTrend(),
      getBillingOverview(),
      getSupportTicketsOverview(),
      getDoctorCaseload(),
      getRosterCoverage(),
      getPolypharmacyRisk(),
      getStaffingRequirement(),
      getReportsRiskData(),
    ]).then(
      ([
        reports,
        summary,
        admissions,
        staff,
        tasks,
        apptSummary,
        apptTrend,
        billing,
        supportTickets,
        doctors,
        roster,
        polypharmacy,
        staffingReq,
        risk,
      ]) => {
        if (!isMounted) return;

        setBaseReports(reports.data);
        setReportsSummary(summary.data);
        setAdmissionsData(admissions.data);
        setStaffData(staff.data);
        setTasksOverview(tasks.data);
        setAppointmentsSummary(apptSummary.data);
        setAppointmentsTrend(apptTrend.data);
        setBillingOverview(billing.data);
        setSupportTicketsOverview(supportTickets.data);
        setDoctorCaseload(doctors.data);
        setRosterCoverage(roster.data);
        setPolypharmacyRisk(polypharmacy.data);
        setStaffingRequirement(staffingReq.data);
        setRiskOverview(risk.overview);
        setRiskReports(risk.summaries.map(riskSummaryToReportRow));
        notifyFlaggedPatients(risk.summaries).catch(() => {});

        setSources({
          reports: reports.source,
          summary: summary.source,
          admissions: admissions.source,
          staff: staff.source,
          tasks: tasks.source,
          appointmentsSummary: apptSummary.source,
          appointmentsTrend: apptTrend.source,
          billing: billing.source,
          supportTickets: supportTickets.source,
          doctorCaseload: doctors.source,
          rosterCoverage: roster.source,
          polypharmacy: polypharmacy.source,
          staffingRequirement: staffingReq.source,
          risk: risk.source || "fallback",
        });

        setPageLoading(false);

      const candidates = risk.summaries.filter((s) => s.source === "live");
      setLiveTrajectoryCandidates(candidates);

      if (candidates.length > 0) {
        setSelectedTrajectoryPatientId(candidates[0].patientId);
        loadTrajectory(candidates[0]);
      }
    });

    return () => {
      isMounted = false;
    };
  }, []);

  const reports = [...baseReports, ...riskReports];

  const handleApplyFilters = () => {
    setAppliedDepartment(selectedDepartment);
    setAppliedRole(selectedRole);
    setAppliedDate(selectedDate);
  };

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

  const getStatusClass = (status) => {
    if (status === "Completed") {
      return "completed";
    }

    if (status === "Pending") {
      return "pending";
    }

    if (status === "Flagged") {
      return "flagged";
    }

    if (status === "Watch") {
      return "watch";
    }

    return "review";
  };

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

  const handleDownloadReport = (report) => {
    const doc = new jsPDF();

    doc.setFontSize(18);
    doc.text("Guardian Admin - Report", 14, 20);

    doc.setFontSize(10);
    doc.text("Individual report details", 14, 28);

    const body = [
      ["Report ID", report.id],
      ["Report Name", report.name],
      ["Department", report.department],
      ["Role", report.role],
      ["Date", report.date],
      ["Status", report.status],
    ];

    if (report.reasons) {
      body.push(["Reasons", report.reasons.join("; ")]);
    }

    autoTable(doc, {
      startY: 35,
      head: [["Field", "Details"]],
      body,
      styles: {
        fontSize: 10,
      },
      headStyles: {
        fillColor: [47, 128, 237],
      },
    });

    doc.save(`${report.id}.pdf`);
  };

  // Counts for the Risk Distribution chart
  const riskDistributionData = riskOverview
    ? [
        { level: "Normal", count: riskOverview.normal },
        { level: "Watch", count: riskOverview.watch },
        { level: "Flagged", count: riskOverview.critical },
      ]
    : [];

  const MONTH_ORDER = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
  const throughputMap = new Map();

  admissionsData.forEach((d) => {
    throughputMap.set(d.month, {
      month: d.month,
      admissions: d.admissions,
      admissionsForecast: d.forecast,
    });
  });

  appointmentsTrend.forEach((d) => {
    const existing = throughputMap.get(d.month) || { month: d.month };
    existing.appointments = d.appointments;
    existing.appointmentsForecast = d.predicted;
    throughputMap.set(d.month, existing);
  });

  const throughputData = [...throughputMap.values()].sort(
    (a, b) => MONTH_ORDER.indexOf(a.month) - MONTH_ORDER.indexOf(b.month)
  );

  const outstandingList = tasksOverview?.outstanding || [];
  const outstandingTotalPages = Math.max(1, Math.ceil(outstandingList.length / OUTSTANDING_PAGE_SIZE));
  const safeOutstandingPage = Math.min(outstandingPage, outstandingTotalPages);
  const outstandingPageItems = outstandingList.slice(
    (safeOutstandingPage - 1) * OUTSTANDING_PAGE_SIZE,
    safeOutstandingPage * OUTSTANDING_PAGE_SIZE
  );

  return (
    <div className="reports-page">
      <section className="reports-header">
        <div>
          <p className="reports-eyebrow">Analytics & Reporting</p>
          <h1>Reports</h1>
          <p className="reports-subtitle">
            View key performance insights, filter report data, and export
            results.
          </p>
          {!pageLoading && (
            <p className="data-source-summary">
              Data sources — Reports: <SourceBadge source={sources.reports} />
              {" "}Summary: <SourceBadge source={sources.summary} />
              {" "}Admissions: <SourceBadge source={sources.admissions} />
              {" "}Staff: <SourceBadge source={sources.staff} />
              {" "}Risk: <SourceBadge source={sources.risk} />
            </p>
          )}
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

      <div className="report-tabs">
        <button
          className={`report-tab ${activeTab === "reports" ? "active" : ""}`}
          onClick={() => setActiveTab("reports")}
        >
          Reports
        </button>
        <button
          className={`report-tab ${activeTab === "analytics" ? "active" : ""}`}
          onClick={() => setActiveTab("analytics")}
        >
          Analytics &amp; Insights
        </button>
      </div>

      {activeTab === "reports" && (
        <>
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
          <option value="System">System</option>
        </select>

        <button
          className="primary-btn"
          onClick={handleApplyFilters}
        >
          Apply Filters
        </button>
      </section>

      <section className="reports-summary">
        <div className="summary-card">
          <h3>Total Reports</h3>
          <h2>{pageLoading ? "…" : reportsSummary.totalReports}</h2>
          <p>Generated this month</p>
        </div>

        <div className="summary-card">
          <h3>Pending Reviews</h3>
          <h2>{pageLoading ? "…" : reportsSummary.pendingReviews}</h2>
          <p>Awaiting approval</p>
        </div>

        <div className="summary-card">
          <h3>Critical Alerts</h3>
          <h2>{pageLoading ? "…" : riskOverview.critical}</h2>
          <p>
            {pageLoading
              ? "Loading risk data…"
              : "Live — from patient risk model"}
          </p>
        </div>

        <div className="summary-card">
          <h3>Exported Reports</h3>
          <h2>{pageLoading ? "…" : reportsSummary.exportedReports}</h2>
          <p>Last 30 days</p>
        </div>
      </section>

      <section className="reports-table-section">
        <div className="table-header">
          <div>
            <h2>Recent Reports <SourceBadge source={sources.reports} /></h2>
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
                    <td title={report.reasons ? report.reasons.join(" | ") : undefined}>
                      {report.name}
                      {report.source && (
                        <span className={`source-tag ${report.source}`}>
                          {report.source === "live" ? "Live" : "Mock"}
                        </span>
                      )}
                    </td>
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
                        onClick={() => handleDownloadReport(report)}
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

      <section className="reports-chart">
        <h2>Task Completion by Staff <SourceBadge source={sources.staff} /></h2>

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

      <section className="reports-table-section">
        <div className="table-header">
          <div>
            <h2>Outstanding Tasks <SourceBadge source={sources.tasks} /></h2>
            <p>
              {pageLoading
                ? "Loading…"
                : `${tasksOverview.completedCount} completed, ${tasksOverview.notDoneCount} outstanding — ordered by priority.`}
            </p>
          </div>
        </div>

        <div className="reports-table-wrapper">
          <table className="reports-table">
            <thead>
              <tr>
                <th>Title</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Due Date</th>
                <th>Assignee</th>
              </tr>
            </thead>

            <tbody>
              {!pageLoading && outstandingPageItems.length > 0 ? (
                outstandingPageItems.map((task) => (
                  <tr key={task.id}>
                    <td>{task.title}</td>
                    <td>
                      <span className={`priority-badge ${task.priority}`}>{task.priority}</span>
                    </td>
                    <td>{task.status}</td>
                    <td>{task.dueDate}</td>
                    <td>{task.assignee}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="5">
                    {pageLoading ? "Loading…" : "No outstanding tasks."}
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {!pageLoading && outstandingTotalPages > 1 && (
          <div className="table-pagination">
            <span>
              Page {safeOutstandingPage} of {outstandingTotalPages} — {tasksOverview.outstanding.length} outstanding
            </span>

            <div className="pagination-buttons">
              <button
                disabled={safeOutstandingPage <= 1}
                onClick={() => setOutstandingPage((p) => Math.max(1, p - 1))}
              >
                Previous
              </button>
              <span className="active-page">{safeOutstandingPage}</span>
              <button
                disabled={safeOutstandingPage >= outstandingTotalPages}
                onClick={() => setOutstandingPage((p) => Math.min(outstandingTotalPages, p + 1))}
              >
                Next
              </button>
            </div>
          </div>
        )}
      </section>
        </>
      )}

      {activeTab === "analytics" && (
        <>
      <section className="reports-summary">
        <div className="summary-card">
          <h3>Total Appointments <SourceBadge source={sources.appointmentsSummary} /></h3>
          <h2>{pageLoading ? "…" : appointmentsSummary.totalAppointments}</h2>
          <p>This month</p>
        </div>

        <div className="summary-card">
          <h3>No-Shows <SourceBadge source={sources.appointmentsSummary} /></h3>
          <h2>{pageLoading ? "…" : appointmentsSummary.noShows}</h2>
          <p>This month</p>
        </div>
      </section>

      <section className="reports-summary">
        <div className="summary-card">
          <h3>Total Revenue <SourceBadge source={sources.billing} /></h3>
          <h2>{pageLoading ? "…" : `$${billingOverview.totalRevenue.toLocaleString()}`}</h2>
          <p>Settled billing</p>
        </div>

        <div className="summary-card">
          <h3>Outstanding Payments <SourceBadge source={sources.billing} /></h3>
          <h2>{pageLoading ? "…" : `$${billingOverview.outstandingPayments.toLocaleString()}`}</h2>
          <p>Pending invoices</p>
        </div>

        <div className="summary-card">
          <h3>Collection Rate <SourceBadge source={sources.billing} /></h3>
          <h2>{pageLoading ? "…" : `${billingOverview.collectionRate}%`}</h2>
          <p>Settled vs. total billed</p>
        </div>
      </section>

      <section className="reports-chart">
        <h2>Revenue by Service <SourceBadge source={sources.billing} /></h2>
        <p className="chart-note">
          Top revenue sources by service — highlights where facility revenue concentrates.
        </p>

        <div className="chart-container">
          <ResponsiveContainer width="100%" height={320}>
            <BarChart
              data={pageLoading ? [] : billingOverview.revenueByService}
              margin={{ top: 10, right: 10, left: 0, bottom: 70 }}
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="service" angle={-25} textAnchor="end" height={90} interval={0} />
              <YAxis />
              <Tooltip />
              <Bar dataKey="revenue" fill="#2f80ed" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </section>

      <section className="reports-summary">
        <div className="summary-card">
          <h3>Open Tickets <SourceBadge source={sources.supportTickets} /></h3>
          <h2>{pageLoading ? "…" : supportTicketsOverview.statusCounts.open}</h2>
          <p>Awaiting action</p>
        </div>

        <div className="summary-card">
          <h3>Avg. Resolution Time <SourceBadge source={sources.supportTickets} /></h3>
          <h2>
            {pageLoading
              ? "…"
              : supportTicketsOverview.avgResolutionHours != null
              ? `${supportTicketsOverview.avgResolutionHours}h`
              : "N/A"}
          </h2>
          <p>Resolved &amp; closed tickets</p>
        </div>

        <div className="summary-card">
          <h3>First Response Rate <SourceBadge source={sources.supportTickets} /></h3>
          <h2>{pageLoading ? "…" : `${supportTicketsOverview.firstResponseRate}%`}</h2>
          <p>Tickets with an admin reply</p>
        </div>
      </section>

      <section className="reports-chart">
        <h2>Tickets by Status <SourceBadge source={sources.supportTickets} /></h2>

        <div className="chart-container">
          <ResponsiveContainer width="100%" height={280}>
            <BarChart
              data={
                pageLoading
                  ? []
                  : [
                      { status: "Open", count: supportTicketsOverview.statusCounts.open },
                      { status: "In Progress", count: supportTicketsOverview.statusCounts.inProgress },
                      { status: "Resolved", count: supportTicketsOverview.statusCounts.resolved },
                      { status: "Closed", count: supportTicketsOverview.statusCounts.closed },
                    ]
              }
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="status" />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Bar dataKey="count" fill="#eb5757" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </section>

      <section className="reports-table-section">
        <div className="table-header">
          <div>
            <h2>Most Common Issues <SourceBadge source={sources.supportTickets} /></h2>
            <p>Recurring support subjects — an indicator of operational bottlenecks.</p>
          </div>
        </div>

        <div className="reports-table-wrapper">
          <table className="reports-table">
            <thead>
              <tr>
                <th>Subject</th>
                <th>Occurrences</th>
              </tr>
            </thead>

            <tbody>
              {!pageLoading && supportTicketsOverview.topSubjects.length > 0 ? (
                supportTicketsOverview.topSubjects.map((s, i) => (
                  <tr key={i}>
                    <td>{s.subject}</td>
                    <td>{s.count}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="2">{pageLoading ? "Loading…" : "No tickets found."}</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>

      <section className="reports-chart">
        <h2>Patients per Doctor <SourceBadge source={sources.doctorCaseload} /></h2>
        <p className="chart-note">Caseload balance across doctors — flags who's carrying the most patients.</p>

        <div className="chart-container">
          <ResponsiveContainer width="100%" height={320}>
            <BarChart
              data={doctorCaseload}
              margin={{ top: 10, right: 10, left: 0, bottom: 60 }}
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" angle={-25} textAnchor="end" height={80} interval={0} />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Bar dataKey="patientCount" fill="#2f80ed" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </section>

      <section className="reports-summary">
        <div className="summary-card">
          <h3>Clocked On Now <SourceBadge source={sources.rosterCoverage} /></h3>
          <h2>{pageLoading ? "…" : rosterCoverage.clockedOnNow}</h2>
          <p>Staff currently on shift</p>
        </div>

        <div className="summary-card">
          <h3>Total Shifts <SourceBadge source={sources.rosterCoverage} /></h3>
          <h2>{pageLoading ? "…" : rosterCoverage.totalShifts}</h2>
          <p>All rostered shifts</p>
        </div>
      </section>

      <section className="reports-chart">
        <h2>Shifts by Day of Week <SourceBadge source={sources.rosterCoverage} /></h2>
        <p className="chart-note">Recurring staffing pattern — low bars may indicate under-covered days.</p>

        <div className="chart-container">
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={pageLoading ? [] : rosterCoverage.shiftsByDay}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="day" />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Bar dataKey="shifts" fill="#4a944a" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </section>

      <section className="reports-chart">
        <h2>Staffing Requirement (RTRT) <SourceBadge source={sources.staffingRequirement} /></h2>
        <p className="chart-note">
          Right-Time-Right-Task: expected staff needed per day, modeled from historical
          admissions + appointments demand, against actual rostered shifts. Expected staff
          assumes 1 staff member per 4 patients/appointments in a day — a policy assumption
          (matches PATIENTS_PER_STAFF_RATIO in reportsService.js), not a measured value, and
          worth validating against real staffing guidelines.
        </p>

        <div className="chart-container">
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={staffingRequirement}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="day" />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Legend />
              <Bar dataKey="actualShifts" name="Actual shifts" fill="#2f80ed" radius={[6, 6, 0, 0]} />
              <Bar dataKey="expectedStaff" name="Expected staff (RTRT)" fill="#eb5757" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </section>

          <section className="reports-chart">
        <h2>Patient Admissions Trend <SourceBadge source={sources.admissions} /></h2>
        <p className="chart-note">Dashed segment is a projected next month, from a linear trend fit.</p>

        <div className="chart-container">
          <ResponsiveContainer width="100%" height={320}>
            <LineChart data={admissionsData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip />

              <Line
                type="monotone"
                dataKey="admissions"
                name="Actual"
                stroke="#2f80ed"
                strokeWidth={3}
                connectNulls={false}
              />
              <Line
                type="monotone"
                dataKey="forecast"
                name="Forecast"
                stroke="#2f80ed"
                strokeWidth={2}
                strokeDasharray="6 4"
                dot={{ r: 3 }}
                connectNulls
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </section>

      <section className="reports-chart">
        <h2>
          Appointments vs. Admissions Throughput{" "}
          <SourceBadge source={sources.admissions === "live" && sources.appointmentsTrend === "live" ? "live" : "fallback"} />
        </h2>
        <p className="chart-note">
          Shows appointment volume alongside admissions — a gap between the two suggests
          appointments aren't converting to admissions. Dashed segments are projected next month.
        </p>

        <div className="chart-container">
          <ResponsiveContainer width="100%" height={320}>
            <LineChart data={throughputData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip />
              <Legend />

              <Line
                type="monotone"
                dataKey="admissions"
                name="Admissions"
                stroke="#2f80ed"
                strokeWidth={3}
                connectNulls={false}
              />
              <Line
                type="monotone"
                dataKey="admissionsForecast"
                name="Admissions (forecast)"
                stroke="#2f80ed"
                strokeWidth={2}
                strokeDasharray="6 4"
                dot={{ r: 3 }}
                connectNulls
              />
              <Line
                type="monotone"
                dataKey="appointments"
                name="Appointments"
                stroke="#eb5757"
                strokeWidth={3}
                connectNulls={false}
              />
              <Line
                type="monotone"
                dataKey="appointmentsForecast"
                name="Appointments (forecast)"
                stroke="#eb5757"
                strokeWidth={2}
                strokeDasharray="6 4"
                dot={{ r: 3 }}
                connectNulls
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </section>

      <section className="reports-chart">
        <h2>Patient Risk Distribution <SourceBadge source={sources.risk} /></h2>

        <div className="chart-container">
          {pageLoading ? (
            <p>Loading risk data…</p>
          ) : (
            <ResponsiveContainer width="100%" height={320}>
              <BarChart data={riskDistributionData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="level" />
                <YAxis />
                <Tooltip />

                <Bar
                  dataKey="count"
                  fill="#eb5757"
                  radius={[6, 6, 0, 0]}
                />
              </BarChart>
            </ResponsiveContainer>
          )}
        </div>
      </section>

      {liveTrajectoryCandidates.length > 0 && (
        <section className="reports-chart">
          <div className="table-header">
            <div>
              <h2>
                Risk trajectory — {trajectoryPatientName} <SourceBadge source={trajectorySource} />
              </h2>
              <p className="chart-note">
                Replays this patient's full reading history, showing how many independent
                warning signs were present at each point in time — not just the latest snapshot.
              </p>
            </div>

            {liveTrajectoryCandidates.length > 1 && (
              <div className="table-sort">
                <label htmlFor="trajectory-patient">Patient</label>
                <select
                  id="trajectory-patient"
                  value={selectedTrajectoryPatientId || ""}
                  onChange={(event) => {
                    const candidate = liveTrajectoryCandidates.find(
                      (c) => c.patientId === event.target.value
                    );
                    setSelectedTrajectoryPatientId(event.target.value);
                    if (candidate) loadTrajectory(candidate);
                  }}
                >
                  {liveTrajectoryCandidates.map((c) => (
                    <option key={c.patientId} value={c.patientId}>
                      {c.patientName}
                    </option>
                  ))}
                </select>
              </div>
            )}
          </div>

          <div className="chart-container">
            {trajectoryLoading ? (
              <p>Loading trajectory…</p>
            ) : (
              <ResponsiveContainer width="100%" height={280}>
                <LineChart data={trajectoryData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="reading" label={{ value: "Reading #", position: "insideBottom", offset: -5 }} />
                  <YAxis allowDecimals={false} label={{ value: "Warning signs", angle: -90, position: "insideLeft" }} />
                  <Tooltip />

                  <Line
                    type="stepAfter"
                    dataKey="reasonCount"
                    stroke="#eb5757"
                    strokeWidth={3}
                    dot={{ r: 4 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            )}
          </div>
        </section>
      )}

      

      <section className="reports-summary">
        <div className="summary-card">
          <h3>Patients Checked <SourceBadge source={sources.polypharmacy} /></h3>
          <h2>{pageLoading ? "…" : polypharmacyRisk.totalChecked}</h2>
          <p>Screened for prescription load</p>
        </div>

        <div className="summary-card">
          <h3>Polypharmacy Flags <SourceBadge source={sources.polypharmacy} /></h3>
          <h2>{pageLoading ? "…" : polypharmacyRisk.flagged.length}</h2>
          <p>{pageLoading ? "" : `${polypharmacyRisk.threshold}+ active prescriptions`}</p>
        </div>
      </section>

      <section className="reports-table-section">
        <div className="table-header">
          <div>
            <h2>Polypharmacy Risk <SourceBadge source={sources.polypharmacy} /></h2>
            <p>
              Patients on {pageLoading ? "several" : polypharmacyRisk.threshold}+ concurrent active
              prescriptions — a recognized clinical risk factor, independent of the vitals-based risk
              indicator above.
            </p>
          </div>
        </div>

        <div className="reports-table-wrapper">
          <table className="reports-table">
            <thead>
              <tr>
                <th>Patient</th>
                <th>Active Prescriptions</th>
              </tr>
            </thead>

            <tbody>
              {!pageLoading && polypharmacyRisk.flagged.length > 0 ? (
                polypharmacyRisk.flagged.map((p) => (
                  <tr key={p.patientId}>
                    <td>{p.patientName}</td>
                    <td>{p.activeCount}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="2">{pageLoading ? "Loading…" : "No patients flagged."}</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>
        </>
      )}
    </div>
  );
}