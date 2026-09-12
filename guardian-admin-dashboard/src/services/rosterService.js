import api from "./api";

export async function getRosters({
  date = "",
  assignedStaffId = "",
  location = "",
  page = 1,
  limit = 100,
} = {}) {
  const params = {
    page,
    limit,
  };

  if (date) {
    params.date = date;
  }

  if (assignedStaffId) {
    params.assignedStaffId = assignedStaffId;
  }

  if (location) {
    params.location = location;
  }

  const response = await api.get("/rosters", {
    params,
  });

  return response.data;
}

export async function getRosterByShiftId(shiftId) {
  const response = await api.get(
    `/rosters/${shiftId}`
  );

  return response.data;
}

export async function createRoster({
  shiftId,
  location,
  room,
  description,
  generalNotes = "",
  date,
  startTime,
  endTime,
  assignedStaffId,
}) {
  const response = await api.post("/rosters", {
    shiftId,
    location,
    room,
    description,
    generalNotes,
    date,
    startTime,
    endTime,
    assignedStaffId,
  });

  return response.data;
}

export async function updateRoster(
  shiftId,
  {
    location,
    room,
    description,
    generalNotes,
    date,
    startTime,
    endTime,
    assignedStaffId,
  }
) {
  const payload = {};

  if (location !== undefined) {
    payload.location = location;
  }

  if (room !== undefined) {
    payload.room = room;
  }

  if (description !== undefined) {
    payload.description = description;
  }

  if (generalNotes !== undefined) {
    payload.generalNotes = generalNotes;
  }

  if (date !== undefined) {
    payload.date = date;
  }

  if (startTime !== undefined) {
    payload.startTime = startTime;
  }

  if (endTime !== undefined) {
    payload.endTime = endTime;
  }

  if (assignedStaffId !== undefined) {
    payload.assignedStaffId = assignedStaffId;
  }

  const response = await api.put(
    `/rosters/${shiftId}`,
    payload
  );

  return response.data;
}

export async function deleteRoster(shiftId) {
  const response = await api.delete(
    `/rosters/${shiftId}`
  );

  return response.data;
}

export async function clockOn(shiftId) {
  const response = await api.patch(
    `/rosters/${shiftId}/clock-on`
  );

  return response.data;
}

export async function clockOff(shiftId) {
  const response = await api.patch(
    `/rosters/${shiftId}/clock-off`
  );

  return response.data;
}

export async function getStaffShifts(staffId) {
  const response = await api.get(
    `/rosters/staff/${staffId}`
  );

  return response.data;
}