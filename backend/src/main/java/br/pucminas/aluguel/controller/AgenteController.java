package br.pucminas.aluguel.controller;

import br.pucminas.aluguel.dto.AgenteAtualizacaoRequest;
import br.pucminas.aluguel.dto.AgenteCadastroRequest;
import br.pucminas.aluguel.dto.AgenteResponse;
import br.pucminas.aluguel.dto.BancoResumo;
import br.pucminas.aluguel.security.UsuarioLogado;
import br.pucminas.aluguel.service.AgenteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgenteController {

    private final AgenteService agenteService;

    public AgenteController(AgenteService agenteService) {
        this.agenteService = agenteService;
    }

    /** Público: cadastro de empresa (locadora ou banco). */
    @PostMapping("/api/agentes")
    @ResponseStatus(HttpStatus.CREATED)
    public AgenteResponse cadastrar(@Valid @RequestBody AgenteCadastroRequest request) {
        return agenteService.cadastrar(request);
    }

    /** "Minhas informações" da empresa logada. */
    @GetMapping("/api/agentes/me")
    @PreAuthorize("hasAnyRole('LOCADORA','BANCO')")
    public AgenteResponse minhasInformacoes(@AuthenticationPrincipal Jwt jwt) {
        return agenteService.buscarLogado(UsuarioLogado.de(jwt));
    }

    @PutMapping("/api/agentes/me")
    @PreAuthorize("hasAnyRole('LOCADORA','BANCO')")
    public AgenteResponse atualizar(@Valid @RequestBody AgenteAtualizacaoRequest request,
                                    @AuthenticationPrincipal Jwt jwt) {
        return agenteService.atualizarLogado(request, UsuarioLogado.de(jwt));
    }

    /** Bancos disponíveis (cliente escolhe a quem pedir crédito; locadora escolhe proprietário). */
    @GetMapping("/api/bancos")
    public List<BancoResumo> listarBancos() {
        return agenteService.listarBancos();
    }
}
