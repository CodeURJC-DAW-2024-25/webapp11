package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import es.daw.demo.service.ReviewService;
import es.daw.demo.service.CourseService;
import es.daw.demo.dto.ReviewDTO;

import java.net.URI;
import java.util.Collection;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/reviews")
public class ReviewApiController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CourseService courseService;

    // Obtener todas las reseñas de un curso
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Collection<ReviewDTO>> getReviewsByCourse(@PathVariable Long courseId) {
        Collection<ReviewDTO> reviews = reviewService.findParentReviewsByCourse(courseId);
        return ResponseEntity.ok(reviews);
    }

    // Crear una reseña
    @PostMapping("/")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO review) {
        if (review.user() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (courseService.findById(review.course().id()) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (review.parent() != null && reviewService.getParentReview(review.parent().id()) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        review = reviewService.createReview(review.text(), review.user(), review.course(), review.parent());
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(review.id()).toUri();
        return ResponseEntity.created(location).body(review);
    }

    // Marcar reseña como pendiente de revisión
    @PutMapping("/{id}/mark-pending")
    public ResponseEntity<?> markReviewAsPending(@PathVariable Long id) {
        ReviewDTO review = reviewService.findReviewById(id);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reseña no encontrada");
        }
        reviewService.markReviewAsPending(id);
        return ResponseEntity.ok().build();
    }

    // Desmarcar reseña como pendiente de revisión
    @PutMapping("/{id}/desmark-pending")
    public ResponseEntity<?> markReviewAsNoPending(@PathVariable Long id) {
        ReviewDTO review = reviewService.findReviewById(id);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reseña no encontrada");
        }
        reviewService.markReviewAsNoPending(id);
        return ResponseEntity.ok().build();
    }

    // Editar reseña
    @PutMapping("/")
    public ResponseEntity<?> editReview(@RequestBody ReviewDTO review) {
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reseña no encontrada");
        }
        
        reviewService.editReview(review.id(), review.text());
        return ResponseEntity.ok().build();
    }

    // Delete a review
    @DeleteMapping("/{id}")
    public ReviewDTO deleteReview(@PathVariable Long id) {
        return reviewService.deleteReview(id);
    }

    //Get pending reviews
    @GetMapping("/pending")
    public ResponseEntity<Collection<ReviewDTO>> getPendindReviews() {
        Collection<ReviewDTO> reviews = reviewService.findByPendingTrue();
        return ResponseEntity.ok(reviews);
    }
    
}
