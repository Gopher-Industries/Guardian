import api from "./api";

export async function getEmailTemplates() {
  const response = await api.get("email/templates");
  return response.data;
}

export async function getEmailTemplateSample(type) {
  const response = await api.get(`email/templates/${type}/sample`);
  return response.data;
}

export async function previewEmail(template, data) {
  const response = await api.post("email/preview", {
    template,
    data,
  });

  return response.data;
}

export async function sendEmail(template, data, options = {}) {
  const response = await api.post("email/send", {
    template,
    data,
    ...options,
  });

  return response.data;
}

export async function sendBulkEmail(template, recipients, data = {}, options = {}) {
  const response = await api.post("email/send-bulk", {
    template,
    recipients,
    data,
    ...options,
  });

  return response.data;
}