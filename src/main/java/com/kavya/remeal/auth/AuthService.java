package com.kavya.remeal.auth;

import com.kavya.remeal.auth.dto.AuthResponse;
import com.kavya.remeal.auth.dto.LoginRequest;
import com.kavya.remeal.auth.dto.RegisterRequest;
import com.kavya.remeal.security.JwtService;
import com.kavya.remeal.user.User;
import com.kavya.remeal.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        User user = new User();

        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(
                passwordEncoder.encode(request.password())
        );
        user.setContactNumber(request.contactNumber());
        user.setRole(request.role());
        user.setOrganisationName(request.organisationName());
        user.setCity(request.city().trim());
        user.setAddress(request.address().trim());

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return buildAuthResponse(savedUser, token);
    }

    public AuthResponse login(LoginRequest request) {

        String email = request.email().trim().toLowerCase();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            request.password()
                    )
            );

        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                    )
                );

        String token = jwtService.generateToken(user);

        return buildAuthResponse(user, token);
    }

    private AuthResponse buildAuthResponse(
            User user,
            String token) {

        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}