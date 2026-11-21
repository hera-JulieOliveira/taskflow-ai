export default function EmptyState({ icon, title, text, children }) {
  return (
    <div className="empty">
      <div className="empty-icon" aria-hidden="true">{icon}</div>
      <h3>{title}</h3>
      <p>{text}</p>
      {children}
    </div>
  )
}
