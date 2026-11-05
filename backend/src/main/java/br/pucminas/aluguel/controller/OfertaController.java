package br.pucminas.aluguel.controller;

import br.pucminas.aluguel.dto.OfertaAtualizacaoRequest;
import br.pucminas.aluguel.dto.OfertaRequest;
import br.pucminas.aluguel.dto.OfertaResponse;
import br.pucminas.aluguel.security.UsuarioLogado;
import br.pucminas.aluguel.service.OfertaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Ofertas de locação (veículo + valor diário). */
@RestController
@RequestMapping("/api/ofertas")
public class OfertaController {

    private final OfertaService ofertaService;

    public OfertaController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    /** Cliente: ofertas ativas de todas as locadoras. Locadora: todas as suas. */
    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE','LOCADORA')")
    public List<OfertaResponse> listar(@AuthenticationPrincipal Jwt jwt) {
        return ofertaService.listar(UsuarioLogado.de(jwt));
    }

    @PostMapping
    @PreAuthorize("hasRole('LOCADORA')")
    @ResponseStatus(HttpStatus.CREATED)
    public OfertaResponse criar(@Valid @RequestBody OfertaRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ofertaService.criar(request, UsuarioLogado.de(jwt));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LOCADORA')")
    public OfertaResponse atualizar(@PathVariable Long id,
                                    @Valid @RequestBody OfertaAtualizacaoRequest request,
                                    @AuthenticationPrincipal Jwt jwt) {
        return ofertaService.atualizar(id, request, UsuarioLogado.de(jwt));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LOCADORA')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        ofertaService.excluir(id, UsuarioLogado.de(jwt));
    }
}
