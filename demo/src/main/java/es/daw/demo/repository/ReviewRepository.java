package es.daw.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import es.daw.demo.model.Review;
import es.daw.demo.model.Course;
import es.daw.demo.model.User;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCourse(Course course); // search all reviews of a course
    List<Review> findByCourseIdAndParentIsNull(Long courseId);
    List<Review> findByUser(User user); // search all reviews of a user
    List<Review> findByPendingTrue();
    Review findById(long id);
}