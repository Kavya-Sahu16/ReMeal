package com.kavya.remeal.user;

import com.kavya.remeal.user.dto.ProfileResponse;
import com.kavya.remeal.user.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ProfileResponse getMyProfile(
            @AuthenticationPrincipal User user) {

        return userService.getProfile(user.getId());
    }

    @PutMapping("/me")
    public ProfileResponse updateMyProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfileRequest request) {

        return userService.updateProfile(user.getId(), request);
    }
}