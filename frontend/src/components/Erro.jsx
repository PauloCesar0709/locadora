/** Mostra a mensagem de um ApiError e a lista de campos inválidos (se houver). */
export default function Erro({ erro }) {
  if (!erro) return null
  return (
    <div className="alert alert-error" role="alert">
      {erro.message}
      {erro.detalhes?.length > 0 && (
        <ul>
          {erro.detalhes.map((d) => (
            <li key={d}>{d}</li>
          ))}
        </ul>
      )}
    </div>
  )
}
