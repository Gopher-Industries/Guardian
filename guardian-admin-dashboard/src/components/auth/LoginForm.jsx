import { useState } from "react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import InputField from "../common/InputField";
import Button from "../common/Button";
import { loginAdmin, sendPin } from "../../services/authService";
import {
  setPendingEmail,
  setPendingToken,
  setPendingUser,
} from "../../utils/storage";

export default function LoginForm() {
  const [form, setForm] = useState({
    email: "",
    password: "",
  });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError("");

    try {
      const result = await loginAdmin(form);

      const token = result?.token;
      const user = result?.user;

      if (!token || !user) {
        throw new Error("Login response is missing token or user.");
      }

      setPendingEmail(form.email);
      setPendingToken(token);
      setPendingUser(user);

      if (user?.twoFactorRequired) {
        await sendPin(form.email);
        navigate("/otp", { state: { email: form.email } });
      } else {
        navigate("/dashboard");
      }
    } catch (err) {
      setError(
        err?.response?.data?.message ||
          err?.message ||
          "Login failed. Please check your credentials and try again."
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <motion.form
      className="auth-card"
      onSubmit={handleSubmit}
      initial={{ opacity: 0, x: 24 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ duration: 0.55, delay: 0.15 }}
    >
      <div className="auth-card-header">
        <h2>Sign in to Admin Portal</h2>
        <p>Secure access for monitoring, alerts, and care administration.</p>
      </div>

      <div className="auth-card-body">
        <InputField
          label="Email Address"
          type="email"
          name="email"
          value={form.email}
          onChange={handleChange}
          placeholder="admin@guardian.com"
          autoComplete="email"
        />

        <InputField
          label="Password"
          type="password"
          name="password"
          value={form.password}
          onChange={handleChange}
          placeholder="Enter your password"
          autoComplete="current-password"
        />

        {error ? <p className="form-error">{error}</p> : null}

        <div className="auth-card-helper">
          <span>Two-step verification enabled</span>
          <button type="button" className="text-link">
            Forgot password?
          </button>
        </div>
      </div>

      <Button type="submit" fullWidth disabled={submitting}>
        {submitting ? "Signing in..." : "Continue to OTP"}
      </Button>
    </motion.form>
  );
}