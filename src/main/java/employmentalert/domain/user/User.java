package employmentalert.domain.user;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends BaseEntity {

    @Comment("이메일")
    @Column(nullable = false)
    private String email;

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
}
