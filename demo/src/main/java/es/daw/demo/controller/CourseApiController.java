package es.daw.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import es.daw.demo.service.CourseService;
import es.daw.demo.service.UserService;
import es.daw.demo.dto.CourseDTO;
import es.daw.demo.dto.UserDTO;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;


@RestController
@RequestMapping("/api/v1/courses")
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
        course = courseService.createCourse(course, null, null);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(course.id()).toUri();
        return ResponseEntity.created(location).body(course);
    }

    @PostMapping("/{id}/image")
	public ResponseEntity<Object> createCourseImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
		
        courseService.createCourseImage(id, imageFile.getInputStream(), imageFile.getSize());
        URI location = fromCurrentRequest().build().toUri();
		return ResponseEntity.created(location).build();
	}

    @PostMapping("/{id}/notes")
	public ResponseEntity<Object> createCourseNotes(@PathVariable long id, @RequestParam MultipartFile noteFile) throws IOException {
		courseService.createCourseNotes(id, noteFile.getInputStream(), noteFile.getSize());
		URI location = fromCurrentRequest().build().toUri();
		return ResponseEntity.created(location).build();
	}

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable Long id) {
        CourseDTO course = courseService.getCourse(id);
        return (course != null) ? ResponseEntity.ok(course) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getCourseImage(@PathVariable Long id) throws SQLException {
        Resource image = courseService.getCourseImage(id);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(image);
    }

    @GetMapping("/{id}/notes")
    public ResponseEntity<Resource> getCourseNotes(@PathVariable Long id) throws SQLException {
        Resource notes = courseService.getCourseNotes(id);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/pdf").body(notes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseDTO course) throws Exception {
        courseService.updateCourse(id, course.title(), course.description(), course.topic(), null, null);
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

    @GetMapping("/taught/{userId}/")
    public ResponseEntity<List<CourseDTO>> getTaughtCourses(@PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<CourseDTO> coursesPage = courseService.findByInstructor(userService.findById(userId), pageable);
        return ResponseEntity.ok(coursesPage.getContent());
    }

    @PutMapping("/{id}/image")
	public ResponseEntity<Object> updateCourseImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
		
        courseService.createCourseImage(id, imageFile.getInputStream(), imageFile.getSize());
        URI location = fromCurrentRequest().build().toUri();
		return ResponseEntity.created(location).build();
	}

    @PutMapping("/{id}/notes")
	public ResponseEntity<Object> updateCourseNotes(@PathVariable long id, @RequestParam MultipartFile noteFile) throws IOException {
		courseService.createCourseNotes(id, noteFile.getInputStream(), noteFile.getSize());
		URI location = fromCurrentRequest().build().toUri();
		return ResponseEntity.created(location).build();
	}

    @GetMapping("/recommended")
    public ResponseEntity<Collection<CourseDTO>> getTop4CoursesByRating() {
        Collection<CourseDTO> courses = courseService.findTop4ByOrderByRatingDesc();
        return ResponseEntity.ok(courses);
    }
    
    @GetMapping("/{userId}/{courseId}")
    public boolean isUserInstructor(@PathVariable Long userId, @PathVariable Long courseId) {
        return courseService.isUserInstructor(userId, courseId);
    }

    @GetMapping("/mostpopular")
    public ResponseEntity<List<Object[]>> mostInscribedCategories() {
        List<Object[]> result = courseService.getMostInscribedCategoriesNameAndCount();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/most-courses")
    public ResponseEntity<List<Object[]>> mostCoursesCategories() {
        List<Object[]> result = courseService.getMostCoursesCategoriesNameAndCount();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
