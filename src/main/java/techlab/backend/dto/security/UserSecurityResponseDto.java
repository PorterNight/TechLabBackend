package techlab.backend.dto.security;

import techlab.backend.repository.jpa.courses.Courses;

import java.time.OffsetDateTime;
import java.util.Set;

public record UserSecurityResponseDto(
    Long id,
    Long userUniqueId,
    String name,
    String email,
    String role,
    String status,
    OffsetDateTime registeredAt,
    Set<Courses> courses
){}
