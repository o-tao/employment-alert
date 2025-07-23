package employmentalert.api.user.service;

import employmentalert.api.user.service.dto.UserCreateInfo;
import employmentalert.domain.user.User;
import employmentalert.domain.user.repository.UserRepository;
import employmentalert.global.exception.EmploymentAlertException;
import employmentalert.global.exception.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void clean() {
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("유저가 정상적으로 등록된다.")
    public void createUserSuccessTest() {
        // given
        UserCreateInfo userCreateInfo = new UserCreateInfo(
                "test@example.com",
                "career",
                "education",
                "employmentType",
                "region"
        );

        // when
        userService.create(userCreateInfo);

        // then
        User user = userRepository.findAll().getFirst();
        assertThat(user.getEmail()).isEqualTo(userCreateInfo.getEmail());
        assertThat(user.getCareer()).isEqualTo(userCreateInfo.getCareer());
        assertThat(user.getEducation()).isEqualTo(userCreateInfo.getEducation());
        assertThat(user.getEmploymentType()).isEqualTo(userCreateInfo.getEmploymentType());
        assertThat(user.getRegion()).isEqualTo(userCreateInfo.getRegion());
    }

    @Test
    @DisplayName("이미 등록된 이메일로 유저를 등록할 경우 예외가 발생한다.")
    public void createUserDuplicateEmailTest() {
        // given
        UserCreateInfo userCreateInfo = new UserCreateInfo(
                "test@example.com", "career", "education", "employmentType", "region"
        );
        userService.create(userCreateInfo);

        // when
        EmploymentAlertException exception = assertThrows(EmploymentAlertException.class, () -> userService.create(userCreateInfo));

        // then
        assertThat(exception.getClass()).isEqualTo(EmploymentAlertException.class);
        assertThat(exception.getExceptionMessage()).isEqualTo(ErrorCode.DUPLICATED_USER_EMAIL.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 유저를 조회하면 예외가 발생한다.")
    void getUserByIdFailTest() {
        // given & when
        EmploymentAlertException exception = assertThrows(EmploymentAlertException.class, () -> userService.getUserById(999L));

        // then
        assertThat(exception.getClass()).isEqualTo(EmploymentAlertException.class);
        assertThat(exception.getExceptionMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}
