import { useState } from "react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import InputField from "../common/InputField";
import Button from "../common/Button";
import { requestPasswordReset } from "../../services/authService";

export default function ForgotPasswordForm() {
  const [email, setEmail] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [submitted, setSubmitted] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError("");

    try {
      await requestPasswordReset(email);
      setSubmitted(true);
    } catch (err) {
      const status = err?.response?.status;
      const backendMessage = err?.response?.data?.message;

      if (status === 400) {
        setError(backendMessage || "Please enter a valid email address.");
      } else if (status === 404) {
        setError(backendMessage || "No account found for that email.");
      } else {
        setError(backendMessage || "Something went wrong. Please try again later.");
      }
    } finally {
      setSubmitting(false);
    }
  };

  if (submitted) {
    return (
      <motion.div
        className="auth-card"
        initial={{ opacity: 0, x: 24 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.55, delay: 0.15 }}
      >
        <div className="auth-card-header">
          <h2>Check your email</h2>
          <p>
            If an account exists for <strong>{email}</strong>, a password
            reset link has been sent.
          </p>
        </div>

        <Button type="button" fullWidth onClick={() => navigate("/login")}>
          Back to login
        </Button>
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
        <h2>Forgot your password?</h2>
        <p>Enter your email and we'll send you a password reset link.</p>
      </div>

      <div className="auth-card-body">
        <InputField
          label="Email Address"
          type="email"
          name="email"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
          placeholder="admin@guardian.com"
          autoComplete="email"
        />

        {error ? <p className="form-error">{error}</p> : null}

        <div className="auth-card-helper">
          <button
            type="button"
            className="text-link"
            onClick={() => navigate("/login")}
          >
            Back to login
          </button>
        </div>
      </div>

      <Button type="submit" fullWidth disabled={submitting}>
        {submitting ? "Sending..." : "Send reset instructions"}
      </Button>
    </motion.form>
  );
}