package techlab.backend.dto.security;

public record UserInfoUpdateRequest(
        Long userUniqueId,
        String name,
        String email,
        String role,
        String status
) {}
