package employmentalert.global.email;

import employmentalert.global.email.dto.EmailSendRequest;
import employmentalert.global.exception.EmploymentAlertException;
import employmentalert.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private final String sender = "test@example.com";

    @Test
    @DisplayName("이메일 전송 시 SesClient가 올바른 파라미터로 호출된다.")
    void sendEmailTest() throws Exception {
        // given
        MockitoAnnotations.openMocks(this);
        SesClient sesClient = mock(SesClient.class);
        EmailService emailService = new EmailService(sesClient);

        Field senderField = EmailService.class.getDeclaredField("sender");
        senderField.setAccessible(true);
        senderField.set(emailService, sender);

        EmailSendRequest request = new EmailSendRequest(
                List.of("test@example.com"),
                "Test Subject",
                "<p>Test Content</p>"
        );

        // when
        emailService.sendEmail(request);

        // then
        ArgumentCaptor<SendEmailRequest> captor = ArgumentCaptor.forClass(SendEmailRequest.class);
        verify(sesClient).sendEmail(captor.capture());

        SendEmailRequest sendEmailRequest = captor.getValue();
        assertThat(sendEmailRequest.source()).isEqualTo(sender);
        assertThat(sendEmailRequest.destination().toAddresses()).containsExactlyElementsOf(request.getRecipient());
        assertThat(sendEmailRequest.message().subject().data()).isEqualTo(request.getSubject());
        assertThat(sendEmailRequest.message().body().html().data()).isEqualTo(request.getContent());
    }

    @Test
    @DisplayName("SesClient에서 예외 발생 시 EmploymentAlertException이 발생한다.")
    void sendEmailExceptionTest() throws Exception {
        // given
        MockitoAnnotations.openMocks(this);
        SesClient sesClient = mock(SesClient.class);
        EmailService emailService = new EmailService(sesClient);

        Field senderField = EmailService.class.getDeclaredField("sender");
        senderField.setAccessible(true);
        senderField.set(emailService, sender);

        EmailSendRequest request = new EmailSendRequest(
                List.of("test@example.com"),
                "Test Subject",
                "<p>Test Content</p>"
        );

        doThrow(new RuntimeException("AWS SES error")).when(sesClient).sendEmail(any(SendEmailRequest.class));

        // when, then
        assertThatThrownBy(() -> emailService.sendEmail(request))
                .isInstanceOf(EmploymentAlertException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EMAIL_SEND_FAILED);
    }
}
