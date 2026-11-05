package br.pucminas.aluguel.service;

import br.pucminas.aluguel.dto.PedidoAtualizacaoRequest;
import br.pucminas.aluguel.dto.PedidoRequest;
import br.pucminas.aluguel.dto.PedidoResponse;
import br.pucminas.aluguel.exception.AcessoNegadoException;
import br.pucminas.aluguel.exception.NegocioException;
import br.pucminas.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguel.model.Banco;
import br.pucminas.aluguel.model.Cliente;
import br.pucminas.aluguel.model.Contrato;
import br.pucminas.aluguel.model.ContratoCredito;
import br.pucminas.aluguel.model.FormaPagamento;
import br.pucminas.aluguel.model.OfertaLocacao;
import br.pucminas.aluguel.model.Pedido;
import br.pucminas.aluguel.model.StatusCredito;
import br.pucminas.aluguel.model.StatusPedido;
import br.pucminas.aluguel.repository.BancoRepository;
import br.pucminas.aluguel.repository.ClienteRepository;
import br.pucminas.aluguel.repository.ContratoCreditoRepository;
import br.pucminas.aluguel.repository.ContratoRepository;
import br.pucminas.aluguel.repository.OfertaLocacaoRepository;
import br.pucminas.aluguel.repository.PedidoRepository;
import br.pucminas.aluguel.security.UsuarioLogado;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fluxo da locação (lado do cliente).
 * 1. O cliente escolhe uma oferta e as datas; o total é dias x valor diário.
 * 2. Pagamento próprio: a locação é concluída na hora (gera o contrato).
 *    Crédito: o cliente escolhe um banco e o pedido aguarda a resposta do banco.
 * 3. Se o banco recusar, o cliente paga por conta própria, pede a outro banco ou cancela.
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final OfertaLocacaoRepository ofertaRepository;
    private final BancoRepository bancoRepository;
    private final ContratoCreditoRepository creditoRepository;
    private final ContratoRepository contratoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         OfertaLocacaoRepository ofertaRepository,
                         BancoRepository bancoRepository,
                         ContratoCreditoRepository creditoRepository,
                         ContratoRepository contratoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.ofertaRepository = ofertaRepository;
        this.bancoRepository = bancoRepository;
        this.creditoRepository = creditoRepository;
        this.contratoRepository = contratoRepository;
    }

    /** Cliente introduz o pedido de locação. */
    @Transactional
    public PedidoResponse criar(PedidoRequest request, UsuarioLogado logado) {
        Cliente cliente = clienteRepository.findById(logado.id())
                .orElseThrow(() -> new AcessoNegadoException("Apenas clientes fazem pedidos de locação."));
        OfertaLocacao oferta = ofertaRepository.findById(request.ofertaId())
                .orElseThrow(() -> new NegocioException("Oferta de locação não encontrada."));
        if (!oferta.isAtiva()) {
            throw new NegocioException("Esta oferta não está mais disponível.");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setOferta(oferta);
        pedido.setValorDiario(oferta.getValorDiario());
        aplicarPeriodo(pedido, request.dataInicio(), request.dataFim(), -1L);
        pedido.setFormaPagamento(request.formaPagamento());

        if (request.formaPagamento() == FormaPagamento.PROPRIO) {
            pedido.setStatus(StatusPedido.CONCLUIDO);
            pedidoRepository.save(pedido);
            gerarContrato(pedido, FormaPagamento.PROPRIO, null);
        } else {
            Banco banco = carregarBanco(request.bancoId());
            pedido.setStatus(StatusPedido.AGUARDANDO_CREDITO);
            pedidoRepository.save(pedido);
            criarPedidoCredito(pedido, banco);
        }
        return resposta(pedido);
    }

    /** Cliente vê os seus pedidos; locadora vê os pedidos das suas ofertas. */
    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(UsuarioLogado logado) {
        List<Pedido> pedidos;
        if (logado.isCliente()) {
            pedidos = pedidoRepository.findByClienteIdOrderByIdDesc(logado.id());
        } else if (logado.isLocadora()) {
            pedidos = pedidoRepository.findByOfertaLocadoraIdOrderByIdDesc(logado.id());
        } else {
            throw new AcessoNegadoException("Bancos consultam os pedidos de crédito na área de crédito.");
        }
        return pedidos.stream().map(this::resposta).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscar(Long id, UsuarioLogado logado) {
        Pedido pedido = carregar(id);
        boolean donoCliente = logado.isCliente() && pedido.getCliente().getId().equals(logado.id());
        boolean donaLocadora = logado.isLocadora() && pedido.getOferta().getLocadora().getId().equals(logado.id());
        if (!donoCliente && !donaLocadora) {
            throw new AcessoNegadoException("Você não tem acesso a esta locação.");
        }
        return resposta(pedido);
    }

    /** Modificar as datas: permitido enquanto aguarda o banco ou após uma recusa. */
    @Transactional
    public PedidoResponse atualizar(Long id, PedidoAtualizacaoRequest request, UsuarioLogado logado) {
        Pedido pedido = carregarDoCliente(id, logado);
        exigirStatus(pedido, "Só é possível alterar as datas enquanto o crédito não foi aprovado.",
                StatusPedido.AGUARDANDO_CREDITO, StatusPedido.CREDITO_RECUSADO);
        aplicarPeriodo(pedido, request.dataInicio(), request.dataFim(), pedido.getId());
        pedidoRepository.save(pedido);

        // O pedido de crédito pendente acompanha o novo valor.
        creditoRepository.findByPedidoIdAndStatus(pedido.getId(), StatusCredito.PENDENTE).ifPresent(c -> {
            c.setValorSolicitado(pedido.getValorTotal());
            c.setQuantidadeDias(pedido.getQuantidadeDias());
            creditoRepository.save(c);
        });
        return resposta(pedido);
    }

    /** Cancelar: permitido antes da data de início. */
    @Transactional
    public PedidoResponse cancelar(Long id, UsuarioLogado logado) {
        Pedido pedido = carregarDoCliente(id, logado);
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new NegocioException("Esta locação já está cancelada.");
        }
        if (!LocalDate.now().isBefore(pedido.getDataInicio())) {
            throw new NegocioException("Só é possível cancelar antes da data de início da locação.");
        }
        creditoRepository.findByPedidoIdAndStatus(pedido.getId(), StatusCredito.PENDENTE).ifPresent(c -> {
            c.setStatus(StatusCredito.CANCELADO);
            creditoRepository.save(c);
        });
        pedido.setStatus(StatusPedido.CANCELADO);
        return resposta(pedidoRepository.save(pedido));
    }

    /** Após recusa do banco: o próprio cliente assume o pagamento e a locação é concluída. */
    @Transactional
    public PedidoResponse pagarPorContaPropria(Long id, UsuarioLogado logado) {
        Pedido pedido = carregarDoCliente(id, logado);
        exigirStatus(pedido, "Esta ação só está disponível após a recusa do crédito.", StatusPedido.CREDITO_RECUSADO);
        exigirInicioNaoPassado(pedido);
        pedido.setFormaPagamento(FormaPagamento.PROPRIO);
        pedido.setStatus(StatusPedido.CONCLUIDO);
        pedidoRepository.save(pedido);
        gerarContrato(pedido, FormaPagamento.PROPRIO, null);
        return resposta(pedido);
    }

    /** Após recusa do banco: o cliente pede crédito a outro (ou ao mesmo) banco. */
    @Transactional
    public PedidoResponse solicitarNovoCredito(Long id, Long bancoId, UsuarioLogado logado) {
        Pedido pedido = carregarDoCliente(id, logado);
        exigirStatus(pedido, "Esta ação só está disponível após a recusa do crédito.", StatusPedido.CREDITO_RECUSADO);
        exigirInicioNaoPassado(pedido);
        Banco banco = carregarBanco(bancoId);
        pedido.setFormaPagamento(FormaPagamento.CREDITO);
        pedido.setStatus(StatusPedido.AGUARDANDO_CREDITO);
        pedidoRepository.save(pedido);
        criarPedidoCredito(pedido, banco);
        return resposta(pedido);
    }

    // ------------------------------------------------------------------ apoio

    /** Chamado também pelo CreditoService quando o banco aprova. */
    void gerarContrato(Pedido pedido, FormaPagamento forma, ContratoCredito credito) {
        Contrato contrato = new Contrato();
        contrato.setPedido(pedido);
        contrato.setFormaPagamento(forma);
        contrato.setContratoCredito(credito);
        contratoRepository.save(contrato);
    }

    private void criarPedidoCredito(Pedido pedido, Banco banco) {
        ContratoCredito credito = new ContratoCredito();
        credito.setPedido(pedido);
        credito.setBanco(banco);
        credito.setValorSolicitado(pedido.getValorTotal());
        credito.setQuantidadeDias(pedido.getQuantidadeDias());
        credito.setStatus(StatusCredito.PENDENTE);
        creditoRepository.save(credito);
    }

    /** Valida o período, verifica conflito de datas do veículo e calcula dias e total. */
    private void aplicarPeriodo(Pedido pedido, LocalDate inicio, LocalDate fim, Long idIgnorado) {
        if (!fim.isAfter(inicio)) {
            throw new NegocioException("A data de fim deve ser posterior à data de início.");
        }
        if (inicio.isBefore(LocalDate.now())) {
            throw new NegocioException("A data de início não pode estar no passado.");
        }
        Long automovelId = pedido.getOferta().getAutomovel().getId();
        if (pedidoRepository.existeConflito(automovelId, inicio, fim, idIgnorado, StatusPedido.CANCELADO)) {
            throw new NegocioException("O veículo já está reservado em parte deste período. Escolha outras datas.");
        }
        int dias = (int) ChronoUnit.DAYS.between(inicio, fim);
        pedido.setDataInicio(inicio);
        pedido.setDataFim(fim);
        pedido.setQuantidadeDias(dias);
        pedido.setValorTotal(pedido.getValorDiario().multiply(BigDecimal.valueOf(dias)));
    }

    private Banco carregarBanco(Long bancoId) {
        if (bancoId == null) {
            throw new NegocioException("Escolha o banco para o pedido de crédito.");
        }
        return bancoRepository.findById(bancoId)
                .orElseThrow(() -> new NegocioException("Banco não encontrado."));
    }

    private Pedido carregar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Locação não encontrada."));
    }

    private Pedido carregarDoCliente(Long id, UsuarioLogado logado) {
        Pedido pedido = carregar(id);
        if (!logado.isCliente() || !pedido.getCliente().getId().equals(logado.id())) {
            throw new AcessoNegadoException("Você não tem acesso a esta locação.");
        }
        return pedido;
    }

    private static void exigirStatus(Pedido pedido, String mensagem, StatusPedido... permitidos) {
        for (StatusPedido s : permitidos) {
            if (pedido.getStatus() == s) {
                return;
            }
        }
        throw new NegocioException(mensagem);
    }

    private static void exigirInicioNaoPassado(Pedido pedido) {
        if (pedido.getDataInicio().isBefore(LocalDate.now())) {
            throw new NegocioException("A data de início já passou. Altere as datas antes de continuar.");
        }
    }

    PedidoResponse resposta(Pedido pedido) {
        Contrato contrato = contratoRepository.findByPedidoId(pedido.getId()).orElse(null);
        List<ContratoCredito> creditos = creditoRepository.findByPedidoIdOrderByIdAsc(pedido.getId());
        return Mapeador.pedido(pedido, contrato, creditos);
    }
}
