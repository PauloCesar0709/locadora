package br.pucminas.aluguel.repository;

import br.pucminas.aluguel.model.Contrato;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    Optional<Contrato> findByPedidoId(Long pedidoId);
}
