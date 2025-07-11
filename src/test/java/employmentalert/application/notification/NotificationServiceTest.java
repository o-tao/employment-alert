package employmentalert.application.notification;

import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.jobPosting.repository.JobPostingRepository;
import employmentalert.domain.notification.NotificationChannel;
import employmentalert.domain.notification.NotificationHistory;
import employmentalert.domain.notification.NotificationRecord;
import employmentalert.domain.notification.repository.NotificationHistoryRepository;
import employmentalert.domain.notification.repository.NotificationRecordRepository;
import employmentalert.domain.user.User;
import employmentalert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationHistoryRepository notificationHistoryRepository;
    @Autowired
    private NotificationRecordRepository notificationRecordRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobPostingRepository jobPostingRepository;

    @AfterEach
    public void clear() {
        notificationRecordRepository.deleteAllInBatch();
        notificationHistoryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        jobPostingRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("status true 상태로 이메일 전송 정보가 저장된다.")
    void notificationSavedToSuccessTest() {
        // given
        String email = "test@example.com";
        String subject = "subject";
        String content = "content";
        NotificationChannel channel = NotificationChannel.EMAIL;

        // when
        NotificationHistory saved = notificationService.success(email, subject, content, channel);

        // then
        NotificationHistory found = notificationHistoryRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getRecipientEmail()).isEqualTo(email);
        assertThat(found.getSubject()).isEqualTo(subject);
        assertThat(found.getContent()).isEqualTo(content);
        assertThat(found.getNotificationChannel()).isEqualTo(channel);
        assertThat(found.isStatus()).isTrue();
    }

    @Test
    @DisplayName("status false 상태로 이메일 전송 정보가 저장된다.")
    void notificationSavedToFailTest() {
        // given
        String email = "test@example.com";
        String subject = "subject";
        String content = "content";
        NotificationChannel channel = NotificationChannel.EMAIL;
        String errorMessage = "Email service unavailable";

        // when
        notificationService.fail(email, subject, content, channel, errorMessage);

        // then
        List<NotificationHistory> all = notificationHistoryRepository.findAll();
        assertThat(all).hasSize(1);

        NotificationHistory failed = all.getFirst();
        assertThat(failed.isStatus()).isFalse();
        assertThat(failed.getErrorMessage()).isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("유저, 채용공고, 알림발송 로그가 Record 테이블에 매핑되어 정상적으로 저장된다.")
    @Transactional
    void createNotificationRecordTest() {
        // given
        User user = new User(
                "test@example.com",
                null,
                null,
                null,
                null
        );
        userRepository.save(user);

        JobPosting jobPosting = new JobPosting(
                "company",
                "title",
                "test@example.com",
                "career",
                "education",
                "employmentType",
                "region",
                "deadline"
        );
        jobPostingRepository.save(jobPosting);

        NotificationHistory history = notificationService.success(
                user.getEmail(),
                "Subject",
                "Some content",
                NotificationChannel.EMAIL
        );

        // when
        notificationService.createRecord(user, jobPosting, history);

        // then
        List<NotificationRecord> records = notificationRecordRepository.findAll();
        assertThat(records).hasSize(1);
        NotificationRecord record = records.getFirst();
        assertThat(record.getUser()).isEqualTo(user);
        assertThat(record.getJobPosting()).isEqualTo(jobPosting);
        assertThat(record.getNotificationHistory()).isEqualTo(history);
    }

}
