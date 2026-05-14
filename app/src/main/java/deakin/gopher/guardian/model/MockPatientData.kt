package deakin.gopher.guardian.model

import deakin.gopher.guardian.model.register.User

/** Local samples for DEBUG UI review (doctor list + overview + admin overview card). */
object MockPatientData {
    private val stubCaretaker =
        User(
            id = "mock-caretaker-1",
            email = "j.morgan@example.com",
            name = "Jamie Morgan",
            roleName = "caretaker",
            photoUrl = "",
            organization = null,
        )

    private val stubNurses =
        listOf(
            User(
                id = "mock-nurse-1",
                email = "a.patel@example.com",
                name = "Priya Patel",
                roleName = "nurse",
                photoUrl = "",
                organization = null,
            ),
            User(
                id = "mock-nurse-2",
                email = "l.chen@example.com",
                name = "Liam Chen",
                roleName = "nurse",
                photoUrl = "",
                organization = null,
            ),
        )

    val patients: List<Patient>
        get() =
            listOf(
                Patient(
                    id = "mock-patient-1",
                    fullname = "Eleanor Whitmore",
                    photoUrl = "",
                    dateOfBirth = "1948-06-15T00:00:00Z",
                    _age = 77,
                    gender = "female",
                    healthConditions =
                        listOf(
                            "type 2 diabetes",
                            "mild hypertension",
                            "early stage osteoarthritis",
                        ),
                    caretaker = stubCaretaker,
                    assignedNurses = stubNurses,
                ),
                Patient(
                    id = "mock-patient-2",
                    fullname = "Robert Singh",
                    photoUrl = "",
                    dateOfBirth = "1939-03-08T10:30:00Z",
                    _age = 87,
                    gender = "male",
                    healthConditions =
                        listOf(
                            "congestive heart failure",
                            "chronic kidney disease stage 3",
                        ),
                    caretaker = stubCaretaker,
                    assignedNurses = listOf(stubNurses.first()),
                ),
                Patient(
                    id = "mock-patient-3",
                    fullname = "Maria Santos",
                    photoUrl = "",
                    dateOfBirth = "1962-11-02T00:00:00Z",
                    _age = 62,
                    gender = "female",
                    healthConditions = emptyList(),
                    caretaker = stubCaretaker,
                    assignedNurses = emptyList(),
                ),
            )

    fun patientById(id: String): Patient? = patients.find { it.id == id }

    fun patientForOverviewOrDefault(id: String): Patient =
        patientById(id) ?: patients.first()

    /** Fills the “Clinical / admin overview” section during mock previews. */
    fun adminOverviewForPatient(id: String): PatientAdminOverviewResponse {
        val name = patientById(id)?.fullname ?: patients.first().fullname
        return PatientAdminOverviewResponse(
            summary =
                "$name — routine follow-up cadence weekly. Stable on current medication plan.",
            overview =
                "Focus areas: hydration, mobilisation indoors, adherence to diabetic diet sheet.",
            carePlanSummary = "Morning vitals weekly; pharmacist review fortnightly.",
            notes = "Prefers reminders via SMS. Son visits Tuesdays.",
            riskLevel = when (patientById(id)?.id) {
                "mock-patient-2" -> "elevated"
                "mock-patient-3" -> "low"
                else -> "moderate"
            },
            alerts =
                listOf(
                    "Renew script for ACE inhibitor due next month.",
                    "Updated emergency contact verified Jan 2026.",
                ),
        )
    }
}
