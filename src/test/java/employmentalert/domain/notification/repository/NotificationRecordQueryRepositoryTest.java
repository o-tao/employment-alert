package employmentalert.domain.notification.repository;

import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.jobPosting.repository.JobPostingRepository;
import employmentalert.domain.notification.NotificationChannel;
import employmentalert.domain.notification.NotificationHistory;
import employmentalert.domain.notification.NotificationRecord;
import employmentalert.domain.user.User;
import employmentalert.domain.user.repository.UserRepository;
import employmentalert.global.config.QueryDslConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({NotificationRecordQueryRepository.class, QueryDslConfig.class})
class NotificationRecordQueryRepositoryTest {

    @Autowired
    private NotificationRecordQueryRepository notificationRecordQueryRepository;
    @Autowired
    private NotificationRecordRepository notificationRecordRepository;
    @Autowired
    private NotificationHistoryRepository notificationHistoryRepository;
    @Autowired
    private JobPostingRepository jobPostingRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void clean() {
        notificationRecordRepository.deleteAllInBatch();
        notificationHistoryRepository.deleteAllInBatch();
        jobPostingRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("주어진 알림 이력 ID 리스트로 알림 기록들을 삭제하면, 매핑된 알림 기록이 삭제된다.")
    public void deleteNotificationRecordsByHistoryIdsTest() {
        // given
        User user = userRepository.save(
                User.create("test.com", "career", "education", "employmentType", "region")
        );
        NotificationHistory notificationHistory = notificationHistoryRepository.save(
                NotificationHistory.success("test.com", "subject", "content", NotificationChannel.EMAIL)
        );
        JobPosting jobPosting = jobPostingRepository.save(
                JobPosting.create("company", "title", "test.com", "career", "education", "employmentType", "region", "deadline")
        );
        notificationRecordRepository.save(
                NotificationRecord.create(user, jobPosting, notificationHistory)
        );

        // when
        notificationRecordQueryRepository.deleteNotificationRecordsByHistoryIds(List.of(notificationHistory.getId()));

        // then
        List<NotificationRecord> notificationRecord = notificationRecordRepository.findAll();
        assertThat(notificationRecord).isEmpty();
    }

}
