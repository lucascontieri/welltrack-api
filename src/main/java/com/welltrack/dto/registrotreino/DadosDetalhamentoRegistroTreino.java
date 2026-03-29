package com.welltrack.dto.registrotreino;

import com.welltrack.domain.registrotreino.RegistroTreino;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record DadosDetalhamentoRegistroTreino(
        UUID idRegistro,
        LocalDate dataExecucao,
        LocalTime horaEntrada,
        LocalTime horaSaida,
        Boolean concluido,
        UUID idTreino,
        String nomeTreino,
        UUID idUsuario) {

    public DadosDetalhamentoRegistroTreino(RegistroTreino registroTreino) {
        this(registroTreino.getIdRegistro(),
                registroTreino.getDataExecucao(),
                registroTreino.getHoraEntrada(),
                registroTreino.getHoraSaida(),
                registroTreino.getConcluido(),
                registroTreino.getTreino().getIdTreino(),
                registroTreino.getTreino().getNomeTreino(),
                registroTreino.getUsuario().getIdUsuario());
    }
}

