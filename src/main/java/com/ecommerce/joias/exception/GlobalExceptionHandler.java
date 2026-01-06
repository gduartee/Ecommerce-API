package com.ecommerce.joias.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Esse método captura erros de validação (@NotBlank, @Size, etc)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {

        // Cria um objeto de erro padrão do Spring (RFC 7807)
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Erro de validação nos campos informados"
        );

        problemDetail.setTitle("Dados Inválidos");
        problemDetail.setProperty("timestamp", Instant.now());

        // Cria um mapa para listar: campo -> erro (ex: "password" -> "mínimo 6 caracteres")
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        // Adiciona a lista de campos errados na resposta
        problemDetail.setProperty("errors", fieldErrors);

        return problemDetail;
    }

    // Captura erros de banco de dados (ex: SKU ou E-mail duplicado)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ProblemDetail handleDatabaseExceptions(org.springframework.dao.DataIntegrityViolationException ex) {

        // Status 409 = Conflict (Conflito)
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "Já existe um registro com essa informação (provavelmente SKU ou nome duplicado)."
        );

        problemDetail.setTitle("Conflito de Dados");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
    // Captura erros lançados manualmente (throw new ResponseStatusException...)
    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                ex.getStatusCode(),
                ex.getReason()
        );

        problemDetail.setTitle("Erro na Operação");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

}