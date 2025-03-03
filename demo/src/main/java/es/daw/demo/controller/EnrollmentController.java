package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        // Check if the user in log in
        if (request.getUserPrincipal() == null) {
            return "redirect:/login"; 
        }

        // Get id of the user
        Long idUser = userService.findByEmail(request.getUserPrincipal().getName()).get().getId();

        // Enroll to the course
        String result = enrollmentService.enrollUserToCourse(idUser, idCourse);
        
        if (result.equals("success")) {
            return "redirect:/showCourse/" + idCourse;
        } else {
            model.addAttribute("errorTitle", "Error al inscribirse al curso");
            model.addAttribute("errorMessage", result);
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

        // Update the rating
        enrollment.setRating(rating);
        enrollmentService.save(enrollment);

        // Calculate course rating
        Optional<Course> optionalCourse = courseService.findById(courseId);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            List<Enrollment> enrollments = enrollmentService.findEnrollmentsByCourse(courseId);
    
            int totalRating = enrollments.stream()
                                         .mapToInt(Enrollment::getRating)
                                         .sum();
            int newAverageRating = enrollments.isEmpty() ? 0 : totalRating / enrollments.size();
    
            // Update course rating
            course.setRating(newAverageRating);
            courseService.save(course);
        } else {
            model.addAttribute("errorTitle", "Error al crear la valoración");
            model.addAttribute("errorMessage", "Curso no encontrado");
            return "error";
        }

        return "redirect:/showCourse/" + courseId;
    }

    @GetMapping("/statistics/{id}")
	public String charts(@PathVariable Long id, Model model, HttpServletRequest request) {
		model.addAttribute("pagetitle", "Gráfica de valoraciones");
        model.addAttribute("id", id);
		return "chartCourse";
	}

    @GetMapping("/puntuationChart/{course_id}") 
    public ResponseEntity<List<Object[]>> punctuationChart(@PathVariable Long course_id) {
        return new ResponseEntity<>(enrollmentService.getMostPunctuation(course_id), HttpStatus.OK);
    }

}
