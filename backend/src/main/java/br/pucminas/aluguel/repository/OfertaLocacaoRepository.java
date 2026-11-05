package br.pucminas.aluguel.repository;

import br.pucminas.aluguel.model.OfertaLocacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfertaLocacaoRepository extends JpaRepository<OfertaLocacao, Long> {

    List<OfertaLocacao> findByLocadoraIdOrderByIdDesc(Long locadoraId);

    List<OfertaLocacao> findByAtivaTrueOrderByIdDesc();

    boolean existsByAutomovelIdAndAtivaTrue(Long automovelId);

    boolean existsByAutomovelIdAndAtivaTrueAndIdNot(Long automovelId, Long id);

    boolean existsByAutomovelId(Long automovelId);
}
