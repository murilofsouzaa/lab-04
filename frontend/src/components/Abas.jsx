export default function Abas({ abas, atual, onTrocar }) {
  return (
    <nav className="abas">
      {abas.map(([id, rotulo]) => (
        <button key={id} className={id === atual ? 'ativa' : ''} onClick={() => onTrocar(id)}>{rotulo}</button>
      ))}
    </nav>
  )
}
