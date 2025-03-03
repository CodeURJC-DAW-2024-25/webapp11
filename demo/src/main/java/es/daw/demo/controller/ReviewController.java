package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import es.daw.demo.service.CourseService;
import es.daw.demo.service.ReviewService;
import es.daw.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import es.daw.demo.model.Review;
import es.daw.demo.model.User;
import es.daw.demo.model.Course;
import java.util.Optional;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    // Create a review
    @PostMapping("/course/newReview")
    public String newReview(@RequestParam String text,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long parentId,
            HttpServletRequest request,
            Model model) {
        // Get the user
        Optional<User> userOpt = userService.findByEmail(request.getUserPrincipal().getName());
        if (userOpt.isEmpty()) {
            model.addAttribute("errorTitle", "Error al crear reseña");
            model.addAttribute("errorMessage", "Usuario no encontrado");
            return "error";
        }
        Course course = null;
        if (courseId != null) {
            Optional<Course> courseOpt = courseService.findById(courseId);
            if (courseOpt.isEmpty()) {
                model.addAttribute("errorTitle", "Error al crear reseña");
                model.addAttribute("errorMessage", "Curso no existe");
                return "error";
            }
            course = courseOpt.get();
        }
        // Get the parent review if it`s attached
        Review parentReview = null;
        if (parentId != null) {
            Optional<Review> parentReviewOpt = reviewService.getParentReview(parentId);
            if (parentReviewOpt.isEmpty()) {
                model.addAttribute("errorTitle", "Error al crear reseña");
                model.addAttribute("errorMessage", "Reseña padre no existe");
                return "error";
            }
            parentReview = parentReviewOpt.get();
            course = parentReview.getCourse();
        }
        if (course == null) {
            model.addAttribute("errorTitle", "Error al crear reseña");
            model.addAttribute("errorMessage", "O el id del curso o el de la reseña padre deben estar presentes");
            return "error";
        }

        // Create and save the new review
        reviewService.createReview(text, userOpt.get(), course, parentReview);

        return "redirect:/showCourse/" + course.getId();
    }

    // Mark a review for revision
    @PostMapping("/reviews/{id}/mark-pending")
    public String markReviewAsPending(@PathVariable Long id, Model model) {
        Optional<Review> optionalReview = reviewService.findReviewById(id);

        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setState(true);
            reviewService.save(review);
        }

        return "redirect:/showCourse/" + optionalReview.get().getCourse().getId();
    }

    // Desmark a review for revision
    @PostMapping("/reviews/{id}/desmark-pending")
    public String markReviewAsNoPending(@PathVariable Long id, Model model) {
        Optional<Review> optionalReview = reviewService.findReviewById(id);

        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setState(false);
            reviewService.save(review);
        }

        return "redirect:/profile";
    }

    // Edit review
    @PostMapping("/editReview")
    public String editReview(@RequestParam Long reviewId,
            @RequestParam String newText,
            HttpServletRequest request,
            Model model) {
        // Get the user
        String currentUserEmail = request.getUserPrincipal().getName();
        Optional<User> userOpt = userService.findByEmail(currentUserEmail);

        if (userOpt.isEmpty()) {
            model.addAttribute("errorTitle", "Error al editar reseña");
            model.addAttribute("errorMessage", "Reseña no encontrada");
            return "error";
        }

        // Look for the review
        Optional<Review> reviewOptional = reviewService.findReviewById(reviewId);

        if (reviewOptional.isEmpty()) {
            model.addAttribute("errorTitle", "Error al editar reseña");
            model.addAttribute("errorMessage", "Reseña no encontrada");
            return "error";
        }

        Review review = reviewOptional.get();

        // Check that the user is the author of the review or the admin
        if (!review.getUser().getEmail().equals(currentUserEmail) && !request.isUserInRole("ADMIN")) {
            model.addAttribute("errorTitle", "Error al editar reseña");
            model.addAttribute("errorMessage", "Acción no autorizada");
            return "error";
        }

        // Update the review
        review.setText(newText);
        reviewService.save(review);

        if (request.isUserInRole("ADMIN")) {
            return "redirect:/profile";
        } else {
            return "redirect:/showCourse/" + review.getCourse().getId();
        }

    }

    // Delete review
    @PostMapping("/deleteReview")
    public String deleteReview(@RequestParam Long reviewId, Model model) {
        Optional<Review> reviewOptional = reviewService.findReviewById(reviewId);

        if (reviewOptional.isPresent()) {
            reviewService.deleteReview(reviewId);
            return "redirect:/profile";
        } else {
            model.addAttribute("errorTitle", "Error al borrar reseña");
            model.addAttribute("errorMessage", "Reseña no encontrada");
            return "error";
        }
    }

}