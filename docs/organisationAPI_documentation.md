# API Documentation: Organisation API
## 1. Overview
The Organisation API manages care facilities and medical institutions (such as Sunrise Care Facility and Deakin Health) within the Guardian platform. It acts as the bridge allowing administrative interfaces and public-facing workflows to securely interact with the MongoDB Atlas cloud database.
It primarily manages:
•	Administrative Scope: Fetching and creating individual enterprise environments.
•	Public Discovery: Allowing external users or freelance personnel to discover active facilities.
•	Onboarding Workflows: Processing validation requests to join existing organizational workspaces.
________________________________________
## 2. API Endpoints Reference
**Endpoint 1:** Fetch Authenticated Admin Organizations
- HTTP Method: GET
- URL: [https://guardian-backend-79dw.vercel.app/api/v1/orgs/mine](https://guardian-backend-79dw.vercel.app/api/v1/orgs/mine)
- Headers Required:
- Authorization: Bearer <ACCESS_TOKEN>

**Inputs (Request):**

- Query Parameters / Body: None. The endpoint extracts user context implicitly from the cryptographically verified bearer token.
  
**Outputs (Response):**

- Status Code: 200 OK
- Response Body (JSON Array):
  
```json
[
  {
    "_id": "6a3f80dbc841114104e06bdb",
    "name": "Sunrise Care Facility",
    "description": "",
    "active": true,
    "createdBy": "6a228ad244f192b7412e4fdf",
    "staff": ["6a3f82ebc841114104e06be1", "6a40a50bc841114104e06cff"],
    "created_at": "2026-06-27T07:50:51.254Z",
    "updated_at": "2026-07-14T09:25:32.515Z"
  }
]
```
________________________________________
**Endpoint 2:** Register a New Organization
- HTTP Method: POST
- URL: [https://guardian-backend-79dw.vercel.app/api/v1/orgs](https://guardian-backend-79dw.vercel.app/api/v1/orgs)
- Headers Required:
- Authorization: Bearer <ACCESS_TOKEN>
- Content-Type: application/json

**Inputs (Request Body - JSON):**

| FIELD | TYPE | REQUIRED | DESCRIPTION 
| --- | --- | --- | --- |
| NAME | String |	Yes |	The explicit corporate name of the medical or care facility.
| DESCRIPTION	| String | No |	Explanatory context regarding the entity's medical focus or location.
| ACTIVE | Boolean | No |	Baseline system configuration status (defaults to true).

``` JSON
{
    "message": "Organization created",
    "org": {
        "name": "Test Health Cline",
        "description": "Validation environment built via automated integration testing.",
        "active": true,
        "createdBy": "6a228ad244f192b7412e4fdf",
        "staff": [
            "6a228ad244f192b7412e4fdf"
        ],
        "_id": "6a5b24020422e4c78f16940d",
        "created_at": "2026-07-18T06:58:10.925Z",
        "updated_at": "2026-07-18T06:58:10.926Z",
        "__v": 0,
        "staffCount": 1,
        "id": "6a5b24020422e4c78f16940d"
    }
}
```
________________________________________
**Endpoint 3:** List Public Active Organizations
- HTTP Method: GET
- URL: [https://guardian-backend-79dw.vercel.app/api/v1/orgs/public](https://guardian-backend-79dw.vercel.app/api/v1/orgs/public) 
- Headers Required:
- Authorization: Bearer <ACCESS_TOKEN>

**Inputs (Request):**
- Query Parameters / Body: None. This endpoint queries the database collection filtering exclusively by the evaluation active: true.

**Outputs (Response):**
- Status Code: 200 OK
- Response Body (JSON Array):
``` JSON
[
  {
    "_id": "6a3f80dbc841114104e06bdb",
    "name": "Sunrise Care Facility",
    "description": ""
  },
  {
    "_id": "6a583dde77d141a1f0e8b472",
    "name": "Deakin Health",
    "description": "Student run research and care organisation"
  }
]
```

________________________________________
**Endpoint 4:** Submit an Organization Join Request
- HTTP Method: POST
- URL: [https://guardian-backend-79dw.vercel.app/api/v1/orgs/join-request](https://guardian-backend-79dw.vercel.app/api/v1/orgs/join-request)
- Headers Required:
- Authorization: Bearer <ACCESS_TOKEN>
- Content-Type: application/json

**Inputs (Request Body - JSON):**

| FIELD | TYPE | REQUIRED | DESCRIPTION |
| --- | --- | --- | --- |
| ORGANIZATIONID | String | Yes | The exact target MongoDB _id hex string of the target organization.

``` JSON
{
  "organizationId": "6a583dde77d141a1f0e8b472"
}
Outputs (Response):
•	Status Code: 200 OK / 202 Accepted
•	Response Body (JSON Object):
JSON
{
  "success": true,
  "message": "Join request submitted successfully. Awaiting administrator verification.",
  "requestId": "6a67300ab5ebfcbf02619cf2"
}
```
________________________________________
## 3. Database Verification & Integration Testing
- Testing Method: Requests were routed sequentially through Postman utilizing active Bearer Session Tokens captured during application workflows.
- Database Synchronization Verification:
- Executing POST /api/v1/orgs appends a unique document entity into the SamsTest database instance within the guardian.organizations collection.
- System hooks autonomously assign created_at / updated_at timestamps, track the requesting payload user profile as the createdBy relational element, and establish an empty initialization array for staff.
- Changes propagate instantly across downstream dependencies, satisfying structural synchronization tests.
________________________________________

## 4. Frontend Application Usage
#### Current Implementation
The endpoints are directly mapped to the Administrator Workspace dashboard (/dashboard/org-assignment).
- Table Rendering: The dashboard maps the GET /api/v1/orgs/mine output to render the active management tables dynamically extracting the Name and Description values.
- Creation Interactivity: The [Add Organization] button triggers an asynchronous structural layout framework capturing administrative parameters and dispatching the values payload directly to the POST /api/v1/orgs pipeline.

#### Proposed Extensions
- Staff Onboarding Registration: Freelance users (nurses or caretakers) can utilize the GET /api/v1/orgs/public endpoint on their onboarding interface to search for active partner centres. Selecting a facility can dispatch a structured hook call payload through the POST /api/v1/orgs/join-request route, safely populating the admin workflow queues (/api/v1/admin/staff/pending) for quick operational approval.

