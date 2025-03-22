package es.daw.demo.controller;

import es.daw.demo.service.ReviewService;
import es.daw.demo.service.CourseService;
import es.daw.demo.service.UserService;
import es.daw.demo.model.Review;
import es.daw.demo.model.User;
import es.daw.demo.dto.CourseDTO;
import es.daw.demo.dto.ReviewDTO;
import es.daw.demo.dto.UserDTO;
import es.daw.demo.model.Course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class RESTReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Obtener una lista de reseñas de un curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reseñas obtenida", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Review.class))
            }),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado", content = @Content)
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByCourseId(@PathVariable Long courseId) {
        try {
            CourseDTO course = courseService.findById(courseId);

            // Usamos el método que ya tienes en ReviewService
            List<ReviewDTO> reviews = reviewService.findParentReviewsByCourse(courseId); // Este método ya existe en
                                                                                      // ReviewService
            return ResponseEntity.ok(reviews);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Crear una nueva reseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reseña creada correctamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Review.class))
            }),
            @ApiResponse(responseCode = "404", description = "Curso o usuario no encontrado", content = @Content)
    })
    @PostMapping("/new")
    public ResponseEntity<?> newReview(@RequestParam String text,
            @RequestParam Long courseId,
            @RequestParam(required = false) Long parentId,
            HttpServletRequest request) {
        // Obtener el usuario
        UserDTO user = userService.findByEmail(request.getUserPrincipal().getName());

        // Obtener el curso
        CourseDTO course = courseService.findById(courseId);
        if (course==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso no encontrado");
        }

        // Obtener la reseña padre si existe
        ReviewDTO parentReview = null;
        if (parentId != null) {
            parentReview = reviewService.getParentReview(parentId);
        }

        // Crear y guardar la nueva reseña
        reviewService.createReview(text, user, course, parentReview);

        return ResponseEntity.status(HttpStatus.CREATED).body("Reseña creada exitosamente");
    }

    @Operation(summary = "Marcar una reseña como pendiente de revisión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña marcada como pendiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada", content = @Content)
    })
    @PostMapping("/{id}/mark-pending")
    public ResponseEntity<?> markReviewAsPending(@PathVariable Long id) {
        try{
            ReviewDTO review = reviewService.findReviewById(id);
            reviewService.setState(review, true);
            reviewService.save(review);
            return ResponseEntity.ok("Reseña marcada como pendiente");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reseña no encontrada");
        }
    }

    @Operation(summary = "Desmarcar una reseña como pendiente de revisión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña desmarcada de pendiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada", content = @Content)
    })
    @PostMapping("/{id}/desmark-pending")
    public ResponseEntity<?> markReviewAsNoPending(@PathVariable Long id) {
        try {
            ReviewDTO review = reviewService.findReviewById(id);
            reviewService.setState(review, false);
            reviewService.save(review);
            return ResponseEntity.ok("Reseña desmarcada de pendiente");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reseña no encontrada");
        }
    }

    @Operation(summary = "Editar una reseña existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña actualizada correctamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Review.class))
            }),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> editReview(@PathVariable Long id,
            @RequestParam String newText,
            HttpServletRequest request) {
        ReviewDTO review = reviewService.findReviewById(id);

        // Verificar que el usuario es el autor de la reseña o el admin
        String currentUserEmail = request.getUserPrincipal().getName();
        if (!review.user().email().equals(currentUserEmail) && !request.isUserInRole("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acción no autorizada");
        }

        // Actualizar la reseña
        reviewService.setText(review, newText);
        reviewService.save(review);

        return ResponseEntity.ok("Reseña actualizada exitosamente");
    }

    @Operation(summary = "Eliminar una reseña por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña eliminada correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Reseña eliminada exitosamente");
    }
}