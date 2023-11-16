package techlab.backend.dto.security;

public record UserSignUpRequest(String username, String email, String password) {
}
