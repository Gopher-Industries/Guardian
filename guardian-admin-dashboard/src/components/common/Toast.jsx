import { motion, AnimatePresence } from 'framer-motion';
import Button from './Button';

const VARIANT_STYLES = {
  confirm: 'var(--color-danger, #e53e3e)',
  success: 'var(--color-success, #38a169)',
};

export default function Toast({
  open,
  variant = 'confirm',
  title,
  message,
  confirmLabel,
  cancelLabel = 'Cancel',
  onConfirm,
  onCancel,
}) {
  const defaultTitle = variant === 'success' ? 'Success' : 'Are you sure?';
  const defaultConfirmLabel = variant === 'success' ? 'OK' : 'Confirm';

  return (
    <AnimatePresence>
      {open && (
        <>
          <motion.div
            className='modal-backdrop'
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onCancel ?? onConfirm}
          />
          <div className='modal-container'>
            <motion.div
              className='modal'
              initial={{ opacity: 0, y: 24, scale: 0.97 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: 16, scale: 0.97 }}
              transition={{ duration: 0.22 }}
              style={{ maxWidth: '400px' }}
            >
              <div className='modal-header'>
                <h3 className='modal-title'>{title ?? defaultTitle}</h3>
              </div>
              <div className='modal-body'>
                <p style={{ margin: '0 0 24px' }}>{message}</p>
                <div className='modal-footer'>
                  {variant === 'confirm' && (
                    <button
                      className='btn-secondary'
                      style={{ padding: '12px 18px' }}
                      onClick={onCancel}
                    >
                      {cancelLabel}
                    </button>
                  )}
                  <Button
                    onClick={onConfirm}
                    style={{
                      padding: '12px 18px',
                      background: VARIANT_STYLES[variant],
                    }}
                  >
                    {confirmLabel ?? defaultConfirmLabel}
                  </Button>
                </div>
              </div>
            </motion.div>
          </div>
        </>
      )}
    </AnimatePresence>
  );
}
