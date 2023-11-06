package com.example.tg_patient_profile.model;

import java.util.HashMap;
import java.util.Map;

public class Medical_diagnostic {
    String patient_id, name, patient_temp, blood_pressure, glicose_level, oxygen_saturation, pulse_rate, respiration_rate, bloodfast_level;
    Boolean current;

    public Medical_diagnostic(final String patient_id, final String name, final String blood_pressure, final String patient_temp, final String glicose_level, final String oxygen_saturation, final String pulse_rate, final String respiration_rate, final String bloodfast_level, final Boolean isCurrent) {
        this.patient_id = patient_id;
        this.name = name;
        this.patient_temp = patient_temp;
        this.blood_pressure = blood_pressure;
        this.glicose_level = glicose_level;
        this.oxygen_saturation = oxygen_saturation;
        this.pulse_rate = pulse_rate;
        this.respiration_rate = respiration_rate;
        this.bloodfast_level = bloodfast_level;
        this.current = isCurrent;
    }

    public Medical_diagnostic(final String patient_id, final Boolean isCurrent) {
        this.patient_id = patient_id;
        this.current = isCurrent;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(final String patient_id) {
        this.patient_id = patient_id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPatient_temp() {
        return patient_temp;
    }

    public void setPatient_temp(final String patient_temp) {
        this.patient_temp = patient_temp;
    }

    public String getBlood_pressure() {
        return blood_pressure;
    }

    public void setBlood_pressure(final String blood_pressure) {
        this.blood_pressure = blood_pressure;
    }

    public String getGlicose_level() {
        return glicose_level;
    }

    public void setGlicose_level(final String glicose_level) {
        this.glicose_level = glicose_level;
    }

    public String getOxygen_saturation() {
        return oxygen_saturation;
    }

    public void setOxygen_saturation(final String oxygen_saturation) {
        this.oxygen_saturation = oxygen_saturation;
    }

    public String getPulse_rate() {
        return pulse_rate;
    }

    public void setPulse_rate(final String pulse_rate) {
        this.pulse_rate = pulse_rate;
    }


    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(final Boolean isCurrent) {
        this.current = isCurrent;
    }

    public String getRespiration_rate() {
        return respiration_rate;
    }

    public void setRespiration_rate(final String respiration_rate) {
        this.respiration_rate = respiration_rate;
    }

    public String getBloodfast_level() {
        return bloodfast_level;
    }

    public void setBloodfast_level(final String bloodfast_level) {
        this.bloodfast_level = bloodfast_level;
    }

    @Override
    public String toString() {
        return "Medical_diagnostic{" +
                "patient_id='" + patient_id + '\'' +
                ", name='" + name + '\'' +
                ", patient_temp='" + patient_temp + '\'' +
                ", blood_pressure='" + blood_pressure + '\'' +
                ", glicose_level='" + glicose_level + '\'' +
                ", oxygen_saturation='" + oxygen_saturation + '\'' +
                ", pulse_rate='" + pulse_rate + '\'' +
                ", respiration_rate='" + respiration_rate + '\'' +
                ", bloodfast_level='" + bloodfast_level + '\'' +
                ", isCurrent=" + current +
                '}';
    }

    public Map<String, Object> toMap() {
        final HashMap<String, Object> res = new HashMap<>();
        res.put("patient_id", patient_id);
        res.put("name", name);
        res.put("patient_temp", patient_temp);
        res.put("blood_pressure", blood_pressure);
        res.put("glicose_level", glicose_level);
        res.put("oxygen_saturation", oxygen_saturation);
        res.put("pulse_rate", pulse_rate);
        res.put("respiration_rate", respiration_rate);
        res.put("bloodfast_level", bloodfast_level);
        res.put("isCurrent", current);

        return res;
    }

}
