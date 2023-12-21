package techlab.backend.dto.security;

public record UserLogOut(
        Long userUniqueId,
        String refreshToken
) {}
