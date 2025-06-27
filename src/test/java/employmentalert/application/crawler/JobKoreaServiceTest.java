package employmentalert.application.crawler;

import employmentalert.application.crawler.dto.JobKoreaPostingInfo;
import employmentalert.domain.jobPosting.JobPosting;
import employmentalert.domain.jobPosting.repository.JobPostingRepository;
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

    @AfterEach
    public void clear() {
        jobPostingRepository.deleteAllInBatch();
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

}
