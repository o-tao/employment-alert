package employmentalert.domain.jobPosting.repository;

import employmentalert.domain.jobPosting.JobPosting;
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

    @AfterEach
    void clean() {
        jobPostingRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("DB에 저장된 url이 있을 때 존재하는 url만 반환한다.")
    public void findExistingUrls() {
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
}
