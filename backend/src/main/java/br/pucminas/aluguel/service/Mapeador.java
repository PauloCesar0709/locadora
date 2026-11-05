package br.pucminas.aluguel.service;

import br.pucminas.aluguel.dto.AgenteResponse;
import br.pucminas.aluguel.dto.AutomovelResponse;
import br.pucminas.aluguel.dto.ClienteResponse;
import br.pucminas.aluguel.dto.CreditoResponse;
import br.pucminas.aluguel.dto.OfertaResponse;
import br.pucminas.aluguel.dto.PedidoResponse;
import br.pucminas.aluguel.dto.RendimentoDto;
import br.pucminas.aluguel.model.Agente;
import br.pucminas.aluguel.model.Automovel;
import br.pucminas.aluguel.model.Banco;
import br.pucminas.aluguel.model.Cliente;
import br.pucminas.aluguel.model.Contrato;
import br.pucminas.aluguel.model.ContratoCredito;
import br.pucminas.aluguel.model.OfertaLocacao;
import br.pucminas.aluguel.model.Pedido;
import br.pucminas.aluguel.model.Usuario;
import java.util.List;

/**
 * Converte entidades em DTOs de resposta.
 * Deve ser chamado DENTRO de um método @Transactional, porque lê relacionamentos LAZY.
 */
final class Mapeador {

    private Mapeador() {
    }

    static ClienteResponse cliente(Cliente c) {
        List<RendimentoDto> rendimentos = c.getRendimentos().stream()
                .map(r -> new RendimentoDto(r.getEntidadeEmpregadora(), r.getValor()))
                .toList();
        return new ClienteResponse(c.getId(), c.getLogin(), c.getNome(), c.getCpf(), c.getRg(),
                c.getEndereco(), c.getProfissao(), rendimentos);
    }

    static AgenteResponse agente(Agente a) {
        String codigoBanco = a instanceof Banco b ? b.getCodigoBanco() : null;
        return new AgenteResponse(a.getId(), a.getLogin(), a.getNome(), a.getPerfil(), a.getCnpj(),
                a.getEndereco(), a.getTelefone(), codigoBanco);
    }

    static AutomovelResponse automovel(Automovel a) {
        Usuario p = a.getProprietario();
        return new AutomovelResponse(a.getId(), a.getMatricula(), a.getAno(), a.getMarca(), a.getModelo(),
                a.getPlaca(), p.getId(), p.getNome(), p.getPerfil());
    }

    static OfertaResponse oferta(OfertaLocacao o) {
        return new OfertaResponse(o.getId(), o.getLocadora().getId(), o.getLocadora().getNome(),
                automovel(o.getAutomovel()), o.getValorDiario(), o.isAtiva());
    }

    static CreditoResponse credito(ContratoCredito c) {
        Pedido p = c.getPedido();
        Automovel a = p.getOferta().getAutomovel();
        String veiculo = a.getMarca() + " " + a.getModelo() + " (" + a.getAno() + ") · " + a.getPlaca();
        return new CreditoResponse(
                c.getId(), c.getStatus(), p.getId(),
                c.getBanco().getId(), c.getBanco().getNome(),
                c.getValorSolicitado(), c.getQuantidadeDias(),
                c.getTaxaJurosDiaria(), c.getValorTotalComJuros(), c.getMotivoRecusa(),
                c.getDataSolicitacao(), c.getDataResposta(),
                veiculo, p.getOferta().getLocadora().getNome(),
                p.getDataInicio(), p.getDataFim(),
                cliente(p.getCliente()));
    }

    static PedidoResponse pedido(Pedido p, Contrato contrato, List<ContratoCredito> creditos) {
        return new PedidoResponse(
                p.getId(), p.getStatus(), p.getFormaPagamento(),
                p.getCliente().getId(), p.getCliente().getNome(),
                oferta(p.getOferta()),
                p.getDataInicio(), p.getDataFim(), p.getQuantidadeDias(),
                p.getValorDiario(), p.getValorTotal(), p.getDataCriacao(),
                contrato != null ? contrato.getDataExecucao() : null,
                creditos.stream().map(Mapeador::credito).toList());
    }
}
