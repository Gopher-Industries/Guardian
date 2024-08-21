package deakin.gopher.guardian.model

class CarePlan  // Constructors, getters, and setters
{
    @JvmField
    var behavioralManagement: String? = null
    @JvmField
    var carePlanType: String? = null
    @JvmField
    var dietTimings: String? = null
    @JvmField
    var drinkLikings: String? = null
    @JvmField
    var nutritionHydration: String? = null
    @JvmField
    var painCategories: String? = null
    @JvmField
    var painScore: Int = 0
    var patientId: String? = null
    @JvmField
    var sleepPattern: String? = null
    @JvmField
    var supportRequirement: String? = null
}
