import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { useNavigate, Link } from "react-router-dom";
import InputField from "../common/InputField";
import Button from "../common/Button";
import { ROLE_OPTIONS } from "../../utils/constants";
import { registerUser } from "../../services/authService";
import { getPublicOrganizations } from "../../services/orgService";

export default function RegisterForm() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    role: "",
    phone: "",
    organizationId: "",
  });

  const [orgs, setOrgs] = useState([]);
  const [orgsLoading, setOrgsLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);


useEffect(() => {
  getPublicOrganizations()
    .then((data) => setOrgs(data?.orgs || data || []))
    .catch((err) => {
      console.error("Failed to load organizations", err);
      // setError("Couldn't load organizations. Please refresh and try again.");
       // TEMP fallback
      setOrgs([{ _id: "664f1c2e8b1a2c3d4e5f6a7b", name: "Guardian Health Org" }]);
    })
    .finally(() => setOrgsLoading(false));
}, []);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setSubmitting(true);
    try {
      await registerUser({
        name: form.name,
        email: form.email,
        password: form.password,
        role: form.role,
        phone: form.phone,
        organizationId: form.organizationId,
      });
      setSuccess(true);
    } catch (err) {
      const status = err?.response?.status;
      const backendMessage = err?.response?.data?.error || err?.response?.data?.message;

      if (status === 409) {
        setError("A user with this email already exists.");
      } else if (backendMessage) {
        setError(backendMessage);
      } else {
        setError("Registration failed. Please try again later.");
      }
    } finally {
      setSubmitting(false);
    }
  };

  if (success) {
    return (
      <motion.div
        className="auth-card"
        initial={{ opacity: 0, x: 24 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.55, delay: 0.15 }}
      >
        <div className="auth-card-header">
          <h2>Request submitted</h2>
          <p>Your account is pending admin approval. You'll be notified once reviewed.</p>
        </div>
        <div className="auth-card-body">
          <Button type="button" fullWidth onClick={() => navigate("/login")}>
            Back to Login
          </Button>
        </div>
      </motion.div>
    );
  }

  return (
    <motion.form
      className="auth-card"
      onSubmit={handleSubmit}
      initial={{ opacity: 0, x: 24 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ duration: 0.55, delay: 0.15 }}
    >
      <div className="auth-card-header">
        <h2>Create an account</h2>
        <p>Register for Doctor or Nurse access.</p>
      </div>

      <div className="auth-card-body">
        <InputField
          label="Full Name"
          type="text"
          name="name"
          value={form.name}
          onChange={handleChange}
          placeholder="Jane Smith"
          autoComplete="name"
        />

        <InputField
          label="Email Address"
          type="email"
          name="email"
          value={form.email}
          onChange={handleChange}
          placeholder="jane.smith@guardian.com"
          autoComplete="email"
        />

        <InputField
          label="Phone"
          type="tel"
          name="phone"
          value={form.phone}
          onChange={handleChange}
          placeholder="+61412345678"
          autoComplete="tel"
        />

        <InputField
          label="Password"
          type="password"
          name="password"
          value={form.password}
          onChange={handleChange}
          placeholder="Create a password"
          autoComplete="new-password"
        />

        <InputField
          label="Confirm Password"
          type="password"
          name="confirmPassword"
          value={form.confirmPassword}
          onChange={handleChange}
          placeholder="Re-enter your password"
          autoComplete="new-password"
        />

        {/* TEMP: plain select until Dropdown.jsx prop contract is confirmed */}
        <label className="input-field">
          <span className="input-field-label">Role</span>
          <select name="role" value={form.role} onChange={handleChange} required>
            <option value="" disabled>Select role</option>
            {ROLE_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
        </label>

        <label className="input-field">
          <span className="input-field-label">Organization</span>
          <select
            name="organizationId"
            value={form.organizationId}
            onChange={handleChange}
            required
            disabled={orgsLoading}
          >
            <option value="" disabled>
              {orgsLoading ? "Loading organizations..." : "Select organization"}
            </option>
            {orgs.map((org) => (
              <option key={org._id} value={org._id}>{org.name}</option>
            ))}
          </select>
        </label>

        {error ? <p className="form-error">{error}</p> : null}
      </div>

      <Button type="submit" fullWidth disabled={submitting}>
        {submitting ? "Registering..." : "Register"}
      </Button>

      <p className="auth-card-footer">
        Already have an account?{" "}
        <button type="button" className="text-link" onClick={() => navigate("/login")}>
          Log in
        </button>
      </p>
    </motion.form>
  );
}