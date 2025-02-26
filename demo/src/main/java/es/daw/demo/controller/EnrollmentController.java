package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import es.daw.demo.repository.EnrollmentRepository;
import es.daw.demo.repository.UserRepository;
import es.daw.demo.service.CourseService;
import es.daw.demo.service.EnrollmentService;
import es.daw.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.model.Enrollment;
import es.daw.demo.model.User;
import es.daw.demo.model.Course;
import java.util.Optional;
import java.util.List;

@Controller
public class EnrollmentController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseService courseService;
    
    // Create new enrollment
    @PostMapping("/newEnrollment")
    public String newEnrollment(@RequestParam Long userId, @RequestParam Long courseId, Model model) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Course> course = courseRepository.findById(courseId);

        if (user.isPresent() && course.isPresent()) {
            Enrollment enrollment = new Enrollment(user.get(), course.get());
            enrollmentRepository.save(enrollment);
            model.addAttribute("message", "Enrollment created successfully");       //??????
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

    // Update/crete rating of a course
    @PostMapping("/enrollment/rate")
    public String rateCourse(@RequestParam Long courseId, 
                            @RequestParam int rating,
                            HttpServletRequest request,
                            Model model) {
        Optional<User> user = userService.findByEmail(request.getUserPrincipal().getName());
        
        Enrollment enrollment = enrollmentService.findByUserAndCourse(user.get().getId(), courseId);
        if (enrollment == null) {
            model.addAttribute("errorTitle", "Error al crear la valoración");
            model.addAttribute("errorMessage", "No existe su inscripción al curso");
            return "error";
        }

        // Actualizar el rating en la inscripción
        enrollment.setRating(rating);
        enrollmentService.save(enrollment);

        // Recalcular la media de ratings del curso
        Optional<Course> optionalCourse = courseService.findById(courseId);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            List<Enrollment> enrollments = enrollmentService.findEnrollmentsByCourse(courseId);
    
            int totalRating = enrollments.stream()
                                         .mapToInt(Enrollment::getRating)
                                         .sum();
            int newAverageRating = enrollments.isEmpty() ? 0 : totalRating / enrollments.size();
    
            // Actualizar el rating del curso
            course.setRating(newAverageRating);
            courseService.save(course);
        } else {
            model.addAttribute("errorTitle", "Error al crear la valoración");
            model.addAttribute("errorMessage", "Curso no encontrado");
            return "error";
        }

        return "redirect:/showCourse/" + courseId; // Redirige de vuelta a la página del curso
    }

}
