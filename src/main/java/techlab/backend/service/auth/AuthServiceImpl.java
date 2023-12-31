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
import techlab.backend.repository.jpa.security.Account;
import techlab.backend.repository.jpa.security.UserSecurity;
import techlab.backend.repository.jpa.security.UserSecurityRepository;
import techlab.backend.security.JwtTokenProvider;
import techlab.backend.service.exception.RestResponseException;
import techlab.backend.service.snowflake.SnowFlake;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;

@Slf4j
@Component
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserSecurityRepository userSecurityRepository;
    private final CoursesRepository coursesRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SnowFlake snowFlake;
    private final EmailService emailService;
    private final ConfirmationTokenService confirmationTokenService;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserSecurityRepository userSecurityRepository, CoursesRepository coursesRepository, JwtTokenProvider jwtTokenProvider, SnowFlake snowFlake, EmailService emailService, ConfirmationTokenService confirmationTokenService) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userSecurityRepository = userSecurityRepository;
        this.coursesRepository = coursesRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.snowFlake = snowFlake;
        this.emailService = emailService;
        this.confirmationTokenService = confirmationTokenService;
    }

    public List<UserSecurityResponseDto> getAllUsers(Long id1, Long id2) {
        try {
            List<UserSecurity> userSecurities = userSecurityRepository.findAllByIdBetween(id1, id2);
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
        } catch (Exception e) {
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
        userSecuritySave.setStatus("email is not confirmed");
        userSecuritySave.setRole("user");
        userSecuritySave.setUserUniqueId(snowFlake.nextId());
        userSecuritySave.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        Account account = new Account();
        account.setBalance(new BigDecimal("100"));
        account.setFundedAmount(new BigDecimal("10"));
        account.setCurrencyType("usd");
        account.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        userSecuritySave.setAccount(account);

        if (userSecurityRepository.findByName(usernamePasswordDto.username()).isPresent()) throw
                new RestResponseException("User '" + usernamePasswordDto.username() + "' is already registered", 400);

        UserSecurity savedUser;
        try {
            savedUser = userSecurityRepository.saveAndFlush(userSecuritySave);
            //emailService.sendUserEmailConfirmation(savedUser.getEmail(), uuid);
            String uuid = UUID.randomUUID().toString();
            Long userUniqueId = savedUser.getUserUniqueId();

            log.info("confirmationTokenService.storeToken: " + "uuid:" + uuid + " userUniqueId:" + userUniqueId);

            confirmationTokenService.storeToken(uuid, userUniqueId, 48, HOURS);
            return new UserSignedUpResponseDto(savedUser.getName(), savedUser.isEnabled(), savedUser.getUserUniqueId(), savedUser.getRole());
        } catch (Exception e) {
            log.warn(e.getMessage());
            e.printStackTrace();
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


            String accessToken = jwtTokenProvider.createAccessToken(usernamePasswordDto.username(), user.getRole());
            String refreshToken = jwtTokenProvider.createRefreshToken(usernamePasswordDto.username(), user.getRole());
            return new UserSignedInResponseDto(user.getName(), user.getUserUniqueId(), accessToken, refreshToken);
        } catch (AuthenticationException e) {
            log.info(String.valueOf(e));
            throw new RestResponseException("User authentication failed", 401);
        }
    }

    @Transactional
    @Override
    public String userLogOut(UserLogOut userLogOut) {
        confirmationTokenService.storeToken(userLogOut.refreshToken(), userLogOut.userUniqueId(), 7, DAYS);
        return "user logout completed";
    }

    @Transactional
    @Override
    public AccessRefreshTokenResponse issueAccessRefreshToken(String refreshToken) {

        Long userUniqueID = confirmationTokenService.getByToken(refreshToken);

        if (userUniqueID != null) {
            throw new RestResponseException("refresh token is invalidated", 400);
        } else {

            String username = jwtTokenProvider.getUsernameFromRefreshToken(refreshToken);

            UserSecurity userSecurity = userSecurityRepository.findByName(username).orElseThrow(() ->
                    new RestResponseException("No valid token for user '" + username + "' is found", 400));

            String newAccessToken = jwtTokenProvider.createAccessToken(username, userSecurity.getRole());
            String newRefreshToken = jwtTokenProvider.createRefreshToken(username, userSecurity.getRole());

            confirmationTokenService.storeToken(refreshToken, userSecurity.getUserUniqueId(), 7, DAYS);

            return new AccessRefreshTokenResponse(userSecurity.getUserUniqueId(), newAccessToken, newRefreshToken);
        }
    }
}
