package techlab.backend.service.useraccount;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import techlab.backend.dto.courses.CourseCreateRequest;
import techlab.backend.dto.useraccount.AccoundFundingRequest;
import techlab.backend.dto.useraccount.UserAccountResponseDto;
import techlab.backend.repository.jpa.courses.Courses;
import techlab.backend.repository.jpa.courses.CoursesRepository;
import techlab.backend.repository.jpa.security.Account;
import techlab.backend.repository.jpa.security.UserSecurity;
import techlab.backend.repository.jpa.security.UserSecurityRepository;
import techlab.backend.service.auth.ConfirmationTokenService;
import techlab.backend.service.exception.RestResponseException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.HOURS;

@Slf4j
@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final UserSecurityRepository userSecurityRepository;
    private final CoursesRepository coursesRepository;
    private final ConfirmationTokenService confirmationTokenService;

    public UserAccountServiceImpl(UserSecurityRepository userSecurityRepository, CoursesRepository coursesRepository, ConfirmationTokenService confirmationTokenService) {
        this.userSecurityRepository = userSecurityRepository;
        this.coursesRepository = coursesRepository;
        this.confirmationTokenService = confirmationTokenService;
    }

    @Transactional
    @Override
    public UserAccountResponseDto getUserAccountInfo(Long id) {
        Optional<UserSecurity> userSecurities = userSecurityRepository.findByUserUniqueId(id);

        if (userSecurities.isPresent()) {
            UserSecurity user = userSecurities.get();
            return new UserAccountResponseDto(
                    user.getUserUniqueId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getStatus(),
                    user.getCreatedAt(),
                    user.getCourses()
            );
        }
        throw new RestResponseException("No user is found", 400);
    }

    @Override
    public List<Courses> getAllCourses() {
        List<Courses> courses = coursesRepository.findAllByIdBetween(0L, 111L);
        log.info(String.valueOf(courses));
        return courses;
    }

    @Override
    public Courses createCourse(CourseCreateRequest course) {
        Optional<Courses> foundCourse = coursesRepository.findByName(course.name());
        if (foundCourse.isEmpty()) {
            Courses newCourse = new Courses();
            newCourse.setName(course.name());
            newCourse.setDescription(course.description());
            newCourse.setType(course.type());
            newCourse.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            return coursesRepository.saveAndFlush(newCourse);
        } else {
            throw new RestResponseException("Course with this name is already exists: " + course.name(), 400);
        }
    }

    @Override
    public List<Courses> getCoursesBySearchName(String partialName) {
        List<Courses> courses = coursesRepository.findByNameContainingIgnoreCase(partialName);
        log.info(String.valueOf(courses));
        return courses;
    }

    @Transactional
    @Override
    public String confirmUserEmail(String token) {
        Long result = confirmationTokenService.getTokenAndDelete(token);

        if (result == null) {
            log.info("[confirmUserEmail], token is not found: " + token);
            throw new RestResponseException("Email confirmation failed, no token is found", 400);
        } else {
            UserSecurity user = userSecurityRepository.findByUserUniqueId(result).orElseThrow();
            user.setStatus("activated");
            userSecurityRepository.save(user);
            log.info("[confirmUserEmail], user: " + user.getName() + " confirmed email");
        }
        return "Email is confirmed";
    }

    @Transactional
    @Override
    public String confirmUserAccountFunding(String token) {
        Long result = confirmationTokenService.getTokenAndDelete(token);

        if (result == null) {
            log.info("[confirmUserAccountFunding], token is not found: " + token);
            throw new RestResponseException("Account funding confirmation failed, no token is found", 400);
        } else {
            UserSecurity user = userSecurityRepository.findByUserUniqueId(result).orElseThrow();
            Account account = user.getAccount();
            account.setBalance(account.getBalance().add(account.getFundedAmount()));

            userSecurityRepository.save(user);
            log.info("[confirmUserAccountFunding], user: " + user.getName() + " account is funded");
        }
        return "Account is funded";
    }

    @Transactional
    @Override
    public String userAccountFunding(AccoundFundingRequest request) {
        UserSecurity user = userSecurityRepository.findByUserUniqueId(request.userUniqueID()).orElseThrow(() ->
                new RestResponseException("No user is found for account funding", 400));

        user.getAccount().setFundedAmount(request.account_funding_sum());
        userSecurityRepository.save(user);
//emailService.sendUserEmailConfirmation(savedUser.getEmail(), uuid);
        String uuid = UUID.randomUUID().toString();
        confirmationTokenService.storeToken(uuid, request.userUniqueID(), 48, HOURS);
        return "account funding updated";
    }
}
