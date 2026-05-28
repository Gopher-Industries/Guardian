import { useMemo, useState } from "react";
import {
  Search,
  Filter,
  Plus,
  CalendarDays,
  Clock3,
  UserRound,
  Phone,
  Mail,
  ClipboardList,
  Eye,
  Pencil,
} from "lucide-react";
import "./NurseRoasterPage.css";

const mockNurses = [
  {
    id: "NUR-001",
    name: "Emily Stone",
    email: "emily.stone@guardian.com",
    phone: "+61 412 458 221",
    shift: "Morning",
    ward: "Ward A",
    status: "On Duty",
    assignedPatients: 5,
    nextShift: "16 May 2026, 7:00 AM",
  },
  {
    id: "NUR-002",
    name: "Ava Lee",
    email: "ava.lee@guardian.com",
    phone: "+61 431 225 198",
    shift: "Evening",
    ward: "Ward B",
    status: "Off Duty",
    assignedPatients: 3,
    nextShift: "16 May 2026, 3:00 PM",
  },
  {
    id: "NUR-003",
    name: "Nurse Emily",
    email: "nurse.emily@guardian.com",
    phone: "+61 490 785 640",
    shift: "Night",
    ward: "Ward C",
    status: "On Leave",
    assignedPatients: 0,
    nextShift: "18 May 2026, 11:00 PM",
  },
  {
    id: "NUR-004",
    name: "Sophia Turner",
    email: "sophia.turner@guardian.com",
    phone: "+61 402 118 534",
    shift: "Morning",
    ward: "Ward A",
    status: "On Duty",
    assignedPatients: 4,
    nextShift: "16 May 2026, 7:00 AM",
  },
];

export default function NurseRoasterPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedShift, setSelectedShift] = useState("All");
  const [selectedStatus, setSelectedStatus] = useState("All");

  const filteredNurses = useMemo(() => {
    return mockNurses.filter((nurse) => {
      const matchesSearch =
        `${nurse.name} ${nurse.email} ${nurse.phone} ${nurse.ward}`.toLowerCase()
          .includes(searchTerm.toLowerCase());

      const matchesShift =
        selectedShift === "All" || nurse.shift === selectedShift;

      const matchesStatus =
        selectedStatus === "All" || nurse.status === selectedStatus;

      return matchesSearch && matchesShift && matchesStatus;
    });
  }, [searchTerm, selectedShift, selectedStatus]);

  const onDutyCount = mockNurses.filter((nurse) => nurse.status === "On Duty").length;
  const offDutyCount = mockNurses.filter((nurse) => nurse.status === "Off Duty").length;
  const onLeaveCount = mockNurses.filter((nurse) => nurse.status === "On Leave").length;

  const getStatusClass = (status) => {
    if (status === "On Duty") return "nurse-status on-duty";
    if (status === "Off Duty") return "nurse-status off-duty";
    return "nurse-status on-leave";
  };

  return (
    <section className="nurse-roaster-page">
      <div className="nurse-roaster-header">
        <div>
          <p className="nurse-roaster-eyebrow">Guardian Monitor Admin</p>
          <h1>Nurse Roster</h1>
          <p className="nurse-roaster-subtitle">
            Review nurse availability, assigned workload, and shift details from the admin workspace.
          </p>
        </div>

        <button className="nurse-primary-btn" type="button">
          <Plus size={18} />
          Add Nurse
        </button>
      </div>

      <div className="nurse-summary-grid">
        <div className="nurse-summary-card">
          <UserRound size={18} />
          <div>
            <strong>{mockNurses.length}</strong>
            <span>Total Nurses</span>
          </div>
        </div>

        <div className="nurse-summary-card">
          <Clock3 size={18} />
          <div>
            <strong>{onDutyCount}</strong>
            <span>On Duty</span>
          </div>
        </div>

        <div className="nurse-summary-card">
          <CalendarDays size={18} />
          <div>
            <strong>{offDutyCount}</strong>
            <span>Off Duty</span>
          </div>
        </div>

        <div className="nurse-summary-card">
          <ClipboardList size={18} />
          <div>
            <strong>{onLeaveCount}</strong>
            <span>On Leave</span>
          </div>
        </div>
      </div>

      <div className="nurse-toolbar">
        <div className="nurse-search-box">
          <Search size={16} />
          <input
            type="text"
            placeholder="Search by nurse, ward, email, or phone..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        <div className="nurse-filter-group">
          <div className="nurse-filter-select">
            <Filter size={16} />
            <select
              value={selectedShift}
              onChange={(e) => setSelectedShift(e.target.value)}
            >
              <option value="All">All Shifts</option>
              <option value="Morning">Morning</option>
              <option value="Evening">Evening</option>
              <option value="Night">Night</option>
            </select>
          </div>

          <div className="nurse-filter-select">
            <Filter size={16} />
            <select
              value={selectedStatus}
              onChange={(e) => setSelectedStatus(e.target.value)}
            >
              <option value="All">All Status</option>
              <option value="On Duty">On Duty</option>
              <option value="Off Duty">Off Duty</option>
              <option value="On Leave">On Leave</option>
            </select>
          </div>
        </div>
      </div>

      <div className="nurse-roaster-card">
        <div className="nurse-roaster-card-header">
          <h3>Roster List</h3>
          <p>Current nurse records prepared in the admin dashboard interface.</p>
        </div>

        <div className="nurse-roaster-table-wrap">
          <table className="nurse-roaster-table">
            <thead>
              <tr>
                <th>Nurse</th>
                <th>Contact</th>
                <th>Shift</th>
                <th>Ward</th>
                <th>Patients</th>
                <th>Status</th>
                <th>Next Shift</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {filteredNurses.length > 0 ? (
                filteredNurses.map((nurse) => (
                  <tr key={nurse.id}>
                    <td>
                      <div className="nurse-name-cell">
                        <div className="nurse-avatar">
                          {nurse.name.charAt(0)}
                        </div>
                        <div>
                          <strong>{nurse.name}</strong>
                          <span>{nurse.id}</span>
                        </div>
                      </div>
                    </td>

                    <td>
                      <div className="nurse-contact-cell">
                        <span><Mail size={14} /> {nurse.email}</span>
                        <span><Phone size={14} /> {nurse.phone}</span>
                      </div>
                    </td>

                    <td>{nurse.shift}</td>
                    <td>{nurse.ward}</td>
                    <td>{nurse.assignedPatients}</td>
                    <td>
                      <span className={getStatusClass(nurse.status)}>
                        {nurse.status}
                      </span>
                    </td>
                    <td>{nurse.nextShift}</td>
                    <td>
                      <div className="nurse-actions">
                        <button className="nurse-icon-btn" type="button">
                          <Eye size={16} />
                        </button>
                        <button className="nurse-icon-btn" type="button">
                          <Pencil size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="8" className="nurse-empty-state">
                    No nurse records found for the selected filters.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
}