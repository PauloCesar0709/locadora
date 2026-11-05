package br.pucminas.aluguel.controller;

import br.pucminas.aluguel.dto.ClienteRequest;
import br.pucminas.aluguel.dto.ClienteResponse;
import br.pucminas.aluguel.security.UsuarioLogado;
import br.pucminas.aluguel.service.ClienteService;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /** Público: cadastro prévio do cliente. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse cadastrar(@Valid @RequestBody ClienteRequest request) {
        return clienteService.cadastrar(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ClienteResponse buscar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return clienteService.buscar(id, UsuarioLogado.de(jwt));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ClienteResponse atualizar(@PathVariable Long id,
                                     @Valid @RequestBody ClienteRequest request,
                                     @AuthenticationPrincipal Jwt jwt) {
        return clienteService.atualizar(id, request, UsuarioLogado.de(jwt));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        clienteService.excluir(id, UsuarioLogado.de(jwt));
    }
}
