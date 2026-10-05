import { motion } from "framer-motion";
import { HeartPulse, ShieldCheck, UserRoundPlus } from "lucide-react";
import RegisterForm from "../components/auth/RegisterForm";
import AnimatedHeading from "../components/common/AnimatedHeading";
import Logo from "../components/common/Logo";

export default function RegisterPage() {
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
            eyebrow="Healthcare Administration"
            title="Join Guardian Admin"
            subtitle="Register for Doctor or Nurse access to the care coordination workspace."
          />

          <div className="hero-feature-list">
            <div className="hero-feature-card">
              <ShieldCheck size={20} />
              <div>
                <strong>Secure onboarding</strong>
                <p>Your account is reviewed by an administrator before access is granted.</p>
              </div>
            </div>

            <div className="hero-feature-card">
              <HeartPulse size={20} />
              <div>
                <strong>Built for care teams</strong>
                <p>Purpose-built tools for doctors and nurses coordinating patient care.</p>
              </div>
            </div>

            <div className="hero-feature-card">
              <UserRoundPlus size={20} />
              <div>
                <strong>Quick setup</strong>
                <p>Fill in your details and you'll be notified as soon as you're approved.</p>
              </div>
            </div>
          </div>
        </motion.div>

        <RegisterForm />
      </div>
    </section>
  );
}