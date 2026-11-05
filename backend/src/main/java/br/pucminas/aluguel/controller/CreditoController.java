package br.pucminas.aluguel.controller;

import br.pucminas.aluguel.dto.AprovacaoCreditoRequest;
import br.pucminas.aluguel.dto.CreditoResponse;
import br.pucminas.aluguel.dto.RecusaCreditoRequest;
import br.pucminas.aluguel.security.UsuarioLogado;
import br.pucminas.aluguel.service.CreditoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Hub do banco: pedidos de crédito recebidos. */
@RestController
@RequestMapping("/api/creditos")
@PreAuthorize("hasRole('BANCO')")
public class CreditoController {

    private final CreditoService creditoService;

    public CreditoController(CreditoService creditoService) {
        this.creditoService = creditoService;
    }

    @GetMapping
    public List<CreditoResponse> listar(@AuthenticationPrincipal Jwt jwt) {
        return creditoService.listar(UsuarioLogado.de(jwt));
    }

    @GetMapping("/{id}")
    public CreditoResponse buscar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return creditoService.buscar(id, UsuarioLogado.de(jwt));
    }

    @PostMapping("/{id}/aprovacao")
    public CreditoResponse aprovar(@PathVariable Long id,
                                   @Valid @RequestBody AprovacaoCreditoRequest request,
                                   @AuthenticationPrincipal Jwt jwt) {
        return creditoService.aprovar(id, request, UsuarioLogado.de(jwt));
    }

    @PostMapping("/{id}/recusa")
    public CreditoResponse recusar(@PathVariable Long id,
                                   @Valid @RequestBody(required = false) RecusaCreditoRequest request,
                                   @AuthenticationPrincipal Jwt jwt) {
        return creditoService.recusar(id, request, UsuarioLogado.de(jwt));
    }
}
