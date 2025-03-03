package es.daw.demo.service;

import es.daw.demo.model.Course;
import es.daw.demo.model.Review;
import es.daw.demo.model.User;
import es.daw.demo.repository.ReviewRepository;
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
    private UserRepository userRepository;

    public Review createReview(String text, User user, Course course, Review parentReview) {
        // Crete new review
        Review review = new Review(text, user, course, parentReview);
        // Save review in the database
        return reviewRepository.save(review);
    }

    public List<Review> findParentReviewsByCourse(Long courseId) {
        return reviewRepository.findByCourseIdAndParentIsNull(courseId);
    }

    public List<Review> findReviewsByUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            return reviewRepository.findByUser(user.get());
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    public Review editReview(Long reviewId, String newText, String newRating) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setText(newText);
            // review.setRating(newRating);
            return reviewRepository.save(review); // Save is also used here
        } else {
            throw new RuntimeException("Reseña no enocntrada");
        }
    }

    public void deleteReview(Long reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isPresent()) {
            reviewRepository.delete(reviewOptional.get());
        } else {
            throw new RuntimeException("Reseña no encontrada");
        }
    }

    public Optional<Review> findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    public Optional<Review> getParentReview(Long parentId) {
        return reviewRepository.findById(parentId);
    }

    public Review save(Review review) {
        return reviewRepository.save(review); // Save review, new or edited
    }

    public List<Review> findByPendingTrue() {
        return reviewRepository.findByPendingTrue();
    }
}
