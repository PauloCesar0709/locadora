package br.pucminas.aluguel.service;

import br.pucminas.aluguel.dto.AgenteAtualizacaoRequest;
import br.pucminas.aluguel.dto.AgenteCadastroRequest;
import br.pucminas.aluguel.dto.AgenteResponse;
import br.pucminas.aluguel.dto.BancoResumo;
import br.pucminas.aluguel.exception.NegocioException;
import br.pucminas.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguel.model.Agente;
import br.pucminas.aluguel.model.Banco;
import br.pucminas.aluguel.model.Locadora;
import br.pucminas.aluguel.model.Perfil;
import br.pucminas.aluguel.repository.AgenteRepository;
import br.pucminas.aluguel.repository.BancoRepository;
import br.pucminas.aluguel.repository.UsuarioRepository;
import br.pucminas.aluguel.security.UsuarioLogado;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cadastro e dados das empresas (locadoras e bancos). */
@Service
public class AgenteService {

    private final AgenteRepository agenteRepository;
    private final BancoRepository bancoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AgenteService(AgenteRepository agenteRepository,
                         BancoRepository bancoRepository,
                         UsuarioRepository usuarioRepository,
                         PasswordEncoder passwordEncoder) {
        this.agenteRepository = agenteRepository;
        this.bancoRepository = bancoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Cadastro público de empresa: o tipo define se é Locadora ou Banco. */
    @Transactional
    public AgenteResponse cadastrar(AgenteCadastroRequest request) {
        if (request.tipo() == Perfil.CLIENTE) {
            throw new NegocioException("Para cadastrar um cliente, use o cadastro de cliente.");
        }
        String login = request.login().trim();
        if (usuarioRepository.existsByLogin(login)) {
            throw new NegocioException("Já existe um usuário com este login.");
        }
        if (agenteRepository.existsByCnpj(request.cnpj())) {
            throw new NegocioException("Já existe uma empresa com este CNPJ.");
        }

        Agente agente;
        if (request.tipo() == Perfil.BANCO) {
            exigirCodigoBanco(request.codigoBanco());
            Banco banco = new Banco();
            banco.setCodigoBanco(request.codigoBanco().trim());
            agente = banco;
        } else {
            agente = new Locadora();
        }
        agente.setLogin(login);
        agente.setSenha(passwordEncoder.encode(request.senha()));
        agente.setNome(request.nome().trim());
        agente.setCnpj(request.cnpj());
        agente.setEndereco(textoOuNulo(request.endereco()));
        agente.setTelefone(textoOuNulo(request.telefone()));
        return Mapeador.agente(agenteRepository.save(agente));
    }

    /** "Minhas informações" da empresa logada. */
    @Transactional(readOnly = true)
    public AgenteResponse buscarLogado(UsuarioLogado logado) {
        return Mapeador.agente(carregar(logado.id()));
    }

    @Transactional
    public AgenteResponse atualizarLogado(AgenteAtualizacaoRequest request, UsuarioLogado logado) {
        Agente agente = carregar(logado.id());
        if (agenteRepository.existsByCnpjAndIdNot(request.cnpj(), agente.getId())) {
            throw new NegocioException("Já existe outra empresa com este CNPJ.");
        }
        if (agente instanceof Banco banco) {
            exigirCodigoBanco(request.codigoBanco());
            banco.setCodigoBanco(request.codigoBanco().trim());
        }
        agente.setNome(request.nome().trim());
        agente.setCnpj(request.cnpj());
        agente.setEndereco(textoOuNulo(request.endereco()));
        agente.setTelefone(textoOuNulo(request.telefone()));
        if (request.senha() != null && !request.senha().isBlank()) {
            agente.setSenha(passwordEncoder.encode(request.senha()));
        }
        return Mapeador.agente(agenteRepository.save(agente));
    }

    /** Bancos disponíveis para o cliente pedir crédito. */
    @Transactional(readOnly = true)
    public List<BancoResumo> listarBancos() {
        return bancoRepository.findAllByOrderByNomeAsc().stream()
                .map(b -> new BancoResumo(b.getId(), b.getNome(), b.getCodigoBanco()))
                .toList();
    }

    private Agente carregar(Long id) {
        return agenteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada."));
    }

    private static void exigirCodigoBanco(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new NegocioException("Informe o código do banco.");
        }
    }

    private static String textoOuNulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
