package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import es.daw.demo.service.EnrollmentService;
import es.daw.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import es.daw.demo.dto.CourseDTO;
import es.daw.demo.dto.EnrollmentDTO;
import es.daw.demo.dto.UserDTO;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/{idCourse}")
    public ResponseEntity<String> enrollToCourse(@PathVariable Long idCourse, @RequestParam Long userId) {
        UserDTO user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        String result = enrollmentService.enrollUserToCourse(userId, idCourse);
        return result.equals("success") ? ResponseEntity.ok("Inscripción exitosa")
                : ResponseEntity.badRequest().body(result);
    }

    @PutMapping("/{EnrollmentId}")
    public ResponseEntity<String> rateCourse(@PathVariable Long EnrollmentId,
            @RequestParam int rating,
            HttpServletRequest request) throws Exception {
        // Validar si el usuario está autenticado
        if (request.getUserPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }
        UserDTO user = userService.findByEmail(request.getUserPrincipal().getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }
        EnrollmentDTO enrollmentDTO = enrollmentService.findById(EnrollmentId);
        System.out.println(user.id());
        System.out.println(enrollmentDTO.user().id());
        if (enrollmentDTO == null || enrollmentDTO.user().id() != user.id()) {
            return ResponseEntity.badRequest().body("No existe su inscripción al curso");
        }

        enrollmentService.updateRating(EnrollmentId, rating);
        return ResponseEntity.ok("Valoración actualizada");
    }

    @GetMapping("/statistics/{id}")
    public ResponseEntity<List<Object[]>> getCourseStatistics(@PathVariable Long id) {
        return new ResponseEntity<>(enrollmentService.getMostPunctuation(id), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Page<CourseDTO>> getEnrollCourses(@PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<CourseDTO> coursesPage = enrollmentService.getCoursesByUser(userService.findById(userId), pageable);

        return ResponseEntity.ok(coursesPage);
    }
}
