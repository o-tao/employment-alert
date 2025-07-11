package employmentalert.domain.jobPosting;

import employmentalert.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Table(name = "job_postings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPosting extends BaseEntity {

    @Comment("회사명")
    @Column(nullable = false)
    private String company;

    @Comment("공고 제목")
    @Column(nullable = false)
    private String title;

    @Comment("공고 주소")
    @Column(nullable = false, unique = true)
    private String url;

    @Comment("경력사항")
    @Column(nullable = true)
    private String career;

    @Comment("학력")
    @Column(nullable = true)
    private String education;

    @Comment("고용 형태")
    @Column(nullable = true)
    private String employmentType;

    @Comment("지역")
    @Column(nullable = true)
    private String region;

    @Comment("공고 마감일")
    @Column(nullable = true)
    private String deadline;

    public JobPosting(String company,
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

    public static JobPosting create(
            String company,
            String title,
            String url,
            String career,
            String education,
            String employmentType,
            String region,
            String deadline
    ) {
        return new JobPosting(
                company,
                title,
                url,
                career,
                education,
                employmentType,
                region,
                deadline);
    }
}
