function doPost(e) {
  try {
    // Parse JSON payload from POST body
    const data = JSON.parse(e.postData.contents);
    if (!Array.isArray(data)) {
      return ContentService.createTextOutput(
        JSON.stringify({ status: "error", message: "Expected JSON array" })
      ).setMimeType(ContentService.MimeType.JSON);
    }

    insertSyntheticDataToSheet(data);

    return ContentService.createTextOutput(
      JSON.stringify({ status: "success", message: "Data inserted" })
    ).setMimeType(ContentService.MimeType.JSON);

  } catch (error) {
    return ContentService.createTextOutput(
      JSON.stringify({ status: "error", message: error.message })
    ).setMimeType(ContentService.MimeType.JSON);
  }
}

/**
 * Inserts synthetic patient data JSON array into the active sheet.
 * @param {Array} data - Array of patient observation objects matching your schema.
 */
function insertSyntheticDataToSheet(data) {
  const sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();

  // Optional: Clear sheet before inserting new data
  //sheet.clear();

  const headers = [
    "patientId", "age", "gender",
    "observationStart", "observationEnd",
    "nursingNote",
    "medications",
    "heartRate", "spo2", "temperature", "bloodPressure",
    "stepsTaken", "calorieIntake", "sleepHours", "waterIntakeMl", "mealsSkipped", "exerciseMinutes","bathroomVisits",
    "behaviourTags",
    "emotionTags",
    "clinicalSummary",
    "entitiesExtracted"

  ];

  sheet.appendRow(headers);

  data.forEach(record => {
    const medsSummary = record.medications.map(m => `${m.name}(${m.complianceStatus})`).join(", ");

    const heartRate = record.vitals.heartRate.value + " " + record.vitals.heartRate.unit;
    const spo2 = record.vitals.spo2.value + record.vitals.spo2.unit;
    const temperature = record.vitals.temperature.value + " " + record.vitals.temperature.unit;
    const bp = record.vitals.bloodPressure.systolic + "/" + record.vitals.bloodPressure.diastolic + " " + record.vitals.bloodPressure.unit;

    const adls = record.adls;

    const behaviourTags = record.behaviourTags.join(", ");
    const emotionTags = record.emotionAnalysis?.tags?.join(", ") || "";

    const row = [
      record.patientId,
      record.age,
      record.gender,
      record.observationStart,
      record.observationEnd,
      record.nursingNote,
      medsSummary,
      heartRate,
      spo2,
      temperature,
      bp,
      adls.stepsTaken,
      adls.calorieIntake,
      adls.sleepHours,
      adls.waterIntakeMl,
      adls.mealsSkipped,
      adls.exerciseMinutes,
      adls.bathroomVisits,
      behaviourTags,
      emotionTags,
      record.clinicalSummary,
      record.entitiesExtracted,
    ];

    sheet.appendRow(row);
  });
}
