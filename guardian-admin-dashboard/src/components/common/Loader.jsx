export default function Loader({ text = "Loading..." }) {
  return (
    <div className="inline-loader">
      <span className="loader-dot" />
      <span>{text}</span>
    </div>
  );
}