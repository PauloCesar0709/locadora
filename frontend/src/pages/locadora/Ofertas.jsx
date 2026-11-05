import { useCallback, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { automovelApi, ofertaApi } from '../../api/servicos'
import Erro from '../../components/Erro'
import { moeda } from '../../utils/format'

/** Ofertas de locação: a locadora define um valor fixo diário para cada veículo. */
export default function Ofertas() {
  const navegar = useNavigate()
  const [ofertas, setOfertas] = useState(null)
  const [veiculos, setVeiculos] = useState([])
  const [automovelId, setAutomovelId] = useState('')
  const [valorDiario, setValorDiario] = useState('')
  const [editando, setEditando] = useState(null)
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  const carregar = useCallback(() => {
    ofertaApi.listar().then(setOfertas).catch(setErro)
    automovelApi.listar().then(setVeiculos).catch(setErro)
  }, [])

  useEffect(() => {
    carregar()
  }, [carregar])

  /** Só aparecem no formulário os veículos sem oferta ativa. */
  const veiculosLivres = useMemo(() => {
    const comOferta = new Set((ofertas || []).filter((o) => o.ativa).map((o) => o.automovel.id))
    return veiculos.filter((v) => !comOferta.has(v.id))
  }, [ofertas, veiculos])

  function limpar() {
    setEditando(null)
    setAutomovelId('')
    setValorDiario('')
  }

  function editar(o) {
    setEditando(o)
    setAutomovelId(String(o.automovel.id))
    setValorDiario(String(o.valorDiario))
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  async function executar(acao) {
    setErro(null)
    setEnviando(true)
    try {
      await acao()
      limpar()
      carregar()
    } catch (e) {
      setErro(e)
    } finally {
      setEnviando(false)
    }
  }

  function salvar(evento) {
    evento.preventDefault()
    if (editando) {
      executar(() => ofertaApi.atualizar(editando.id, { valorDiario: Number(valorDiario), ativa: editando.ativa }))
    } else {
      executar(() => ofertaApi.criar({ automovelId: Number(automovelId), valorDiario: Number(valorDiario) }))
    }
  }

  function alternarAtiva(o) {
    executar(() => ofertaApi.atualizar(o.id, { valorDiario: Number(o.valorDiario), ativa: !o.ativa }))
  }

  function excluir(o) {
    if (!window.confirm(`Excluir a oferta do ${o.automovel.marca} ${o.automovel.modelo}?`)) return
    executar(() => ofertaApi.excluir(o.id))
  }

  return (
    <div className="stack">
      <div className="row-between">
        <div>
          <h1>Ofertas de locação</h1>
          <p className="muted">Os clientes veem as ofertas ativas e escolhem as datas da locação.</p>
        </div>
        <button type="button" className="btn btn-outline" onClick={() => navegar('/veiculos')}>
          Gerenciar veículos
        </button>
      </div>

      <Erro erro={erro} />

      <form className="card stack" onSubmit={salvar}>
        <h2>{editando ? 'Editar valor diário' : 'Nova oferta'}</h2>
        <div className="grid-2">
          <div className="field">
            <label className="label" htmlFor="o-veiculo">Veículo</label>
            {editando ? (
              <input id="o-veiculo" className="input" value={`${editando.automovel.marca} ${editando.automovel.modelo} · ${editando.automovel.placa}`} disabled />
            ) : (
              <select id="o-veiculo" className="input" value={automovelId} onChange={(e) => setAutomovelId(e.target.value)} required>
                <option value="">Selecione...</option>
                {veiculosLivres.map((v) => (
                  <option key={v.id} value={v.id}>{v.marca} {v.modelo} ({v.ano}) · {v.placa}</option>
                ))}
              </select>
            )}
            {!editando && veiculosLivres.length === 0 && (
              <span className="hint">Todos os veículos já têm oferta ativa. Cadastre um novo veículo.</span>
            )}
          </div>
          <div className="field">
            <label className="label" htmlFor="o-valor">Valor fixo diário (R$)</label>
            <input id="o-valor" type="number" min="0.01" step="0.01" className="input" value={valorDiario} onChange={(e) => setValorDiario(e.target.value)} required />
          </div>
        </div>
        {editando && <p className="hint">Locações já feitas mantêm o valor em que foram contratadas.</p>}
        <div className="row">
          <button type="submit" className="btn btn-accent" disabled={enviando}>
            {editando ? 'Salvar valor' : 'Criar oferta'}
          </button>
          {editando && <button type="button" className="btn btn-outline" onClick={limpar}>Cancelar edição</button>}
        </div>
      </form>

      {ofertas === null && !erro && <p className="muted">Carregando...</p>}
      {ofertas && ofertas.length === 0 && <div className="card empty">Nenhuma oferta criada ainda.</div>}

      {ofertas && ofertas.length > 0 && (
        <div className="card table-wrap">
          <table>
            <thead>
              <tr>
                <th>Veículo</th>
                <th>Placa</th>
                <th>Valor diário</th>
                <th>Situação</th>
                <th><span className="sr-only">Ações</span></th>
              </tr>
            </thead>
            <tbody>
              {ofertas.map((o) => (
                <tr key={o.id}>
                  <td>{o.automovel.marca} {o.automovel.modelo} <span className="small muted">({o.automovel.ano})</span></td>
                  <td>{o.automovel.placa}</td>
                  <td>{moeda(o.valorDiario)}</td>
                  <td>
                    <span className={o.ativa ? 'badge badge-success' : 'badge badge-neutral'}>{o.ativa ? 'Ativa' : 'Inativa'}</span>
                  </td>
                  <td>
                    <div className="row">
                      <button type="button" className="btn btn-outline btn-sm" onClick={() => editar(o)} disabled={enviando}>Editar valor</button>
                      <button type="button" className="btn btn-outline btn-sm" onClick={() => alternarAtiva(o)} disabled={enviando}>
                        {o.ativa ? 'Desativar' : 'Ativar'}
                      </button>
                      <button type="button" className="btn btn-danger btn-sm" onClick={() => excluir(o)} disabled={enviando}>Excluir</button>
                    </div>
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
