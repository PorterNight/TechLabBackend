package techlab.backend.dto.security;

public record UserSignedInResponseDto(String username, Long uniqueUserId, String token) {
}
