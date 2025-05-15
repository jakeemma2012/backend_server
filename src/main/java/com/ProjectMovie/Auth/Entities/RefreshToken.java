package com.ProjectMovie.Auth.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tokenId;

    @Column(nullable = false, length = 500)
    @NotBlank(message = "Refresh token không được để trống")
    private String refreshToken;

    @Column(nullable = false)
    private Instant expiryTime;

    @OneToOne
    private User user;

}
