package techlab.backend.repository.jpa.courses;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @JsonBackReference
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_course",
            joinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private Set<UserSecurity> users = new HashSet<>();
}
