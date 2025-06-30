package employmentalert.global.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import employmentalert.global.exception.dto.EmploymentAlertExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid 처리
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<EmploymentAlertExceptionResponse> handleBindException(BindException exception) {
        log.error("Bind Exception : {}", exception.getFieldErrors(), exception);
        return new ResponseEntity<>(
                new EmploymentAlertExceptionResponse(
                        HttpStatus.BAD_REQUEST,
                        Objects.requireNonNull(exception.getFieldError()).getDefaultMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Json 파싱 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<EmploymentAlertExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.error("HttpMessageNotReadableException : {}", exception.getHttpInputMessage(), exception);
        if (exception.getCause() instanceof InvalidFormatException invalidFormatException) {
            String fieldName = invalidFormatException.getPath().getFirst().getFieldName();
            String targetType = invalidFormatException.getTargetType().getSimpleName();
            String errorMessage = String.format(ErrorCode.INVALID_JSON_INPUT.getMessage(), fieldName, targetType);
            return new ResponseEntity<>(
                    new EmploymentAlertExceptionResponse(
                            ErrorCode.INVALID_REQUEST.getHttpStatus(),
                            errorMessage
                    ),
                    ErrorCode.INVALID_REQUEST.getHttpStatus()
            );
        } else {
            return new ResponseEntity<>(
                    new EmploymentAlertExceptionResponse(
                            ErrorCode.INVALID_REQUEST.getHttpStatus(),
                            ErrorCode.INVALID_REQUEST.getMessage()
                    ),
                    ErrorCode.INVALID_REQUEST.getHttpStatus()
            );
        }
    }

    /**
     * Custom 처리
     */
    @ExceptionHandler(EmploymentAlertException.class)
    public ResponseEntity<EmploymentAlertExceptionResponse> handleCustomException(EmploymentAlertException exception) {
        log.error("Custom Exception : {}", exception.getExceptionMessage(), exception);
        return new ResponseEntity<>(
                new EmploymentAlertExceptionResponse(
                        exception.getExceptionHttpStatus(),
                        exception.getExceptionMessage()
                ),
                exception.getExceptionHttpStatus()
        );
    }

    /**
     * EndPoint 처리
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<EmploymentAlertExceptionResponse> handleNoResourceFoundException(NoResourceFoundException exception) {
        log.error("NoResourceFoundException : {}", exception.getResourcePath(), exception);
        return new ResponseEntity<>(
                new EmploymentAlertExceptionResponse(
                        ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus(),
                        ErrorCode.RESOURCE_NOT_FOUND.getMessage()
                ),
                ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus()
        );
    }

    /**
     * 전체 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<EmploymentAlertExceptionResponse> handleServerException(Exception exception) {
        log.error("ServerException : {}", exception.getMessage(), exception);
        return new ResponseEntity<>(
                new EmploymentAlertExceptionResponse(
                        ErrorCode.SERVER_ERROR.getHttpStatus(),
                        ErrorCode.SERVER_ERROR.getMessage()
                ),
                ErrorCode.SERVER_ERROR.getHttpStatus()
        );
    }
}
