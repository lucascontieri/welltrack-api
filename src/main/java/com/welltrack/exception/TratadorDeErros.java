package com.welltrack.exception;

import com.welltrack.dto.resposta.ApiErro;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratadorDeErros {

    private static final Logger log = LoggerFactory.getLogger(TratadorDeErros.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErro> tratarErro404() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErro(404, "Recurso nao encontrado."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosErrosValidacao>> tratarErro400(MethodArgumentNotValidException exception) {
        var erros = exception.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErrosValidacao::new).toList());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErro> tratarErro400(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiErro(400, "Corpo da requisicao invalido ou mal formatado."));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErro> tratarErroBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErro(401, "Credenciais invalidas."));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiErro> tratarContaDesabilitada() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiErro(403, "Confirme seu e-mail para ativar a conta."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErro> tratarErroIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiErro(400, ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErro> tratarErroAuthentication() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErro(401, "Falha na autenticacao."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErro> tratarErroAcessoNegado() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiErro(403, "Acesso negado."));
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ApiErro> tratarErroValidacao(ValidacaoException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiErro(400, ex.getMessage()));
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ApiErro> tratarErroConflito(ConflitoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErro(409, ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErro> tratarErroConflitoIntegridade() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErro(409, "Os dados informados conflitam com um registro existente."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErro> tratarErro500(Exception ex) {
        log.error("Erro interno nao tratado: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErro(500, "Erro interno do servidor."));
    }

    private record DadosErrosValidacao(String campo, String mensagem) {

        public DadosErrosValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}
