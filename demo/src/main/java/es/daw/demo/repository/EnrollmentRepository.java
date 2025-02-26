package es.daw.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import es.daw.demo.model.Enrollment;
import es.daw.demo.model.User;
import es.daw.demo.model.Course;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUser(User user);

    List<Enrollment> findByCourse(Course course);

    List<Enrollment> findByCourseIdAndRatingIsNotNull(Long courseId);
}
