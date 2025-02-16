package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import es.daw.demo.repository.EnrollmentRepository;
import es.daw.demo.repository.UserRepository;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.model.Enrollment;
import es.daw.demo.model.User;
import es.daw.demo.model.Course;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Create new enrollment
    @PostMapping("/newEnrollment")
    public String newEnrollment(@RequestParam Long userId, @RequestParam Long courseId, Model model) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Course> course = courseRepository.findById(courseId);

        if (user.isPresent() && course.isPresent()) {
            Enrollment enrollment = new Enrollment(user.get(), course.get());
            enrollmentRepository.save(enrollment);
            model.addAttribute("message", "Enrollment created successfully");
            return "index";
        } else {
            model.addAttribute("errorTitle", "Error creating enrollment");
            model.addAttribute("errorMessage", "User or course does not exist");
            return "error";
        }
    }

    // Search enrollments by user
    @GetMapping("/searchByUser")
    public String searchEnrollmentsByUser(@RequestParam Long userId, Model model) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            List<Enrollment> enrollments = enrollmentRepository.findByUser(user.get());
            model.addAttribute("enrollments", enrollments);
            return "enrollments";
        } else {
            model.addAttribute("errorTitle", "Error searching enrollments");
            model.addAttribute("errorMessage", "User not found");
            return "error";
        }
    }

    // Search enrollments by course
    @GetMapping("/searchByCourse")
    public String searchEnrollmentsByCourse(@RequestParam Long courseId, Model model) {
        Optional<Course> course = courseRepository.findById(courseId);

        if (course.isPresent()) {
            List<Enrollment> enrollments = enrollmentRepository.findByCourse(course.get());
            model.addAttribute("enrollments", enrollments);
            return "enrollments";
        } else {
            model.addAttribute("errorTitle", "Error searching enrollments");
            model.addAttribute("errorMessage", "Course not found");
            return "error";
        }
    }

    // Delete enrollment
    @PostMapping("/deleteEnrollment")
    public String deleteEnrollment(@RequestParam Long enrollmentId, Model model) {
        Optional<Enrollment> enrollmentOptional = enrollmentRepository.findById(enrollmentId);

        if (enrollmentOptional.isPresent()) {
            enrollmentRepository.delete(enrollmentOptional.get());
            model.addAttribute("message", "Enrollment deleted successfully");
            return "enrollments";
        } else {
            model.addAttribute("errorTitle", "Error deleting enrollment");
            model.addAttribute("errorMessage", "Enrollment not found");
            return "error";
        }
    }
}
