# Annotation Guidelines – Guardian Monitor AI

## 1. Objective
These guidelines define how to annotate nursing notes for the Guardian Monitor AI project.  
They will be used to train clinical NLP models for:
- **Named Entity Recognition (NER)** – extracting structured information from notes.
- **Summarisation** – generating concise patient status updates.
- **Trend Analysis** – tracking changes over time.

---

## 2. Entity Set

| Entity        | Description | Examples |
|---------------|-------------|----------|
| **SYMPTOM**   | Patient-reported or observed conditions. | nausea, agitation, fever |
| **MEDICATION**| Drug names, brands, or classes. | paracetamol, metformin |
| **DOSAGE**    | Strength or quantity of a drug. | 500 mg, 1 tab, 10 mL |
| **ROUTE**     | Administration route. | PO, IV, IM, topical |
| **FREQUENCY** | Schedule of administration. | bd, tds, nightly, q6h, PRN |
| **VITAL_TYPE**| Type of vital sign. | BP, heart rate, temperature |
| **VITAL_VALUE**| Measurement and units. | 120/80 mmHg, 37.8 °C, 88 bpm |
| **DIET_ITEM** | Food, drink, or nutrition. | porridge, fluids, puree diet |
| **MEAL_TYPE** | Meal context. | breakfast, lunch, dinner |
| **ADL_ACTIVITY**| Activity domain in daily living. | bathing, dressing, mobility |
| **ADL_STATUS**| Status or assistance level for ADL. | independent, needs assistance |
| **TIMEX**     | Date, time, or duration. | 07/08/2025, this morning |
| **NOTE_EVENT**| Clinical events or actions. | fall, wound care, transfer |

---

## 3. Relations

| Relation      | Head Entity  | Tail Entity |
|---------------|--------------|-------------|
| **HAS_DOSAGE**    | MEDICATION   | DOSAGE      |
| **HAS_ROUTE**     | MEDICATION   | ROUTE       |
| **HAS_FREQUENCY** | MEDICATION   | FREQUENCY   |
| **HAS_VALUE**     | VITAL_TYPE   | VITAL_VALUE |
| **HAS_STATUS**    | ADL_ACTIVITY | ADL_STATUS  |

---

## 4. Span Rules
- **Tight boundaries**: include only meaningful tokens (no trailing spaces or punctuation).
- **Separate entities**: MEDICATION, DOSAGE, ROUTE, and FREQUENCY are separate spans.
- **Vitals split**: label VITAL_TYPE and VITAL_VALUE separately.
- **Negations**: do not label the negation word (e.g., “denies”), only the concept.
- **Overlaps**: avoid overlapping spans; pick the most specific entity.
- **TIMEX**: capture full date/time phrase.

---

## 5. Edge Cases
- “Panadol 1g PO bd PRN” → MEDICATION=Panadol, DOSAGE=1g, ROUTE=PO, FREQUENCY=bd  
- “Temp 37.8C” → VITAL_TYPE=Temp, VITAL_VALUE=37.8C  
- “Mobility – needs assistance” → ADL_ACTIVITY=Mobility, ADL_STATUS=needs assistance  
- “Porridge at breakfast” → DIET_ITEM=porridge, MEAL_TYPE=breakfast  

---

## 6. Examples

**Example 1**  
_BP 120/80 mmHg, HR 88 bpm. Denies chest pain._  
- VITAL_TYPE=BP → VITAL_VALUE=120/80 mmHg  
- VITAL_TYPE=HR → VITAL_VALUE=88 bpm  
- SYMPTOM=chest pain

**Example 2**  
_Paracetamol 500 mg PO tds since 07/08/2025._  
- MEDICATION=Paracetamol  
- DOSAGE=500 mg  
- ROUTE=PO  
- FREQUENCY=tds  
- TIMEX=07/08/2025

**Example 3**  
_Breakfast taken: porridge; Mobility: needs assistance._  
- MEAL_TYPE=Breakfast  
- DIET_ITEM=porridge  
- ADL_ACTIVITY=Mobility  
- ADL_STATUS=needs assistance

---

## 7. Quality Checklist
- [ ] No overlapping spans.
- [ ] Vitals split correctly into TYPE and VALUE.
- [ ] Medication regimen split into separate entities.
- [ ] TIMEX captured when present.
- [ ] Negations excluded from entity span.
- [ ] At least 5–10 examples per entity in the pilot dataset.

---

**Version:** 1.1  
**Author:** Ramesh Senarath  
**Last Updated:** 22 Aug 2025
