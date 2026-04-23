package deakin.gopher.guardian.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.logbook.LogEntry
import java.text.SimpleDateFormat
import java.util.*

class LogEntryDialog {

    companion object {
        fun show(
            context: Context,
            patientId: String? = null,
            existingLog: LogEntry? = null,
            isViewOnly: Boolean = false,
            onSave: ((String, String, String) -> Unit)? = null
        ) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)

            val view = LayoutInflater.from(context).inflate(R.layout.dialog_log_entry, null)
            dialog.setContentView(view)

            setupDialog(view, dialog, patientId, existingLog, isViewOnly, onSave)

            // Show dialog
            dialog.show()

            // Set dialog size
            val window = dialog.window
            window?.setLayout(
                (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        private fun setupDialog(
            view: android.view.View,
            dialog: Dialog,
            patientId: String?,
            existingLog: LogEntry?,
            isViewOnly: Boolean,
            onSave: ((String, String, String) -> Unit)?
        ) {
            val titleText = view.findViewById<TextView>(R.id.dialog_title)
            val titleEditText = view.findViewById<EditText>(R.id.log_title_edit_text)
            val descriptionEditText = view.findViewById<EditText>(R.id.log_description_edit_text)
            val patientIdEditText = view.findViewById<EditText>(R.id.patient_id_edit_text)
            val saveButton = view.findViewById<Button>(R.id.save_button)
            val cancelButton = view.findViewById<Button>(R.id.cancel_button)
            val createdByLayout = view.findViewById<LinearLayout>(R.id.created_by_layout)
            val createdByText = view.findViewById<TextView>(R.id.created_by_text)
            val createdAtText = view.findViewById<TextView>(R.id.created_at_text)
            val roleBadge = view.findViewById<TextView>(R.id.role_badge)

            if (existingLog != null) {
                // Viewing existing log
                titleText.text = if (isViewOnly) "Log Entry Details" else "Edit Log Entry"
                titleEditText.setText(existingLog.title)
                descriptionEditText.setText(existingLog.description)
                patientIdEditText.setText(existingLog.patient)

                // Show creator info
                createdByLayout.visibility = android.view.View.VISIBLE
                createdByText.text = "Created by: ${existingLog.createdBy.fullname}"
                createdAtText.text = formatCreatedAt(existingLog.createdAt)
                roleBadge.text = existingLog.createdBy.role.uppercase()
                roleBadge.visibility = android.view.View.VISIBLE

                if (isViewOnly) {
                    // Make fields read-only
                    titleEditText.isEnabled = false
                    descriptionEditText.isEnabled = false
                    patientIdEditText.isEnabled = false
                    saveButton.visibility = android.view.View.GONE
                }
            } else {
                // Creating new log
                titleText.text = "Create New Log Entry"
                patientId?.let { patientIdEditText.setText(it) }
                createdByLayout.visibility = android.view.View.GONE
            }

            // Button click listeners
            cancelButton.setOnClickListener { dialog.dismiss() }

            saveButton.setOnClickListener {
                val title = titleEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()
                val selectedPatientId = patientIdEditText.text.toString().trim()

                if (validateInput(title, description, selectedPatientId)) {
                    dialog.dismiss()
                    onSave?.invoke(title, description, selectedPatientId)
                } else {
                    showValidationError(view.context)
                }
            }
        }

        private fun validateInput(title: String, description: String, patientId: String): Boolean {
            return title.isNotEmpty() && description.isNotEmpty() && patientId.isNotEmpty()
        }

        private fun showValidationError(context: Context) {
            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
        }

        private fun formatCreatedAt(createdAt: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy 'at' h:mm a", Locale.getDefault())
                val date = inputFormat.parse(createdAt)
                date?.let { outputFormat.format(it) } ?: createdAt
            } catch (e: Exception) {
                createdAt
            }
        }
    }
}