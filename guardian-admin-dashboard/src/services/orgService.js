export async function getMyOrganizations() {
  return {
    orgs: [
      {
        _id: "1",
        name: "Guardian Health Org",
        description: "Primary org for testing",
        active: true,
        createdBy: "admin001",
        staff: ["u1", "u2", "u3"],
        created_at: "2026-04-11T08:43:07.933Z",
        updated_at: "2026-04-11T08:43:07.933Z"
      },
      {
        _id: "2",
        name: "Care Support Org",
        description: "Secondary organisation",
        active: false,
        createdBy: "admin002",
        staff: ["u4"],
        created_at: "2026-03-02T10:20:00.000Z",
        updated_at: "2026-03-15T11:10:00.000Z"
      }
    ]
  };
}

import api from "./api";

export async function getOrganizations() {
  const response = await api.get('/orgs/mine');
  console.log('GET my organizations response:', response.data);
  return response.data;
}

export async function createOrganization(orgData) {
  const response = await api.post('/orgs', orgData);
  console.log('POST create organization response:', response.data);
  return response.data;
}