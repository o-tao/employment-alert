package employmentalert.global.exception;

import employmentalert.global.exception.dto.EmploymentAlertExceptionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    @Test
    @DisplayName("잘못된 값 요청 시 GlobalExceptionHandler가 handleBindException을 처리한다.")
    public void handleBindExceptionTest() {
        // given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        Object target = new Object();
        String objectName = "BindException";
        BindingResult bindingResult = new BeanPropertyBindingResult(target, objectName);

        String fieldName = "employmentAlert";
        String rejectedValue = "invalid value";
        String errorMessage = "Value is invalid";
        bindingResult.addError(new FieldError(objectName, fieldName, rejectedValue, false, null, null, errorMessage));

        BindException bindException = new BindException(bindingResult);

        // when
        ResponseEntity<EmploymentAlertExceptionResponse> response = globalExceptionHandler.handleBindException(bindException);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).contains(errorMessage);

        FieldError fieldError = bindException.getFieldError();
        assertThat(fieldError).isNotNull();
        assertThat(fieldError.getField()).isEqualTo(fieldName);
        assertThat(fieldError.getRejectedValue()).isEqualTo(rejectedValue);
        assertThat(fieldError.getDefaultMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("커스텀 예외 EmploymentAlertException 발생 시 지정한 ErrorCode를 응답한다.")
    public void handleCustomExceptionTest() {
        // given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        HttpStatus exceptionStatus = ErrorCode.USER_NOT_FOUND.getHttpStatus();
        String exceptionMessage = ErrorCode.USER_NOT_FOUND.getMessage();

        EmploymentAlertException exception = new EmploymentAlertException(ErrorCode.USER_NOT_FOUND);

        // when
        ResponseEntity<EmploymentAlertExceptionResponse> response = globalExceptionHandler
                .handleCustomException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(exceptionStatus);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(exceptionStatus.value());
        assertThat(response.getBody().getMessage()).isEqualTo(exceptionMessage);
    }

    @Test
    @DisplayName("잘못된 엔드 포인트 요청 시 GlobalExceptionHandler가 handleNoResourceFoundException을 처리한다.")
    public void handleNoResourceFoundExceptionTest() {
        // given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        NoResourceFoundException noResourceFoundException = new NoResourceFoundException(HttpMethod.GET, "/test/path");

        // when
        ResponseEntity<EmploymentAlertExceptionResponse> response = globalExceptionHandler.handleNoResourceFoundException(noResourceFoundException);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("일반 Exception 발생 시 GlobalExceptionHandler가 INTERNAL_SERVER_ERROR를 처리한다.")
    public void handleServerExceptionTest() {
        // given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        Exception exception = new Exception(ErrorCode.SERVER_ERROR.getMessage());

        // when
        ResponseEntity<EmploymentAlertExceptionResponse> response = globalExceptionHandler.handleServerException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.SERVER_ERROR.getMessage());
    }
}
