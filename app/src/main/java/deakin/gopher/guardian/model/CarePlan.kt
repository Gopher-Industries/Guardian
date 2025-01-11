package deakin.gopher.guardian.model

data class CarePlan(
    // Existing fields
    var behavioralManagement: String? = null,
    var carePlanType: String? = null,
    var dietTimings: String? = null,
    var drinkLikings: String? = null,
    var nutritionHydration: String? = null,
    var painCategories: String? = null,
    var painScore: Int = 0,
    var patientId: String? = null,
    var sleepPattern: String? = null,
    var supportRequirement: String? = null,

    // New fields for Care Plan List
    var title: String? = null, // Title of the care plan
    var assignedNurse: String? = null, // Nurse or caretaker assigned to the plan
    var status: String? = null, // Status (e.g., "In Progress", "Completed")
    var completionRate: Int = 0 // Percentage of completion
)


