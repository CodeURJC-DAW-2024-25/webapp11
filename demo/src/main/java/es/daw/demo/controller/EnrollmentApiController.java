package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import es.daw.demo.service.EnrollmentService;
import es.daw.demo.service.UserService;
import es.daw.demo.dto.EnrollmentDTO;
import es.daw.demo.dto.UserDTO;
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
        return result.equals("success") ? ResponseEntity.ok("Inscripción exitosa") : ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/rate")
    public ResponseEntity<String> rateCourse(@RequestParam Long courseId, 
                                             @RequestParam int rating,
                                             @RequestParam String userEmail) {
        UserDTO user = userService.findByEmail(userEmail);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }
        
        EnrollmentDTO enrollmentDTO = enrollmentService.findByUserAndCourse(user.id(), courseId);
        if (enrollmentDTO == null) {
            return ResponseEntity.badRequest().body("No existe su inscripción al curso");
        }
        
        enrollmentService.updateRating(enrollmentDTO.id(), rating);
        return ResponseEntity.ok("Valoración actualizada");
    }

    @GetMapping("/statistics/{id}")
    public ResponseEntity<List<Object[]>> getCourseStatistics(@PathVariable Long id) {
        return new ResponseEntity<>(enrollmentService.getMostPunctuation(id), HttpStatus.OK);
    }
}
