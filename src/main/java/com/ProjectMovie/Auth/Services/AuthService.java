package com.ProjectMovie.Auth.Services;

import com.Exceptions.UserAlreadyExistsException;
import com.ProjectMovie.Auth.Entities.User;
import com.ProjectMovie.Auth.Entities.UserRole;
import com.ProjectMovie.Auth.Repositories.UserRepositories;
import com.ProjectMovie.Exceptions.UserException;
import com.ProjectMovie.Utils.AuthResponse;
import com.ProjectMovie.Utils.LoginRequest;
import com.ProjectMovie.Utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepositories userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        User userSaved = userRepository.save(user);

        var accessToken = jwtService.generateToken(userSaved);
        var refreshToken = refreshTokenService.createRefreshToken(userSaved.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .name(userSaved.getName())
                .email(userSaved.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        System.out.println("email: " + email);
        System.out.println("password: " + password);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException("User not found"));

        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
