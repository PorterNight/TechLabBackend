package techlab.backend.dto.security;

public record UserSignedUpResponseDto(String username, boolean registered, Long userUniqueId, String role) {
}
