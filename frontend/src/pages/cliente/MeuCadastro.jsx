import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import ClienteForm from '../../components/ClienteForm'
import Erro from '../../components/Erro'
import { clienteApi } from '../../api/servicos'
import { useAuth } from '../../context/AuthContext'

/** Consultar, modificar e excluir o próprio cadastro (CRUD de cliente). */
export default function MeuCadastro() {
  const { usuario, sair, atualizarNome } = useAuth()
  const navegar = useNavigate()
  const [cliente, setCliente] = useState(null)
  const [erro, setErro] = useState(null)
  const [salvo, setSalvo] = useState(false)

  useEffect(() => {
    clienteApi.buscar(usuario.id).then(setCliente).catch(setErro)
  }, [usuario.id])

  async function atualizar(dados) {
    setSalvo(false)
    const atualizado = await clienteApi.atualizar(usuario.id, dados)
    setCliente(atualizado)
    atualizarNome(atualizado.nome)
    setSalvo(true)
  }

  async function excluir() {
    if (!window.confirm('Excluir definitivamente o seu cadastro?')) return
    setErro(null)
    try {
      await clienteApi.excluir(usuario.id)
      sair()
      navegar('/login', { state: { mensagem: 'Cadastro excluído com sucesso.' } })
    } catch (e) {
      setErro(e)
    }
  }

  return (
    <div className="stack">
      <div>
        <h1>Meu cadastro</h1>
        <p className="muted">Mantenha seus dados e rendimentos atualizados: o banco os usa na análise de crédito.</p>
      </div>

      <Erro erro={erro} />
      {salvo && <div className="alert alert-success" role="status">Dados atualizados com sucesso.</div>}

      {cliente && (
        <div className="card stack">
          <ClienteForm key={cliente.id + cliente.nome} modo="edicao" inicial={cliente} aoEnviar={atualizar} rotuloBotao="Salvar alterações" />
        </div>
      )}

      <div className="card stack">
        <h2>Excluir cadastro</h2>
        <p className="muted">Só é possível excluir se você não tiver locações vinculadas.</p>
        <div className="row">
          <button type="button" className="btn btn-danger" onClick={excluir}>Excluir meu cadastro</button>
        </div>
      </div>
    </div>
  )
}
