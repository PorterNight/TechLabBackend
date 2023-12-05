package techlab.backend.service.useraccount;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import techlab.backend.dto.courses.CourseCreateRequest;
import techlab.backend.dto.exceptions.ApiErrorResponse;
import techlab.backend.dto.useraccount.AccoundFundingRequest;
import techlab.backend.dto.useraccount.UserAccountResponseDto;
import techlab.backend.repository.jpa.courses.Courses;
import techlab.backend.repository.jpa.security.Account;
import techlab.backend.repository.jpa.security.UserSecurity;
import techlab.backend.service.auth.ConfirmationTokenService;
import techlab.backend.service.exception.RestResponseException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Operation(description = "Getting user account information")
    @ApiResponses({
            @ApiResponse(description = "Getting user account information successful", responseCode = "200",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserAccountResponseDto.class))}),
            @ApiResponse(description = "No user is found", responseCode = "400",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))})})
    @GetMapping("/users/{id}")
    public ResponseEntity<UserAccountResponseDto> getUserAccountInfo(@PathVariable Long id) {
        UserAccountResponseDto result = userAccountService.getUserAccountInfo(id);
        return ResponseEntity.ok(result);
    }

    @Operation(description = "Getting information of all courses")
    @ApiResponses({
            @ApiResponse(description = "\"Getting information of all courses successful", responseCode = "200",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = Courses.class)))}),
    })
    @GetMapping("/courses")
    public ResponseEntity<List<Courses>> getCoursesInfo() {
        List<Courses> result = userAccountService.getAllCourses();
        return ResponseEntity.ok(result);
    }

    @Operation(description = "Create a new course")
    @ApiResponses({
            @ApiResponse(description = "Create a new course was successful", responseCode = "200",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Courses.class))}),
            @ApiResponse(description = "Course with this name is already exists", responseCode = "400",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))})})
    @PostMapping("/courses")
    public ResponseEntity<Courses> createCourse(@RequestBody CourseCreateRequest request) {
        Courses result = userAccountService.createCourse(request);
        return ResponseEntity.status(201).body(result);
    }

    @Operation(description = "Getting information of courses by partial name")
    @GetMapping("/courses/{name}")
    public ResponseEntity<List<Courses>> getCoursesInfo(@PathVariable String name) {
        List<Courses> result = userAccountService.getCoursesBySearchName(name);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/account-funding")
    public ResponseEntity<String> accountFunding(@RequestBody AccoundFundingRequest request) {
        String result = userAccountService.userAccountFunding(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestParam String token) {
        String result = userAccountService.confirmUserEmail(token);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/confirm-account-funding")
    public ResponseEntity<String> confirmAccountFinding(@RequestParam String token) {
        String result = userAccountService.confirmUserAccountFunding(token);
        return ResponseEntity.ok(result);
    }


//    public ResponseEntity<String> buyCourses(@RequestBody CoursesId request) {
//        String result = userAccountService.buyCourse(request);
//        return ResponseEntity.ok();
//    }

}
