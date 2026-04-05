import { motion } from "framer-motion";
import { ShieldCheck } from "lucide-react";
import OtpForm from "../components/auth/OtpForm";
import AnimatedHeading from "../components/common/AnimatedHeading";
import Logo from "../components/common/Logo";

export default function OtpPage() {
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
            eyebrow="Second-step verification"
            title="Confirm access to the admin workspace"
            subtitle="Use the one-time password to securely continue into the Guardian administrative dashboard."
          />

          <div className="hero-feature-list">
            <div className="hero-feature-card">
              <ShieldCheck size={20} />
              <div>
                <strong>Secure verification</strong>
                <p>
                  A second verification step helps protect access to
                  administrative records and organisation-level controls.
                </p>
              </div>
            </div>
          </div>
        </motion.div>

        <OtpForm />
      </div>
    </section>
  );
}