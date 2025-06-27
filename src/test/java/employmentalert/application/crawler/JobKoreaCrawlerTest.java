package employmentalert.application.crawler;

import employmentalert.application.crawler.dto.JobKoreaPostingInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JobKoreaCrawlerTest {

    @Test
    @DisplayName("JobKorea 채용 공고 20개가 정상적으로 크롤링된다.")
    void jobKoreaCrawlerTest() {
        // given
        JobKoreaCrawler jobKoreaCrawler = new JobKoreaCrawler();

        // when
        List<JobKoreaPostingInfo> jobPostings = jobKoreaCrawler.getJobLinks();
        JobKoreaPostingInfo jobKoreaPostingInfo = jobPostings.getFirst();

        // then
        assertThat(jobPostings).isNotEmpty();
        assertThat(jobPostings).size().isEqualTo(20);
        assertThat(jobKoreaPostingInfo).isNotNull();
        assertThat(jobKoreaPostingInfo.getCompany()).isNotNull();
        assertThat(jobKoreaPostingInfo.getTitle()).isNotNull();
        assertThat(jobKoreaPostingInfo.getUrl()).isNotNull();
        assertThat(jobKoreaPostingInfo.getCareer()).isNotNull();
        assertThat(jobKoreaPostingInfo.getEducation()).isNotNull();
        assertThat(jobKoreaPostingInfo.getEmploymentType()).isNotNull();
        assertThat(jobKoreaPostingInfo.getRegion()).isNotNull();
        assertThat(jobKoreaPostingInfo.getDeadline()).isNotNull();
    }

}
