package br.pucminas.aluguel.controller;

import br.pucminas.aluguel.dto.PedidoAtualizacaoRequest;
import br.pucminas.aluguel.dto.PedidoRequest;
import br.pucminas.aluguel.dto.PedidoResponse;
import br.pucminas.aluguel.dto.SolicitacaoCreditoRequest;
import br.pucminas.aluguel.security.UsuarioLogado;
import br.pucminas.aluguel.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Pedidos de locação. */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@Valid @RequestBody PedidoRequest request, @AuthenticationPrincipal Jwt jwt) {
        return pedidoService.criar(request, UsuarioLogado.de(jwt));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE','LOCADORA')")
    public List<PedidoResponse> listar(@AuthenticationPrincipal Jwt jwt) {
        return pedidoService.listar(UsuarioLogado.de(jwt));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','LOCADORA')")
    public PedidoResponse buscar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return pedidoService.buscar(id, UsuarioLogado.de(jwt));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse atualizar(@PathVariable Long id,
                                    @Valid @RequestBody PedidoAtualizacaoRequest request,
                                    @AuthenticationPrincipal Jwt jwt) {
        return pedidoService.atualizar(id, request, UsuarioLogado.de(jwt));
    }

    @PostMapping("/{id}/cancelamento")
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse cancelar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return pedidoService.cancelar(id, UsuarioLogado.de(jwt));
    }

    @PostMapping("/{id}/pagamento-proprio")
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse pagarPorContaPropria(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return pedidoService.pagarPorContaPropria(id, UsuarioLogado.de(jwt));
    }

    @PostMapping("/{id}/solicitacao-credito")
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse solicitarCredito(@PathVariable Long id,
                                           @Valid @RequestBody SolicitacaoCreditoRequest request,
                                           @AuthenticationPrincipal Jwt jwt) {
        return pedidoService.solicitarNovoCredito(id, request.bancoId(), UsuarioLogado.de(jwt));
    }
}
