import api from "./api";

const USE_MOCK_DATA = true;

// ---------------------------------------------------------------------
// Fallback data
// ---------------------------------------------------------------------

const FALLBACK_REPORTS = [
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

const FALLBACK_SUMMARY = {
  totalReports: 248,
  pendingReviews: 18,
  exportedReports: 91,
};

const FALLBACK_ADMISSIONS = [
  { month: "Jan", admissions: 120 },
  { month: "Feb", admissions: 145 },
  { month: "Mar", admissions: 160 },
  { month: "Apr", admissions: 190 },
  { month: "May", admissions: 175 },
  { month: "Jun", admissions: 210 },
  { month: "Jul", admissions: 248 },
];

const FALLBACK_STAFF = [
  { name: "Dr. Smith", completed: 42 },
  { name: "Nurse Lee", completed: 36 },
  { name: "Dr. Patel", completed: 31 },
  { name: "Nurse Brown", completed: 27 },
  { name: "Admin Team", completed: 22 },
];

const FALLBACK_TASKS_OVERVIEW = {
  completedCount: 34,
  notDoneCount: 3,
  outstanding: [
    { id: "T-1", title: "Follow up on medication change", priority: "high", status: "pending", dueDate: "02 Sep 2026", assignee: "Dr. Smith" },
    { id: "T-2", title: "Update care plan notes", priority: "medium", status: "in progress", dueDate: "04 Sep 2026", assignee: "Nurse Lee" },
    { id: "T-3", title: "File monthly billing summary", priority: "low", status: "pending", dueDate: "10 Sep 2026", assignee: "Admin Team" },
  ],
};

const FALLBACK_APPOINTMENTS_SUMMARY = {
  totalAppointments: 156,
  noShows: 14,
};

const FALLBACK_APPOINTMENTS_TREND = [
  { month: "Jun", appointments: 38 },
  { month: "Jul", appointments: 45 },
  { month: "Aug", appointments: 52 },
];

const FALLBACK_BILLING_OVERVIEW = {
  totalRevenue: 48250,
  outstandingPayments: 12300,
  collectionRate: 79.7,
  revenueByService: [
    { service: "Level B Consultation", revenue: 15200 },
    { service: "Specialist Attendance", revenue: 11400 },
    { service: "Nursing Home Visit", revenue: 9800 },
    { service: "Care Plan Review", revenue: 6700 },
    { service: "Telehealth Consultation", revenue: 5150 },
  ],
};

const FALLBACK_SUPPORT_OVERVIEW = {
  statusCounts: { open: 6, inProgress: 3, resolved: 22, closed: 9 },
  avgResolutionHours: 14.5,
  firstResponseRate: 82,
  topSubjects: [
    { subject: "Roster clock-in failure", count: 4 },
    { subject: "Billing PDF export timeout", count: 3 },
    { subject: "Notification not received", count: 2 },
  ],
};

const FALLBACK_DOCTOR_CASELOAD = [
  { name: "Dr. Smith", patientCount: 18 },
  { name: "Dr. Patel", patientCount: 12 },
  { name: "Dr. Lee", patientCount: 9 },
  { name: "Dr. Brown", patientCount: 6 },
];

const FALLBACK_ROSTER_COVERAGE = {
  shiftsByDay: [
    { day: "Sun", shifts: 4 },
    { day: "Mon", shifts: 9 },
    { day: "Tue", shifts: 8 },
    { day: "Wed", shifts: 9 },
    { day: "Thu", shifts: 7 },
    { day: "Fri", shifts: 8 },
    { day: "Sat", shifts: 5 },
  ],
  clockedOnNow: 6,
  totalShifts: 50,
};

const FALLBACK_POLYPHARMACY = {
  threshold: 5,
  totalChecked: 42,
  flagged: [
    { patientId: "F-1", patientName: "Eleanor R.", activeCount: 7 },
    { patientId: "F-2", patientName: "Harold P.", activeCount: 6 },
    { patientId: "F-3", patientName: "Grace T.", activeCount: 5 },
  ],
};

const FALLBACK_STAFFING_REQUIREMENT = [
  { day: "Sun", actualShifts: 4, expectedStaff: 3 },
  { day: "Mon", actualShifts: 9, expectedStaff: 8 },
  { day: "Tue", actualShifts: 8, expectedStaff: 9 },
  { day: "Wed", actualShifts: 9, expectedStaff: 8 },
  { day: "Thu", actualShifts: 7, expectedStaff: 7 },
  { day: "Fri", actualShifts: 8, expectedStaff: 9 },
  { day: "Sat", actualShifts: 5, expectedStaff: 4 },
];

const FALLBACK_RISK = {
  overview: { critical: 6, watch: 12, normal: 230 },
  source: "fallback",
  summaries: [
    {
      patientId: "P-1042",
      patientName: "John A.",
      department: "ICU",
      riskLevel: "flagged",
      reasons: [
        "Temperature 2.3°C above 7-day baseline",
        "Mobility activity down 40% vs 7-day average",
      ],
      date: "25 Aug 2026",
      source: "mock",
    },
    {
      patientId: "P-1108",
      patientName: "Mary B.",
      department: "Cardiology",
      riskLevel: "watch",
      reasons: ["Heart rate trending upward over last 3 readings"],
      date: "25 Aug 2026",
      source: "mock",
    },
  ],
};

// ---------------------------------------------------------------------
// Reports table
// ---------------------------------------------------------------------

function normalizeCaretakerReport(raw, index) {
  const createdAt = raw.created_at || raw.createdAt || raw.date;
  const dateObj = createdAt ? new Date(createdAt) : null;

  return {
    id: raw._id || raw.id || `CARE-${String(index + 1).padStart(3, "0")}`,
    name: raw.summary
      ? `Daily Report — ${raw.summary.slice(0, 40)}${raw.summary.length > 40 ? "…" : ""}`
      : `Daily Caretaker Report ${index + 1}`,
    department: raw.department || raw.ward || "Unassigned",
    role: "Caretaker",
    date: dateObj
      ? dateObj.toLocaleDateString("en-GB", { day: "2-digit", month: "short", year: "numeric" })
      : "Unknown date",
    filterDate: dateObj ? dateObj.toISOString().slice(0, 10) : "",
    status: "Completed",
  };
}

export async function getReportsList(filters = {}) {
  if (USE_MOCK_DATA) {
    return { data: FALLBACK_REPORTS, source: "fallback" };
  }

  try {
    const response = await api.get("/caretaker/reports", { params: filters });
    console.log("[reportsService] GET /caretaker/reports -> LIVE", response.data);

    const raw = response.data;
    const list = Array.isArray(raw) ? raw : raw.reports || raw.data || [];

    if (list.length === 0) throw new Error("No caretaker reports found");

    return { data: list.map(normalizeCaretakerReport), source: "live" };
  } catch (error) {
    console.warn(
      `[reportsService] GET /caretaker/reports unavailable (${error.response?.status || error.message}) -> using fallback data`
    );
    return { data: FALLBACK_REPORTS, source: "fallback" };
  }
}

// ---------------------------------------------------------------------
// Summary stat cards
// ---------------------------------------------------------------------

async function getPendingStaffCount() {
  const response = await api.get("/admin/staff/pending");
  console.log("[reportsService] GET /admin/staff/pending -> LIVE", response.data);
  const raw = response.data;
  const list = Array.isArray(raw) ? raw : raw.pending || raw.data || [];
  return list.length;
}

export async function getReportsSummary() {
  if (USE_MOCK_DATA) {
    return { data: FALLBACK_SUMMARY, source: "fallback" };
  }

  try {
    const [reportsResult, pendingCount] = await Promise.all([
      getReportsList(),
      getPendingStaffCount(),
    ]);

    return {
      data: {
        totalReports: reportsResult.data.length,
        pendingReviews: pendingCount,
        exportedReports: FALLBACK_SUMMARY.exportedReports,
      },
      source: reportsResult.source === "live" ? "live" : "fallback",
    };
  } catch (error) {
    console.warn(
      `[reportsService] Reports summary unavailable (${error.response?.status || error.message}) -> using fallback data`
    );
    return { data: FALLBACK_SUMMARY, source: "fallback" };
  }
}

// ---------------------------------------------------------------------
// Charts
// ---------------------------------------------------------------------

async function getAdminPatientsForTrend() {
  const response = await api.get("/admin/patients");
  console.log("[reportsService] GET /admin/patients -> LIVE", response.data);
  const raw = response.data;
  return Array.isArray(raw) ? raw : raw.patients || raw.data || [];
}

function monthLabel(date) {
  return date.toLocaleDateString("en-GB", { month: "short" });
}

const MONTH_ORDER = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

function nextMonthLabel(lastLabel) {
  const idx = MONTH_ORDER.indexOf(lastLabel);
  return idx === -1 ? "Next" : MONTH_ORDER[(idx + 1) % 12];
}

function linearRegressionForecast(values) {
  const n = values.length;
  if (n < 2) return values[n - 1] ?? 0;

  const xs = values.map((_, i) => i);
  const sumX = xs.reduce((a, b) => a + b, 0);
  const sumY = values.reduce((a, b) => a + b, 0);
  const sumXY = xs.reduce((acc, x, i) => acc + x * values[i], 0);
  const sumX2 = xs.reduce((acc, x) => acc + x * x, 0);

  const denominator = n * sumX2 - sumX * sumX;
  const slope = denominator === 0 ? 0 : (n * sumXY - sumX * sumY) / denominator;
  const intercept = (sumY - slope * sumX) / n;

  return Math.max(0, Math.round(slope * n + intercept));
}

function addForecastPoint(data) {
  if (!data || data.length === 0) return data;

  const values = data.map((d) => d.admissions);
  const forecastValue = linearRegressionForecast(values);
  const nextLabel = nextMonthLabel(data[data.length - 1].month);

  const withForecastField = data.map((d, i) =>
    i === data.length - 1 ? { ...d, forecast: d.admissions } : { ...d, forecast: null }
  );

  withForecastField.push({ month: nextLabel, admissions: null, forecast: forecastValue });

  return withForecastField;
}

export async function getAdmissionsTrend() {
  if (USE_MOCK_DATA) {
    return { data: addForecastPoint(FALLBACK_ADMISSIONS), source: "fallback" };
  }

  try {
    const patients = await getAdminPatientsForTrend();

    const dated = patients
      .map((p) => p.createdAt || p.created_at || p.admittedAt || p.admissionDate)
      .filter(Boolean)
      .map((d) => new Date(d))
      .filter((d) => !isNaN(d));

    if (dated.length === 0) {
      throw new Error("Patient records have no usable creation/admission date field");
    }

    const countsByMonth = new Map();
    for (const d of dated) {
      const key = `${d.getFullYear()}-${d.getMonth()}`;
      countsByMonth.set(key, (countsByMonth.get(key) || 0) + 1);
    }

    const data = [...countsByMonth.entries()]
      .map(([key, count]) => {
        const [year, month] = key.split("-").map(Number);
        return { sortKey: new Date(year, month, 1), month: monthLabel(new Date(year, month, 1)), admissions: count };
      })
      .sort((a, b) => a.sortKey - b.sortKey)
      .map(({ month, admissions }) => ({ month, admissions }));

    return { data: addForecastPoint(data), source: "live" };
  } catch (error) {
    console.warn(
      `[reportsService] Admissions trend unavailable (${error.response?.status || error.message}) -> using fallback data`
    );
    return { data: addForecastPoint(FALLBACK_ADMISSIONS), source: "fallback" };
  }
}

// ---------------------------------------------------------------------
// Staff completion chart
// ---------------------------------------------------------------------

async function getAllCompletedTasks() {
  const allTasks = [];
  let page = 1;
  const limit = 100;

  while (true) {
    const response = await api.get("/tasks", {
      params: { status: "completed", page, limit },
    });

    const pageData = Array.isArray(response.data)
      ? response.data
      : response.data.items || response.data.tasks || response.data.data || [];

    allTasks.push(...pageData);

    if (pageData.length < limit) break;
    page += 1;

    if (page > 20) break; 
  }

  return allTasks;
}

export async function getStaffCompletion() {
  if (USE_MOCK_DATA) {
    return { data: FALLBACK_STAFF, source: "fallback" };
  }

  try {
    const tasks = await getAllCompletedTasks();

    console.log(`[reportsService] Staff completion: ${tasks.length} completed tasks -> LIVE`);

    if (tasks.length === 0) throw new Error("No completed tasks found");

    const countsByAssignee = tasks.reduce((acc, task) => {
      const assigneeId = task.assignee?._id || task.assigneeId;
      if (!assigneeId) return acc;

      if (!acc[assigneeId]) {
        acc[assigneeId] = { count: 0, name: task.assignee?.fullname || task.assignee?.name };
      }
      acc[assigneeId].count += 1;
      return acc;
    }, {});

    const data = Object.entries(countsByAssignee)
      .map(([assigneeId, info]) => ({
        name: info.name || `Staff ${assigneeId.slice(-4)}`,
        completed: info.count,
      }))
      .sort((a, b) => b.completed - a.completed);

    if (data.length === 0) throw new Error("No completed tasks with assignees found");

    return { data, source: "live" };
  } catch (error) {
    console.warn(
      `[reportsService] Staff completion unavailable (${error.response?.status || error.message}) -> using fallback data.`
    );
    return { data: FALLBACK_STAFF, source: "fallback" };
  }
}

// ---------------------------------------------------------------------
// Tasks overview
// ---------------------------------------------------------------------

async function getAllTasks(filters = {}) {
  const allTasks = [];
  let page = 1;
  const limit = 100;

  while (true) {
    const response = await api.get("/tasks", { params: { ...filters, page, limit } });

    const pageData = Array.isArray(response.data)
      ? response.data
      : response.data.items || response.data.tasks || response.data.data || [];

    allTasks.push(...pageData);

    if (pageData.length < limit) break;
    page += 1;

    if (page > 20) break;
  }

  return allTasks;
}

const PRIORITY_WEIGHT = { high: 3, medium: 2, low: 1 };

export async function getTasksOverview() {
  if (USE_MOCK_DATA) {
    return { data: FALLBACK_TASKS_OVERVIEW, source: "fallback" };
  }

  try {
    const tasks = await getAllTasks();

    if (tasks.length === 0) throw new Error("No tasks found");

    console.log(`[reportsService] Tasks overview: ${tasks.length} total tasks -> LIVE`);

    const completedCount = tasks.filter((t) => t.status === "completed").length;
    const notDone = tasks.filter((t) => t.status !== "completed");

    const outstanding = [...notDone]
      .sort((a, b) => {
        const priorityDiff = (PRIORITY_WEIGHT[b.priority] || 0) - (PRIORITY_WEIGHT[a.priority] || 0);
        if (priorityDiff !== 0) return priorityDiff;
        return new Date(a.dueDate || 0) - new Date(b.dueDate || 0);
      })
      .map((t) => ({
        id: t._id || t.id,
        title: t.title || t.description,
        priority: t.priority || "unspecified",
        status: t.status,
        dueDate: t.dueDate
          ? new Date(t.dueDate).toLocaleDateString("en-GB", { day: "2-digit", month: "short", year: "numeric" })
          : "No due date",
        assignee: t.assignee?.fullname || t.assignee?.name || "Unassigned",
      }));

    return {
      data: { completedCount, notDoneCount: notDone.length, outstanding },
      source: "live",
    };
  } catch (error) {
    console.warn(
      `[reportsService] Tasks overview unavailable (${error.response?.status || error.message}) -> using fallback data`
    );
    return { data: FALLBACK_TASKS_OVERVIEW, source: "fallback" };
  }
}

// ---------------------------------------------------------------------
// Appointments
// ---------------------------------------------------------------------

async function getAllMedicalRecords(filters = {}) {
  const allRecords = [];
  let page = 1;
  const limit = 100;

  while (true) {
    const response = await api.get("/medical-records", { params: { ...filters, page, limit } });

    const pageData = Array.isArray(response.data)
      ? response.data
      : response.data.records || response.data.data || [];

    allRecords.push(...pageData);

    if (pageData.length < limit) break;
    page += 1;

    if (page > 20) break;
  }

  return allRecords;
}

export async function getAppointmentsSummary() {
  if (USE_MOCK_DATA) {
    return { data: FALLBACK_APPOINTMENTS_SUMMARY, source: "fallback" };
  }

  try {
    const records = await getAllMedicalRecords();
    console.log(`[reportsService] GET /medical-records -> LIVE, ${records.length} records`);

    if (records.length === 0) throw new Error("No appointment records found");

    const now = new Date();
    const noShows = records.filter(
      (r) => r.status === "booked" && r.appointmentDate && new Date(r.appointmentDate) < now
    ).length;

    return {
      data: { totalAppointments: records.length, noShows },
      source: "live",
    };
  } catch (error) {
    console.warn(
      `[reportsService] Appointments summary unavailable (${error.response?.status || error.message}) -> using fallback data`
    );
    return { data: FALLBACK_APPOINTMENTS_SUMMARY, source: "fallback" };
  }
}

function buildAppointmentsForecast(data) {
  const values = data.map((d) => d.appointments);
  const forecastValue = linearRegressionForecast(values);
  const nextLabel = nextMonthLabel(data[data.length - 1].month);

  const withField = data.map((d, i) =>
    i === data.length - 1 ? { ...d, predicted: d.appointments } : { ...d, predicted: null }
  );

  withField.push({ month: nextLabel, appointments: null, predicted: forecastValue });
  return withField;
}

export async function getAppointmentsTrend() {
  if (USE_MOCK_DATA) {
    return { data: buildAppointmentsForecast(FALLBACK_APPOINTMENTS_TREND), source: "fallback" };
  }

  try {
    const records = await getAllMedicalRecords();

    const dated = records
      .map((r) => r.appointmentDate)
      .filter(Boolean)
      .map((d) => new Date(d))
      .filter((d) => !isNaN(d));

    if (dated.length === 0) throw new Error("No appointment records with a usable date");

    const countsByMonth = new Map();
    for (const d of dated) {
      const key = `${d.getFullYear()}-${d.getMonth()}`;
      countsByMonth.set(key, (countsByMonth.get(key) || 0) + 1);
    }

    const data = [...countsByMonth.entries()]
      .map(([key, count]) => {
        const [year, month] = key.split("-").map(Number);
        return { sortKey: new Date(year, month, 1), month: monthLabel(new Date(year, month, 1)), appointments: count };
      })
      .sort((a, b) => a.sortKey - b.sortKey)
      .map(({ month, appointments }) => ({ month, appointments }));

    return { data: buildAppointmentsForecast(data), source: "live" };
  } catch (error) {
    console.warn(
      `[reportsService] Appointments trend unavailable (${error.response?.status || error.message}) -> using fallback data`
    );
    return { data: buildAppointmentsForecast(FALLBACK_APPOINTMENTS_TREND), source: "fallback" };
  }
}

// ---------------------------------------------------------------------
// Financial & Billing Analytics
// ---------------------------------------------------------------------

async function getAllBillingRecords() {
  const response = await api.get("/billing");
  const raw = response.data;
  return Array.isArray(raw) ? raw : raw.billing || raw.records || raw.data || [];
}

export async function getBillingOverview() {
  if (USE_MOCK_DATA) {
    return { data: FALLBACK_BILLING_OVERVIEW, source: "fallback" };
  }

  try {
    const records = await getAllBillingRecords();
    if (records.length === 0) throw new Error("No billing records found");

    console.log(`[reportsService] GET /billing -> LIVE, ${records.length} records`);

    let totalRevenue = 0;
    let outstandingPayments = 0;
    const revenueByServiceMap = new Map();

    records.forEach((r) => {
      const total = Number(r.Total ?? r.total ?? 0);
      const pending = r.Payment_Pending ?? r.paymentPending ?? r.payment_pending;

      if (pending) {
        outstandingPayments += total;
      } else {
        totalRevenue += total;
      }

      const service = r.Description || r.description || "Unspecified";
      revenueByServiceMap.set(service, (revenueByServiceMap.get(service) || 0) + total);
    });

    const totalAll = totalRevenue + outstandingPayments;
    const collectionRate = totalAll > 0 ? (totalRevenue / totalAll) * 100 : 0;

    const revenueByService = [...revenueByServiceMap.entries()]
      .map(([service, revenue]) => ({ service, revenue: Math.round(revenue * 100) / 100 }))
      .sort((a, b) => b.revenue - a.revenue)
      .slice(0, 6);

    return {
      data: {
        totalRevenue: Math.round(totalRevenue * 100) / 100,
        outstandingPayments: Math.round(outstandingPayments * 100) / 100,
        collectionRate: Math.round(collectionRate * 10) / 10,
        revenueByService,
      },
      source: "live",
    };
  } catch (error) {
    console.warn(
      `[reportsService] Billing overview unavailable (${error.response?.status || error.message}) -> using fallback data`
    );
    return { data: FALLBACK_BILLING_OVERVIEW, source: "fallback" };
  }
}

// ---------------------------------------------------------------------
// Admin Operations & Support Ticket Performance
// ---------------------------------------------------------------------

async function getAllSupportTickets() {
  const response = await api.get("/admin/support-tickets");
  const raw = response.data;
  return Array.isArray(raw) ? raw : raw.tickets || raw.data || [];
}

function normalizeStatus(status) {
  return (status || "").toLowerCase().replace(/\s+/g, "");
}

export async function getSupportTicketsOverview() {
  if (USE_MOCK_DATA) {
    return { data: FALLBACK_SUPPORT_OVERVIEW, source: "fallback" };
  }

  try {
    const tickets = await getAllSupportTickets();
    if (tickets.length === 0) throw new Error("No support tickets found");

    console.log(`[reportsService] GET /admin/support-tickets -> LIVE, ${tickets.length} tickets`);

    const statusCounts = { open: 0, inProgress: 0, resolved: 0, closed: 0 };
    const resolvedDurationsHours = [];
    let respondedCount = 0;

    tickets.forEach((t) => {
      const status = normalizeStatus(t.status);
      if (status === "open") statusCounts.open += 1;
      else if (status === "inprogress") statusCounts.inProgress += 1;
      else if (status === "resolved") statusCounts.resolved += 1;
      else if (status === "closed") statusCounts.closed += 1;

      if ((status === "resolved" || status === "closed") && t.created_at && t.updated_at) {
        const hours = (new Date(t.updated_at) - new Date(t.created_at)) / 3.6e6;
        if (hours >= 0) resolvedDurationsHours.push(hours);
      }

      if (t.adminResponse) respondedCount += 1;
    });

    const avgResolutionHours =
      resolvedDurationsHours.length > 0
        ? Math.round((resolvedDurationsHours.reduce((a, b) => a + b, 0) / resolvedDurationsHours.length) * 10) / 10
        : null;

    const firstResponseRate = tickets.length > 0 ? Math.round((respondedCount / tickets.length) * 100) : 0;

    const subjectCounts = new Map();
    tickets.forEach((t) => {
      const subject = t.subject || "Unspecified";
      subjectCounts.set(subject, (subjectCounts.get(subject) || 0) + 1);
    });

    const topSubjects = [...subjectCounts.entries()]
      .map(([subject, count]) => ({ subject, count }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 5);

    return {
      data: { statusCounts, avgResolutionHours, firstResponseRate, topSubjects },
      source: "live",
    };
  } catch (error) {
    console.warn(
      `[reportsService] Support tickets overview unavailable (${error.response?.status || error.message}) -> using fallback data`
    );
    return { data: FALLBACK_SUPPORT_OVERVIEW, source: "fallback" };
  }
}

// ---------------------------------------------------------------------
// Doctor Caseload Balance
// ---------------------------------------------------------------------

async function getAllDoctors() {
  const response = await api.get("/doctors");
  const raw = response.data;
  return Array.isArray(raw) ? raw : raw.doctors || raw.data || [];
}

function doctorDisplayName(d) {
  return (
    d.fullname ||
    d.name ||
    d.fullName ||
    `${d.firstName || ""} ${d.lastName || ""}`.trim() ||
    "Unknown doctor"
  );
}

export async function getDoctorCaseload() {
  if (USE_MOCK_DATA) {
    return { data: FALLBACK_DOCTOR_CASELOAD, source: "fallback" };
  }

  try {
    const doctors = await getAllDoctors();
    if (doctors.length === 0) throw new Error("No doctors found");

    console.log(`[reportsService] GET /doctors -> LIVE, ${doctors.length} doctors`);

    const results = await Promise.all(
      doctors.map(async (doc) => {
        const id = doc._id || doc.id;
        try {
          const response = await api.get(`/doctors/${id}/patients`);
          const raw = response.data;
          const patients = Array.isArray(raw) ? raw : raw.patients || raw.data || [];
          return { name: doctorDisplayName(doc), patientCount: patients.length };
        } catch {
          return { name: doctorDisplayName(doc), patientCount: 0 };
        }
      })
    );

    const data = results.sort((a, b) => b.patientCount - a.patientCount);

    return { data, source: "live" };
  } catch (error) {
    console.warn(
      `[reportsService] Doctor caseload unavailable (${error.response?.status || error.message}) -> using fallback data`
    );
    return { data: FALLBACK_DOCTOR_CASELOAD, source: "fallback" };
  }
}

// ---------------------------------------------------------------------
// Roster Staffing Coverage
// ---------------------------------------------------------------------

async function getAllRosters() {
  const response = await api.get("/rosters");
  const raw = response.data;
  return Array.isArray(raw) ? raw : raw.rosters || raw.shifts || raw.data || [];
}

const WEEKDAY_ORDER = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

export async function getRosterCoverage() {
  if (USE_MOCK_DATA) {
    return { data: FALLBACK_ROSTER_COVERAGE, source: "fallback" };
  }

  try {
    const shifts = await getAllRosters();
    if (shifts.length === 0) throw new Error("No roster shifts found");

    console.log(`[reportsService] GET /rosters -> LIVE, ${shifts.length} shifts`);

    const shiftsByDayCount = { Sun: 0, Mon: 0, Tue: 0, Wed: 0, Thu: 0, Fri: 0, Sat: 0 };
    let clockedOnNow = 0;

    shifts.forEach((s) => {
      const dateVal = s.date || s.shiftDate || s.startTime || s.start_time;
      if (dateVal) {
        const d = new Date(dateVal);
        if (!isNaN(d)) shiftsByDayCount[WEEKDAY_ORDER[d.getDay()]] += 1;
      }

      const clockOn = s.clockOnTime || s.clockOnAt || s.clockedInAt || s.clock_on;
      const clockOff = s.clockOffTime || s.clockOffAt || s.clockedOutAt || s.clock_off;
      if (clockOn && !clockOff) clockedOnNow += 1;
    });

    const data = {
      shiftsByDay: WEEKDAY_ORDER.map((day) => ({ day, shifts: shiftsByDayCount[day] })),
      clockedOnNow,
      totalShifts: shifts.length,
    };

    return { data, source: "live" };
  } catch (error) {
    console.warn(
      `[reportsService] Roster coverage unavailable (${error.response?.status || error.message}) -> using fallback data`
    );
    return { data: FALLBACK_ROSTER_COVERAGE, source: "fallback" };
  }
}

// ---------------------------------------------------------------------
// Polypharmacy Risk
// ---------------------------------------------------------------------

async function getPatientPrescriptions(patientId) {
  const response = await api.get(`/patients/${patientId}/prescriptions`);
  const raw = response.data;
  return Array.isArray(raw) ? raw : raw.prescriptions || raw.data || [];
}

const POLYPHARMACY_THRESHOLD = 5;

export async function getPolypharmacyRisk() {
  if (USE_MOCK_DATA) {
    return { data: FALLBACK_POLYPHARMACY, source: "fallback" };
  }

  try {
    const patients = await getAdminPatientsForTrend();
    if (patients.length === 0) throw new Error("No patients found");

    const results = await Promise.all(
      patients.map(async (p) => {
        const id = p._id || p.id;
        const name = p.fullname || p.name || p.fullName || `Patient …${String(id).slice(-6)}`;

        try {
          const prescriptions = await getPatientPrescriptions(id);
          const activeCount = prescriptions.filter((rx) => {
            const status = (rx.status || "").toLowerCase();
            return status ? status === "active" : !rx.discontinued && !rx.discontinuedAt;
          }).length;
          return { patientId: id, patientName: name, activeCount };
        } catch {
          return null;
        }
      })
    );

    const valid = results.filter(Boolean);
    if (valid.length === 0) throw new Error("No prescription data available for any patient");

    console.log(`[reportsService] Polypharmacy check: ${valid.length} patients with data -> LIVE`);

    const flagged = valid
      .filter((p) => p.activeCount >= POLYPHARMACY_THRESHOLD)
      .sort((a, b) => b.activeCount - a.activeCount);

    return {
      data: { threshold: POLYPHARMACY_THRESHOLD, flagged, totalChecked: valid.length },
      source: "live",
    };
  } catch (error) {
    console.warn(
      `[reportsService] Polypharmacy risk unavailable (${error.response?.status || error.message}) -> using fallback data`
    );
    return { data: FALLBACK_POLYPHARMACY, source: "fallback" };
  }
}

// ---------------------------------------------------------------------
// Staffing Requirement
// ---------------------------------------------------------------------

const PATIENTS_PER_STAFF_RATIO = 4;

async function getWeekdayDemandCounts() {
  const [patients, appointments] = await Promise.all([
    getAdminPatientsForTrend(),
    getAllMedicalRecords(),
  ]);

  const admissionsByDay = { Sun: 0, Mon: 0, Tue: 0, Wed: 0, Thu: 0, Fri: 0, Sat: 0 };
  patients.forEach((p) => {
    const dateVal = p.createdAt || p.created_at || p.admittedAt || p.admissionDate;
    if (dateVal) {
      const d = new Date(dateVal);
      if (!isNaN(d)) admissionsByDay[WEEKDAY_ORDER[d.getDay()]] += 1;
    }
  });

  const appointmentsByDay = { Sun: 0, Mon: 0, Tue: 0, Wed: 0, Thu: 0, Fri: 0, Sat: 0 };
  appointments.forEach((r) => {
    if (r.appointmentDate) {
      const d = new Date(r.appointmentDate);
      if (!isNaN(d)) appointmentsByDay[WEEKDAY_ORDER[d.getDay()]] += 1;
    }
  });

  return { admissionsByDay, appointmentsByDay };
}

export async function getStaffingRequirement() {
  if (USE_MOCK_DATA) {
    return { data: FALLBACK_STAFFING_REQUIREMENT, source: "fallback" };
  }

  try {
    const [{ admissionsByDay, appointmentsByDay }, shifts] = await Promise.all([
      getWeekdayDemandCounts(),
      getAllRosters(),
    ]);

    const hasDemandData =
      Object.values(admissionsByDay).some((v) => v > 0) ||
      Object.values(appointmentsByDay).some((v) => v > 0);
    if (!hasDemandData) throw new Error("No admissions/appointments data to base staffing requirement on");

    const actualShiftsByDay = { Sun: 0, Mon: 0, Tue: 0, Wed: 0, Thu: 0, Fri: 0, Sat: 0 };
    shifts.forEach((s) => {
      const dateVal = s.date || s.shiftDate || s.startTime || s.start_time;
      if (dateVal) {
        const d = new Date(dateVal);
        if (!isNaN(d)) actualShiftsByDay[WEEKDAY_ORDER[d.getDay()]] += 1;
      }
    });

    const data = WEEKDAY_ORDER.map((day) => {
      const demand = admissionsByDay[day] + appointmentsByDay[day];
      return {
        day,
        actualShifts: actualShiftsByDay[day],
        expectedStaff: Math.ceil(demand / PATIENTS_PER_STAFF_RATIO),
      };
    });

    return { data, source: "live" };
  } catch (error) {
    console.warn(
      `[reportsService] Staffing requirement unavailable (${error.response?.status || error.message}) -> using fallback data`
    );
    return { data: FALLBACK_STAFFING_REQUIREMENT, source: "fallback" };
  }
}

// ---------------------------------------------------------------------
// Risk indicator feature
// ---------------------------------------------------------------------

let loggedMissingNameKeys = false;

function normalizePatient(raw) {
  const patientId = raw._id || raw.id || raw.patientId;

  const combinedFirstLast =
    (raw.firstName || raw.first_name) && (raw.lastName || raw.last_name)
      ? `${raw.firstName || raw.first_name} ${raw.lastName || raw.last_name}`
      : null;

  const patientName =
    raw.fullname ||
    raw.name ||
    raw.fullName ||
    raw.full_name ||
    raw.patientName ||
    combinedFirstLast ||
    raw.profile?.name ||
    raw.personalInfo?.fullName ||
    null;

  if (!patientName && !loggedMissingNameKeys) {
    loggedMissingNameKeys = true;
    console.warn(
      "[reportsService] Could not find a patient name field. Raw keys on this patient object:",
      Object.keys(raw)
    );
  }

  return {
    patientId,
    patientName: patientName || `Patient …${String(patientId).slice(-6)}`,
    department: raw.department || raw.ward || "Unassigned",
  };
}

async function getAllPatients() {
  const response = await api.get("/patients");
  console.log("[reportsService] GET /patients -> LIVE", response.data);
  const raw = response.data;
  const list = Array.isArray(raw) ? raw : raw.patients || raw.data || [];
  return list.map(normalizePatient);
}

async function getPatientHealthRecords(patientId) {
  const response = await api.get(`/patient/${patientId}/health-records`);
  return response.data;
}

async function getPatientActivities(patientId) {
  const response = await api.get("/patients/activities", {
    params: { patientId },
  });
  return response.data;
}

function mean(numbers) {
  if (numbers.length === 0) return null;
  return numbers.reduce((a, b) => a + b, 0) / numbers.length;
}

function checkVitalAgainstBaseline(records, vitalKey, label, unit, deviationThreshold) {
  const values = records
    .map((r) => r?.vitals?.[vitalKey])
    .filter((v) => typeof v === "number");

  if (values.length < 2) return null; 

  const latest = values[values.length - 1];
  const baseline = mean(values.slice(0, -1));
  const deviation = latest - baseline;

  if (Math.abs(deviation) >= deviationThreshold) {
    const direction = deviation > 0 ? "above" : "below";
    return `${label} ${Math.abs(deviation).toFixed(1)}${unit} ${direction} this patient's baseline (${baseline.toFixed(1)}${unit})`;
  }

  return null;
}

function checkActivityTrend(activities) {
  if (!activities || activities.length < 4) return null;

  const sorted = [...activities].sort(
    (a, b) => new Date(a.loggedAt || a.timestamp) - new Date(b.loggedAt || b.timestamp)
  );

  const recentWindow = sorted.slice(-3);
  const priorWindow = sorted.slice(0, -3);

  if (priorWindow.length === 0) return null;

  const daySpan = (window) => {
    const dates = window.map((a) => new Date(a.loggedAt || a.timestamp));
    const spanDays = Math.max(1, (Math.max(...dates) - Math.min(...dates)) / 86400000);
    return window.length / spanDays;
  };

  const recentRate = daySpan(recentWindow);
  const priorRate = daySpan(priorWindow);

  if (priorRate === 0) return null;

  const dropPct = ((priorRate - recentRate) / priorRate) * 100;

  if (dropPct >= 40) {
    return `Activity logging frequency down ${dropPct.toFixed(0)}% vs prior average`;
  }

  return null;
}

function computeRisk(patient, healthRecords, activities) {
  const sortedRecords = [...(healthRecords || [])].sort(
    (a, b) => new Date(a.created_at || a.createdAt) - new Date(b.created_at || b.createdAt)
  );

  const reasons = [
    checkVitalAgainstBaseline(sortedRecords, "temperature", "Temperature", "°C", 1.0),
    checkVitalAgainstBaseline(sortedRecords, "heartRate", "Heart rate", " bpm", 15),
    checkVitalAgainstBaseline(sortedRecords, "respiratoryRate", "Respiratory rate", " br/min", 5),
    checkActivityTrend(activities),
  ].filter(Boolean);

  let riskLevel = "normal";
  if (reasons.length >= 2) riskLevel = "flagged";
  else if (reasons.length === 1) riskLevel = "watch";

  return {
    ...patient,
    riskLevel,
    reasons,
    date: new Date().toLocaleDateString("en-GB", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    }),
    source: "live",
  };
}

export async function getReportsRiskData() {
  if (USE_MOCK_DATA) {
    return FALLBACK_RISK;
  }

  let patients;

  try {
    patients = await getAllPatients();
    if (patients.length === 0) throw new Error("No patients returned");
  } catch (error) {
    console.warn(
      `[reportsService] GET /patients failed (${error.response?.status || error.message}) -> using fallback risk data entirely`
    );
    return FALLBACK_RISK;
  }

  const skipped = [];

  const summaries = await Promise.all(
    patients.map(async (patient) => {
      try {
        const [healthRecords, activities] = await Promise.all([
          getPatientHealthRecords(patient.patientId),
          getPatientActivities(patient.patientId).catch(() => []),
        ]);

        return computeRisk(patient, healthRecords, activities);
      } catch (error) {
        skipped.push({
          patientId: patient.patientId,
          status: error.response?.status ?? "no response",
          reason:
            error.response?.data?.message ||
            JSON.stringify(error.response?.data) ||
            error.message,
        });
        return null; 
      }
    })
  );

  const validSummaries = summaries.filter(Boolean);

  if (validSummaries.length === 0) {
    return FALLBACK_RISK;
  }

  const overview = validSummaries.reduce(
    (acc, s) => {
      if (s.riskLevel === "flagged") acc.critical += 1;
      else if (s.riskLevel === "watch") acc.watch += 1;
      else acc.normal += 1;
      return acc;
    },
    { critical: 0, watch: 0, normal: 0 }
  );

  return {
    overview,
    summaries: validSummaries.filter((s) => s.riskLevel !== "normal"),
    source: "live",
  };
}

// ---------------------------------------------------------------------
// Risk -> Notification
// ---------------------------------------------------------------------

const NOTIFIED_STORAGE_KEY = "guardian_notified_patients";

function getNotifiedSet() {
  try {
    const raw = localStorage.getItem(NOTIFIED_STORAGE_KEY);
    return new Set(raw ? JSON.parse(raw) : []);
  } catch {
    return new Set();
  }
}

function saveNotifiedSet(set) {
  try {
    localStorage.setItem(NOTIFIED_STORAGE_KEY, JSON.stringify([...set]));
  } catch {
    // localStorage unavailable
  }
}

export async function notifyFlaggedPatients(summaries) {
  const liveFlaggedOrWatch = summaries.filter(
    (s) => s.source === "live" && (s.riskLevel === "flagged" || s.riskLevel === "watch")
  );

  if (liveFlaggedOrWatch.length === 0) return { sent: 0, skipped: 0, failed: 0 };

  const notified = getNotifiedSet();
  let sent = 0;
  let skipped = 0;
  let failed = 0;

  for (const patient of liveFlaggedOrWatch) {
    const dedupeKey = `${patient.patientId}:${patient.riskLevel}`;

    if (notified.has(dedupeKey)) {
      skipped += 1;
      continue;
    }

    try {
      await api.post("/notifications", {
        message: `${patient.patientName} marked ${patient.riskLevel}: ${patient.reasons.join("; ")}`,
        type: "risk-alert",
        patientId: patient.patientId,
      });
      notified.add(dedupeKey);
      sent += 1;
    } catch (error) {
      failed += 1;
    }
  }

  saveNotifiedSet(notified);
  return { sent, skipped, failed };
}

// ---------------------------------------------------------------------
// Risk trajectory
// ---------------------------------------------------------------------

export async function getPatientRiskTrajectory(patientId) {
  const healthRecords = await getPatientHealthRecords(patientId);

  const sorted = [...(healthRecords || [])].sort(
    (a, b) => new Date(a.created_at || a.createdAt) - new Date(b.created_at || b.createdAt)
  );

  return sorted.map((record, idx) => {
    const recordsUpToNow = sorted.slice(0, idx + 1);

    const reasons =
      idx < 1
        ? []
        : [
            checkVitalAgainstBaseline(recordsUpToNow, "temperature", "Temperature", "°C", 1.0),
            checkVitalAgainstBaseline(recordsUpToNow, "heartRate", "Heart rate", " bpm", 15),
            checkVitalAgainstBaseline(recordsUpToNow, "respiratoryRate", "Respiratory rate", " br/min", 5),
          ].filter(Boolean);

    return {
      date: record.created_at || record.createdAt,
      reasonCount: reasons.length,
      reasons,
    };
  });
}