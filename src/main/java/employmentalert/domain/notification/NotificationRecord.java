package employmentalert.domain.notification;

import employmentalert.domain.BaseEntity;
import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_record")
public class NotificationRecord extends BaseEntity {

    @Comment("유저")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Comment("채용공고")
    @ManyToOne
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @Comment("알림 로그")
    @ManyToOne
    @JoinColumn(name = "notification_history_id", nullable = false)
    private NotificationHistory notificationHistory;

}
