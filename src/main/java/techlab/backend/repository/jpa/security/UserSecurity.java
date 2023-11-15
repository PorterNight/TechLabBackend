package techlab.backend.repository.jpa.security;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import techlab.backend.repository.jpa.courses.Courses;

import java.time.OffsetDateTime;
import java.util.*;

@Data
@Entity
@Table(name = "users")
public class UserSecurity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_unique_id")
    private Long userUniqueId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    private String role;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    //@JsonManagedReference
    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Courses> courses = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.role));
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return this.status.equalsIgnoreCase("active");
    }
}
