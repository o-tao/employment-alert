package employmentalert.application.email.dto;

import employmentalert.global.email.dto.EmailSendRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailInfo {

    private List<String> to;
    private String subject;
    private String content;

    public EmailInfo(List<String> to, String subject, String content) {
        this.to = to;
        this.subject = subject;
        this.content = content;
    }

    public EmailSendRequest toSend() {
        return new EmailSendRequest(to, subject, content);
    }
}
