package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import es.daw.demo.service.CourseService;
import es.daw.demo.service.ReviewService;
import es.daw.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import es.daw.demo.dto.CourseDTO;
import es.daw.demo.dto.ReviewDTO;
import es.daw.demo.dto.UserDTO;


@Controller
public class ReviewWebController {

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
        UserDTO user = userService.findByEmail(request.getUserPrincipal().getName());
        if (user == null) {
            model.addAttribute("errorTitle", "Error al crear reseña");
            model.addAttribute("errorMessage", "Usuario no encontrado");
            return "error";
        }
        CourseDTO course = null;
        if (courseId != null) {
            course = courseService.findById(courseId);
            if (course == null) {
                model.addAttribute("errorTitle", "Error al crear reseña");
                model.addAttribute("errorMessage", "Curso no existe");
                return "error";
            }
        }
        ReviewDTO parentReview = null;
        // Get the parent review if it`s attached
        if (parentId != null) {
            parentReview = reviewService.getParentReview(parentId);
            if (parentReview == null) {
                model.addAttribute("errorTitle", "Error al crear reseña");
                model.addAttribute("errorMessage", "Reseña padre no existe");
                return "error";
            }
        }
        // Create and save the new review
        reviewService.createReview(text, user, course, parentReview);

        return "redirect:/showCourse/" + courseId;
    }

    // Mark a review for revision
    @PostMapping("/reviews/{id}/mark-pending")
    public String markReviewAsPending(@PathVariable Long id, Model model) {
        ReviewDTO reviewDTO = reviewService.findReviewById(id);
        if (reviewDTO != null) {
            reviewService.markReviewAsPending(id);
        }
        return "redirect:/showCourse/" + reviewDTO.course().id();
    }

    // Desmark a review for revision
    @PostMapping("/reviews/{id}/desmark-pending")
    public String markReviewAsNoPending(@PathVariable Long id, Model model) {
        ReviewDTO reviewDTO = reviewService.findReviewById(id);

        if (reviewDTO != null) {
            reviewService.markReviewAsNoPending(id);
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
        UserDTO userDto = userService.findByEmail(currentUserEmail);

        if (userDto == null) {
            model.addAttribute("errorTitle", "Error al editar reseña");
            model.addAttribute("errorMessage", "Reseña no encontrada");
            return "error";
        }

        // Look for the review
        ReviewDTO reviewDto = reviewService.findReviewById(reviewId);

        if (reviewDto == null) {
            model.addAttribute("errorTitle", "Error al editar reseña");
            model.addAttribute("errorMessage", "Reseña no encontrada");
            return "error";
        }
        // Check that the user is the author of the review or the admin
        if (reviewDto.user().email().equals(currentUserEmail) && !request.isUserInRole("ADMIN")) {
            model.addAttribute("errorTitle", "Error al editar reseña");
            model.addAttribute("errorMessage", "Acción no autorizada");
            return "error";
        }

        // Update the review
        reviewService.editReview(reviewId, newText);

        if (request.isUserInRole("ADMIN")) {
            return "redirect:/profile";
        } else {
            return "redirect:/showCourse/" + reviewDto.course().id();
        }

    }

    // Delete review
    @PostMapping("/deleteReview")
    public String deleteReview(@RequestParam Long reviewId, Model model) {
        ReviewDTO reviewDto = reviewService.findReviewById(reviewId);

        if (reviewDto != null) {
            reviewService.deleteReview(reviewId);
            return "redirect:/profile";
        } else {
            model.addAttribute("errorTitle", "Error al borrar reseña");
            model.addAttribute("errorMessage", "Reseña no encontrada");
            return "error";
        }
    }

}