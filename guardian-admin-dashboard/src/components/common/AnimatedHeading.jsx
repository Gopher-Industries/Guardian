import { motion } from "framer-motion";

export default function AnimatedHeading({
  eyebrow,
  title,
  subtitle,
  align = "left",
}) {
  return (
    <div className={`animated-heading ${align}`}>
      {eyebrow ? (
        <motion.p
          className="animated-heading-eyebrow"
          initial={{ opacity: 0, y: 14 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45 }}
        >
          {eyebrow}
        </motion.p>
      ) : null}

      <motion.h1
        className="animated-heading-title"
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.55, delay: 0.1 }}
      >
        {title}
      </motion.h1>

      {subtitle ? (
        <motion.p
          className="animated-heading-subtitle"
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.55, delay: 0.18 }}
        >
          {subtitle}
        </motion.p>
      ) : null}
    </div>
  );
}