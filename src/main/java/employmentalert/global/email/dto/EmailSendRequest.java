package employmentalert.global.email.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailSendRequest {

    private List<String> recipient;
    private String subject;
    private String content;

    public EmailSendRequest(List<String> recipient, String subject, String content) {
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
    }
}
