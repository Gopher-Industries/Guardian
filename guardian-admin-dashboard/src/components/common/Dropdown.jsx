import { useState, useRef, useEffect } from 'react';
import { ChevronDown } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const MotionSpan = motion.span;
const MotionUl = motion.ul;

export default function Dropdown({ label, name, value, onChange, options = [], placeholder, error, disabled }) {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  const selected = options.find((o) => o.value === value);

  useEffect(() => {
    function handleClickOutside(e) {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false);
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  function handleSelect(optValue) {
    onChange({ target: { name, value: optValue } });
    setOpen(false);
  }

  return (
    <div className="field">
      {label && <span className="field-label">{label}</span>}
      <div className="dropdown-wrapper" ref={ref}>
        <button
          type="button"
          className={`field-input dropdown-trigger${!value ? ' placeholder-shown' : ''}${error ? ' field-input--error' : ''}`}
          onClick={() => !disabled && setOpen((prev) => !prev)}
          disabled={disabled}
        >
          <span>{selected ? selected.label : placeholder}</span>
          <MotionSpan
            animate={{ rotate: open ? 180 : 0 }}
            transition={{ duration: 0.2 }}
            style={{ display: 'inline-flex' }}
          >
            <ChevronDown size={16} />
          </MotionSpan>
        </button>

        <AnimatePresence>
          {open && (
            <MotionUl
              className="dropdown-menu"
              initial={{ opacity: 0, y: -6, scale: 0.98 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: -6, scale: 0.98 }}
              transition={{ duration: 0.15 }}
            >
              {options.map((opt) => (
                <li
                  key={opt.value}
                  className={`dropdown-option${opt.value === value ? ' selected' : ''}`}
                  onClick={() => handleSelect(opt.value)}
                >
                  {opt.label}
                </li>
              ))}
            </MotionUl>
          )}
        </AnimatePresence>
      </div>
      {error && <span className="field-error">{error}</span>}
    </div>
  );
}
