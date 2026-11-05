package br.pucminas.aluguel.repository;

import br.pucminas.aluguel.model.ContratoCredito;
import br.pucminas.aluguel.model.StatusCredito;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratoCreditoRepository extends JpaRepository<ContratoCredito, Long> {

    List<ContratoCredito> findByBancoIdOrderByIdDesc(Long bancoId);

    List<ContratoCredito> findByPedidoIdOrderByIdAsc(Long pedidoId);

    Optional<ContratoCredito> findByPedidoIdAndStatus(Long pedidoId, StatusCredito status);
}
