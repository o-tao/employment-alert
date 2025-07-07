package employmentalert.domain.notification;

import employmentalert.domain.BaseEntity;
import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.user.User;
import jakarta.persistence.*;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Comment("채용공고")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @Comment("알림 로그")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_history_id", nullable = false)
    private NotificationHistory notificationHistory;

    public NotificationRecord(User user, JobPosting jobPosting, NotificationHistory notificationHistory) {
        this.user = user;
        this.jobPosting = jobPosting;
        this.notificationHistory = notificationHistory;
    }

    /**
     * 로그 매핑 정보 저장
     */
    public static NotificationRecord create(User user, JobPosting jobPosting, NotificationHistory history) {
        return new NotificationRecord(user, jobPosting, history);
    }
}
