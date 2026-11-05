package br.pucminas.aluguel.repository;

import br.pucminas.aluguel.model.Pedido;
import br.pucminas.aluguel.model.StatusPedido;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteIdOrderByIdDesc(Long clienteId);

    List<Pedido> findByOfertaLocadoraIdOrderByIdDesc(Long locadoraId);

    boolean existsByClienteId(Long clienteId);

    boolean existsByOfertaId(Long ofertaId);

    /**
     * Existe outro pedido (não cancelado) do mesmo veículo cujo período se sobrepõe?
     * Os períodos [inicio, fim) se sobrepõem quando inicioA < fimB e inicioB < fimA.
     * Use idIgnorado = -1 ao criar um pedido novo.
     */
    @Query("""
            select count(p) > 0 from Pedido p
            where p.oferta.automovel.id = :automovelId
              and p.status <> :cancelado
              and p.id <> :idIgnorado
              and p.dataInicio < :fim
              and :inicio < p.dataFim
            """)
    boolean existeConflito(@Param("automovelId") Long automovelId,
                           @Param("inicio") LocalDate inicio,
                           @Param("fim") LocalDate fim,
                           @Param("idIgnorado") Long idIgnorado,
                           @Param("cancelado") StatusPedido cancelado);
}
