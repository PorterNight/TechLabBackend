package techlab.backend.repository.jpa.security;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserSecurityRepository extends JpaRepository<UserSecurity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserSecurity> findByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserSecurity> findByUserUniqueId(Long id);

    List<UserSecurity> findAllByIdBetween(Long id0, long id1);
}
