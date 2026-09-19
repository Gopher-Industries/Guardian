import { useEffect, useMemo, useState } from "react";
import {
  Mail,
  Eye,
  Send,
  FlaskConical,
  CheckCircle2,
  AlertCircle,
} from "lucide-react";
import {
  getEmailTemplates,
  getEmailTemplateSample,
  previewEmail,
  sendEmail,
} from "../services/emailService";
import "./EmailTemplatesPage.css";

export default function EmailTemplatesPage() {
  const [templates, setTemplates] = useState([]);
  const [selectedTemplateKey, setSelectedTemplateKey] = useState("");
  const [formData, setFormData] = useState({});
  const [loading, setLoading] = useState(true);
  const [loadingSample, setLoadingSample] = useState(false);
  const [previewing, setPreviewing] = useState(false);
  const [sending, setSending] = useState(false);
  const [preview, setPreview] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    async function loadTemplates() {
      try {
        setLoading(true);
        setError("");

        const data = await getEmailTemplates();
        const templateList = data?.templates || [];

        setTemplates(templateList);

        if (templateList.length > 0) {
          setSelectedTemplateKey(templateList[0].key);
        }
      } catch (err) {
        console.error("Failed to load email templates:", err);
        setError("Unable to load email templates.");
      } finally {
        setLoading(false);
      }
    }

    loadTemplates();
  }, []);

  const selectedTemplate = useMemo(
    () =>
      templates.find(
        (template) => template.key === selectedTemplateKey,
      ),
    [templates, selectedTemplateKey],
  );

  useEffect(() => {
    if (!selectedTemplate) {
      return;
    }

    const initialValues = {};

    selectedTemplate.fields?.forEach((field) => {
      initialValues[field.name] = "";
    });

    setFormData(initialValues);
    setPreview(null);
    setError("");
    setSuccess("");
  }, [selectedTemplate]);

  function handleFieldChange(fieldName, value) {
    setFormData((previous) => ({
      ...previous,
      [fieldName]: value,
    }));

    setSuccess("");
  }

  async function handleLoadSample() {
    if (!selectedTemplateKey) {
      return;
    }

    try {
      setLoadingSample(true);
      setError("");
      setSuccess("");

      const data = await getEmailTemplateSample(selectedTemplateKey);

      setFormData((previous) => ({
        ...previous,
        ...(data?.sample || {}),
      }));
    } catch (err) {
      console.error("Failed to load template sample:", err);
      setError("Unable to load sample data.");
    } finally {
      setLoadingSample(false);
    }
  }

  async function handlePreview() {
    if (!selectedTemplateKey) {
      return;
    }

    try {
      setPreviewing(true);
      setError("");
      setSuccess("");

      const data = await previewEmail(
        selectedTemplateKey,
        formData,
      );

      setPreview(data);
    } catch (err) {
      console.error("Failed to preview email:", err);
      setError(
        err?.response?.data?.message ||
          "Unable to preview this email.",
      );
    } finally {
      setPreviewing(false);
    }
  }

  async function handleSend() {
    if (!selectedTemplateKey) {
      return;
    }

    try {
      setSending(true);
      setError("");
      setSuccess("");

      await sendEmail(selectedTemplateKey, formData, {
        dryRun: false,
      });

      setSuccess("Email Delivered Successfully.");
    } catch (err) {
      console.error("Failed to send email:", err);
      setError(
        err?.response?.data?.message ||
          "Unable to process this email.",
      );
    } finally {
      setSending(false);
    }
  }

  function renderField(field) {
    const value = formData[field.name] ?? "";

    if (field.choices?.length) {
      return (
        <select
          id={`email-field-${field.name}`}
          className="email-form-control"
          value={value}
          required={field.required}
          onChange={(event) =>
            handleFieldChange(field.name, event.target.value)
          }
        >
          <option value="">Select an option</option>

          {field.choices.map((choice) => (
            <option key={choice} value={choice}>
              {choice}
            </option>
          ))}
        </select>
      );
    }

    if (field.type === "textarea") {
      return (
        <textarea
          id={`email-field-${field.name}`}
          className="email-form-control email-textarea"
          value={value}
          required={field.required}
          placeholder={field.sample ? `Example: ${field.sample}` : ""}
          onChange={(event) =>
            handleFieldChange(field.name, event.target.value)
          }
          rows={4}
        />
      );
    }

    return (
      <input
        id={`email-field-${field.name}`}
        className="email-form-control"
        type={field.type || "text"}
        value={value}
        required={field.required}
        placeholder={field.sample ? `Example: ${field.sample}` : ""}
        onChange={(event) =>
          handleFieldChange(field.name, event.target.value)
        }
      />
    );
  }

  if (loading) {
    return (
      <div className="email-page-state">
        <div className="email-loading-spinner" />
        <p>Loading email templates...</p>
      </div>
    );
  }

  return (
    <div className="email-templates-page">
      <div className="email-page-header">
        <div>
          <div className="email-page-eyebrow">
            <Mail size={16} />
            Guardian Communications
          </div>

          <h1>Email Templates</h1>

          <p>
            Select a Guardian email template, complete the required
            information and preview the message before sending.
          </p>
        </div>

        <div className="email-template-count">
          <span>{templates.length}</span>
          <small>Templates available</small>
        </div>
      </div>

      {error ? (
        <div className="email-alert email-alert-error">
          <AlertCircle size={19} />
          <span>{error}</span>
        </div>
      ) : null}

      {success ? (
        <div className="email-alert email-alert-success">
          <CheckCircle2 size={19} />
          <span>{success}</span>
        </div>
      ) : null}

      <div className="email-workspace">
        <section className="email-compose-card">
          <div className="email-card-header">
            <div>
              <h2>Compose Email</h2>
              <p>Choose a template and provide the message details.</p>
            </div>
          </div>

          <div className="email-template-selector">
            <label htmlFor="template-select">
              Email Template
            </label>

            <select
              id="template-select"
              className="email-form-control email-template-select"
              value={selectedTemplateKey}
              onChange={(event) =>
                setSelectedTemplateKey(event.target.value)
              }
            >
              {templates.map((template) => (
                <option key={template.key} value={template.key}>
                  {template.category} — {template.name}
                </option>
              ))}
            </select>
          </div>

          {selectedTemplate ? (
            <>
              <div className="email-selected-template">
                <div className="email-template-icon">
                  <Mail size={21} />
                </div>

                <div>
                  <div className="email-template-meta">
                    {selectedTemplate.category}
                  </div>

                  <h3>{selectedTemplate.name}</h3>

                  <p>{selectedTemplate.description}</p>
                </div>
              </div>

              <div className="email-divider" />

              <div className="email-form-grid">
                {selectedTemplate.fields?.map((field) => (
                  <div
                    className={`email-field ${
                      field.type === "textarea"
                        ? "email-field-full"
                        : ""
                    }`}
                    key={field.name}
                  >
                    <label htmlFor={`email-field-${field.name}`}>
                      {field.label}

                      {field.required ? (
                        <span className="email-required">*</span>
                      ) : (
                        <span className="email-optional">
                          Optional
                        </span>
                      )}
                    </label>

                    {renderField(field)}

                    {field.help ? (
                      <small className="email-field-help">
                        {field.help}
                      </small>
                    ) : null}
                  </div>
                ))}
              </div>

              <div className="email-actions">
                <button
                  className="email-button email-button-secondary"
                  type="button"
                  onClick={handleLoadSample}
                  disabled={loadingSample}
                >
                  <FlaskConical size={17} />

                  {loadingSample ? "Loading..." : "Load Sample"}
                </button>

                <button
                  className="email-button email-button-secondary"
                  type="button"
                  onClick={handlePreview}
                  disabled={previewing}
                >
                  <Eye size={17} />

                  {previewing ? "Previewing..." : "Preview Email"}
                </button>

                <button
                    className="email-button email-button-primary"
                    type="button"
                    onClick={handleSend}
                    disabled={sending}
                >
                    <Send size={17} />

                    {sending ? "Sending..." : "Send Email"}
                </button>
              </div>

              
            </>
          ) : (
            <div className="email-empty-state">
              No email templates are available.
            </div>
          )}
        </section>

        <section className="email-preview-card">
          <div className="email-card-header email-preview-header">
            <div>
              <h2>Email Preview</h2>
              <p>
                Preview the rendered email before sending.
              </p>
            </div>

            {preview ? (
              <span className="email-preview-ready">
                <CheckCircle2 size={15} />
                Ready
              </span>
            ) : null}
          </div>

          {preview ? (
            <>
              <div className="email-preview-subject">
                <span>Subject</span>
                <strong>{preview.subject}</strong>
              </div>

              {preview.html ? (
                <div className="email-preview-frame-wrapper">
                  <iframe
                    className="email-preview-frame"
                    title="Email preview"
                    srcDoc={preview.html}
                  />
                </div>
              ) : (
                <div className="email-empty-state">
                  No HTML preview is available for this template.
                </div>
              )}
            </>
          ) : (
            <div className="email-preview-placeholder">
              <div className="email-preview-placeholder-icon">
                <Eye size={28} />
              </div>

              <h3>No preview yet</h3>

              <p>
                Complete the template fields and select
                <strong> Preview Email</strong> to see the rendered
                message here.
              </p>
            </div>
          )}
        </section>
      </div>
    </div>
  );
}