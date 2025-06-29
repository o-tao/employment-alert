package employmentalert.global.email;

import employmentalert.global.email.dto.EmailSendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final SesClient sesClient;
    @Value("${aws.ses.send-mail-from}")
    private String sender;

    public void sendEmail(EmailSendRequest emailSendRequest) {
        try {
            sesClient.sendEmail(toSendEmailRequest(
                    sender,
                    emailSendRequest.getTo(),
                    emailSendRequest.getSubject(),
                    emailSendRequest.getContent()
            ));
        } catch (Exception exception) {
            log.error("이메일 전송 실패", exception);
        }
    }

    // SES 에서 제공하는 이메일 전송 요청
    public SendEmailRequest toSendEmailRequest(String sender, List<String> to, String subject, String content) {
        Destination destination = Destination.builder()
                .toAddresses(to)
                .build();

        Message message = Message.builder()
                .subject(createContent(subject))
                .body(Body.builder().html(createContent(content)).build())
                .build();

        return SendEmailRequest.builder()
                .source(sender)
                .destination(destination)
                .message(message)
                .build();
    }

    // Email Subject, Body UTF-8 설정
    private Content createContent(String text) {
        return Content.builder()
                .charset("UTF-8")
                .data(text)
                .build();
    }
}
