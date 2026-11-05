package br.pucminas.aluguel.service;

import br.pucminas.aluguel.dto.OfertaAtualizacaoRequest;
import br.pucminas.aluguel.dto.OfertaRequest;
import br.pucminas.aluguel.dto.OfertaResponse;
import br.pucminas.aluguel.exception.AcessoNegadoException;
import br.pucminas.aluguel.exception.NegocioException;
import br.pucminas.aluguel.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguel.model.Automovel;
import br.pucminas.aluguel.model.Locadora;
import br.pucminas.aluguel.model.OfertaLocacao;
import br.pucminas.aluguel.repository.AutomovelRepository;
import br.pucminas.aluguel.repository.LocadoraRepository;
import br.pucminas.aluguel.repository.OfertaLocacaoRepository;
import br.pucminas.aluguel.repository.PedidoRepository;
import br.pucminas.aluguel.security.UsuarioLogado;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ofertas de locação: a locadora oferece um veículo com valor fixo diário.
 * Regra: no máximo uma oferta ativa por veículo.
 */
@Service
public class OfertaService {

    private final OfertaLocacaoRepository ofertaRepository;
    private final LocadoraRepository locadoraRepository;
    private final AutomovelRepository automovelRepository;
    private final PedidoRepository pedidoRepository;

    public OfertaService(OfertaLocacaoRepository ofertaRepository,
                         LocadoraRepository locadoraRepository,
                         AutomovelRepository automovelRepository,
                         PedidoRepository pedidoRepository) {
        this.ofertaRepository = ofertaRepository;
        this.locadoraRepository = locadoraRepository;
        this.automovelRepository = automovelRepository;
        this.pedidoRepository = pedidoRepository;
    }

    /** Cliente vê as ofertas ativas de todas as locadoras; a locadora vê todas as suas. */
    @Transactional(readOnly = true)
    public List<OfertaResponse> listar(UsuarioLogado logado) {
        List<OfertaLocacao> ofertas = logado.isLocadora()
                ? ofertaRepository.findByLocadoraIdOrderByIdDesc(logado.id())
                : ofertaRepository.findByAtivaTrueOrderByIdDesc();
        return ofertas.stream().map(Mapeador::oferta).toList();
    }

    @Transactional
    public OfertaResponse criar(OfertaRequest request, UsuarioLogado logado) {
        Locadora locadora = locadoraRepository.findById(logado.id())
                .orElseThrow(() -> new AcessoNegadoException("Apenas locadoras criam ofertas de locação."));
        Automovel automovel = automovelRepository.findById(request.automovelId())
                .orElseThrow(() -> new NegocioException("Veículo não encontrado."));
        if (!automovel.getLocadora().getId().equals(locadora.getId())) {
            throw new AcessoNegadoException("Este veículo pertence a outra locadora.");
        }
        if (ofertaRepository.existsByAutomovelIdAndAtivaTrue(automovel.getId())) {
            throw new NegocioException("Este veículo já possui uma oferta ativa.");
        }
        OfertaLocacao oferta = new OfertaLocacao();
        oferta.setLocadora(locadora);
        oferta.setAutomovel(automovel);
        oferta.setValorDiario(request.valorDiario());
        oferta.setAtiva(true);
        return Mapeador.oferta(ofertaRepository.save(oferta));
    }

    /** Altera valor diário e status. Locações já feitas mantêm o valor em que foram contratadas. */
    @Transactional
    public OfertaResponse atualizar(Long id, OfertaAtualizacaoRequest request, UsuarioLogado logado) {
        OfertaLocacao oferta = carregarDaLocadora(id, logado);
        if (Boolean.TRUE.equals(request.ativa()) && !oferta.isAtiva()
                && ofertaRepository.existsByAutomovelIdAndAtivaTrueAndIdNot(oferta.getAutomovel().getId(), id)) {
            throw new NegocioException("Este veículo já possui outra oferta ativa.");
        }
        oferta.setValorDiario(request.valorDiario());
        oferta.setAtiva(request.ativa());
        return Mapeador.oferta(ofertaRepository.save(oferta));
    }

    @Transactional
    public void excluir(Long id, UsuarioLogado logado) {
        OfertaLocacao oferta = carregarDaLocadora(id, logado);
        if (pedidoRepository.existsByOfertaId(id)) {
            throw new NegocioException("Esta oferta já tem locações. Desative-a em vez de excluir.");
        }
        ofertaRepository.delete(oferta);
    }

    private OfertaLocacao carregarDaLocadora(Long id, UsuarioLogado logado) {
        OfertaLocacao oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Oferta não encontrada."));
        if (!oferta.getLocadora().getId().equals(logado.id())) {
            throw new AcessoNegadoException("Esta oferta pertence a outra locadora.");
        }
        return oferta;
    }
}
