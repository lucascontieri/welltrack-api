package com.welltrack.dto.resposta;

import java.time.LocalDateTime;

public record ApiErro(int status, String mensagem, LocalDateTime timestamp) {

    public ApiErro(int status, String mensagem) {
        this(status, mensagem, LocalDateTime.now());
    }
}
