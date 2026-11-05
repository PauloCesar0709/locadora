import { useState } from 'react'
import Erro from './Erro'
import { mascaraCnpj, somenteDigitos } from '../utils/format'

/**
 * Formulário de empresa.
 * modo "cadastro": escolhe o tipo (Locadora ou Banco) e define usuário e senha.
 * modo "edicao": o tipo e o usuário são fixos; a senha é opcional.
 */
export default function EmpresaForm({ modo, inicial, aoEnviar, rotuloBotao }) {
  const edicao = modo === 'edicao'
  const [valores, setValores] = useState({
    tipo: inicial?.perfil || 'LOCADORA',
    login: inicial?.login || '',
    senha: '',
    nome: inicial?.nome || '',
    cnpj: mascaraCnpj(inicial?.cnpj || ''),
    endereco: inicial?.endereco || '',
    telefone: inicial?.telefone || '',
    codigoBanco: inicial?.codigoBanco || '',
  })
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)
  const ehBanco = valores.tipo === 'BANCO'

  function alterar(campo, valor) {
    setValores((v) => ({ ...v, [campo]: valor }))
  }

  async function enviar(evento) {
    evento.preventDefault()
    setErro(null)
    setEnviando(true)
    try {
      const dados = {
        nome: valores.nome.trim(),
        cnpj: somenteDigitos(valores.cnpj),
        endereco: valores.endereco.trim() || null,
        telefone: somenteDigitos(valores.telefone) || null,
        codigoBanco: ehBanco ? valores.codigoBanco.trim() : null,
        senha: valores.senha || null,
      }
      if (!edicao) {
        dados.tipo = valores.tipo
        dados.login = valores.login.trim()
      }
      await aoEnviar(dados)
      if (edicao) setValores((v) => ({ ...v, senha: '' }))
    } catch (e) {
      setErro(e)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <form className="stack" onSubmit={enviar}>
      <Erro erro={erro} />

      {!edicao && (
        <fieldset>
          <legend>Tipo de empresa</legend>
          <div className="opcoes">
            <label className="opcao">
              <input type="radio" name="tipo" value="LOCADORA" checked={valores.tipo === 'LOCADORA'} onChange={() => alterar('tipo', 'LOCADORA')} />
              <span>
                <strong>Locadora</strong>
                <span className="small">Cadastra veículos e oferece locações.</span>
              </span>
            </label>
            <label className="opcao">
              <input type="radio" name="tipo" value="BANCO" checked={valores.tipo === 'BANCO'} onChange={() => alterar('tipo', 'BANCO')} />
              <span>
                <strong>Banco</strong>
                <span className="small">Analisa e concede crédito aos clientes.</span>
              </span>
            </label>
          </div>
        </fieldset>
      )}

      <div className="grid-2">
        <div className="field">
          <label className="label" htmlFor="e-login">Usuário</label>
          <input id="e-login" className="input" value={valores.login} onChange={(e) => alterar('login', e.target.value)} required={!edicao} disabled={edicao} maxLength={60} autoComplete="username" />
        </div>
        <div className="field">
          <label className="label" htmlFor="e-senha">{edicao ? 'Nova senha (opcional)' : 'Senha'}</label>
          <input id="e-senha" type="password" className="input" value={valores.senha} onChange={(e) => alterar('senha', e.target.value)} required={!edicao} minLength={6} maxLength={72} autoComplete="new-password" />
          <span className="hint">Mínimo de 6 caracteres.</span>
        </div>
      </div>

      <div className="field">
        <label className="label" htmlFor="e-nome">Razão social</label>
        <input id="e-nome" className="input" value={valores.nome} onChange={(e) => alterar('nome', e.target.value)} required maxLength={120} />
      </div>

      <div className="grid-2">
        <div className="field">
          <label className="label" htmlFor="e-cnpj">CNPJ</label>
          <input id="e-cnpj" className="input" value={valores.cnpj} onChange={(e) => alterar('cnpj', mascaraCnpj(e.target.value))} placeholder="00.000.000/0000-00" inputMode="numeric" required />
        </div>
        {ehBanco && (
          <div className="field">
            <label className="label" htmlFor="e-codigo">Código do banco</label>
            <input id="e-codigo" className="input" value={valores.codigoBanco} onChange={(e) => alterar('codigoBanco', e.target.value)} required maxLength={10} />
          </div>
        )}
        <div className="field">
          <label className="label" htmlFor="e-telefone">Telefone</label>
          <input id="e-telefone" className="input" value={valores.telefone} onChange={(e) => alterar('telefone', e.target.value)} inputMode="tel" maxLength={20} />
        </div>
      </div>

      <div className="field">
        <label className="label" htmlFor="e-endereco">Endereço</label>
        <input id="e-endereco" className="input" value={valores.endereco} onChange={(e) => alterar('endereco', e.target.value)} maxLength={200} />
      </div>

      <div className="row">
        <button type="submit" className="btn btn-accent" disabled={enviando}>
          {enviando ? 'Salvando...' : rotuloBotao}
        </button>
      </div>
    </form>
  )
}
