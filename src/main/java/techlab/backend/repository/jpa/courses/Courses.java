package techlab.backend.repository.jpa.courses;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import techlab.backend.repository.jpa.security.UserSecurity;

import java.time.OffsetDateTime;
import java.util.*;

@Data
@EqualsAndHashCode(exclude = "users")
@Entity
@Table(name = "courses")
public class Courses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "group_learning")
    private String groupLearning;

    @Column(name = "self_placed_learning")
    private String selfPlacedLearning;

    @Column(name = "unit_of_lessons")
    private String unitOfLessons;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

//    @JsonBackReference
    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_course",
            joinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private Set<UserSecurity> users = new HashSet<>();

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "unit_courses",
            joinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "unit_id", referencedColumnName = "id")
    )
    private Set<UnitOfCourse> unitOfCourses = new HashSet<>();

}
