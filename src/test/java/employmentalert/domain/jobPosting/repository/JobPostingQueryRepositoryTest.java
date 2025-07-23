package employmentalert.domain.jobPosting.repository;

import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.notification.NotificationChannel;
import employmentalert.domain.notification.NotificationHistory;
import employmentalert.domain.notification.NotificationRecord;
import employmentalert.domain.notification.repository.NotificationHistoryRepository;
import employmentalert.domain.notification.repository.NotificationRecordRepository;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JobPostingQueryRepository.class, QueryDslConfig.class})
class JobPostingQueryRepositoryTest {

    @Autowired
    private JobPostingQueryRepository jobPostingQueryRepository;
    @Autowired
    private JobPostingRepository jobPostingRepository;
    @Autowired
    private NotificationHistoryRepository notificationHistoryRepository;
    @Autowired
    private NotificationRecordRepository notificationRecordRepository;
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
    @DisplayName("DB에 저장된 url이 있을 때 존재하는 url만 반환한다.")
    public void findExistingUrlsTest() {
        // given
        String existUrl = "test.com";
        String notExistUrl = "notExist.com";

        JobPosting posting = JobPosting.create(
                "Test Company",
                "Test Title",
                existUrl,
                "Career",
                "Education",
                "Employment",
                "Region",
                "Deadline"
        );

        jobPostingRepository.save(posting);

        // when
        Set<String> existing = jobPostingQueryRepository.existingUrls(List.of(existUrl, notExistUrl));

        // then
        assertThat(existing).containsOnly(existUrl);
    }

    @Test
    @DisplayName("빈 리스트를 전당할 때 빈 Set이 반환된다.")
    public void emptyInputReturnsEmpty() {
        // given & when
        Set<String> result = jobPostingQueryRepository.existingUrls(List.of());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채용공고 조회 시 이메일 발송 이력이 없는 공고만 조회된다.")
    void findUnsentJobPostingsTest() {
        // given
        JobPosting sentPosting = JobPosting.create(
                "company",
                "title",
                "test1.com",
                "career",
                "education",
                "employment",
                "region",
                "deadline"
        );
        JobPosting unsentPosting = JobPosting.create(
                "company",
                "title",
                "test2.com",
                "career",
                "education",
                "employment",
                "region",
                "deadline"
        );
        jobPostingRepository.saveAll(List.of(sentPosting, unsentPosting));

        User user = new User("test@example.com", "career", "education", "employment", "region");
        userRepository.save(user);

        NotificationHistory sentHistory = NotificationHistory.success(
                "test@example.com",
                "content",
                "subject",
                NotificationChannel.EMAIL
        );
        notificationHistoryRepository.save(sentHistory);

        NotificationRecord sentRecord = NotificationRecord.create(user, sentPosting, sentHistory);
        notificationRecordRepository.save(sentRecord);

        // when
        List<JobPosting> jobPosting = jobPostingQueryRepository.findUnsentJobPostings(user.getEmail());

        // then
        assertThat(jobPosting).containsExactly(unsentPosting);
        assertThat(jobPosting).doesNotContain(sentPosting);
    }

    @Test
    @DisplayName("알림 발송 이력이 존재하는 공고 ID만 조회한다.")
    public void findJobPostingIdsByNotificationHistoryIdsTest() {
        // given
        JobPosting postingWithHistory = jobPostingRepository.save(JobPosting.create(
                "company1", "title1", "url1", "career", "education", "employment", "region", "deadline"
        ));
        JobPosting postingWithoutHistory = jobPostingRepository.save(JobPosting.create(
                "company2", "title2", "url2", "career", "education", "employment", "region", "deadline"
        ));

        User user = userRepository.save(new User("user@example.com", "career", "education", "employment", "region"));

        NotificationHistory history = notificationHistoryRepository.save(
                NotificationHistory.success("user@example.com", "subject", "content", NotificationChannel.EMAIL)
        );

        notificationRecordRepository.save(NotificationRecord.create(user, postingWithHistory, history));

        // when
        List<Long> result = jobPostingQueryRepository.findJobPostingIdsByNotificationHistoryIds(List.of(history.getId()));

        // then
        assertThat(result).containsExactly(postingWithHistory.getId());
        assertThat(result).doesNotContain(postingWithoutHistory.getId());
    }

    @Test
    @DisplayName("채용공고 ID 리스트를 전달하면 해당 공고들이 삭제된다.")
    public void deleteByIdsTest() {
        // given
        JobPosting jobPosting1 = jobPostingRepository.save(JobPosting.create(
                "company1", "title1", "url1", "career", "education", "employment", "region", "deadline"
        ));
        JobPosting jobPosting2 = jobPostingRepository.save(JobPosting.create(
                "company2", "title2", "url2", "career", "education", "employment", "region", "deadline"
        ));

        // when
        jobPostingQueryRepository.deleteByIds(List.of(jobPosting1.getId()));

        // then
        List<JobPosting> jobPostings = jobPostingRepository.findAll();
        assertThat(jobPostings).containsExactly(jobPosting2);
        assertThat(jobPostings).doesNotContain(jobPosting1);
    }
}
