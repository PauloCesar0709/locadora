import { useState } from 'react'
import Erro from './Erro'
import { mascaraCpf, somenteDigitos } from '../utils/format'

const MAX_RENDIMENTOS = 3

/**
 * Formulário de cliente usado no cadastro (modo "cadastro") e na edição (modo "edicao").
 * No cadastro, login e senha são obrigatórios; na edição, o login é fixo e a senha é opcional.
 */
export default function ClienteForm({ modo, inicial, aoEnviar, rotuloBotao }) {
  const edicao = modo === 'edicao'
  const [valores, setValores] = useState({
    login: inicial?.login || '',
    senha: '',
    nome: inicial?.nome || '',
    cpf: mascaraCpf(inicial?.cpf || ''),
    rg: inicial?.rg || '',
    endereco: inicial?.endereco || '',
    profissao: inicial?.profissao || '',
  })
  const [rendimentos, setRendimentos] = useState(
    (inicial?.rendimentos || []).map((r) => ({
      entidadeEmpregadora: r.entidadeEmpregadora,
      valor: String(r.valor),
    })),
  )
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  function alterar(campo, valor) {
    setValores((v) => ({ ...v, [campo]: valor }))
  }

  function alterarRendimento(indice, campo, valor) {
    setRendimentos((lista) => lista.map((r, i) => (i === indice ? { ...r, [campo]: valor } : r)))
  }

  function adicionarRendimento() {
    setRendimentos((lista) => [...lista, { entidadeEmpregadora: '', valor: '' }])
  }

  function removerRendimento(indice) {
    setRendimentos((lista) => lista.filter((_, i) => i !== indice))
  }

  async function enviar(evento) {
    evento.preventDefault()
    setErro(null)
    setEnviando(true)
    try {
      await aoEnviar({
        login: edicao ? undefined : valores.login.trim(),
        senha: valores.senha || undefined,
        nome: valores.nome.trim(),
        cpf: somenteDigitos(valores.cpf),
        rg: valores.rg.trim(),
        endereco: valores.endereco.trim(),
        profissao: valores.profissao.trim() || null,
        rendimentos: rendimentos.map((r) => ({
          entidadeEmpregadora: r.entidadeEmpregadora.trim(),
          valor: Number(r.valor),
        })),
      })
    } catch (e) {
      setErro(e)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <form className="stack" onSubmit={enviar}>
      <Erro erro={erro} />

      <div className="grid-2">
        <div className="field">
          <label className="label" htmlFor="login">Usuário</label>
          <input
            id="login"
            className="input"
            value={valores.login}
            onChange={(e) => alterar('login', e.target.value)}
            required={!edicao}
            disabled={edicao}
            maxLength={60}
            autoComplete="username"
          />
        </div>
        <div className="field">
          <label className="label" htmlFor="senha">{edicao ? 'Nova senha (opcional)' : 'Senha'}</label>
          <input
            id="senha"
            type="password"
            className="input"
            value={valores.senha}
            onChange={(e) => alterar('senha', e.target.value)}
            required={!edicao}
            minLength={6}
            maxLength={72}
            autoComplete="new-password"
          />
          <span className="hint">Mínimo de 6 caracteres.</span>
        </div>
      </div>

      <div className="field">
        <label className="label" htmlFor="nome">Nome completo</label>
        <input id="nome" className="input" value={valores.nome} onChange={(e) => alterar('nome', e.target.value)} required maxLength={120} />
      </div>

      <div className="grid-2">
        <div className="field">
          <label className="label" htmlFor="cpf">CPF</label>
          <input
            id="cpf"
            className="input"
            value={valores.cpf}
            onChange={(e) => alterar('cpf', mascaraCpf(e.target.value))}
            placeholder="000.000.000-00"
            inputMode="numeric"
            required
          />
        </div>
        <div className="field">
          <label className="label" htmlFor="rg">RG</label>
          <input id="rg" className="input" value={valores.rg} onChange={(e) => alterar('rg', e.target.value)} required maxLength={20} />
        </div>
      </div>

      <div className="field">
        <label className="label" htmlFor="endereco">Endereço</label>
        <input id="endereco" className="input" value={valores.endereco} onChange={(e) => alterar('endereco', e.target.value)} required maxLength={200} />
      </div>

      <div className="field">
        <label className="label" htmlFor="profissao">Profissão</label>
        <input id="profissao" className="input" value={valores.profissao} onChange={(e) => alterar('profissao', e.target.value)} maxLength={80} />
      </div>

      <fieldset>
        <legend>Rendimentos (máximo {MAX_RENDIMENTOS})</legend>
        {rendimentos.length === 0 && <p className="hint" style={{ marginBottom: 12 }}>Nenhum rendimento informado.</p>}
        {rendimentos.map((r, i) => (
          <div className="rendimento-row" key={i}>
            <div className="field">
              <label className="label" htmlFor={`emp-${i}`}>Entidade empregadora</label>
              <input
                id={`emp-${i}`}
                className="input"
                value={r.entidadeEmpregadora}
                onChange={(e) => alterarRendimento(i, 'entidadeEmpregadora', e.target.value)}
                required
                maxLength={120}
              />
            </div>
            <div className="field">
              <label className="label" htmlFor={`val-${i}`}>Rendimento mensal (R$)</label>
              <input
                id={`val-${i}`}
                className="input"
                type="number"
                min="0"
                step="0.01"
                value={r.valor}
                onChange={(e) => alterarRendimento(i, 'valor', e.target.value)}
                required
              />
            </div>
            <button type="button" className="btn btn-danger" onClick={() => removerRendimento(i)}>
              Remover
            </button>
          </div>
        ))}
        {rendimentos.length < MAX_RENDIMENTOS && (
          <button type="button" className="btn btn-outline" onClick={adicionarRendimento}>
            Adicionar rendimento
          </button>
        )}
      </fieldset>

      <div className="row">
        <button type="submit" className="btn btn-accent" disabled={enviando}>
          {enviando ? 'Salvando...' : rotuloBotao}
        </button>
      </div>
    </form>
  )
}
