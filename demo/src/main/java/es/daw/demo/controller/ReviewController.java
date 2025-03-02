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
import java.util.List;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @PostMapping("/course/newReview")
    public String newReview(@RequestParam String text,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long parentId,
            HttpServletRequest request,
            Model model) {
        // Obtener el usuario autenticado
        Optional<User> userOpt = userService.findByEmail(request.getUserPrincipal().getName());
        if (userOpt.isEmpty()) {
            model.addAttribute("errorTitle", "Error creating review");
            model.addAttribute("errorMessage", "User not found");
            return "error";
        }
        Course course = null;
        if (courseId != null) {
            Optional<Course> courseOpt = courseService.findById(courseId);
            if (courseOpt.isEmpty()) {
                model.addAttribute("errorTitle", "Error creating review");
                model.addAttribute("errorMessage", "Course does not exist");
                return "error";
            }
            course = courseOpt.get();
        }
        // Obtener la reseña padre si se proporciona un ID válido
        Review parentReview = null;
        if (parentId != null) {
            Optional<Review> parentReviewOpt = reviewService.getParentReview(parentId);
            if (parentReviewOpt.isEmpty()) {
                model.addAttribute("errorTitle", "Error creating review");
                model.addAttribute("errorMessage", "Parent review does not exist");
                return "error";
            }
            parentReview = parentReviewOpt.get();
            course = parentReview.getCourse(); // Asegurar que la respuesta pertenezca al mismo curso
        }
        if (course == null) {
            model.addAttribute("errorTitle", "Error creating review");
            model.addAttribute("errorMessage", "Either a courseId or a valid parentId must be provided");
            return "error";
        }

        // Crear y guardar la nueva reseña utilizando el servicio
        reviewService.createReview(text, userOpt.get(), course, parentReview);
        // Redirigir al curso donde se hizo el comentario
        return "redirect:/showCourse/" + course.getId();
    }
    /* 
    // search reviews by user
    @GetMapping("/searchReviewsByUser")
    public String searchReviewsByUser(@RequestParam Long userId, Model model) {
        Optional<User> user = userService.findById(userId);

        if (user.isPresent()) {
            List<Review> reviews = reviewService.findReviewsByUser(userId);
            model.addAttribute("reviews", reviews);
            return "reviews";       //ESTA PLANTILLA NO EXISTE
        } else {
            model.addAttribute("errorTitle", "error searching reviews");
            model.addAttribute("errorMessage", "user not found");
            return "error";
        }
    }*/

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

    @PostMapping("/editReview")
    public String editReview(@RequestParam Long reviewId,
            @RequestParam String newText,
            HttpServletRequest request,
            Model model) {
        // Get the user
        String currentUserEmail = request.getUserPrincipal().getName();
        Optional<User> userOpt = userService.findByEmail(currentUserEmail);

        if (userOpt.isEmpty()) {
            model.addAttribute("errorTitle", "Error editing review");
            model.addAttribute("errorMessage", "User not found");
            return "error";
        }

        // Look for the review
        Optional<Review> reviewOptional = reviewService.findReviewById(reviewId);

        if (reviewOptional.isEmpty()) {
            model.addAttribute("errorTitle", "Error editing review");
            model.addAttribute("errorMessage", "Review not found");
            return "error";
        }

        Review review = reviewOptional.get();

        // Check that the user is the author of the review or the admin
        if (!review.getUser().getEmail().equals(currentUserEmail) && !request.isUserInRole("ADMIN")) {
            model.addAttribute("errorTitle", "Error editing review");
            model.addAttribute("errorMessage", "Unauthorized action");
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

    // delete review
    @PostMapping("/deleteReview")
    public String deleteReview(@RequestParam Long reviewId, Model model) {
        Optional<Review> reviewOptional = reviewService.findReviewById(reviewId);

        if (reviewOptional.isPresent()) {
            reviewService.deleteReview(reviewId);
            return "redirect:/profile";
        } else {
            model.addAttribute("errorTitle", "error deleting review");
            model.addAttribute("errorMessage", "review not found");
            return "error";
        }
    }
    /*
     * @PostMapping("/course/{id}/comment")
     * public String respondComment( @PathVariable Long id,
     * 
     * @RequestParam Long parentId,
     * 
     * @RequestParam String content,
     * Model model,
     * HttpServletRequest request){
     * System.out.println("workinga" + id);
     * 
     * /////////////////
     * Optional<User> userOpt =
     * userService.findByEmail(request.getUserPrincipal().getName());
     * if (userOpt.isEmpty()) {
     * model.addAttribute("errorTitle", "Error creating review");
     * model.addAttribute("errorMessage", "User not found");
     * return "error";
     * }
     * 
     * // Obtener el curso
     * Optional<Course> courseOpt = courseService.findById(id);
     * if (courseOpt.isEmpty()) {
     * model.addAttribute("errorTitle", "Error creating review");
     * model.addAttribute("errorMessage", "Course does not exist");
     * return "error";
     * }
     * 
     * //Obtener el comentario
     * Optional<Review> reviewOpt = reviewService.findReviewById(parentId);
     * if(reviewOpt.isEmpty()){
     * model.addAttribute("errorTitle", "Error creating review");
     * model.addAttribute("errorTitle", "Comment does not exist");
     * return "error";
     * }
     * 
     * // Obtener la reseña padre si se proporciona un ID válido
     * Review parentReview = null;
     * if (parentId != null) {
     * Optional<Review> parentReviewOpt = reviewService.getParentReview(parentId);
     * parentReview = parentReviewOpt.orElse(null);
     * }
     * 
     * // Guardar la respuesta al comentario en la lista de respuestas del
     * comentario
     * Review childReview = reviewService.createReview(content, userOpt.get(),
     * courseOpt.get(), parentReview);
     * parentReview.addHijo(childReview);
     * /////////////////
     * 
     * return "redirect:/showCourse/" + id;
     * }
     */
}