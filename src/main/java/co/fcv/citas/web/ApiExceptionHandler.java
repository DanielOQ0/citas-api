package co.fcv.citas.web;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Formato único de error del contrato REST (HU-026, D-21): {status, error, message, fieldErrors, path}.
 * Resolver los errores aquí evita el reenvío a /error, que el filtro de seguridad convertía en 401.
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

  public record FieldError(String field, String message) {}

  public record ApiError(int status, String error, String message, List<FieldError> fieldErrors, String path) {}

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    List<FieldError> fields = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
        .toList();
    return body(HttpStatus.BAD_REQUEST, "Revisa los datos enviados", fields, path(request));
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    String detail = ex instanceof ErrorResponse response ? response.getBody().getDetail() : null;
    return body(status, detail != null ? detail : reason(status), List.of(), path(request));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<Object> conflict(HttpServletRequest request) {
    return body(HttpStatus.CONFLICT, "El registro entra en conflicto con datos existentes", List.of(), request.getRequestURI());
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  ResponseEntity<Object> notFound(HttpServletRequest request) {
    return body(HttpStatus.NOT_FOUND, "Recurso no encontrado", List.of(), request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<Object> unexpected(Exception ex, HttpServletRequest request) {
    log.error("Error no controlado en {}", request.getRequestURI(), ex);
    return body(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", List.of(), request.getRequestURI());
  }

  private static ResponseEntity<Object> body(HttpStatusCode status, String message, List<FieldError> fields, String path) {
    return ResponseEntity.status(status).body(new ApiError(status.value(), reason(status), message, fields, path));
  }

  private static String reason(HttpStatusCode status) {
    HttpStatus known = HttpStatus.resolve(status.value());
    return known != null ? known.getReasonPhrase() : String.valueOf(status.value());
  }

  private static String path(WebRequest request) {
    return request instanceof ServletWebRequest servlet ? servlet.getRequest().getRequestURI() : "";
  }
}
