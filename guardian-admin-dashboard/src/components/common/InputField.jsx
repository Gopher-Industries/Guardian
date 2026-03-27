export default function InputField({
  label,
  type = "text",
  value,
  onChange,
  name,
  placeholder,
  autoComplete,
}) {
  return (
    <label className="field">
      <span className="field-label">{label}</span>
      <input
        className="field-input"
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        autoComplete={autoComplete}
      />
    </label>
  );
}