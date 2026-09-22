import { motion, AnimatePresence } from "framer-motion";
import { X, AlertCircle } from "lucide-react";

export default function ConfirmationModal({
  isOpen,
  onClose,
  onConfirm,
  title = "Are you sure?",
  message,
  children,
  confirmText = "Confirm",
  cancelText = "Cancel",
  type = "danger",
  isLoading = false,
  icon: CustomIcon,
  maxWidth = "440px"
}) {
  if (!isOpen) return null;

  const getIcon = () => {
    if (CustomIcon) return <CustomIcon className={`text-${type}`} size={22} />;
    return <AlertCircle className={`text-${type}`} size={22} />;
  };

  return (
    <AnimatePresence>
      <div className="modal-overlay" onClick={isLoading ? null : onClose}>
        <motion.div
          className={`modal-content modal-${type}`}
          style={{ maxWidth }}
          onClick={(e) => e.stopPropagation()}
          initial={{ opacity: 0, scale: 0.95, y: 20 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.95, y: 20 }}
          transition={{ type: "spring", damping: 25, stiffness: 300 }}
        >
          <div className="modal-header">
            <div className="modal-title-wrap">
              {getIcon()}
              <h2>{title}</h2>
            </div>
            {!isLoading && (
              <button className="icon-button" onClick={onClose} aria-label="Close modal">
                <X size={18} />
              </button>
            )}
          </div>

          <div className="modal-body">
            {message && <p>{message}</p>}
            {children}
          </div>

          <div className="modal-footer">
            <button
              type="button"
              className="ui-button secondary"
              onClick={onClose}
              disabled={isLoading}
            >
              {cancelText}
            </button>
            <button
              type="button"
              className={`ui-button ${type === 'danger' ? 'danger-btn' : ''}`}
              onClick={onConfirm}
              disabled={isLoading}
              style={{ minWidth: '100px' }}
            >
              {isLoading ? (
                <div className="button-loader"></div>
              ) : confirmText}
            </button>
          </div>
        </motion.div>
      </div>
    </AnimatePresence>
  );
}
