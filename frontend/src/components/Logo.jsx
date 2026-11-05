/** Marca do sistema: volante estilizado em âmbar sobre azul-marinho. */
export default function Logo({ tamanho = 32 }) {
  return (
    <svg width={tamanho} height={tamanho} viewBox="0 0 48 48" aria-hidden="true">
      <rect width="48" height="48" rx="12" fill="#0F2A5C" />
      <circle cx="24" cy="24" r="14" fill="none" stroke="#F5A524" strokeWidth="4" />
      <circle cx="24" cy="24" r="4" fill="#F5A524" />
      <path d="M10.5 22h9M28.5 22h9M24 28v10" stroke="#F5A524" strokeWidth="4" strokeLinecap="round" />
    </svg>
  )
}

export const NOME_SISTEMA = 'Rodar'
