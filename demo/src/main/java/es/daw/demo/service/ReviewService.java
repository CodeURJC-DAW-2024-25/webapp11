package es.daw.demo.service;

import es.daw.demo.model.Course;
import es.daw.demo.model.Review;
import es.daw.demo.model.User;
import es.daw.demo.repository.ReviewRepository;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public Review createReview(String text, User user, Course course, Review parentReview) {
        // Crear la nueva reseña
        Review review = new Review(text, user, course, parentReview);
        // Guardar la reseña en la base de datos
        return reviewRepository.save(review);
    }

    public List<Review> findReviewsByCourse(Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);

        if (course.isPresent()) {
            return reviewRepository.findByCourse(course.get());
        } else {
            throw new RuntimeException("Course not found");
        }
    }

    public List<Review> findReviewsByUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            return reviewRepository.findByUser(user.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public Review editReview(Long reviewId, String newText, String newRating) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setText(newText);
            //review.setRating(newRating);
            return reviewRepository.save(review); // El save también se usa aquí
        } else {
            throw new RuntimeException("Review not found");
        }
    }

    public void deleteReview(Long reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isPresent()) {
            reviewRepository.delete(reviewOptional.get());
        } else {
            throw new RuntimeException("Review not found");
        }
    }

    public Optional<Review> findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    public Optional<Review> getParentReview(Long parentId) {
        return reviewRepository.findById(parentId);
    }

    public Review save(Review review) {
        return reviewRepository.save(review); // save review, new or edited
    }
}
