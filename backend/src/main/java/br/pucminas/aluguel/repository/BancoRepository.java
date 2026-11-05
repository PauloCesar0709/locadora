package br.pucminas.aluguel.repository;

import br.pucminas.aluguel.model.Banco;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco, Long> {

    List<Banco> findAllByOrderByNomeAsc();
}
