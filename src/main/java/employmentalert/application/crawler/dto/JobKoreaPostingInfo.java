package employmentalert.application.crawler.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobKoreaPostingInfo {
    private String company;
    private String title;
    private String url;
    private String career;
    private String education;
    private String employmentType;
    private String region;
    private String deadline;

    public JobKoreaPostingInfo(String company,
                               String title,
                               String url,
                               String career,
                               String education,
                               String employmentType,
                               String region,
                               String deadline
    ) {
        this.company = company;
        this.title = title;
        this.url = url;
        this.career = career;
        this.education = education;
        this.employmentType = employmentType;
        this.region = region;
        this.deadline = deadline;
    }
}
