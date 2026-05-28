import { useState } from "react";
import { Eye, EyeOff } from "lucide-react";
import "./InputField.css";

export default function InputField({
  label,
  type = "text",
  value,
  onChange,
  name,
  placeholder,
  autoComplete,
  error,
}) {
  const [showPassword, setShowPassword] = useState(false);
  const isPasswordField = type === "password";

  return (
    <label className="field">
      <span className="field-label">{label}</span>

      <div className="input-shell">
        <input
          className={`field-input${error ? " field-input--error" : ""}${
            isPasswordField ? " password-input" : ""
          }`}
          type={isPasswordField ? (showPassword ? "text" : "password") : type}
          name={name}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          autoComplete={autoComplete}
        />

        {isPasswordField && (
          <button
            type="button"
            className="password-toggle"
            onClick={() => setShowPassword((prev) => !prev)}
            aria-label={showPassword ? "Hide password" : "Show password"}
          >
            {showPassword ? <EyeOff size={17} /> : <Eye size={17} />}
          </button>
        )}
      </div>

      {error ? <span className="field-error">{error}</span> : null}
    </label>
  );
}