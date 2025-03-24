package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import es.daw.demo.service.ReviewService;
import es.daw.demo.service.UserService;
import es.daw.demo.service.CourseService;
import es.daw.demo.dto.ReviewDTO;
import es.daw.demo.dto.UserDTO;
import es.daw.demo.dto.CourseDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;

@RestController
@RequestMapping("/api/reviews")
public class ReviewApiController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    // Obtener todas las reseñas de un curso
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Collection<ReviewDTO>> getReviewsByCourse(@PathVariable Long courseId) {
        Collection<ReviewDTO> reviews = reviewService.findParentReviewsByCourse(courseId);
        return ResponseEntity.ok(reviews);
    }

    // Crear una reseña
    @PostMapping("/new")
    public ResponseEntity<?> createReview(@RequestParam String text,
                                          @RequestParam(required = false) Long courseId,
                                          @RequestParam(required = false) Long parentId,
                                          HttpServletRequest request) {
        UserDTO user = userService.findByEmail(request.getUserPrincipal().getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        CourseDTO course = courseId != null ? courseService.findById(courseId) : null;
        if (courseId != null && course == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Curso no encontrado");
        }

        ReviewDTO parentReview = parentId != null ? reviewService.getParentReview(parentId) : null;
        if (parentId != null && parentReview == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reseña padre no encontrada");
        }

        reviewService.createReview(text, user, course, parentReview);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
    @PutMapping("/edit")
    public ResponseEntity<?> editReview(@RequestParam Long reviewId, 
                                        @RequestParam String newText, 
                                        HttpServletRequest request) {
        UserDTO user = userService.findByEmail(request.getUserPrincipal().getName());
        ReviewDTO review = reviewService.findReviewById(reviewId);
        
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reseña no encontrada");
        }
        if (!review.user().email().equals(user.email()) && !request.isUserInRole("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acción no autorizada");
        }
        
        reviewService.editReview(reviewId, newText);
        return ResponseEntity.ok().build();
    }

    // Eliminar reseña
    @DeleteMapping("/{id}")
    public ReviewDTO deleteReview(@PathVariable Long id) {
        ReviewDTO review = reviewService.findReviewById(id);
        return reviewService.deleteReview(id);
    }
}
