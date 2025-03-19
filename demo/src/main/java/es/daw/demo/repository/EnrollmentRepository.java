package es.daw.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.daw.demo.model.Enrollment;
import es.daw.demo.model.User;
import es.daw.demo.model.Course;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUser(Long id);

    List<Enrollment> findByCourse(Course course);

    List<Enrollment> findByCourseIdAndRatingIsNotNull(Long courseId);

    Optional<Enrollment> findByUserAndCourse(User user, Course course);

    @Query("SELECT e.rating, COUNT(e) FROM Enrollment e WHERE e.course.id=?1 GROUP BY e.rating ORDER BY e.rating")
    List<Object[]> getMostPunctuation(Long course_id);

    //@Query("SELECT e.course.topic, COUNT(e) FROM Enrollment e WHERE e.user = :user GROUP BY e.course.topic ORDER BY COUNT(e) DESC")
    //List<Object[]> findMostFrequentTopicByUser(@Param("user") User user);

    @Query("SELECT COUNT(e) > 0 FROM Enrollment e WHERE e.user.id = :userId AND e.course.id = :courseId")
    Boolean existsByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT e.course.topic FROM Enrollment e WHERE e.user = :user GROUP BY e.course.topic ORDER BY COUNT(e) DESC LIMIT 1")
    String findMostFrequentTopicByUser(@Param("user") User user);

    @Query("SELECT AVG(e.rating) FROM Enrollment e WHERE e.course.id = :courseId AND e.rating IS NOT NULL")
    Double findAverageRatingByCourseId(@Param("courseId") Long courseId);

    
}
