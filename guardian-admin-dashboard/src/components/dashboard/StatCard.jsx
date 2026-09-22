import { motion } from "framer-motion";

export default function StatCard({
  title,
  value,
  description,
  tone = "primary",
}) {
  return (
    <motion.article
      className={`stat-card ${tone}`}
      initial={{ opacity: 0, y: 18 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.45 }}
    >
      <div className="stat-card-value">{value}</div>
      <div className="stat-card-title">{title}</div>
      <p className="stat-card-description">{description}</p>
    </motion.article>
  );
}