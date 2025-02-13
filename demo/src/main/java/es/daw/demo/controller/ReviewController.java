package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import es.daw.demo.repository.ReviewRepository;
import es.daw.demo.repository.UserRepository;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.model.Review;
import es.daw.demo.model.User;
import es.daw.demo.model.Course;
import java.util.Optional;
import java.util.List;

public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    // create new review
    @PostMapping("/newReview")
    public String newReview(@RequestParam String text,
            @RequestParam String rating,
            @RequestParam Long userId,
            @RequestParam Long courseId,
            Model model) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Course> course = courseRepository.findById(courseId);

        if (user.isPresent() && course.isPresent()) {
            Review review = new Review(text, rating, user.get(), course.get(), null);
            reviewRepository.save(review);
            model.addAttribute("message", "review created successfully");
            return "index";
        } else {
            model.addAttribute("errorTitle", "error creating review");
            model.addAttribute("errorMessage", "user or course does not exist");
            return "error";
        }
    }

    // search reviews by course
    @GetMapping("/searchReviewsByCourse")
    public String searchReviewsByCourse(@RequestParam Long courseId, Model model) {
        Optional<Course> course = courseRepository.findById(courseId);

        if (course.isPresent()) {
            List<Review> reviews = reviewRepository.findByCourse(course.get());
            model.addAttribute("reviews", reviews);
            return "reviews";
        } else {
            model.addAttribute("errorTitle", "error searching reviews");
            model.addAttribute("errorMessage", "course no encontrado");
            return "error";
        }
    }

    // search reviews by user
    @GetMapping("/searchReviewsByUser")
    public String searchReviewsByUser(@RequestParam Long userId, Model model) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            List<Review> reviews = reviewRepository.findByUser(user.get());
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
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setText(newText);
            review.setRating(newRating);
            reviewRepository.save(review);
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
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);

        if (reviewOptional.isPresent()) {
            reviewRepository.delete(reviewOptional.get());
            model.addAttribute("message", "review deleted successfully");
            return "reviews";
        } else {
            model.addAttribute("errorTitle", "error deleting review");
            model.addAttribute("errorMessage", "review not found");
            return "error";
        }
    }
}