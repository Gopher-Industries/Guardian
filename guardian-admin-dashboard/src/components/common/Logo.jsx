import logo from "../../assets/logo_guardian.png";

export default function Logo({
  size = "medium",
  withText = false,
  text = "Guardian Monitor",
}) {
  const sizeClass =
    {
      small: "logo-small",
      medium: "logo-medium",
      large: "logo-large",
    }[size] || "logo-medium";

  return (
    <div className="brand-lockup">
      <img
        src={logo}
        alt="Guardian Monitor logo"
        className={`brand-logo ${sizeClass}`}
      />
      {withText ? <span className="brand-text">{text}</span> : null}
    </div>
  );
}