import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout'
import RotaProtegida from './components/RotaProtegida'
import { useAuth } from './context/AuthContext'
import { INICIO_POR_PERFIL } from './utils/format'

import Login from './pages/publico/Login'
import EscolhaCadastro from './pages/publico/EscolhaCadastro'
import CadastroCliente from './pages/publico/CadastroCliente'
import CadastroEmpresa from './pages/publico/CadastroEmpresa'

import Locacoes from './pages/comum/Locacoes'
import LocacaoDetalhe from './pages/comum/LocacaoDetalhe'
import MinhaEmpresa from './pages/comum/MinhaEmpresa'

import NovaLocacao from './pages/cliente/NovaLocacao'
import MeuCadastro from './pages/cliente/MeuCadastro'

import Ofertas from './pages/locadora/Ofertas'
import Veiculos from './pages/locadora/Veiculos'

import Creditos from './pages/banco/Creditos'
import CreditoDetalhe from './pages/banco/CreditoDetalhe'

/** Envia cada usuário para o início do seu hub (ou para o login). */
function Inicio() {
  const { autenticado, usuario } = useAuth()
  return <Navigate to={autenticado ? INICIO_POR_PERFIL[usuario.perfil] : '/login'} replace />
}

export default function App() {
  return (
    <Routes>
      {/* Públicas */}
      <Route path="/login" element={<Login />} />
      <Route path="/cadastro" element={<EscolhaCadastro />} />
      <Route path="/cadastro/cliente" element={<CadastroCliente />} />
      <Route path="/cadastro/empresa" element={<CadastroEmpresa />} />

      {/* Exigem login; cada grupo só abre para os perfis indicados */}
      <Route element={<RotaProtegida />}>
        <Route element={<Layout />}>
          <Route element={<RotaProtegida perfis={['CLIENTE']} />}>
            <Route path="/locacoes/nova" element={<NovaLocacao />} />
            <Route path="/meu-cadastro" element={<MeuCadastro />} />
          </Route>

          <Route element={<RotaProtegida perfis={['CLIENTE', 'LOCADORA']} />}>
            <Route path="/locacoes" element={<Locacoes />} />
            <Route path="/locacoes/:id" element={<LocacaoDetalhe />} />
          </Route>

          <Route element={<RotaProtegida perfis={['LOCADORA']} />}>
            <Route path="/ofertas" element={<Ofertas />} />
            <Route path="/veiculos" element={<Veiculos />} />
          </Route>

          <Route element={<RotaProtegida perfis={['BANCO']} />}>
            <Route path="/creditos" element={<Creditos />} />
            <Route path="/creditos/:id" element={<CreditoDetalhe />} />
          </Route>

          <Route element={<RotaProtegida perfis={['LOCADORA', 'BANCO']} />}>
            <Route path="/minha-empresa" element={<MinhaEmpresa />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<Inicio />} />
    </Routes>
  )
}
