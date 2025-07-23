package employmentalert.application.crawler;

import employmentalert.application.crawler.dto.JobKoreaPostingInfo;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JobKoreaServiceTest {

    @Autowired
    private JobKoreaService jobKoreaService;
    @Autowired
    private JobPostingRepository jobPostingRepository;
    @Autowired
    private NotificationHistoryRepository notificationHistoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRecordRepository notificationRecordRepository;

    @AfterEach
    public void clear() {
        notificationRecordRepository.deleteAllInBatch();
        notificationHistoryRepository.deleteAllInBatch();
        jobPostingRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("저장되지 않은 채용공고일 때 정상적으로 저장된다.")
    void saveNewPostings() {
        // given
        JobKoreaPostingInfo posting = new JobKoreaPostingInfo(
                "Test 회사",
                "Test 제목",
                "test.com",
                "Test 경력",
                "Test 학력",
                "Test 고용형태",
                "Test 지역",
                "Test 마감일"
        );
        List<JobKoreaPostingInfo> postings = List.of(posting);

        // when
        jobKoreaService.saveAll(postings);

        // then
        List<JobPosting> saved = jobPostingRepository.findAll();
        assertThat(saved).hasSize(1);
        assertThat(saved.getFirst().getCompany()).isEqualTo(posting.getCompany());
        assertThat(saved.getFirst().getTitle()).isEqualTo(posting.getTitle());
        assertThat(saved.getFirst().getUrl()).isEqualTo(posting.getUrl());
        assertThat(saved.getFirst().getCareer()).isEqualTo(posting.getCareer());
        assertThat(saved.getFirst().getEducation()).isEqualTo(posting.getEducation());
        assertThat(saved.getFirst().getEmploymentType()).isEqualTo(posting.getEmploymentType());
        assertThat(saved.getFirst().getRegion()).isEqualTo(posting.getRegion());
        assertThat(saved.getFirst().getDeadline()).isEqualTo(posting.getDeadline());
    }

    @Test
    @DisplayName("저장하고자 하는 채용공고의 url 이 존재할 경우 저장되지 않는다.")
    void duplicatePostingsAreSkipped() {
        // given
        String url = "test.com";

        JobKoreaPostingInfo first = new JobKoreaPostingInfo(
                "Test 회사1",
                "Test 제목1",
                url,
                "Test 회사1",
                "Test 학력1",
                "Test 고용형태1",
                "Test 지역1",
                "Test 마감일1"
        );

        JobKoreaPostingInfo duplicate = new JobKoreaPostingInfo(
                "Test 회사2",
                "Test 제목2",
                url,
                "Test 경력2",
                "Test 학력2",
                "Test 고용형태2",
                "Test 지역2",
                "Test 마감일2"
        );

        // when
        jobKoreaService.saveAll(List.of(first));
        jobKoreaService.saveAll(List.of(duplicate));

        // then
        List<JobPosting> saved = jobPostingRepository.findAll();
        assertThat(saved).hasSize(1);
    }

    @Test
    @DisplayName("알림 발송 이력이 존재하는 공고 ID만 조회된다.")
    void findJobPostingIdsByNotificationHistoryIdsTest() {
        // given
        JobPosting postingWithHistory = jobPostingRepository.save(
                JobPosting.create("company1", "title1", "test1.com", "career", "education", "employmentType", "region", "deadline")
        );
        JobPosting postingWithoutHistory = jobPostingRepository.save(
                JobPosting.create("company2", "title2", "test2.com", "career", "education", "employmentType", "region", "deadline")
        );

        NotificationHistory history = notificationHistoryRepository.save(
                NotificationHistory.success("user@test.com", "subject", "content", NotificationChannel.EMAIL)
        );
        User user = userRepository.save(User.create("user@test.com", "career", "education", "employment", "region"));
        notificationRecordRepository.save(NotificationRecord.create(user, postingWithHistory, history));

        // when
        List<Long> result = jobKoreaService.findJobPostingIdsByNotificationHistoryIds(List.of(history.getId()));

        // then
        assertThat(result).containsExactly(postingWithHistory.getId());
        assertThat(result).doesNotContain(postingWithoutHistory.getId());
    }

    @Test
    @DisplayName("전달된 공고 ID 리스트에 해당하는 공고가 삭제된다.")
    void deleteJobPostingsByIdsTest() {
        // given
        JobPosting jobPosting1 = jobPostingRepository.save(
                JobPosting.create("company1", "title1", "test1.com", "career", "education", "employmentType", "region", "deadline")
        );
        JobPosting jobPosting2 = jobPostingRepository.save(
                JobPosting.create("company2", "title2", "test2.com", "career", "education", "employmentType", "region", "deadline")
        );

        // when
        jobKoreaService.deleteJobPostings(List.of(jobPosting1.getId()));

        // then
        List<JobPosting> jobPostings = jobPostingRepository.findAll();
        assertThat(jobPostings).hasSize(1);
        assertThat(jobPostings.getFirst().getId()).isEqualTo(jobPosting2.getId());
    }

}
