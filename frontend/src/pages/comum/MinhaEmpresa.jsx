import { useEffect, useState } from 'react'
import EmpresaForm from '../../components/EmpresaForm'
import Erro from '../../components/Erro'
import { agenteApi } from '../../api/servicos'
import { useAuth } from '../../context/AuthContext'
import { mascaraCnpj, PERFIL } from '../../utils/format'

/** "Minhas informações" da locadora ou do banco. */
export default function MinhaEmpresa() {
  const { atualizarNome } = useAuth()
  const [empresa, setEmpresa] = useState(null)
  const [erro, setErro] = useState(null)
  const [salvo, setSalvo] = useState(false)

  useEffect(() => {
    agenteApi.minhasInformacoes().then(setEmpresa).catch(setErro)
  }, [])

  async function atualizar(dados) {
    setSalvo(false)
    const atualizada = await agenteApi.atualizar(dados)
    setEmpresa(atualizada)
    atualizarNome(atualizada.nome)
    setSalvo(true)
  }

  if (!empresa) return erro ? <Erro erro={erro} /> : <p className="muted">Carregando...</p>

  return (
    <div className="stack">
      <div>
        <h1>Minhas informações</h1>
        <p className="muted">Dados cadastrais da empresa.</p>
      </div>

      <div className="card stack">
        <dl className="details">
          <div><dt>Razão social</dt><dd>{empresa.nome}</dd></div>
          <div><dt>Tipo</dt><dd>{PERFIL[empresa.perfil]}</dd></div>
          <div><dt>CNPJ</dt><dd>{mascaraCnpj(empresa.cnpj)}</dd></div>
          {empresa.codigoBanco && <div><dt>Código do banco</dt><dd>{empresa.codigoBanco}</dd></div>}
          <div><dt>Usuário</dt><dd>{empresa.login}</dd></div>
          <div><dt>Telefone</dt><dd>{empresa.telefone || '-'}</dd></div>
          <div><dt>Endereço</dt><dd>{empresa.endereco || '-'}</dd></div>
        </dl>
      </div>

      {salvo && <div className="alert alert-success" role="status">Informações atualizadas com sucesso.</div>}

      <div className="card stack">
        <h2>Editar informações</h2>
        <EmpresaForm key={empresa.nome + empresa.cnpj} modo="edicao" inicial={empresa} aoEnviar={atualizar} rotuloBotao="Salvar alterações" />
      </div>
    </div>
  )
}
