package es.daw.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import es.daw.demo.service.CourseService;
import es.daw.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import es.daw.demo.model.User;
import es.daw.demo.model.Course;
import java.util.Optional;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;


@Controller
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    // new course
    @PostMapping("/newCourse/{id}")
    public String newCourse(@PathVariable Long id,
                            @RequestParam String title,
                            @RequestParam String description,
                            @RequestParam String topic,
                            @RequestParam MultipartFile image,
                            @RequestParam MultipartFile notes,
                            Model model) throws Exception {
        Course course = new Course(title, description, topic, userService.findById(id).orElseThrow());
        courseService.save(course, image, notes);
    
        model.addAttribute("pagetitle", title);
        model.addAttribute("isTeacher", true);
        model.addAttribute("course", course);
        return "redirect:/course/" + course.getId();
    }


    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if (principal != null) {

			model.addAttribute("isLoggedIn", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));

		} else {
			model.addAttribute("isLoggedIn", false);
		}
	}

    // Upload course image
    @GetMapping("/image/{id}")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        Optional<Course> course = courseService.findById(id);
		if (course.isPresent() && course.get().getImageFile() != null) {

			Resource file = new InputStreamResource(course.get().getImageFile().getBinaryStream());

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
					.contentLength(course.get().getImageFile().length()).body(file);

		} else {
			return ResponseEntity.notFound().build();
		}
    }

    // Upload course notes
    @GetMapping("/notes/{id}")
    public ResponseEntity<Object> downloadNotes(@PathVariable long id) throws SQLException {
        Optional<Course> course = courseService.findById(id);
		if (course.isPresent() && course.get().getNotes() != null) {

			Resource file = new InputStreamResource(course.get().getNotes().getBinaryStream());

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/pdf")
					.contentLength(course.get().getNotes().length()).body(file);

		} else {
			return ResponseEntity.notFound().build();
		}
    }   
    

    // search courses by title ????
    @GetMapping("/searchCourse")
    public String searchCourses(@RequestParam String title, Model model) {
        model.addAttribute("courses", courseService.findByTitle(title));
        return "courses";
    }

    // edit course
    @PostMapping("/editCourse")
    public String editCourse(@ModelAttribute Course editedCourse, Model model) {
        Optional<Course> courseOptional = courseService.findById(editedCourse.getId());
        if (courseOptional.isPresent()) {
            courseService.save(editedCourse);
            model.addAttribute("message", "Course edited successfully");
            return "courseDetails";
        } else {
            model.addAttribute("errorTitle", "error editing course");
            model.addAttribute("errorMessage", "course does not exist");
            return "error";
        }
    }

    // delete course
    @PostMapping("/deleteCourse")
    public String deleteCourse(@RequestParam Long id, Model model) {
        Optional<Course> courseOptional = courseService.findById(id);
        if (courseOptional.isPresent()) {
            courseService.deleteById(id);
            model.addAttribute("message", "course deleted successfully");
            return "index";
        } else {
            model.addAttribute("errorTitle", "error deleting course");
            model.addAttribute("errorMessage", "course does not exist");
            return "error";
        }
    }
    // Change view to the new course page
    @PostMapping("/createCourse")
    public String showNewCoursePage(@PathVariable Long id, Model model) {
        model.addAttribute("instructorId", id);
        return "new_course";
    }

    // Update course
    @PostMapping("/updateCourse")
    public String updateCourse(Model model, Course updatedCourse, @PathVariable Long id) {
        Course oldCourse = courseService.findById(id).orElseThrow();
        updatedCourse.setId(id);

        oldCourse.getComments().forEach(comment -> updatedCourse.addComment(comment));
        return "redirect:/showCourse/" + id;
    }

    // Show course
    @GetMapping("/showCourse/{id}")
    public String showCourse(@PathVariable Long id, Model model) {
        Course course = courseService.findById(id).orElseThrow();
        model.addAttribute("course", course);
        return "course";
    }

    // Show all courses
    @GetMapping("/")
    public String getIndex (Model model, @AuthenticationPrincipal UserDetails user) {

        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("isLoggedIn", true);
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        model.addAttribute("pagetitle", "Inicio");
        model.addAttribute("allCourses", courseService.findAll());
        model.addAttribute("recomendCourses", courseService.findTop4ByOrderByRatingDesc());
        return "index";
    }
}