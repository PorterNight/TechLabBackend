package techlab.backend.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import techlab.backend.dto.security.*;
import techlab.backend.repository.jpa.courses.CoursesRepository;
import techlab.backend.repository.jpa.security.UserSecurity;
import techlab.backend.repository.jpa.security.UserSecurityRepository;
import techlab.backend.security.JwtTokenProvider;
import techlab.backend.service.exception.RestResponseException;
import techlab.backend.service.snowflake.SnowFlake;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserSecurityRepository userSecurityRepository;
    private final CoursesRepository coursesRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SnowFlake snowFlake;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserSecurityRepository userSecurityRepository, CoursesRepository coursesRepository, JwtTokenProvider jwtTokenProvider, SnowFlake snowFlake) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userSecurityRepository = userSecurityRepository;
        this.coursesRepository = coursesRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.snowFlake = snowFlake;
    }

    public List<UserSecurityResponseDto> getAllUsers(Long id1, Long id2) {
        try {
            List<UserSecurity>  userSecurities =userSecurityRepository.findAllByIdBetween(id1, id2);
            return userSecurities.stream()
                    .map(user -> new UserSecurityResponseDto(
                            user.getId(),
                            user.getUserUniqueId(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole(),
                            user.getStatus(),
                            user.getCreatedAt(),
                            user.getCourses()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e){
            log.warn("getAllUsers() : error getting users information");
            throw new RestResponseException("Error getting users information", 500);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public UserSecurityResponseDto updateUserInfo(UserInfoUpdateRequest userUpdateRequest) {

        Optional<UserSecurity> userOptional = userSecurityRepository.findByUserUniqueId(userUpdateRequest.userUniqueId());

        userOptional.ifPresent(user -> {
                    user.setUserUniqueId(userUpdateRequest.userUniqueId());
                    user.setName(userUpdateRequest.name());
                    user.setEmail(userUpdateRequest.email());
                    user.setRole(userUpdateRequest.role());
                }
        );
        throw new RestResponseException("User update error", 400);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public UserSignedUpResponseDto signUpUser(UserSignUpRequest usernamePasswordDto) {

        UserSecurity userSecuritySave = new UserSecurity();
        userSecuritySave.setName(usernamePasswordDto.username());
        userSecuritySave.setEmail(usernamePasswordDto.email());
        userSecuritySave.setPassword(passwordEncoder.encode(usernamePasswordDto.password()));
        userSecuritySave.setStatus("active");
        userSecuritySave.setRole("user");
        userSecuritySave.setUserUniqueId(snowFlake.nextId());
        userSecuritySave.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        if (userSecurityRepository.findByName(usernamePasswordDto.username()).isPresent()) throw
                new RestResponseException("User '" + usernamePasswordDto.username() + "' is already registered", 400);

        UserSecurity savedUser;
        try {
            savedUser = userSecurityRepository.saveAndFlush(userSecuritySave);
            return new UserSignedUpResponseDto(savedUser.getName(), savedUser.isEnabled(), savedUser.getUserUniqueId(), savedUser.getRole());
        } catch (Exception e) {
            throw new RestResponseException("Error saving to database user:" + usernamePasswordDto.username(), 500);
        }
    }

    @Transactional
    @Override
    public UserSignedInResponseDto signInUser(UserSignInRequest usernamePasswordDto) {

        try {
            UserSecurity user = userSecurityRepository.findByName(usernamePasswordDto.username()).orElseThrow(() ->
                    new RestResponseException("No user '" + usernamePasswordDto.username() + "' is found", 403));

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernamePasswordDto.username(),
                    usernamePasswordDto.password()));

            log.info("PostMapping UserSecurity = " + user);
            String accessToken = jwtTokenProvider.createAccessToken(usernamePasswordDto.username(), user.getRole());
            String refreshToken = jwtTokenProvider.createRefreshToken(usernamePasswordDto.username(), user.getRole());
            return new UserSignedInResponseDto(user.getName(), user.getUserUniqueId(), accessToken, refreshToken);
        } catch (AuthenticationException e) {
            throw new RestResponseException("User authentication failed", 401);
        }
    }
}
