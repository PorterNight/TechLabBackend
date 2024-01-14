package techlab.backend.repository.jpa.courses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import techlab.backend.repository.jpa.security.UserSecurity;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "unit_of_course")
public class UnitOfCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String name;

    @Column(name = "body")
    private String type;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ManyToMany(mappedBy = "unitOfCourses", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Courses> courses = new HashSet<>();
}
