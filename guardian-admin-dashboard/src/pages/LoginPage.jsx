import { motion } from "framer-motion";
import { HeartPulse, ShieldCheck, UserRoundPlus } from "lucide-react";
import LoginForm from "../components/auth/LoginForm";
import AnimatedHeading from "../components/common/AnimatedHeading";
import Logo from "../components/common/Logo";

export default function LoginPage() {
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
            title="Welcome to Guardian Admin"
            subtitle="A modern care coordination workspace for alerts, monitoring, staff and patient administration."
          />

          <div className="hero-feature-list">
            <div className="hero-feature-card">
              <ShieldCheck size={20} />
              <div>
                <strong>Coordinated care oversight</strong>
                <p>Strengthen administrative decision-making with clearer oversight of staff workflows, patients and support actions.</p>
              </div>
            </div>

            <div className="hero-feature-card">
              <HeartPulse size={20} />
              <div>
                <strong>Care-first monitoring</strong>
                <p>Support safer and more responsive care through better visibility into patient activity and wellbeing.</p>
              </div>
            </div>

            <div className="hero-feature-card">
              <UserRoundPlus size={20} />
              <div>
                <strong>Early risk awareness</strong>
                <p>Help identify unusual patterns, changing behaviours and possible concerns before they escalate.</p>
              </div>
            </div>
          </div>
        </motion.div>

        <LoginForm />
      </div>
    </section>
  );
}