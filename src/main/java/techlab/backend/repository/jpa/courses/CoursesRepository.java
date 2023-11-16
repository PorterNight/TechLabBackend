package techlab.backend.repository.jpa.courses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CoursesRepository extends JpaRepository<Courses, Long> {

    Optional<Courses> findByName(String name);

    List<Courses> findByNameContaining(String name);

    List<Courses> findAllByIdBetween(Long id0, Long id1);

}
