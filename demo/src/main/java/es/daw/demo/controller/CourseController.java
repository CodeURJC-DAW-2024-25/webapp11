package es.daw.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import es.daw.demo.service.CourseService;
import es.daw.demo.service.EnrollmentService;
import es.daw.demo.service.ReviewService;
import es.daw.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import es.daw.demo.model.User;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.model.Course;
import es.daw.demo.model.Review;

import java.util.List;
import java.util.Optional;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;

@Controller
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private ReviewService reviewService;

    // new course
    @PostMapping("/newCourse")
    public String newCourse(@RequestParam String title,
            @RequestParam String description,
            @RequestParam String topic,
            @RequestParam MultipartFile image,
            @RequestParam MultipartFile notes,
            Model model,
            HttpServletRequest request) throws Exception {
        User instructor = userService.findByEmail(request.getUserPrincipal().getName()).get();
        Course course = new Course(title, description, topic, instructor);
        courseService.save(course, image, notes);

        model.addAttribute("pagetitle", title);
        model.addAttribute("isTeacher", true);
        model.addAttribute("course", course);
        return "redirect:/showCourse/" + course.getId();
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
    @GetMapping("/createCourse")
    public String showNewCoursePage(Model model) {
        return "new_course";
    }

    // Update course
    @PostMapping("/updateCourse")
    public String updateCourse(Model model, Course updatedCourse, @PathVariable Long id) {
        Course oldCourse = courseService.findById(id).orElseThrow();
        updatedCourse.setId(id);

        oldCourse.getReviews().forEach(review -> updatedCourse.addReviews(review));
        return "redirect:/showCourse/" + id;
    }

    // Show course
    @GetMapping("/showCourse/{id}")
    public String showCourse(@PathVariable Long id, Model model, HttpServletRequest request) {
        Course course = courseService.findById(id).orElseThrow();
        String teacher = course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName();
        model.addAttribute("pagetitle", "Curso");
        model.addAttribute("teacher", teacher);
        model.addAttribute("course", course);
        List<Review> parentReviews = reviewService.findParentReviewsByCourse(id);
        model.addAttribute("reviews", parentReviews);
        // Falta configurar los comentarios.

        if (request.getUserPrincipal() != null) {
            Optional<User> optionalUser = userService.findByEmail(request.getUserPrincipal().getName());
            if (optionalUser.isPresent()) {
                Long idUser = optionalUser.get().getId();
                // Checks if the user is the instructor of the course

                // Checks if the user is enrolled to the course
                if (request.isUserInRole("USER")) {
                    model.addAttribute("isEnrolled", enrollmentService.isUserEnrolledInCourse(idUser, id));
                    model.addAttribute("isTeacher", courseService.isUserInstructor(id, idUser));
                } else if (request.isUserInRole("ADMIN")) {
                    model.addAttribute("isEnrolled", false);
                    model.addAttribute("isTeacher", true);
                }
            } else {
                model.addAttribute("isEnrolled", false);
                model.addAttribute("isTeacher", false);
            }
        } else {
            // If is an anonymus user
            model.addAttribute("isEnrolled", false);
            model.addAttribute("isTeacher", false);
        }
        return "course";
    }

    // Show all courses
    @GetMapping("/")
    public String getIndex(Model model, @AuthenticationPrincipal UserDetails user) {

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

    @GetMapping("/getCourses")
    public String getCourses(Model model, HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Course> coursesPage = courseRepository.findAll(pageable);

        model.addAttribute("courses", coursesPage.getContent());

        return "coursesPage";
    }

    @GetMapping("/deleteCourse/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return "redirect:/index";
    }
}