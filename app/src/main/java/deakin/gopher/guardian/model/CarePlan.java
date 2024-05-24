package deakin.gopher.guardian.model;

public class CarePlan {
    private String behavioralManagement;
    private String carePlanType;
    private String dietTimings;
    private String drinkLikings;
    private String nutritionHydration;
    private String painCategories;
    private int painScore;
    private String patientId;
    private String sleepPattern;
    private String supportRequirement;

    // Constructors, getters, and setters
    public CarePlan() {
    }

    public String getBehavioralManagement() {
        return behavioralManagement;
    }

    public void setBehavioralManagement(String behavioralManagement) {
        this.behavioralManagement = behavioralManagement;
    }

    public String getCarePlanType() {
        return carePlanType;
    }

    public void setCarePlanType(String carePlanType) {
        this.carePlanType = carePlanType;
    }

    public String getDietTimings() {
        return dietTimings;
    }

    public void setDietTimings(String dietTimings) {
        this.dietTimings = dietTimings;
    }

    public String getDrinkLikings() {
        return drinkLikings;
    }

    public void setDrinkLikings(String drinkLikings) {
        this.drinkLikings = drinkLikings;
    }

    public String getNutritionHydration() {
        return nutritionHydration;
    }

    public void setNutritionHydration(String nutritionHydration) {
        this.nutritionHydration = nutritionHydration;
    }

    public String getPainCategories() {
        return painCategories;
    }

    public void setPainCategories(String painCategories) {
        this.painCategories = painCategories;
    }

    public int getPainScore() {
        return painScore;
    }

    public void setPainScore(int painScore) {
        this.painScore = painScore;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getSleepPattern() {
        return sleepPattern;
    }

    public void setSleepPattern(String sleepPattern) {
        this.sleepPattern = sleepPattern;
    }

    public String getSupportRequirement() {
        return supportRequirement;
    }

    public void setSupportRequirement(String supportRequirement) {
        this.supportRequirement = supportRequirement;
    }
}
