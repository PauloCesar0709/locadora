import { useCallback, useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { agenteApi, pedidoApi } from '../../api/servicos'
import { useAuth } from '../../context/AuthContext'
import StatusBadge from '../../components/StatusBadge'
import Erro from '../../components/Erro'
import { data, dataHora, diasEntre, FORMA_PAGAMENTO, hojeIso, moeda, percentual } from '../../utils/format'

/** Detalhe da locação. Cliente: ações conforme o status. Locadora: somente consulta. */
export default function LocacaoDetalhe() {
  const { id } = useParams()
  const { ehCliente } = useAuth()
  const navegar = useNavigate()
  const [pedido, setPedido] = useState(null)
  const [bancos, setBancos] = useState([])
  const [erro, setErro] = useState(null)
  const [ocupado, setOcupado] = useState(false)
  const [editando, setEditando] = useState(false)
  const [datas, setDatas] = useState({ inicio: '', fim: '' })
  const [novoBanco, setNovoBanco] = useState('')

  const carregar = useCallback(() => {
    pedidoApi.buscar(id).then(setPedido).catch(setErro)
  }, [id])

  useEffect(() => {
    carregar()
    if (ehCliente) agenteApi.listarBancos().then(setBancos).catch(() => {})
  }, [carregar, ehCliente])

  async function executar(acao) {
    setErro(null)
    setOcupado(true)
    try {
      setPedido(await acao())
      setEditando(false)
    } catch (e) {
      setErro(e)
    } finally {
      setOcupado(false)
    }
  }

  if (!pedido) return erro ? <Erro erro={erro} /> : <p className="muted">Carregando...</p>

  const { status } = pedido
  const veiculo = pedido.oferta.automovel
  const antesDoInicio = hojeIso() < pedido.dataInicio
  const podeModificar = ehCliente && (status === 'AGUARDANDO_CREDITO' || status === 'CREDITO_RECUSADO')
  const podeCancelar = ehCliente && status !== 'CANCELADO' && antesDoInicio
  const diasEdicao = diasEntre(datas.inicio, datas.fim)

  function abrirEdicao() {
    setDatas({ inicio: pedido.dataInicio, fim: pedido.dataFim })
    setEditando(true)
  }

  return (
    <div className="stack">
      <div className="row-between">
        <div>
          <h1>Locação nº {pedido.id}</h1>
          <p className="muted">Pedido feito em {dataHora(pedido.dataCriacao)}</p>
        </div>
        <StatusBadge status={status} />
      </div>

      {status === 'AGUARDANDO_CREDITO' && (
        <div className="alert alert-info">O pedido de crédito está com o banco. A locação será concluída quando ele aprovar.</div>
      )}
      {status === 'CREDITO_RECUSADO' && ehCliente && (
        <div className="alert alert-warning">O banco recusou o crédito. Você pode pagar por conta própria, pedir a outro banco ou cancelar.</div>
      )}

      <Erro erro={erro} />

      <div className="card stack">
        <h2>Dados da locação</h2>
        <dl className="details">
          {!ehCliente && <div><dt>Cliente</dt><dd>{pedido.clienteNome}</dd></div>}
          <div><dt>Veículo</dt><dd>{veiculo.marca} {veiculo.modelo} ({veiculo.ano})</dd></div>
          <div><dt>Placa</dt><dd>{veiculo.placa}</dd></div>
          <div><dt>Locadora</dt><dd>{pedido.oferta.locadoraNome}</dd></div>
          <div><dt>Período</dt><dd>{data(pedido.dataInicio)} a {data(pedido.dataFim)}</dd></div>
          <div><dt>Forma de pagamento</dt><dd>{FORMA_PAGAMENTO[pedido.formaPagamento]}</dd></div>
          {pedido.contratoExecutadoEm && <div><dt>Contrato gerado em</dt><dd>{dataHora(pedido.contratoExecutadoEm)}</dd></div>}
        </dl>
        <dl className="resumo">
          <div className="resumo-item"><dt>Diárias</dt><dd>{pedido.quantidadeDias}</dd></div>
          <div className="resumo-item"><dt>Valor diário</dt><dd>{moeda(pedido.valorDiario)}</dd></div>
          <div className="resumo-item destaque"><dt>Valor total</dt><dd>{moeda(pedido.valorTotal)}</dd></div>
        </dl>
      </div>

      {pedido.creditos.length > 0 && (
        <div className="card stack">
          <h2>Pedidos de crédito</h2>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Banco</th>
                  <th>Valor pedido</th>
                  <th>Taxa</th>
                  <th>Total com juros</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {pedido.creditos.map((c) => (
                  <tr key={c.id}>
                    <td>{c.bancoNome}</td>
                    <td>{moeda(c.valorSolicitado)}</td>
                    <td>{percentual(c.taxaJurosDiaria)}</td>
                    <td>{moeda(c.valorTotalComJuros)}</td>
                    <td>
                      <StatusBadge status={c.status} tipo="credito" />
                      {c.motivoRecusa && <div className="small muted">Motivo: {c.motivoRecusa}</div>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* ---------- Modificar datas ---------- */}
      {editando && (
        <form
          className="card stack"
          onSubmit={(e) => {
            e.preventDefault()
            executar(() => pedidoApi.atualizar(pedido.id, { dataInicio: datas.inicio, dataFim: datas.fim }))
          }}
        >
          <h2>Modificar datas</h2>
          <div className="grid-2">
            <div className="field">
              <label className="label" htmlFor="m-inicio">Data de início</label>
              <input id="m-inicio" type="date" className="input" min={hojeIso()} value={datas.inicio} onChange={(e) => setDatas((d) => ({ ...d, inicio: e.target.value }))} required />
            </div>
            <div className="field">
              <label className="label" htmlFor="m-fim">Data de fim</label>
              <input id="m-fim" type="date" className="input" min={datas.inicio || hojeIso()} value={datas.fim} onChange={(e) => setDatas((d) => ({ ...d, fim: e.target.value }))} required />
            </div>
          </div>
          {diasEdicao > 0 && (
            <p className="muted">Novo total: {diasEdicao} diária(s) x {moeda(pedido.valorDiario)} = <strong>{moeda(diasEdicao * Number(pedido.valorDiario))}</strong></p>
          )}
          <div className="row">
            <button type="submit" className="btn btn-accent" disabled={ocupado}>Salvar datas</button>
            <button type="button" className="btn btn-outline" onClick={() => setEditando(false)}>Voltar</button>
          </div>
        </form>
      )}

      {/* ---------- Após recusa: pagar ou pedir a outro banco ---------- */}
      {ehCliente && status === 'CREDITO_RECUSADO' && !editando && (
        <div className="card stack">
          <h2>Como deseja continuar?</h2>
          <div className="row">
            <button
              type="button"
              className="btn btn-accent"
              disabled={ocupado}
              onClick={() => executar(() => pedidoApi.pagarPorContaPropria(pedido.id))}
            >
              Pagar por conta própria
            </button>
          </div>
          <form
            className="row"
            onSubmit={(e) => {
              e.preventDefault()
              executar(() => pedidoApi.solicitarCredito(pedido.id, Number(novoBanco)))
            }}
          >
            <div className="field" style={{ flex: '1 1 260px' }}>
              <label className="label" htmlFor="novo-banco">Pedir crédito a outro banco</label>
              <select id="novo-banco" className="input" value={novoBanco} onChange={(e) => setNovoBanco(e.target.value)} required>
                <option value="">Selecione o banco...</option>
                {bancos.map((b) => (
                  <option key={b.id} value={b.id}>{b.nome}</option>
                ))}
              </select>
            </div>
            <button type="submit" className="btn btn-primary" disabled={ocupado} style={{ alignSelf: 'flex-end' }}>
              Enviar pedido de crédito
            </button>
          </form>
        </div>
      )}

      {/* ---------- Ações gerais ---------- */}
      <div className="row">
        <button type="button" className="btn btn-outline" onClick={() => navegar('/locacoes')}>Voltar</button>
        {podeModificar && !editando && (
          <button type="button" className="btn btn-primary" onClick={abrirEdicao}>Modificar datas</button>
        )}
        {podeCancelar && (
          <button
            type="button"
            className="btn btn-danger"
            disabled={ocupado}
            onClick={() => {
              if (window.confirm('Deseja realmente cancelar esta locação?')) executar(() => pedidoApi.cancelar(pedido.id))
            }}
          >
            Cancelar locação
          </button>
        )}
      </div>
    </div>
  )
}
