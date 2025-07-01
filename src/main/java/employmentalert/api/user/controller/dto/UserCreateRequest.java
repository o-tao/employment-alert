package employmentalert.api.user.controller.dto;

import employmentalert.api.user.service.dto.UserCreateInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCreateRequest {

    private String email;
    private String career;
    private String education;
    private String employmentType;
    private String region;

    public UserCreateRequest(String email, String career, String education, String employmentType, String region) {
        this.email = email;
        this.career = career;
        this.education = education;
        this.employmentType = employmentType;
        this.region = region;
    }

    public UserCreateInfo toCreate() {
        return new UserCreateInfo(email, career, education, employmentType, region);
    }
}
