package br.pucminas.aluguel.service;

import br.pucminas.aluguel.dto.AprovacaoCreditoRequest;
import br.pucminas.aluguel.dto.CreditoResponse;
import br.pucminas.aluguel.dto.RecusaCreditoRequest;
import br.pucminas.aluguel.exception.AcessoNegadoException;
import br.pucminas.aluguel.exception.NegocioException;
import br.pucminas.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguel.model.ContratoCredito;
import br.pucminas.aluguel.model.FormaPagamento;
import br.pucminas.aluguel.model.Pedido;
import br.pucminas.aluguel.model.StatusCredito;
import br.pucminas.aluguel.model.StatusPedido;
import br.pucminas.aluguel.repository.ContratoCreditoRepository;
import br.pucminas.aluguel.repository.PedidoRepository;
import br.pucminas.aluguel.security.UsuarioLogado;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hub do banco: analisa os pedidos de crédito feitos a ele.
 * Aprovar exige a taxa de juros diária (juros simples) e conclui a locação.
 * Recusar devolve a decisão ao cliente.
 */
@Service
public class CreditoService {

    private final ContratoCreditoRepository creditoRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;

    public CreditoService(ContratoCreditoRepository creditoRepository,
                          PedidoRepository pedidoRepository,
                          PedidoService pedidoService) {
        this.creditoRepository = creditoRepository;
        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
    }

    @Transactional(readOnly = true)
    public List<CreditoResponse> listar(UsuarioLogado logado) {
        return creditoRepository.findByBancoIdOrderByIdDesc(logado.id()).stream()
                .map(Mapeador::credito).toList();
    }

    @Transactional(readOnly = true)
    public CreditoResponse buscar(Long id, UsuarioLogado logado) {
        return Mapeador.credito(carregarDoBanco(id, logado));
    }

    @Transactional
    public CreditoResponse aprovar(Long id, AprovacaoCreditoRequest request, UsuarioLogado logado) {
        ContratoCredito credito = carregarPendente(id, logado);
        Pedido pedido = credito.getPedido();

        credito.setTaxaJurosDiaria(request.taxaJurosDiaria());
        credito.setValorTotalComJuros(ContratoCredito.calcularTotalComJuros(
                credito.getValorSolicitado(), request.taxaJurosDiaria(), credito.getQuantidadeDias()));
        credito.setStatus(StatusCredito.APROVADO);
        credito.setDataResposta(LocalDateTime.now());
        creditoRepository.save(credito);

        pedido.setStatus(StatusPedido.CONCLUIDO);
        pedido.setFormaPagamento(FormaPagamento.CREDITO);
        pedidoRepository.save(pedido);
        pedidoService.gerarContrato(pedido, FormaPagamento.CREDITO, credito);
        return Mapeador.credito(credito);
    }

    @Transactional
    public CreditoResponse recusar(Long id, RecusaCreditoRequest request, UsuarioLogado logado) {
        ContratoCredito credito = carregarPendente(id, logado);
        String motivo = request == null || request.motivo() == null || request.motivo().isBlank()
                ? null : request.motivo().trim();
        credito.setMotivoRecusa(motivo);
        credito.setStatus(StatusCredito.RECUSADO);
        credito.setDataResposta(LocalDateTime.now());
        creditoRepository.save(credito);

        Pedido pedido = credito.getPedido();
        pedido.setStatus(StatusPedido.CREDITO_RECUSADO);
        pedidoRepository.save(pedido);
        return Mapeador.credito(credito);
    }

    private ContratoCredito carregarDoBanco(Long id, UsuarioLogado logado) {
        ContratoCredito credito = creditoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido de crédito não encontrado."));
        if (!credito.getBanco().getId().equals(logado.id())) {
            throw new AcessoNegadoException("Este pedido de crédito foi feito a outro banco.");
        }
        return credito;
    }

    private ContratoCredito carregarPendente(Long id, UsuarioLogado logado) {
        ContratoCredito credito = carregarDoBanco(id, logado);
        if (credito.getStatus() != StatusCredito.PENDENTE
                || credito.getPedido().getStatus() != StatusPedido.AGUARDANDO_CREDITO) {
            throw new NegocioException("Este pedido de crédito já foi respondido ou cancelado.");
        }
        return credito;
    }
}
