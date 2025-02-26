package es.daw.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import es.daw.demo.model.Course;
import es.daw.demo.model.User;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTitle(String title);

    List<Course> findByTopic(String topic);

    //Course findById(long id);

    List<Course> findTop4ByOrderByRatingDesc();
    List<Course> findTop4ByTopicOrderByRatingDesc(String topic);
    //List<Course> findByTitleContainingIgnoreCaseOrfindByTopicContainingIgnoreCase(String name);

    List<Course> findByInstructor(User user);
}
