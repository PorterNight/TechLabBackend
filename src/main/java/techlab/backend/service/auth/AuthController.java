package techlab.backend.service.auth;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import techlab.backend.dto.exceptions.ApiErrorResponse;
import techlab.backend.dto.security.*;
import techlab.backend.service.exception.RestResponseException;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(description = "Register a new user by username and password",
            responses = {
                    @ApiResponse(description = "Saving user to database successful", responseCode = "200",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserSignedUpResponseDto.class))}),
                    @ApiResponse(description = "Error, user is already registered", responseCode = "400",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
                    @ApiResponse(description = "Error saving user to database", responseCode = "500",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})})
    @PostMapping("/signup")
    public ResponseEntity<UserSignedUpResponseDto> signUp(@RequestBody UserSignUpRequest request) {
        return ResponseEntity.ok(authService.signUpUser(request));
    }

    @Operation(description = "Log in user and get access JWT token in response",
            responses = {
                    @ApiResponse(description = "Login successful", responseCode = "200",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserSignedInResponseDto.class))}),
                    @ApiResponse(description = "Authentication failed, no user is found", responseCode = "403",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
                    @ApiResponse(description = "User authentication failed", responseCode = "401",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})})
    @PostMapping("/signin")
    public ResponseEntity<UserSignedInResponseDto> signIn(@RequestBody UserSignInRequest request) {
        return ResponseEntity.ok(authService.signInUser(request));
    }

    @Operation(description = "Getting all information about users, from user id1 to user id2, requires the 'admin' authorities")
    //security = @SecurityRequirement(name = "BearerJWT"))
    @ApiResponses({
            @ApiResponse(description = "Getting all information about users successful", responseCode = "200",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = UserSecurityResponseDto.class)))}),
            @ApiResponse(description = "Error getting users information", responseCode = "500",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    @GetMapping("/admin/users/{id1}-{id2}")
    public ResponseEntity<List<UserSecurityResponseDto>> adminGetUserInfo(@PathVariable Long id1, @PathVariable Long id2) {
        List<UserSecurityResponseDto> result = authService.getAllUsers(id1, id2);
        return ResponseEntity.ok(result);
    }

    @Operation(description = "Updating information about the user, requires the 'admin' authorities",
            responses = {
                    @ApiResponse(description = "Updating information about the user was successful", responseCode = "200",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserSecurityResponseDto.class))}),
                    @ApiResponse(description = "Error updating information about user information", responseCode = "400",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})})
    @PutMapping("/admin/users")
    public ResponseEntity<UserSecurityResponseDto> adminUpdateUserInfo(@RequestBody UserInfoUpdateRequest request) {
        UserSecurityResponseDto result = authService.updateUserInfo(request);
        return ResponseEntity.ok(result);
    }

    @Hidden
    @PostMapping("/testException")
    public ResponseEntity<String> testException() {
        if (true) {
            throw new RestResponseException("fwfwfef", 401);
        }
        return ResponseEntity.ok("ok");
    }
}
