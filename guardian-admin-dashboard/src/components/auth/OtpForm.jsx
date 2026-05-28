import { useMemo, useState } from "react";
import { motion } from "framer-motion";
import { useLocation, useNavigate } from "react-router-dom";
import Button from "../common/Button";
import { sendPin, verifyPin } from "../../services/authService";
import { getPendingEmail, promotePendingAuth } from "../../utils/storage";

export default function OtpForm() {
  const [otp, setOtp] = useState(["", "", "", "", "", ""]);
  const [submitting, setSubmitting] = useState(false);
  const [resending, setResending] = useState(false);
  const [error, setError] = useState("");
  const location = useLocation();
  const navigate = useNavigate();

  const email = useMemo(
    () => location.state?.email || getPendingEmail() || "admin@guardian.com",
    [location.state]
  );

  const handleDigitChange = (value, index) => {
    const clean = value.replace(/\D/g, "").slice(0, 1);
    const next = [...otp];
    next[index] = clean;
    setOtp(next);

    if (clean && index < 5) {
      const nextInput = document.getElementById(`otp-${index + 1}`);
      if (nextInput) nextInput.focus();
    }
  };

  const handleKeyDown = (event, index) => {
    if (event.key === "Backspace" && !otp[index] && index > 0) {
      const prevInput = document.getElementById(`otp-${index - 1}`);
      if (prevInput) prevInput.focus();
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError("");

    try {
      await verifyPin({
        email,
        otp: otp.join(""),
      });

      promotePendingAuth();
      navigate("/dashboard");
    } catch (err) {
      setError(
        err?.response?.data?.message ||
          err?.message ||
          "OTP verification failed. Please try again."
      );
    } finally {
      setSubmitting(false);
    }
  };

  const handleResend = async () => {
    setResending(true);
    setError("");

    try {
      await sendPin(email);
    } catch (err) {
      setError(
        err?.response?.data?.message ||
          err?.message ||
          "Could not resend OTP at the moment."
      );
    } finally {
      setResending(false);
    }
  };

  const fillTestOtp = () => {
    setOtp(["1", "2", "3", "4", "5", "6"]);
  };

  return (
    <motion.form
      className="auth-card otp-card"
      onSubmit={handleSubmit}
      initial={{ opacity: 0, x: 24 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ duration: 0.55, delay: 0.15 }}
    >
      <div className="auth-card-header">
        <h2>Verify OTP</h2>
        <p>
          Enter the one-time password associated with{" "}
          <strong>{email}</strong>.
        </p>
      </div>

      <div className="otp-grid otp-grid-six">
        {otp.map((digit, index) => (
          <input
            key={index}
            id={`otp-${index}`}
            className="otp-input"
            type="text"
            inputMode="numeric"
            maxLength="1"
            value={digit}
            onChange={(event) => handleDigitChange(event.target.value, index)}
            onKeyDown={(event) => handleKeyDown(event, index)}
          />
        ))}
      </div>

      <p className="form-note">
        Current backend testing OTP is: <strong>123456</strong>
      </p>

      {error ? <p className="form-error">{error}</p> : null}

      <div className="auth-card-helper otp-helper-wrap">
        <button
          type="button"
          className="text-link"
          onClick={handleResend}
          disabled={resending}
        >
          {resending ? "Resending..." : "Resend OTP"}
        </button>

        <button type="button" className="text-link" onClick={fillTestOtp}>
          Use test OTP
        </button>

        <button
          type="button"
          className="text-link"
          onClick={() => navigate("/login")}
        >
          Back to login
        </button>
      </div>

      <Button type="submit" fullWidth disabled={submitting}>
        {submitting ? "Verifying..." : "Verify and Continue"}
      </Button>
    </motion.form>
  );
}