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

import java.io.IOException;
import java.net.URI;
import java.util.List;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/courses")
public class CourseApiController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public ResponseEntity<CourseDTO> createCourse(
            @RequestBody CourseDTO course,
            HttpServletRequest request) throws Exception {
        UserDTO instructor = userService.findByEmail(request.getUserPrincipal().getName());
        course = new CourseDTO(null, course.title(), course.description(), course.topic(), instructor, 0);
        courseService.createCourse(course, null, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @PostMapping("/{id}/image")
	public ResponseEntity<Object> createCourseImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
		courseService.updateCourse(id, null, null, null, imageFile, null);

		URI location = fromCurrentRequest().build().toUri();

		return ResponseEntity.created(location).build();
	}

    
    @PostMapping("/{id}/notes")
	public ResponseEntity<Object> createCourseNotes(@PathVariable long id, @RequestParam MultipartFile noteFile) throws IOException {
		courseService.updateCourse(id, null, null, null, null, noteFile);

		URI location = fromCurrentRequest().build().toUri();

		return ResponseEntity.created(location).build();
	}

    public String postMethodName(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
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
    public CourseDTO deleteCourse(@PathVariable Long id) {
        return courseService.deleteCourse(id);
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
