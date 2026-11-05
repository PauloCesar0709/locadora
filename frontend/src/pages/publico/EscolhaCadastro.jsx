import { useNavigate } from 'react-router-dom'
import AuthFundo from '../../components/AuthFundo'

/** Primeiro passo do cadastro: cliente ou empresa (locadora/banco). */
export default function EscolhaCadastro() {
  const navegar = useNavigate()

  return (
    <AuthFundo>
      <div className="glass stack">
        <h1>Cadastre-se</h1>
        <p className="subtitulo">Escolha o tipo de conta.</p>

        <div className="escolha-tipo">
          <button type="button" className="btn btn-accent" onClick={() => navegar('/cadastro/cliente')}>
            Sou cliente
            <small>Quero alugar um veículo.</small>
          </button>
          <button type="button" className="btn btn-outline" onClick={() => navegar('/cadastro/empresa')}>
            Sou empresa
            <small>Locadora que oferece veículos ou banco que concede crédito.</small>
          </button>
        </div>

        <button type="button" className="btn btn-outline btn-block" onClick={() => navegar('/login')}>
          Já tenho cadastro
        </button>
      </div>
    </AuthFundo>
  )
}
