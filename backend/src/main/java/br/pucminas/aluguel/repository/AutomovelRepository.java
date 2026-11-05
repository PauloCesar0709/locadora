package br.pucminas.aluguel.repository;

import br.pucminas.aluguel.model.Automovel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutomovelRepository extends JpaRepository<Automovel, Long> {

    List<Automovel> findByLocadoraIdOrderByIdAsc(Long locadoraId);

    boolean existsByPlaca(String placa);

    boolean existsByPlacaAndIdNot(String placa, Long id);

    boolean existsByMatricula(String matricula);

    boolean existsByMatriculaAndIdNot(String matricula, Long id);

    boolean existsByProprietarioId(Long proprietarioId);
}
