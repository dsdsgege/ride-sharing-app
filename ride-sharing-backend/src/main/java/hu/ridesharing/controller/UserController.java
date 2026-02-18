package hu.ridesharing.controller;
import hu.ridesharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/find/profiles")
    public UsersResponse findUsersByUsernames(@RequestBody   UsersRequest request) {
        return new UsersResponse(
                userService.findAllFullNameByUsernames(request.usernames())
                        .entrySet().stream()
                        .map(entry -> new UserDTO(entry.getKey(), entry.getValue()))
                        .toList()
        );
    }

    record UsersResponse(List<UserDTO> users) {
    }

    record UsersRequest(List<String> usernames) {
    }

    record UserDTO(String username, String fullName) {
    }
}
