package es.daw.demo.service;

import es.daw.demo.dto.CourseDTO;
import es.daw.demo.dto.ReviewDTO;
import es.daw.demo.dto.ReviewMapper;
import es.daw.demo.dto.UserDTO;
import es.daw.demo.model.Course;
import es.daw.demo.model.Review;
import es.daw.demo.model.User;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.repository.ReviewRepository;
import es.daw.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private CourseRepository courseRepository;

    public ReviewDTO createReview(String text, UserDTO user, CourseDTO course, ReviewDTO parentReview) {

        Review parentReviewEntity = (parentReview != null) ? 
            reviewRepository.findById(parentReview.id()).orElse(null) : null;
        // Crete new review
        Review review = new Review(text, userRepository.findByFirstName(user.firstName()).get(), courseRepository.findById(course.id()).orElseThrow(), parentReviewEntity);
        // Save review in the database
        return toDTO(reviewRepository.save(review));
    }

    public Collection<ReviewDTO> findParentReviewsByCourse(Long courseId) {
        return toDTOs(reviewRepository.findByCourseIdAndParentIsNull(courseId));
    }

    public Collection<ReviewDTO> findReviewsByUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            return toDTOs(reviewRepository.findByUser(user.get()));
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    public void markReviewAsPending(Long reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setState(true);
            reviewRepository.save(review);
        } else {
            throw new RuntimeException("Reseña no encontrada");
        }
    }

    public void markReviewAsNoPending(Long reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setState(false);
            reviewRepository.save(review);
        } else {
            throw new RuntimeException("Reseña no encontrada");
        }
    }

    public ReviewDTO editReview(Long reviewId, String newText) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
            review.setText(newText);
            return toDTO(reviewRepository.save(review));
    }

    public void deleteReview(Long reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isPresent()) {
            reviewRepository.delete(reviewOptional.get());
        } else {
            throw new RuntimeException("Reseña no encontrada");
        }
    }

    public ReviewDTO findReviewById(Long reviewId) {
        return toDTO(reviewRepository.findById(reviewId).orElseThrow());
    }

    public ReviewDTO getParentReview(Long parentId) {
        return toDTO(reviewRepository.findById(parentId).orElseThrow());
    }

    public ReviewDTO save(ReviewDTO reviewDTO) {
        Review review = toDomain(reviewDTO); // Convert DTO to domain
        return toDTO(reviewRepository.save(review)); // Save review, new or edited
    }

    public Collection<ReviewDTO> findByPendingTrue() {
        return toDTOs(reviewRepository.findByPendingTrue());
    }

    private ReviewDTO toDTO(Review review) {
        return reviewMapper.toDTO(review);
    }

    private Review toDomain(ReviewDTO reviewDTO) {
        return reviewMapper.toDomain(reviewDTO);
    }

    private List<ReviewDTO> toDTOs(Collection<Review> reviews) {
        return reviewMapper.toDTOs(reviews);
    }
}
