package com.kavya.remeal.user;

import com.kavya.remeal.user.dto.ProfileResponse;
import com.kavya.remeal.user.dto.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ProfileResponse getProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        return mapToProfileResponse(user);
    }

    public ProfileResponse updateProfile(
            Long userId, UpdateProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        user.setName(request.name());
        user.setContactNumber(request.contactNumber());
        user.setOrganisationName(request.organisationName());
        user.setCity(request.city());
        user.setAddress(request.address());

        User updatedUser = userRepository.save(user);

        return mapToProfileResponse(updatedUser);
    }

    private ProfileResponse mapToProfileResponse(User user) {

        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getContactNumber(),
                user.getRole(),
                user.getOrganisationName(),
                user.getCity(),
                user.getAddress()
        );
    }
}