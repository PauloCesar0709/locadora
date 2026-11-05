import { useNavigate } from 'react-router-dom'
import AuthFundo from '../../components/AuthFundo'
import ClienteForm from '../../components/ClienteForm'
import { clienteApi } from '../../api/servicos'

export default function CadastroCliente() {
  const navegar = useNavigate()

  async function cadastrar(dados) {
    await clienteApi.cadastrar(dados)
    navegar('/login', { state: { mensagem: 'Cadastro realizado! Faça login para continuar.' } })
  }

  return (
    <AuthFundo centralizado>
      <div className="glass largo stack">
        <div className="stack-sm">
          <h1>Cadastro de cliente</h1>
          <p className="subtitulo">Informe seus dados e até 3 rendimentos.</p>
        </div>
        <ClienteForm modo="cadastro" aoEnviar={cadastrar} rotuloBotao="Cadastrar" />
        <div className="row">
          <button type="button" className="btn btn-outline" onClick={() => navegar('/cadastro')}>
            Voltar
          </button>
        </div>
      </div>
    </AuthFundo>
  )
}
