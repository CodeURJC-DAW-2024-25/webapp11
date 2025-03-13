package es.daw.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.daw.demo.model.Course;
import es.daw.demo.model.User;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTitle(String title);
    List<Course> findByTopic(String topic);

    //Course findById(long id);

    List<Course> findTop4ByOrderByRatingDesc();
    List<Course> findTop4ByTopicOrderByRatingDesc(String topic);
    List<Course> findByInstructor(User user);

    Page<Course> findByInstructor(User user, Pageable page);
    Page<Course> findAllByOrderByRatingDesc(Pageable pageable);
    Page<Course> findByTopicOrderByRatingDesc(String topic, Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.topic) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Course> searchCourses(@Param("query") String query, Pageable pageable);

    @Query("SELECT c.topic, COUNT(c) FROM Course c GROUP BY c.topic ORDER BY COUNT(c) DESC")
    List<Object[]> getMostCoursesCategoriesNameAndCount();

    @Query("SELECT c.topic, COUNT(e) FROM Enrollment e JOIN e.course c GROUP BY c.topic")
    List<Object[]> getMostInscribedCategoriesNameAndCount();
}
