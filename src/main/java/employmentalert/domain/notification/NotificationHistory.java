package employmentalert.domain.notification;

import employmentalert.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_history")
public class NotificationHistory extends BaseEntity {

    @Comment("수신자 이메일 주소")
    @Column(nullable = false)
    private String recipientEmail;

    @Comment("이메일 제목")
    @Column(nullable = false)
    private String subject;

    @Comment("이메일 내용")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Comment("발송 채널")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel notificationChannel;

    @Comment("발송 상태")
    @Column(nullable = false)
    private boolean status;

    @Comment("실패 시 에러 메시지")
    @Column(nullable = true)
    private String errorMessage;

    public NotificationHistory(String recipientEmail,
                               String subject,
                               String content,
                               NotificationChannel notificationChannel,
                               String errorMessage,
                               boolean status
    ) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.content = content;
        this.notificationChannel = notificationChannel;
        this.errorMessage = errorMessage;
        this.status = status;
    }

}
