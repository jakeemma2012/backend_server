package com.ProjectMovie.Auth.Services;

import com.ProjectMovie.Auth.Entities.RefreshToken;
import com.ProjectMovie.Auth.Entities.User;
import com.ProjectMovie.Auth.Repositories.RefreshTokenRepositories;
import com.ProjectMovie.Auth.Repositories.UserRepositories;
import com.ProjectMovie.Exceptions.RefeshTokenExpried;
import com.ProjectMovie.Exceptions.UserException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private RefreshTokenRepositories refreshTokenRepository;
    private UserRepositories userRepositories;

    public RefreshTokenService(RefreshTokenRepositories refreshTokenRepository, UserRepositories userRepositories) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepositories = userRepositories;
    }

    public RefreshToken createRefreshToken(String email) {
        User user = userRepositories.findByEmail(email)
                .orElseThrow(() -> new UserException("Không tìm thấy User có email : " + email));

        RefreshToken refreshToken = user.getRefreshToken();

        long refreshTokenDurationMs = 5 * 60 * 60 * 10000;
        if (refreshToken == null) {
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expiryTime(Instant.now().plusMillis(refreshTokenDurationMs))
                    .user(user)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new UserException("Không tìm thấy Refresh Token"));

        if (refreshToken.getExpiryTime().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefeshTokenExpried("Refresh token đã hết hạn, vui lòng đăng nhập lại");
        }
        return refreshToken;
    }

}
