import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { INICIO_POR_PERFIL, PERFIL } from '../utils/format'
import Logo, { NOME_SISTEMA } from './Logo'

/** Itens do menu de cada hub. */
const MENUS = {
  CLIENTE: [
    { para: '/locacoes', rotulo: 'Minhas locações' },
    { para: '/locacoes/nova', rotulo: 'Nova locação' },
    { para: '/meu-cadastro', rotulo: 'Meu cadastro' },
  ],
  LOCADORA: [
    { para: '/ofertas', rotulo: 'Ofertas de locação' },
    { para: '/veiculos', rotulo: 'Veículos' },
    { para: '/locacoes', rotulo: 'Locações' },
    { para: '/minha-empresa', rotulo: 'Minhas informações' },
  ],
  BANCO: [
    { para: '/creditos', rotulo: 'Pedidos de crédito' },
    { para: '/minha-empresa', rotulo: 'Minhas informações' },
  ],
}

export default function Layout() {
  const { usuario, sair } = useAuth()
  const navegar = useNavigate()

  function aoSair() {
    sair()
    navegar('/login')
  }

  const classe = ({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')

  return (
    <>
      <header className="navbar">
        <div className="navbar-inner">
          <NavLink to={INICIO_POR_PERFIL[usuario.perfil]} className="brand">
            <Logo tamanho={30} />
            {NOME_SISTEMA}
            <span className="brand-tag">{PERFIL[usuario.perfil]}</span>
          </NavLink>
          <nav className="nav-links" aria-label="Principal">
            {MENUS[usuario.perfil].map((item) => (
              <NavLink key={item.para} to={item.para} end className={classe}>
                {item.rotulo}
              </NavLink>
            ))}
            <span className="nav-user">{usuario.nome}</span>
            <button type="button" className="btn btn-accent btn-sm" onClick={aoSair}>
              Sair
            </button>
          </nav>
        </div>
      </header>
      <main className="container">
        <Outlet />
      </main>
    </>
  )
}
