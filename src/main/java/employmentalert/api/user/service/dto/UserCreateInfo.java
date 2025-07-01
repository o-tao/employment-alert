package employmentalert.api.user.service.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCreateInfo {

    private String email;
    private String career;
    private String education;
    private String employmentType;
    private String region;

    public UserCreateInfo(String email, String career, String education, String employmentType, String region) {
        this.email = email;
        this.career = career;
        this.education = education;
        this.employmentType = employmentType;
        this.region = region;
    }
}
