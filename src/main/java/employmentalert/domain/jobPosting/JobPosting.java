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
@Table(name = "job_posting")
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
    @Column(nullable = false)
    private String career;

    @Comment("학력")
    @Column(nullable = false)
    private String education;

    @Comment("고용 형태")
    @Column(nullable = false)
    private String employmentType;

    @Comment("지역")
    @Column(nullable = false)
    private String region;

    @Comment("공고 마감일")
    @Column(nullable = false)
    private String deadline;

}
