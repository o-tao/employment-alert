package employmentalert.api.user.controller;

import employmentalert.api.user.controller.dto.UserCreateRequest;
import employmentalert.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    /**
     * 유저 등록
     */
    @PostMapping
    public void create(@RequestBody UserCreateRequest userCreateRequest) {
        userService.create(userCreateRequest.toCreate());
    }
}
