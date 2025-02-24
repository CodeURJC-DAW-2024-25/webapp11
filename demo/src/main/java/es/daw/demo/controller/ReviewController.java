package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import es.daw.demo.repository.ReviewRepository;
import es.daw.demo.repository.UserRepository;
import es.daw.demo.service.CourseService;
import es.daw.demo.service.ReviewService;
import es.daw.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.model.Review;
import es.daw.demo.model.User;
import es.daw.demo.model.Course;
import java.util.Optional;
import java.util.List;

@Controller
public class ReviewController {

    //@Autowired
    //private ReviewRepository reviewRepository;

    //@Autowired
    //private UserRepository userRepository;

    //@Autowired
    //private CourseRepository courseRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @PostMapping("/course/newReview")
    public String newReview(@RequestParam String text,
                            @RequestParam Long courseId,
                            @RequestParam(required = false) Long parentId,
                            HttpServletRequest request,
                            Model model) {
        // Obtener el usuario autenticado
        System.out.println("Hola");
        Optional<User> userOpt = userService.findByEmail(request.getUserPrincipal().getName());
        if (userOpt.isEmpty()) {
            model.addAttribute("errorTitle", "Error creating review");
            model.addAttribute("errorMessage", "User not found");
            return "error";
        }

        // Obtener el curso
        Optional<Course> courseOpt = courseService.findById(courseId);
        if (courseOpt.isEmpty()) {
            model.addAttribute("errorTitle", "Error creating review");
            model.addAttribute("errorMessage", "Course does not exist");
            return "error";
        }

        // Obtener la reseña padre si se proporciona un ID válido
        Review parentReview = null;
        if (parentId != null) {
            Optional<Review> parentReviewOpt = reviewService.getParentReview(parentId);
            parentReview = parentReviewOpt.orElse(null);
        }

        // Crear y guardar la nueva reseña utilizando el servicio
        reviewService.createReview(text, userOpt.get(), courseOpt.get(), parentReview);

        // Redirigir al curso donde se hizo el comentario
        return "redirect:/showCourse/" + courseId;
    }

    // search reviews by user
    @GetMapping("/searchReviewsByUser")
    public String searchReviewsByUser(@RequestParam Long userId, Model model) {
        Optional<User> user = userService.findById(userId);

        if (user.isPresent()) {
            List<Review> reviews = reviewService.findReviewsByUser(userId);
            model.addAttribute("reviews", reviews);
            return "reviews";
        } else {
            model.addAttribute("errorTitle", "error searching reviews");
            model.addAttribute("errorMessage", "user not found");
            return "error";
        }
    }

    // edit review
    @PostMapping("/editReview")
    public String editReview(@RequestParam Long reviewId,
            @RequestParam String newText,
            @RequestParam String newRating,
            Model model) {
        Optional<Review> reviewOptional = reviewService.findReviewById(reviewId);

        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setText(newText);
            //review.setRating(newRating);
            reviewService.save(review);
            model.addAttribute("message", "review edited successfully");
            return "reviews";
        } else {
            model.addAttribute("errorTitle", "error editing review");
            model.addAttribute("errorMessage", "review not found");
            return "error";
        }
    }

    // delete review
    @PostMapping("/deleteReview")
    public String deleteReview(@RequestParam Long reviewId, Model model) {
        Optional<Review> reviewOptional = reviewService.findReviewById(reviewId);

        if (reviewOptional.isPresent()) {
            reviewService.deleteReview(reviewId);
            model.addAttribute("message", "review deleted successfully");
            return "reviews";
        } else {
            model.addAttribute("errorTitle", "error deleting review");
            model.addAttribute("errorMessage", "review not found");
            return "error";
        }
    }
}