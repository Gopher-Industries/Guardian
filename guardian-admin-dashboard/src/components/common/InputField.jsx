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
  return (
    <label className="field">
      <span className="field-label">{label}</span>
      <input
        className={`field-input${error ? ' field-input--error' : ''}`}
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        autoComplete={autoComplete}
      />
      {error && <span className="field-error">{error}</span>}
    </label>
  );
}