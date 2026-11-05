package br.pucminas.aluguel.service;

import br.pucminas.aluguel.dto.ClienteRequest;
import br.pucminas.aluguel.dto.ClienteResponse;
import br.pucminas.aluguel.dto.RendimentoDto;
import br.pucminas.aluguel.exception.AcessoNegadoException;
import br.pucminas.aluguel.exception.NegocioException;
import br.pucminas.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguel.model.Cliente;
import br.pucminas.aluguel.model.Rendimento;
import br.pucminas.aluguel.repository.AutomovelRepository;
import br.pucminas.aluguel.repository.ClienteRepository;
import br.pucminas.aluguel.repository.PedidoRepository;
import br.pucminas.aluguel.repository.UsuarioRepository;
import br.pucminas.aluguel.security.UsuarioLogado;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** CRUD de cliente. O cliente só acessa o próprio cadastro. */
@Service
public class ClienteService {

    private static final int MAX_RENDIMENTOS = 3;

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final AutomovelRepository automovelRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository,
                          UsuarioRepository usuarioRepository,
                          PedidoRepository pedidoRepository,
                          AutomovelRepository automovelRepository,
                          PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.automovelRepository = automovelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** CREATE: cadastro prévio (público). */
    @Transactional
    public ClienteResponse cadastrar(ClienteRequest request) {
        if (request.login() == null || request.login().isBlank()) {
            throw new NegocioException("Informe o usuário (login).");
        }
        if (request.senha() == null || request.senha().isBlank()) {
            throw new NegocioException("Informe a senha.");
        }
        String login = request.login().trim();
        if (usuarioRepository.existsByLogin(login)) {
            throw new NegocioException("Já existe um usuário com este login.");
        }
        if (clienteRepository.existsByCpf(request.cpf())) {
            throw new NegocioException("Já existe um cliente com este CPF.");
        }

        Cliente cliente = new Cliente();
        cliente.setLogin(login);
        cliente.setSenha(passwordEncoder.encode(request.senha()));
        preencher(cliente, request);
        return Mapeador.cliente(clienteRepository.save(cliente));
    }

    /** READ: somente o próprio cliente. */
    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id, UsuarioLogado logado) {
        exigirProprioCliente(id, logado, "consultar");
        return Mapeador.cliente(carregar(id));
    }

    /** UPDATE: somente o próprio cliente. Senha em branco mantém a atual; o login não muda. */
    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest request, UsuarioLogado logado) {
        exigirProprioCliente(id, logado, "alterar");
        Cliente cliente = carregar(id);

        if (clienteRepository.existsByCpfAndIdNot(request.cpf(), id)) {
            throw new NegocioException("Já existe um cliente com este CPF.");
        }
        if (request.senha() != null && !request.senha().isBlank()) {
            cliente.setSenha(passwordEncoder.encode(request.senha()));
        }
        preencher(cliente, request);
        return Mapeador.cliente(clienteRepository.save(cliente));
    }

    /** DELETE: somente o próprio cliente, sem locações ou veículos vinculados. */
    @Transactional
    public void excluir(Long id, UsuarioLogado logado) {
        exigirProprioCliente(id, logado, "excluir");
        Cliente cliente = carregar(id);

        if (pedidoRepository.existsByClienteId(id)) {
            throw new NegocioException("Não é possível excluir o cadastro: existem locações vinculadas.");
        }
        if (automovelRepository.existsByProprietarioId(id)) {
            throw new NegocioException("Não é possível excluir o cadastro: existem veículos em seu nome.");
        }
        clienteRepository.delete(cliente);
    }

    private Cliente carregar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
    }

    private void exigirProprioCliente(Long id, UsuarioLogado logado, String acao) {
        if (!logado.isCliente() || !logado.id().equals(id)) {
            throw new AcessoNegadoException("Você só pode " + acao + " o seu próprio cadastro.");
        }
    }

    private void preencher(Cliente cliente, ClienteRequest request) {
        List<RendimentoDto> rendimentos = request.rendimentos() == null ? List.of() : request.rendimentos();
        if (rendimentos.size() > MAX_RENDIMENTOS) {
            throw new NegocioException("Informe no máximo " + MAX_RENDIMENTOS + " rendimentos.");
        }
        cliente.setNome(request.nome().trim());
        cliente.setCpf(request.cpf());
        cliente.setRg(request.rg().trim());
        cliente.setEndereco(request.endereco().trim());
        cliente.setProfissao(request.profissao() == null || request.profissao().isBlank() ? null : request.profissao().trim());

        cliente.getRendimentos().clear();
        for (RendimentoDto dto : rendimentos) {
            cliente.getRendimentos().add(new Rendimento(cliente, dto.entidadeEmpregadora().trim(), dto.valor()));
        }
    }
}
