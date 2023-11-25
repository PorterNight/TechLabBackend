package techlab.backend.service.useraccount;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import techlab.backend.dto.courses.CourseCreateRequest;
import techlab.backend.dto.useraccount.CoursesId;
import techlab.backend.dto.useraccount.UserAccountResponseDto;
import techlab.backend.repository.jpa.courses.Courses;
import techlab.backend.repository.jpa.courses.CoursesRepository;
import techlab.backend.repository.jpa.security.UserSecurity;
import techlab.backend.repository.jpa.security.UserSecurityRepository;
import techlab.backend.service.exception.RestResponseException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final UserSecurityRepository userSecurityRepository;
    private final CoursesRepository coursesRepository;

    public UserAccountServiceImpl(UserSecurityRepository userSecurityRepository, CoursesRepository coursesRepository) {
        this.userSecurityRepository = userSecurityRepository;
        this.coursesRepository = coursesRepository;
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
}
