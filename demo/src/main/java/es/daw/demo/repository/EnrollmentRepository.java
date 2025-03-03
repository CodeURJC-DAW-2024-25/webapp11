package es.daw.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.daw.demo.model.Enrollment;
import es.daw.demo.model.User;
import es.daw.demo.model.Course;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUser(User user);

    List<Enrollment> findByCourse(Course course);

    List<Enrollment> findByCourseIdAndRatingIsNotNull(Long courseId);

    Optional<Enrollment> findByUserAndCourse(User user, Course course);

    @Query("SELECT e.rating, COUNT(e) FROM Enrollment e WHERE e.course.id=?1 GROUP BY e.rating ORDER BY COUNT(e) DESC")
    List<Object[]> getMostPunctuation(Long course_id);
}
