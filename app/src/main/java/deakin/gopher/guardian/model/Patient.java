package deakin.gopher.guardian.model;

public class Patient {
  String patientId;
  String address;
  String dob;
  String patientName;
  String phone;
  String photo;
  String underCare;
  String firstName;
  String middleName;
  String lastName;
  String medicareNo;
  String westernAffairsNo;
  String nokId1;
  String nokId2;
  String gpId1;
  String gpId2;

  private long lastExaminedTimestamp;

  private boolean needsAssistance;
  private PatientStatus status;

  public Patient(final String patientId, final String firstName, final String lastName) {
    this.patientId = patientId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.status = PatientStatus.REQUIRES_ASSISTANCE;
    this.needsAssistance = true;
  }

  // constructor for adding a patient
  public Patient(
      final String dob,
      final String firstName,
      final String middleName,
      final String lastName,
      final String medicareNo,
      final String westernAffairsNo,
      final String nokId1,
      final String nokId2,
      final String gpId1,
      final String gpId2) {
    this.dob = dob;
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.medicareNo = medicareNo;
    this.westernAffairsNo = westernAffairsNo;
    this.nokId1 = nokId1;
    this.nokId2 = nokId2;
    this.gpId1 = gpId1;
    this.gpId2 = gpId2;
    this.status = PatientStatus.REQUIRES_ASSISTANCE;
    this.needsAssistance = true;
  }

  public String getPatientId() {
    return patientId;
  }

  public void setMedicareNo(final String medicareNo) {
    this.medicareNo = medicareNo;
  }

  public void setWesternAffairsNo(final String westernAffairsNo) {
    this.westernAffairsNo = westernAffairsNo;
  }

  public void setNokId1(final String nokId1) {
    this.nokId1 = nokId1;
  }

  public void setNokId2(final String nokId2) {
    this.nokId2 = nokId2;
  }

  public void setGpId1(final String gpId1) {
    this.gpId1 = gpId1;
  }

  public void setGpId2(final String gpId2) {
    this.gpId2 = gpId2;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(final String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(final String middleName) {
    this.middleName = middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(final String lastName) {
    this.lastName = lastName;
  }

  public void setDob(final String dob) {
    this.dob = dob;
  }

  public String getDob() {
    return this.dob;
  }

  public String getMedicareNo() {
    return this.medicareNo;
  }

  public String getWesternAffairsNo() {
    return this.westernAffairsNo;
  }

  public PatientStatus getStatus() {
    return status;
  }

  public void setStatus(PatientStatus status) {
    this.status = status;
  }

  public boolean getNeedsAssistance() {
    return needsAssistance;
  }

  public void setNeedsAssistance(boolean needsAssistance) {
    this.needsAssistance = needsAssistance;
  }

  public long getLastExaminedTimestamp() {
    return lastExaminedTimestamp;
  }

  public void setLastExaminedTimestamp(long lastExaminedTimestamp) {
    this.lastExaminedTimestamp = lastExaminedTimestamp;
  }

  public void examinePatient() {
    if (PatientStatus.REQUIRES_ASSISTANCE == this.status) {
      this.status = PatientStatus.NO_ASSISTANCE_REQUIRED;
      this.needsAssistance = false;
    }
  }

  @Override
  public String toString() {
    return "Patient{"
        + "address='"
        + address
        + '\''
        + ", dob='"
        + dob
        + '\''
        + ", patient_name='"
        + patientName
        + '\''
        + ", phone='"
        + phone
        + '\''
        + ", photo='"
        + photo
        + '\''
        + ", underCare='"
        + underCare
        + '\''
        + ", first_name='"
        + firstName
        + '\''
        + ", middle_name='"
        + middleName
        + '\''
        + ", last_name='"
        + lastName
        + '\''
        + ", medicareNo='"
        + medicareNo
        + '\''
        + ", westernAffairNo='"
        + westernAffairsNo
        + '\''
        + ", nok_id1='"
        + nokId1
        + '\''
        + ", nok_id2='"
        + nokId2
        + '\''
        + ", gp_id1='"
        + gpId1
        + '\''
        + ", gp_id2='"
        + gpId2
        + '\''
        + ", status='"
        + status
        + '\''
        + ", needsAssistance='"
        + needsAssistance
        + '\''
        + '}';
  }
}
