package com.welltrack.dto.registrotreino;

import com.welltrack.domain.registrotreino.RegistroTreino;

import java.time.LocalDate;
import java.util.UUID;

public record DadosListagemRegistroTreino(
        UUID idRegistro,
        LocalDate dataExecucao,
        Boolean concluido,
        String nomeTreino) {

    public DadosListagemRegistroTreino(RegistroTreino registroTreino) {
        this(registroTreino.getIdRegistro(),
                registroTreino.getDataExecucao(),
                registroTreino.getConcluido(),
                registroTreino.getTreino().getNomeTreino());
    }
}

