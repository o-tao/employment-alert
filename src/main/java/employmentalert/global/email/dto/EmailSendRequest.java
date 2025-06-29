package employmentalert.global.email.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailSendRequest {

    private List<String> to;
    private String subject;
    private String content;

    public EmailSendRequest(List<String> to, String subject, String content) {
        this.to = to;
        this.subject = subject;
        this.content = content;
    }
}
