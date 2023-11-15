package techlab.backend.service.useraccount;

import techlab.backend.dto.courses.CourseCreateRequest;
import techlab.backend.dto.useraccount.UserAccountInfoDTO;
import techlab.backend.repository.jpa.courses.Courses;

import java.util.List;

public interface UserAccountService {
    UserAccountInfoDTO getUserAccountInfo(Long id);
    List<Courses> getAllCourses();

    Courses createCourse(CourseCreateRequest course);

    List<Courses> getCoursesBySearchName(String partialName);
}
