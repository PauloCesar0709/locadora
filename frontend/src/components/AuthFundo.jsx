import fundo from '../assets/fundo-login.jpg'
import Logo, { NOME_SISTEMA } from './Logo'

/**
 * Fundo das telas públicas (login e cadastro), no estilo do template:
 * imagem desfocada na tela inteira, quadro arredondado com a imagem nítida
 * e o conteúdo (cartão de vidro) por cima.
 */
export default function AuthFundo({ children, centralizado = false }) {
  return (
    <div className="auth-page" style={{ '--fundo': `url(${fundo})` }}>
      <div className={centralizado ? 'auth-frame centro' : 'auth-frame'}>
        <div className="auth-marca">
          <Logo tamanho={40} />
          {NOME_SISTEMA}
        </div>
        {children}
      </div>
    </div>
  )
}
