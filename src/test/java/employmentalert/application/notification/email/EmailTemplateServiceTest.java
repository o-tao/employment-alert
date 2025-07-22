package employmentalert.application.notification.email;

import employmentalert.domain.jobPosting.JobPosting;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EmailTemplateServiceTest {

    private final List<JobPosting> postings = List.of(
            new JobPosting("company", "title", "test.com", "career", "education", "employmentType", "region", "deadline")
    );
    @Mock
    private SpringTemplateEngine springTemplateEngine;
    @InjectMocks
    private EmailTemplateService emailTemplateService;

    @Test
    @DisplayName("채용공고 목록을 전달하면 템플릿 엔진이 HTML을 생성한다.")
    void buildJobPostingEmailContent() {
        // given
        String expectedHtml = "<html><body>테스트 이메일 본문</body></html>";

        when(springTemplateEngine.process(eq("email/job-posting-email"), any(Context.class)))
                .thenReturn(expectedHtml);

        // when
        String result = emailTemplateService.buildJobPostingEmailContent(postings);

        // then
        assertThat(result).isEqualTo(expectedHtml);

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(springTemplateEngine).process(eq("email/job-posting-email"), contextCaptor.capture());

        Context capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.getVariable("jobCount")).isEqualTo(1);
        assertThat(capturedContext.getVariable("jobList")).isEqualTo(postings);
    }
}
