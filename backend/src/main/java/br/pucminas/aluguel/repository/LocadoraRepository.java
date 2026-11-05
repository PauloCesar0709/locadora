package br.pucminas.aluguel.repository;

import br.pucminas.aluguel.model.Locadora;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocadoraRepository extends JpaRepository<Locadora, Long> {
}
