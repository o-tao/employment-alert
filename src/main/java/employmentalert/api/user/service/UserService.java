package employmentalert.api.user.service;

import employmentalert.api.user.service.dto.UserCreateInfo;
import employmentalert.domain.user.User;
import employmentalert.domain.user.repository.UserRepository;
import employmentalert.global.exception.EmploymentAlertException;
import employmentalert.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 유저 등록
     */
    @Transactional
    public void create(UserCreateInfo userCreateInfo) {
        isEmailDuplicate(userCreateInfo.getEmail());

        userRepository.save(
                User.create(
                        userCreateInfo.getEmail(),
                        userCreateInfo.getCareer(),
                        userCreateInfo.getEducation(),
                        userCreateInfo.getEmploymentType(),
                        userCreateInfo.getRegion()
                )
        );
    }

    /**
     * 유저 단일 조회
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EmploymentAlertException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 이메일 중복 확인
     */
    private void isEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmploymentAlertException(ErrorCode.DUPLICATED_USER_EMAIL);
        }
    }
}
