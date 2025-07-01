package employmentalert.api.user.service;

import employmentalert.api.user.service.dto.UserCreateInfo;
import employmentalert.domain.user.User;
import employmentalert.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 유저 등록
     */
    public void create(UserCreateInfo userCreateInfo) {
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
}
