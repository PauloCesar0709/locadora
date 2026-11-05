package br.pucminas.aluguel.dto;

import br.pucminas.aluguel.model.Perfil;

public record AutomovelResponse(
        Long id,
        String matricula,
        Integer ano,
        String marca,
        String modelo,
        String placa,
        Long proprietarioId,
        String proprietarioNome,
        Perfil proprietarioPerfil) {
}
