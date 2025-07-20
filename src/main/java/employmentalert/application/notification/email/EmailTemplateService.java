package employmentalert.application.notification.email;

import employmentalert.domain.jobPosting.JobPosting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final SpringTemplateEngine springTemplateEngine;

    /**
     * 채용공고 이메일 템플릿
     */
    public String buildJobPostingEmailContent(List<JobPosting> jobPostings) {
        Context context = new Context();
        context.setVariable("jobCount", jobPostings.size());
        context.setVariable("jobList", jobPostings);
        return springTemplateEngine.process("email/job-posting-email", context);
    }
}
