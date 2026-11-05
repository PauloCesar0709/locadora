import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { creditoApi } from '../../api/servicos'
import StatusBadge from '../../components/StatusBadge'
import Erro from '../../components/Erro'
import { dataHora, mascaraCpf, moeda } from '../../utils/format'

const FILTROS = [
  { valor: 'PENDENTE', rotulo: 'Pendentes' },
  { valor: 'TODOS', rotulo: 'Todos' },
]

/** Hub do banco: pedidos de crédito recebidos dos clientes. */
export default function Creditos() {
  const navegar = useNavigate()
  const [creditos, setCreditos] = useState(null)
  const [filtro, setFiltro] = useState('PENDENTE')
  const [erro, setErro] = useState(null)

  useEffect(() => {
    creditoApi.listar().then(setCreditos).catch(setErro)
  }, [])

  const visiveis = useMemo(
    () => (creditos || []).filter((c) => filtro === 'TODOS' || c.status === filtro),
    [creditos, filtro],
  )
  const pendentes = (creditos || []).filter((c) => c.status === 'PENDENTE').length

  return (
    <div className="stack">
      <div>
        <h1>Pedidos de crédito</h1>
        <p className="muted">
          {pendentes === 1 ? '1 pedido aguardando análise.' : `${pendentes} pedidos aguardando análise.`}
        </p>
      </div>

      <div className="row" role="group" aria-label="Filtrar pedidos">
        {FILTROS.map((f) => (
          <button
            key={f.valor}
            type="button"
            className={filtro === f.valor ? 'btn btn-primary btn-sm' : 'btn btn-outline btn-sm'}
            aria-pressed={filtro === f.valor}
            onClick={() => setFiltro(f.valor)}
          >
            {f.rotulo}
          </button>
        ))}
      </div>

      <Erro erro={erro} />
      {creditos === null && !erro && <p className="muted">Carregando...</p>}
      {creditos && visiveis.length === 0 && <div className="card empty">Nenhum pedido de crédito nesta lista.</div>}

      {visiveis.length > 0 && (
        <div className="card table-wrap">
          <table>
            <thead>
              <tr>
                <th>Nº</th>
                <th>Cliente</th>
                <th>Valor pedido</th>
                <th>Diárias</th>
                <th>Recebido em</th>
                <th>Status</th>
                <th><span className="sr-only">Ações</span></th>
              </tr>
            </thead>
            <tbody>
              {visiveis.map((c) => (
                <tr key={c.id}>
                  <td>{c.id}</td>
                  <td>
                    {c.cliente.nome}
                    <div className="small muted">CPF {mascaraCpf(c.cliente.cpf)}</div>
                  </td>
                  <td>{moeda(c.valorSolicitado)}</td>
                  <td>{c.quantidadeDias}</td>
                  <td className="small">{dataHora(c.dataSolicitacao)}</td>
                  <td><StatusBadge status={c.status} tipo="credito" /></td>
                  <td>
                    <button type="button" className="btn btn-outline btn-sm" onClick={() => navegar(`/creditos/${c.id}`)}>
                      {c.status === 'PENDENTE' ? 'Analisar' : 'Ver detalhes'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
