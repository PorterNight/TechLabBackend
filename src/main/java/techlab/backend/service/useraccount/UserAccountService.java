package techlab.backend.service.useraccount;

import techlab.backend.dto.courses.CourseCreateRequest;
import techlab.backend.dto.useraccount.AccoundFundingRequest;
import techlab.backend.dto.useraccount.CoursesId;
import techlab.backend.dto.useraccount.UserAccountResponseDto;
import techlab.backend.repository.jpa.courses.Courses;

import java.math.BigDecimal;
import java.util.List;

public interface UserAccountService {
    UserAccountResponseDto getUserAccountInfo(Long id);
    List<Courses> getAllCourses();

    Courses createCourse(CourseCreateRequest course);

    List<Courses> getCoursesBySearchName(String partialName);

    String confirmUserEmail(String token);

    String confirmUserAccountFunding(String token);

    String userAccountFunding(AccoundFundingRequest request);

}
