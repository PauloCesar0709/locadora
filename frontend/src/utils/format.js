const moedaFmt = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })
const percentFmt = new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 4 })

export function moeda(valor) {
  if (valor === null || valor === undefined || valor === '') return '-'
  return moedaFmt.format(Number(valor))
}

export function percentual(valor) {
  if (valor === null || valor === undefined || valor === '') return '-'
  return `${percentFmt.format(Number(valor))}% ao dia`
}

/** "2026-11-04" -> "04/11/2026" (sem converter fuso horário). */
export function data(iso) {
  if (!iso) return '-'
  const [ano, mes, dia] = iso.split('T')[0].split('-')
  return `${dia}/${mes}/${ano}`
}

export function dataHora(iso) {
  if (!iso) return '-'
  return new Date(iso).toLocaleString('pt-BR')
}

/** Data de hoje no fuso local, no formato do input date (AAAA-MM-DD). */
export function hojeIso() {
  const d = new Date()
  const mes = String(d.getMonth() + 1).padStart(2, '0')
  const dia = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${mes}-${dia}`
}

/** Quantidade de diárias entre as datas (mesma regra do back-end: fim - início). */
export function diasEntre(inicioIso, fimIso) {
  if (!inicioIso || !fimIso) return 0
  const [a1, m1, d1] = inicioIso.split('-').map(Number)
  const [a2, m2, d2] = fimIso.split('-').map(Number)
  return Math.round((Date.UTC(a2, m2 - 1, d2) - Date.UTC(a1, m1 - 1, d1)) / 86400000)
}

/** Juros simples: valor x (1 + taxa/100 x dias), igual ao back-end. */
export function totalComJuros(valor, taxaPercentualDia, dias) {
  const total = Number(valor) * (1 + (Number(taxaPercentualDia) / 100) * Number(dias))
  return Math.round(total * 100) / 100
}

export function somenteDigitos(texto) {
  return (texto || '').replace(/\D/g, '')
}

/** 000.000.000-00 */
export function mascaraCpf(texto) {
  const d = somenteDigitos(texto).slice(0, 11)
  return d
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

/** 00.000.000/0000-00 */
export function mascaraCnpj(texto) {
  const d = somenteDigitos(texto).slice(0, 14)
  return d
    .replace(/^(\d{2})(\d)/, '$1.$2')
    .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
    .replace(/\.(\d{3})(\d)/, '.$1/$2')
    .replace(/(\d{4})(\d)/, '$1-$2')
}

export const STATUS_PEDIDO = {
  AGUARDANDO_CREDITO: { rotulo: 'Aguardando banco', classe: 'badge-info' },
  CREDITO_RECUSADO: { rotulo: 'Crédito recusado', classe: 'badge-danger' },
  CONCLUIDO: { rotulo: 'Concluída', classe: 'badge-success' },
  CANCELADO: { rotulo: 'Cancelada', classe: 'badge-neutral' },
}

export const STATUS_CREDITO = {
  PENDENTE: { rotulo: 'Pendente', classe: 'badge-info' },
  APROVADO: { rotulo: 'Aprovado', classe: 'badge-success' },
  RECUSADO: { rotulo: 'Recusado', classe: 'badge-danger' },
  CANCELADO: { rotulo: 'Cancelado', classe: 'badge-neutral' },
}

export const FORMA_PAGAMENTO = {
  PROPRIO: 'Pagamento próprio',
  CREDITO: 'Contrato de crédito',
}

export const PERFIL = {
  CLIENTE: 'Cliente',
  LOCADORA: 'Locadora',
  BANCO: 'Banco',
}

/** Primeira tela de cada hub. */
export const INICIO_POR_PERFIL = {
  CLIENTE: '/locacoes',
  LOCADORA: '/ofertas',
  BANCO: '/creditos',
}
