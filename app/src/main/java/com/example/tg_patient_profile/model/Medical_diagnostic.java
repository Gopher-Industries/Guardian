package com.example.tg_patient_profile.model;

public class Medical_diagnostic {
  String patient_id;
  String name;
  String patientTemp;
  String bloodPressure;
  String glucoseLevel;
  String oxygenSaturation;
  String pulseRate;
  String respirationRate;
  String bloodFatLevel;
  Boolean current;

  public Medical_diagnostic(
      final String patient_id,
      final String name,
      final String bloodPressure,
      final String patientTemp,
      final String glucoseLevel,
      final String oxygenSaturation,
      final String pulseRate,
      final String respirationRate,
      final String bloodFatLevel,
      final Boolean isCurrent) {
    this.patient_id = patient_id;
    this.name = name;
    this.patientTemp = patientTemp;
    this.bloodPressure = bloodPressure;
    this.glucoseLevel = glucoseLevel;
    this.oxygenSaturation = oxygenSaturation;
    this.pulseRate = pulseRate;
    this.respirationRate = respirationRate;
    this.bloodFatLevel = bloodFatLevel;
    this.current = isCurrent;
  }

  public Medical_diagnostic(final String patient_id, final Boolean isCurrent) {
    this.patient_id = patient_id;
    this.current = isCurrent;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Medical_diagnostic{"
        + "patient_id='"
        + patient_id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", patient_temp='"
        + patientTemp
        + '\''
        + ", blood_pressure='"
        + bloodPressure
        + '\''
        + ", glicose_level='"
        + glucoseLevel
        + '\''
        + ", oxygen_saturation='"
        + oxygenSaturation
        + '\''
        + ", pulse_rate='"
        + pulseRate
        + '\''
        + ", respiration_rate='"
        + respirationRate
        + '\''
        + ", bloodfast_level='"
        + bloodFatLevel
        + '\''
        + ", isCurrent="
        + current
        + '}';
  }
}
