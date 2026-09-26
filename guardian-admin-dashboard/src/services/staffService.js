import api from "./api";

export async function getStaff({
  page = 1,
  limit = 10,
  search = "",
  orgId = "",
  role = "",
} = {}) {
  const params = {
    page,
    limit,
  };

  if (search) {
    params.q = search;
  }

  if (orgId) {
    params.orgId = orgId;
  }

  if (role) {
    params.role = role;
  }

  const response = await api.get("/admin/staff", {
    params,
  });

  return response.data;
}

export async function createStaff(
  userId,
  orgId = ""
) {
  const params = {};

  if (orgId) {
    params.orgId = orgId;
  }

  const response = await api.post(
    "/admin/staff",
    {
      userId,
    },
    {
      params,
    }
  );

  return response.data;
}

export async function deactivateStaff(
  id,
  orgId = ""
) {
  const params = {};

  if (orgId) {
    params.orgId = orgId;
  }

  const response = await api.put(
    `/admin/staff/${id}/deactivate`,
    {},
    {
      params,
    }
  );

  return response.data;
}

export async function getAllDoctors() {
  const response = await api.get("/doctors");

  return response.data;
}

export async function getAllNurses() {
  const response = await api.get("/nurse/all");

  return response.data;
}