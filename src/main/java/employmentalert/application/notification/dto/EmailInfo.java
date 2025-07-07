package employmentalert.application.notification.dto;

import employmentalert.global.email.dto.EmailSendRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailInfo {

    private List<String> recipient;
    private String subject;
    private String content;

    public EmailInfo(List<String> recipient, String subject, String content) {
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
    }

    public EmailSendRequest toSend() {
        return new EmailSendRequest(recipient, subject, content);
    }
}
