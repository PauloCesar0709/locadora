import { useNavigate } from 'react-router-dom'
import AuthFundo from '../../components/AuthFundo'
import EmpresaForm from '../../components/EmpresaForm'
import { agenteApi } from '../../api/servicos'

export default function CadastroEmpresa() {
  const navegar = useNavigate()

  async function cadastrar(dados) {
    await agenteApi.cadastrar(dados)
    navegar('/login', { state: { mensagem: 'Empresa cadastrada! Faça login para continuar.' } })
  }

  return (
    <AuthFundo centralizado>
      <div className="glass largo stack">
        <div className="stack-sm">
          <h1>Cadastro de empresa</h1>
          <p className="subtitulo">Locadoras e bancos têm áreas diferentes no sistema.</p>
        </div>
        <EmpresaForm modo="cadastro" aoEnviar={cadastrar} rotuloBotao="Cadastrar empresa" />
        <div className="row">
          <button type="button" className="btn btn-outline" onClick={() => navegar('/cadastro')}>
            Voltar
          </button>
        </div>
      </div>
    </AuthFundo>
  )
}
