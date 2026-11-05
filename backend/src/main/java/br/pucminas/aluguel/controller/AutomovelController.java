package br.pucminas.aluguel.controller;

import br.pucminas.aluguel.dto.AutomovelRequest;
import br.pucminas.aluguel.dto.AutomovelResponse;
import br.pucminas.aluguel.security.UsuarioLogado;
import br.pucminas.aluguel.service.AutomovelService;
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

/** Veículos da locadora logada. */
@RestController
@RequestMapping("/api/automoveis")
@PreAuthorize("hasRole('LOCADORA')")
public class AutomovelController {

    private final AutomovelService automovelService;

    public AutomovelController(AutomovelService automovelService) {
        this.automovelService = automovelService;
    }

    @GetMapping
    public List<AutomovelResponse> listar(@AuthenticationPrincipal Jwt jwt) {
        return automovelService.listar(UsuarioLogado.de(jwt));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AutomovelResponse criar(@Valid @RequestBody AutomovelRequest request, @AuthenticationPrincipal Jwt jwt) {
        return automovelService.criar(request, UsuarioLogado.de(jwt));
    }

    @PutMapping("/{id}")
    public AutomovelResponse atualizar(@PathVariable Long id,
                                       @Valid @RequestBody AutomovelRequest request,
                                       @AuthenticationPrincipal Jwt jwt) {
        return automovelService.atualizar(id, request, UsuarioLogado.de(jwt));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        automovelService.excluir(id, UsuarioLogado.de(jwt));
    }
}
