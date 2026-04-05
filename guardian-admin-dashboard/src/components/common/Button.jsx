import { motion } from "framer-motion";

export default function Button({
  children,
  type = "button",
  onClick,
  disabled = false,
  fullWidth = false,
}) {
  return (
    <motion.button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={`ui-button ${fullWidth ? "full-width" : ""}`}
      whileHover={{ y: -1, scale: 1.01 }}
      whileTap={{ scale: 0.99 }}
    >
      {children}
    </motion.button>
  );
}