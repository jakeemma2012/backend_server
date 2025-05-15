package com.ProjectMovie.Controllers;

import com.ProjectMovie.Auth.Entities.RefreshToken;
import com.ProjectMovie.Auth.Services.AuthService;
import com.ProjectMovie.Auth.Services.JwtService;
import com.ProjectMovie.Auth.Services.RefreshTokenService;
import com.ProjectMovie.Utils.AuthResponse;
import com.ProjectMovie.Utils.LoginRequest;
import com.ProjectMovie.Utils.RefreshTokenRequest;
import com.ProjectMovie.Utils.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshToken refreshTokenRequest = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        var user = refreshTokenRequest.getUser();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(token)
                .refreshToken(request.getRefreshToken())
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam("refreshToken") String refreshToken) {
        System.out.println("LOGING OUT ....");
        System.out.println("refreshToken: " + refreshToken);
        refreshTokenService.deleteByToken(refreshToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(@RequestParam("token") String token) {
        return refreshTokenService.getCurrentUser(token);
    }

    // @PutMapping("/api/v1/users/profile")
    // @PreAuthorize("isAuthenticated()")
    // public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest
    // request) {
    // return ResponseEntity.ok("Hello World");
    // // Implementation
    // }
}
