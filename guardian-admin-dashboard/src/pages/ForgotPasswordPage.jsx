import { motion } from "framer-motion";
import { KeyRound } from "lucide-react";
import ForgotPasswordForm from "../components/auth/ForgotPasswordForm";
import AnimatedHeading from "../components/common/AnimatedHeading";
import Logo from "../components/common/Logo";

export default function ForgotPasswordPage() {
  return (
    <section className="auth-page">
      <div className="auth-background">
        <span className="blob one" />
        <span className="blob two" />
        <span className="blob three" />
      </div>

      <div className="auth-grid">
        <motion.div
          className="auth-hero"
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.55 }}
        >
          <Logo size="large" />

          <AnimatedHeading
            eyebrow="Account recovery"
            title="Reset access to the admin workspace"
            subtitle="Enter the email linked to your admin account to receive reset instructions."
          />

          <div className="hero-feature-list">
            <div className="hero-feature-card">
              <KeyRound size={20} />
              <div>
                <strong>Secure reset</strong>
                <p>
                  Reset links/codes expire after a short time to keep
                  administrative access protected.
                </p>
              </div>
            </div>
          </div>
        </motion.div>

        <ForgotPasswordForm />
      </div>
    </section>
  );
}