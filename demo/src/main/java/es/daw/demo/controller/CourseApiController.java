package es.daw.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import es.daw.demo.service.CourseService;
import es.daw.demo.service.UserService;
import es.daw.demo.dto.CourseDTO;
import es.daw.demo.dto.UserDTO;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseApiController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public ResponseEntity<CourseDTO> createCourse(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String topic,
            @RequestParam MultipartFile image,
            @RequestParam MultipartFile notes,
            HttpServletRequest request) throws Exception {
        UserDTO instructor = userService.findByEmail(request.getUserPrincipal().getName());
        CourseDTO course = new CourseDTO(null, title, description, topic, instructor, 0);
        courseService.createCourse(course, image, notes);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable Long id) {
        CourseDTO course = courseService.getCourse(id);
        return (course != null) ? ResponseEntity.ok(course) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String topic,
            @RequestParam MultipartFile imageFile,
            @RequestParam MultipartFile notes) throws Exception {
        courseService.updateCourse(id, title, description, topic, imageFile, notes);
        return ResponseEntity.ok(courseService.getCourse(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/")
    public ResponseEntity<List<CourseDTO>> getCourses(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<CourseDTO> coursesPage = courseService.findAllByOrderByRatingDesc(pageable);
        return ResponseEntity.ok(coursesPage.getContent());
    }

    @GetMapping("/topic/{topic}")
    public ResponseEntity<List<CourseDTO>> getCoursesByTopic(@PathVariable String topic,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<CourseDTO> coursesPage = courseService.findByTopicOrderByRatingDesc(topic, pageable);
        return ResponseEntity.ok(coursesPage.getContent());
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<CourseDTO>> getCoursesByTitle(@PathVariable String title,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<CourseDTO> coursesPage = courseService.searchCourses(title, pageable);
        return ResponseEntity.ok(coursesPage.getContent());
    }
}
