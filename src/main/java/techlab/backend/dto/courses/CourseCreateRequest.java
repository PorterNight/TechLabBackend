package techlab.backend.dto.courses;

public record CourseCreateRequest(
        String name,
        String type,
        String description
) {}
