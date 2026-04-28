import { motion, AnimatePresence } from "framer-motion";
import { X } from "lucide-react";

export default function Modal({
  isOpen,
  onClose,
  title,
  children,
  footer,
  maxWidth = "500px",
  type = "default", // default, success, warning, danger
  showClose = true,
  zIndex = 3000,
  className = ""
}) {
  if (!isOpen) return null;

  return (
    <AnimatePresence mode="wait">
      <div className="modal-overlay" style={{ zIndex }} onClick={onClose}>
        <motion.div 
          className={`modal-content modal-${type} ${className}`}
          onClick={(e) => e.stopPropagation()}
          initial={{ opacity: 0, scale: 0.95, y: 10 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.95, y: 10 }}
          transition={{ duration: 0.2, ease: "easeOut" }}
          style={{ maxWidth }}
        >
          {(title || showClose) && (
            <div className="modal-header">
              <div className="modal-title-wrap">
                {title && <h2>{title}</h2>}
              </div>
              {showClose && (
                <button className="icon-button" onClick={onClose} aria-label="Close dialog">
                  <X size={20} />
                </button>
              )}
            </div>
          )}

          <div className="modal-body">
            {children}
          </div>

          {footer && (
            <div className="modal-footer">
              {footer}
            </div>
          )}
        </motion.div>
      </div>
    </AnimatePresence>
  );
}
