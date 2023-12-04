package techlab.backend.repository.jpa.security;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import techlab.backend.repository.jpa.courses.Courses;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Data
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private UserSecurity users;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "funded_amount")
    private BigDecimal fundedAmount;

    @Column(name = "currency_type")
    private String currencyType;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
