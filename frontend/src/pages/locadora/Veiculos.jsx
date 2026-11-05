import { useCallback, useEffect, useState } from 'react'
import { agenteApi, automovelApi } from '../../api/servicos'
import Erro from '../../components/Erro'
import { mascaraCpf, PERFIL, somenteDigitos } from '../../utils/format'

const VAZIO = { matricula: '', ano: '', marca: '', modelo: '', placa: '', tipoProprietario: 'LOCADORA', bancoId: '', clienteCpf: '' }

/** Veículos da locadora: cadastrar, editar e excluir. */
export default function Veiculos() {
  const [veiculos, setVeiculos] = useState(null)
  const [bancos, setBancos] = useState([])
  const [form, setForm] = useState(VAZIO)
  const [editandoId, setEditandoId] = useState(null)
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  const carregar = useCallback(() => {
    automovelApi.listar().then(setVeiculos).catch(setErro)
  }, [])

  useEffect(() => {
    carregar()
    agenteApi.listarBancos().then(setBancos).catch(setErro)
  }, [carregar])

  function alterar(campo, valor) {
    setForm((f) => ({ ...f, [campo]: valor }))
  }

  function editar(v) {
    setEditandoId(v.id)
    setForm({
      matricula: v.matricula,
      ano: String(v.ano),
      marca: v.marca,
      modelo: v.modelo,
      placa: v.placa,
      tipoProprietario: v.proprietarioPerfil,
      bancoId: v.proprietarioPerfil === 'BANCO' ? String(v.proprietarioId) : '',
      clienteCpf: '',
    })
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  function limpar() {
    setEditandoId(null)
    setForm(VAZIO)
  }

  async function salvar(evento) {
    evento.preventDefault()
    setErro(null)
    setEnviando(true)
    const dados = {
      matricula: form.matricula.trim(),
      ano: Number(form.ano),
      marca: form.marca.trim(),
      modelo: form.modelo.trim(),
      placa: form.placa.trim(),
      tipoProprietario: form.tipoProprietario,
      bancoId: form.tipoProprietario === 'BANCO' ? Number(form.bancoId) : null,
      clienteCpf: form.tipoProprietario === 'CLIENTE' ? somenteDigitos(form.clienteCpf) : null,
    }
    try {
      if (editandoId) await automovelApi.atualizar(editandoId, dados)
      else await automovelApi.criar(dados)
      limpar()
      carregar()
    } catch (e) {
      setErro(e)
    } finally {
      setEnviando(false)
    }
  }

  async function excluir(v) {
    if (!window.confirm(`Excluir o veículo ${v.marca} ${v.modelo} (${v.placa})?`)) return
    setErro(null)
    try {
      await automovelApi.excluir(v.id)
      carregar()
    } catch (e) {
      setErro(e)
    }
  }

  return (
    <div className="stack">
      <div>
        <h1>Veículos</h1>
        <p className="muted">Cadastre os veículos que a locadora vai oferecer para locação.</p>
      </div>

      <Erro erro={erro} />

      <form className="card stack" onSubmit={salvar}>
        <h2>{editandoId ? 'Editar veículo' : 'Novo veículo'}</h2>
        <div className="grid-2">
          <div className="field">
            <label className="label" htmlFor="v-marca">Marca</label>
            <input id="v-marca" className="input" value={form.marca} onChange={(e) => alterar('marca', e.target.value)} required maxLength={60} />
          </div>
          <div className="field">
            <label className="label" htmlFor="v-modelo">Modelo</label>
            <input id="v-modelo" className="input" value={form.modelo} onChange={(e) => alterar('modelo', e.target.value)} required maxLength={60} />
          </div>
          <div className="field">
            <label className="label" htmlFor="v-ano">Ano</label>
            <input id="v-ano" type="number" min="1900" max="2100" className="input" value={form.ano} onChange={(e) => alterar('ano', e.target.value)} required />
          </div>
          <div className="field">
            <label className="label" htmlFor="v-placa">Placa</label>
            <input id="v-placa" className="input" value={form.placa} onChange={(e) => alterar('placa', e.target.value.toUpperCase())} required maxLength={7} pattern="[A-Za-z0-9]{7}" title="7 caracteres (letras e números)" />
          </div>
          <div className="field">
            <label className="label" htmlFor="v-matricula">Matrícula</label>
            <input id="v-matricula" className="input" value={form.matricula} onChange={(e) => alterar('matricula', e.target.value)} required maxLength={30} />
          </div>
          <div className="field">
            <label className="label" htmlFor="v-prop">Proprietário</label>
            <select id="v-prop" className="input" value={form.tipoProprietario} onChange={(e) => alterar('tipoProprietario', e.target.value)}>
              <option value="LOCADORA">Esta locadora</option>
              <option value="BANCO">Um banco</option>
              <option value="CLIENTE">Um cliente</option>
            </select>
          </div>
          {form.tipoProprietario === 'BANCO' && (
            <div className="field">
              <label className="label" htmlFor="v-banco">Banco proprietário</label>
              <select id="v-banco" className="input" value={form.bancoId} onChange={(e) => alterar('bancoId', e.target.value)} required>
                <option value="">Selecione...</option>
                {bancos.map((b) => (
                  <option key={b.id} value={b.id}>{b.nome}</option>
                ))}
              </select>
            </div>
          )}
          {form.tipoProprietario === 'CLIENTE' && (
            <div className="field">
              <label className="label" htmlFor="v-cpf">CPF do cliente proprietário</label>
              <input id="v-cpf" className="input" value={form.clienteCpf} onChange={(e) => alterar('clienteCpf', mascaraCpf(e.target.value))} placeholder="000.000.000-00" inputMode="numeric" required />
            </div>
          )}
        </div>
        <div className="row">
          <button type="submit" className="btn btn-accent" disabled={enviando}>
            {editandoId ? 'Salvar alterações' : 'Cadastrar veículo'}
          </button>
          {editandoId && (
            <button type="button" className="btn btn-outline" onClick={limpar}>Cancelar edição</button>
          )}
        </div>
      </form>

      {veiculos === null && !erro && <p className="muted">Carregando...</p>}
      {veiculos && veiculos.length === 0 && <div className="card empty">Nenhum veículo cadastrado.</div>}

      {veiculos && veiculos.length > 0 && (
        <div className="card table-wrap">
          <table>
            <thead>
              <tr>
                <th>Veículo</th>
                <th>Ano</th>
                <th>Placa</th>
                <th>Matrícula</th>
                <th>Proprietário</th>
                <th><span className="sr-only">Ações</span></th>
              </tr>
            </thead>
            <tbody>
              {veiculos.map((v) => (
                <tr key={v.id}>
                  <td>{v.marca} {v.modelo}</td>
                  <td>{v.ano}</td>
                  <td>{v.placa}</td>
                  <td>{v.matricula}</td>
                  <td>{v.proprietarioNome} <span className="small muted">({PERFIL[v.proprietarioPerfil]})</span></td>
                  <td>
                    <div className="row">
                      <button type="button" className="btn btn-outline btn-sm" onClick={() => editar(v)}>Editar</button>
                      <button type="button" className="btn btn-danger btn-sm" onClick={() => excluir(v)}>Excluir</button>
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
