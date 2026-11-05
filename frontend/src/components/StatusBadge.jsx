import { STATUS_CREDITO, STATUS_PEDIDO } from '../utils/format'

/** Selo colorido. tipo = "pedido" (padrão) ou "credito". */
export default function StatusBadge({ status, tipo = 'pedido' }) {
  const mapa = tipo === 'credito' ? STATUS_CREDITO : STATUS_PEDIDO
  const info = mapa[status] || { rotulo: status, classe: 'badge-neutral' }
  return <span className={`badge ${info.classe}`}>{info.rotulo}</span>
}
