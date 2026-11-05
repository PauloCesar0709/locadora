import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { agenteApi, ofertaApi, pedidoApi } from '../../api/servicos'
import Erro from '../../components/Erro'
import IconeCarro from '../../components/IconeCarro'
import { data, diasEntre, hojeIso, moeda } from '../../utils/format'

/**
 * Fluxo de locação do cliente:
 * 1. escolhe uma oferta (veículo + valor diário);
 * 2. informa início e fim; o total (dias x valor diário) aparece na hora;
 * 3. escolhe pagar por conta própria (conclui na hora) ou pedir crédito a um banco.
 */
export default function NovaLocacao() {
  const navegar = useNavigate()
  const [ofertas, setOfertas] = useState(null)
  const [bancos, setBancos] = useState([])
  const [oferta, setOferta] = useState(null)
  const [dataInicio, setDataInicio] = useState('')
  const [dataFim, setDataFim] = useState('')
  const [formaPagamento, setFormaPagamento] = useState('PROPRIO')
  const [bancoId, setBancoId] = useState('')
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    ofertaApi.listar().then(setOfertas).catch(setErro)
    agenteApi.listarBancos().then(setBancos).catch(setErro)
  }, [])

  const dias = useMemo(() => diasEntre(dataInicio, dataFim), [dataInicio, dataFim])
  const periodoValido = dataInicio && dataFim && dias > 0 && dataInicio >= hojeIso()
  const total = oferta && periodoValido ? Number(oferta.valorDiario) * dias : null

  function escolher(o) {
    setOferta(o)
    setErro(null)
    setTimeout(() => document.getElementById('etapa-datas')?.scrollIntoView({ behavior: 'smooth' }), 0)
  }

  async function confirmar(evento) {
    evento.preventDefault()
    setErro(null)
    setEnviando(true)
    try {
      const pedido = await pedidoApi.criar({
        ofertaId: oferta.id,
        dataInicio,
        dataFim,
        formaPagamento,
        bancoId: formaPagamento === 'CREDITO' ? Number(bancoId) : null,
      })
      navegar(`/locacoes/${pedido.id}`)
    } catch (e) {
      setErro(e)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="stack">
      <div>
        <h1>Nova locação</h1>
        <p className="muted">Escolha um veículo, o período e a forma de pagamento.</p>
      </div>

      <section className="stack" aria-labelledby="etapa-1">
        <h2 id="etapa-1">1. Escolha a oferta</h2>
        {ofertas === null && !erro && <p className="muted">Carregando ofertas...</p>}
        {ofertas && ofertas.length === 0 && <div className="card empty">Nenhuma oferta disponível no momento.</div>}
        <div className="grid-cards">
          {ofertas?.map((o) => (
            <article key={o.id} className={oferta?.id === o.id ? 'oferta-card selecionada' : 'oferta-card'}>
              <div className="oferta-icone"><IconeCarro /></div>
              <div>
                <h3>{o.automovel.marca} {o.automovel.modelo}</h3>
                <p className="small muted">{o.automovel.ano} · {o.locadoraNome}</p>
              </div>
              <p className="oferta-preco">{moeda(o.valorDiario)} <span>/ dia</span></p>
              <button
                type="button"
                className={oferta?.id === o.id ? 'btn btn-primary' : 'btn btn-outline'}
                onClick={() => escolher(o)}
                aria-pressed={oferta?.id === o.id}
              >
                {oferta?.id === o.id ? 'Selecionada' : 'Escolher'}
              </button>
            </article>
          ))}
        </div>
      </section>

      {oferta && (
        <form className="stack" onSubmit={confirmar}>
          <section id="etapa-datas" className="card stack" aria-labelledby="etapa-2">
            <h2 id="etapa-2">2. Período da locação</h2>
            <p className="muted small">
              {oferta.automovel.marca} {oferta.automovel.modelo} · {moeda(oferta.valorDiario)} por dia
            </p>
            <div className="grid-2">
              <div className="field">
                <label className="label" htmlFor="inicio">Data de início</label>
                <input id="inicio" type="date" className="input" min={hojeIso()} value={dataInicio} onChange={(e) => setDataInicio(e.target.value)} required />
              </div>
              <div className="field">
                <label className="label" htmlFor="fim">Data de fim (devolução)</label>
                <input id="fim" type="date" className="input" min={dataInicio || hojeIso()} value={dataFim} onChange={(e) => setDataFim(e.target.value)} required />
              </div>
            </div>
            {dataInicio && dataFim && !periodoValido && (
              <div className="alert alert-warning">A data de fim deve ser posterior à de início, e o início não pode estar no passado.</div>
            )}
            {periodoValido && (
              <dl className="resumo">
                <div className="resumo-item"><dt>Diárias</dt><dd>{dias}</dd></div>
                <div className="resumo-item"><dt>Valor diário</dt><dd>{moeda(oferta.valorDiario)}</dd></div>
                <div className="resumo-item destaque"><dt>Valor total</dt><dd>{moeda(total)}</dd></div>
              </dl>
            )}
          </section>

          {periodoValido && (
            <section className="card stack" aria-labelledby="etapa-3">
              <h2 id="etapa-3">3. Forma de pagamento</h2>
              <div className="opcoes" role="radiogroup" aria-labelledby="etapa-3">
                <label className="opcao">
                  <input type="radio" name="pagamento" checked={formaPagamento === 'PROPRIO'} onChange={() => setFormaPagamento('PROPRIO')} />
                  <span>
                    <strong>Eu mesmo pago</strong>
                    <span className="small muted">A locação é concluída imediatamente.</span>
                  </span>
                </label>
                <label className="opcao">
                  <input type="radio" name="pagamento" checked={formaPagamento === 'CREDITO'} onChange={() => setFormaPagamento('CREDITO')} />
                  <span>
                    <strong>Pedir contrato de crédito</strong>
                    <span className="small muted">Um banco analisa o pedido e define a taxa de juros.</span>
                  </span>
                </label>
              </div>

              {formaPagamento === 'CREDITO' && (
                <div className="field">
                  <label className="label" htmlFor="banco">Banco</label>
                  <select id="banco" className="input" value={bancoId} onChange={(e) => setBancoId(e.target.value)} required>
                    <option value="">Selecione o banco...</option>
                    {bancos.map((b) => (
                      <option key={b.id} value={b.id}>{b.nome} (código {b.codigoBanco})</option>
                    ))}
                  </select>
                  {bancos.length === 0 && <span className="hint">Nenhum banco cadastrado no momento.</span>}
                </div>
              )}

              <Erro erro={erro} />

              <p className="small muted">
                Resumo: {oferta.automovel.marca} {oferta.automovel.modelo}, de {data(dataInicio)} a {data(dataFim)}, total de {moeda(total)}.
              </p>
              <div className="row">
                <button type="submit" className="btn btn-accent" disabled={enviando}>
                  {enviando ? 'Enviando...' : formaPagamento === 'PROPRIO' ? 'Concluir locação' : 'Enviar pedido de crédito'}
                </button>
                <button type="button" className="btn btn-outline" onClick={() => navegar('/locacoes')}>
                  Cancelar
                </button>
              </div>
            </section>
          )}
        </form>
      )}

      {!oferta && <Erro erro={erro} />}
    </div>
  )
}
