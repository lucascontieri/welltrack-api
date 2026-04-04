package com.welltrack.controller;

import com.welltrack.domain.usuario.Usuario;
import com.welltrack.dto.analisenutricional.DadosAnaliseNutricional;
import com.welltrack.service.integracao.GeminiService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("analise-nutricional")
@SecurityRequirement(name = "bearer-key")
public class AnaliseNutricionalController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping
    public ResponseEntity<DadosAnaliseNutricional> analisar(
            @RequestParam("imagem") MultipartFile imagem,
            @AuthenticationPrincipal Usuario usuarioLogado) throws IOException {

        if (imagem.isEmpty()) {
            throw new RuntimeException("A imagem não pode estar vazia.");
        }

        String mimeType = imagem.getContentType();
        if (mimeType == null || !mimeType.startsWith("image/")) {
            throw new RuntimeException("O arquivo enviado deve ser uma imagem.");
        }

        var resultado = geminiService.analisarImagem(
                imagem.getBytes(),
                mimeType,
                usuarioLogado.getIdUsuario()
        );

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/consultas-restantes")
    public ResponseEntity<Map<String, Integer>> consultasRestantes(@AuthenticationPrincipal Usuario usuarioLogado) {
        int restantes = geminiService.consultasRestantes(usuarioLogado.getIdUsuario());
        return ResponseEntity.ok(Map.of("consultasRestantes", restantes));
    }
}
