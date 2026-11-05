import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { creditoApi } from '../../api/servicos'
import StatusBadge from '../../components/StatusBadge'
import Erro from '../../components/Erro'
import { data, dataHora, mascaraCpf, moeda, percentual, totalComJuros } from '../../utils/format'

/**
 * Análise de um pedido de crédito pelo banco:
 * dados do cliente e da locação, taxa de juros diária e decisão (aceitar ou recusar).
 */
export default function CreditoDetalhe() {
  const { id } = useParams()
  const navegar = useNavigate()
  const [credito, setCredito] = useState(null)
  const [taxa, setTaxa] = useState('')
  const [motivo, setMotivo] = useState('')
  const [recusando, setRecusando] = useState(false)
  const [erro, setErro] = useState(null)
  const [ocupado, setOcupado] = useState(false)

  useEffect(() => {
    creditoApi.buscar(id).then(setCredito).catch(setErro)
  }, [id])

  async function executar(acao) {
    setErro(null)
    setOcupado(true)
    try {
      setCredito(await acao())
      setRecusando(false)
    } catch (e) {
      setErro(e)
    } finally {
      setOcupado(false)
    }
  }

  if (!credito) return erro ? <Erro erro={erro} /> : <p className="muted">Carregando...</p>

  const cliente = credito.cliente
  const rendaTotal = cliente.rendimentos.reduce((soma, r) => soma + Number(r.valor), 0)
  const pendente = credito.status === 'PENDENTE'
  const taxaValida = taxa !== '' && Number(taxa) >= 0 && Number(taxa) <= 100
  const previsao = taxaValida ? totalComJuros(credito.valorSolicitado, taxa, credito.quantidadeDias) : null

  return (
    <div className="stack">
      <div className="row-between">
        <div>
          <h1>Pedido de crédito nº {credito.id}</h1>
          <p className="muted">Recebido em {dataHora(credito.dataSolicitacao)}</p>
        </div>
        <StatusBadge status={credito.status} tipo="credito" />
      </div>

      <Erro erro={erro} />

      <div className="card stack">
        <h2>Valor pedido</h2>
        <dl className="resumo">
          <div className="resumo-item destaque"><dt>Valor do crédito</dt><dd>{moeda(credito.valorSolicitado)}</dd></div>
          <div className="resumo-item"><dt>Prazo (diárias)</dt><dd>{credito.quantidadeDias}</dd></div>
          <div className="resumo-item"><dt>Renda mensal declarada</dt><dd>{moeda(rendaTotal)}</dd></div>
        </dl>
        <dl className="details">
          <div><dt>Veículo</dt><dd>{credito.veiculo}</dd></div>
          <div><dt>Locadora</dt><dd>{credito.locadoraNome}</dd></div>
          <div><dt>Período da locação</dt><dd>{data(credito.dataInicio)} a {data(credito.dataFim)}</dd></div>
        </dl>
      </div>

      <div className="card stack">
        <h2>Dados do cliente</h2>
        <dl className="details">
          <div><dt>Nome</dt><dd>{cliente.nome}</dd></div>
          <div><dt>CPF</dt><dd>{mascaraCpf(cliente.cpf)}</dd></div>
          <div><dt>RG</dt><dd>{cliente.rg}</dd></div>
          <div><dt>Profissão</dt><dd>{cliente.profissao || '-'}</dd></div>
          <div><dt>Endereço</dt><dd>{cliente.endereco}</dd></div>
        </dl>
        <h3>Rendimentos</h3>
        {cliente.rendimentos.length === 0 ? (
          <p className="muted">Nenhum rendimento informado.</p>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr><th>Entidade empregadora</th><th>Rendimento mensal</th></tr>
              </thead>
              <tbody>
                {cliente.rendimentos.map((r, i) => (
                  <tr key={i}><td>{r.entidadeEmpregadora}</td><td>{moeda(r.valor)}</td></tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* ---------- Resultado ---------- */}
      {credito.status === 'APROVADO' && (
        <div className="card stack">
          <h2>Crédito concedido</h2>
          <dl className="resumo">
            <div className="resumo-item"><dt>Taxa de juros</dt><dd>{percentual(credito.taxaJurosDiaria)}</dd></div>
            <div className="resumo-item destaque"><dt>Total com juros</dt><dd>{moeda(credito.valorTotalComJuros)}</dd></div>
          </dl>
          <p className="small muted">Respondido em {dataHora(credito.dataResposta)}. A locação foi concluída.</p>
        </div>
      )}
      {credito.status === 'RECUSADO' && (
        <div className="alert alert-error">
          Crédito recusado em {dataHora(credito.dataResposta)}.{credito.motivoRecusa ? ` Motivo: ${credito.motivoRecusa}` : ''}
        </div>
      )}
      {credito.status === 'CANCELADO' && <div className="alert alert-info">O cliente cancelou a locação antes da sua resposta.</div>}

      {/* ---------- Decisão ---------- */}
      {pendente && !recusando && (
        <form
          className="card stack"
          onSubmit={(e) => {
            e.preventDefault()
            executar(() => creditoApi.aprovar(credito.id, Number(taxa)))
          }}
        >
          <h2>Conceder crédito</h2>
          <div className="field" style={{ maxWidth: 320 }}>
            <label className="label" htmlFor="taxa">Taxa de juros diária (%)</label>
            <input id="taxa" type="number" min="0" max="100" step="0.0001" className="input" value={taxa} onChange={(e) => setTaxa(e.target.value)} placeholder="Ex.: 0,5" required />
            <span className="hint">Juros simples sobre o valor emprestado: valor x (1 + taxa x dias).</span>
          </div>
          {previsao !== null && (
            <dl className="resumo">
              <div className="resumo-item"><dt>Juros no período</dt><dd>{moeda(previsao - Number(credito.valorSolicitado))}</dd></div>
              <div className="resumo-item destaque"><dt>Total a pagar ao banco</dt><dd>{moeda(previsao)}</dd></div>
            </dl>
          )}
          <div className="row">
            <button type="submit" className="btn btn-accent" disabled={ocupado}>Aceitar e conceder crédito</button>
            <button type="button" className="btn btn-danger" onClick={() => setRecusando(true)}>Recusar</button>
          </div>
        </form>
      )}

      {pendente && recusando && (
        <form
          className="card stack"
          onSubmit={(e) => {
            e.preventDefault()
            executar(() => creditoApi.recusar(credito.id, motivo))
          }}
        >
          <h2>Recusar crédito</h2>
          <div className="field">
            <label className="label" htmlFor="motivo">Motivo (opcional, o cliente verá)</label>
            <textarea id="motivo" className="input" value={motivo} onChange={(e) => setMotivo(e.target.value)} maxLength={500} />
          </div>
          <div className="row">
            <button type="submit" className="btn btn-danger" disabled={ocupado}>Confirmar recusa</button>
            <button type="button" className="btn btn-outline" onClick={() => setRecusando(false)}>Voltar</button>
          </div>
        </form>
      )}

      <div className="row">
        <button type="button" className="btn btn-outline" onClick={() => navegar('/creditos')}>Voltar para a lista</button>
      </div>
    </div>
  )
}
