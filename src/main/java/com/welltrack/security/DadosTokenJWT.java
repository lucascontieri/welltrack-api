package com.welltrack.security;

import java.util.UUID;

public record DadosTokenJWT(String token, UUID idUsuario) {
}
