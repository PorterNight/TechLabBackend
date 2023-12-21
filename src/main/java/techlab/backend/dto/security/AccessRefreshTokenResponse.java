package techlab.backend.dto.security;

public record AccessRefreshTokenResponse(
        Long userUniqueID,
        String accessToken,
        String refreshToken
) {}
