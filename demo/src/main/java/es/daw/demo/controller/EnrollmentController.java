package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import es.daw.demo.service.CourseService;
import es.daw.demo.service.EnrollmentService;
import es.daw.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import es.daw.demo.model.Enrollment;
import es.daw.demo.model.User;
import es.daw.demo.model.Course;
import java.util.Optional;
import java.util.List;

@Controller
public class EnrollmentController {

    @Autowired
    private UserService userService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseService courseService;
    
    // Create new enrollment
    @PostMapping("/course/enroll")
    public String enrollToCourse(@RequestParam Long idCourse, HttpServletRequest request, Model model) {
        // Verificar si el usuario está autenticado
        if (request.getUserPrincipal() == null) {
            return "redirect:/login";  // Redirigir al login si no está autenticado
        }

        // Obtener el ID del usuario autenticado
        Long idUser = userService.findByEmail(request.getUserPrincipal().getName()).get().getId();

        // Inscripción al curso
        String result = enrollmentService.enrollUserToCourse(idUser, idCourse);
        
        if (result.equals("success")) {
            return "redirect:/showCourse/" + idCourse;
        } else {
            model.addAttribute("errorTitle", "Error al inscribirse al curso");
            model.addAttribute("errorMessage", result);
            return "error";
        }
    }

    // Search enrollments by user
    @GetMapping("/searchByUser")
    public String searchEnrollmentsByUser(@RequestParam Long userId, Model model) {
        Optional<User> user = userService.findById(userId);

        if (user.isPresent()) {
            List<Enrollment> enrollments = enrollmentService.findByUser(user.get());
            model.addAttribute("enrollments", enrollments);
            return "enrollments";       //ESTA PLANTILLA NO EXISTE
        } else {
            model.addAttribute("errorTitle", "Error searching enrollments");
            model.addAttribute("errorMessage", "User not found");
            return "error";
        }
    }

    // Search enrollments by course
    @GetMapping("/searchByCourse")
    public String searchEnrollmentsByCourse(@RequestParam Long courseId, Model model) {
        Optional<Course> course = courseService.findById(courseId);

        if (course.isPresent()) {
            List<Enrollment> enrollments = enrollmentService.findByCourse(course.get());
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
        Optional<Enrollment> enrollmentOptional = enrollmentService.findById(enrollmentId);

        if (enrollmentOptional.isPresent()) {
            enrollmentService.delete(enrollmentOptional.get());
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
