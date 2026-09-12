import { useEffect, useMemo, useState } from "react";
import {
  getEmailTemplates,
  getEmailTemplateSample,
  previewEmail,
  sendEmail,
} from "../services/emailService";

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

      await sendEmail(selectedTemplateKey, formData);

      setSuccess("Email accepted for delivery.");
    } catch (err) {
      console.error("Failed to send email:", err);
      setError(
        err?.response?.data?.message ||
          "Unable to send this email.",
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
          value={value}
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
          value={value}
          onChange={(event) =>
            handleFieldChange(field.name, event.target.value)
          }
          rows={4}
        />
      );
    }

    return (
      <input
        type={field.type || "text"}
        value={value}
        onChange={(event) =>
          handleFieldChange(field.name, event.target.value)
        }
      />
    );
  }

  if (loading) {
    return <div>Loading email templates...</div>;
  }

  return (
    <div>
      <h1>Email Templates</h1>

      <p>
        Select an email template, enter the required information,
        preview it, and send it.
      </p>

      {error ? <p>{error}</p> : null}
      {success ? <p>{success}</p> : null}

      <div>
        <label htmlFor="template-select">
          Email Template
        </label>

        <select
          id="template-select"
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
          <div>
            <h2>{selectedTemplate.name}</h2>
            <p>{selectedTemplate.description}</p>
          </div>

          <div>
            {selectedTemplate.fields?.map((field) => (
              <div key={field.name}>
                <label>
                  {field.label}
                  {field.required ? " *" : ""}
                </label>

                {renderField(field)}

                {field.help ? (
                  <small>{field.help}</small>
                ) : null}
              </div>
            ))}
          </div>

          <div>
            <button
              type="button"
              onClick={handleLoadSample}
              disabled={loadingSample}
            >
              {loadingSample ? "Loading..." : "Load Sample"}
            </button>

            <button
              type="button"
              onClick={handlePreview}
              disabled={previewing}
            >
              {previewing ? "Previewing..." : "Preview Email"}
            </button>

            <button
              type="button"
              onClick={handleSend}
              disabled={sending}
            >
              {sending ? "Sending..." : "Send Email"}
            </button>
          </div>
        </>
      ) : null}

      {preview ? (
        <div>
          <h2>Preview</h2>

          <p>
            <strong>Subject:</strong> {preview.subject}
          </p>

          {preview.html ? (
            <iframe
              title="Email preview"
              srcDoc={preview.html}
              style={{
                width: "100%",
                minHeight: "600px",
                border: "1px solid #ddd",
              }}
            />
          ) : null}
        </div>
      ) : null}
    </div>
  );
}