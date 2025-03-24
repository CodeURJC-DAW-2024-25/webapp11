package es.daw.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.daw.demo.service.CourseService;
import es.daw.demo.service.EnrollmentService;
import es.daw.demo.service.ReviewService;
import es.daw.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import es.daw.demo.dto.CourseDTO;
import es.daw.demo.dto.NewCourseRequestDTO;
import es.daw.demo.dto.ReviewDTO;
import es.daw.demo.dto.UserDTO;


import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.security.Principal;
import java.sql.SQLException;

@Controller
public class CourseWebController {

    @Autowired
    private CourseService courseService;

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
        UserDTO instructor = userService.findByEmail(request.getUserPrincipal().getName());
        CourseDTO course = new CourseDTO(null, title, description, topic, instructor, 0);
        course = courseService.createCourse(course, image, notes);

        model.addAttribute("pagetitle", title);
        model.addAttribute("isTeacher", true);
        model.addAttribute("course", course);
        return "redirect:/course/" + course.id();
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
    @GetMapping("/course/{id}/image")
    public ResponseEntity<Object> getCourseImage(@PathVariable long id) throws SQLException, IOException {
        Resource courseImage = courseService.getCourseImage(id);

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
				.body(courseImage);

    }

    // Upload course notes
    @GetMapping("/course/{id}/notes")
    public ResponseEntity<Object> getCourseNotes(@PathVariable long id) throws SQLException, IOException {
        Resource courseNotes = courseService.getCourseNotes(id);

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_TYPE, "application/pdf")
				.body(courseNotes);

    }

    // Change view to the new course page
    @GetMapping("/createCourse")
    public String showNewCoursePage(Model model) {
        return "new_course";
    }

    // Change view to edit course
    @GetMapping("/editCourse/{id}")
    public String editCourse(Model model, @PathVariable Long id, HttpServletRequest request) {
        UserDTO user = userService.findByEmail(request.getUserPrincipal().getName());
        if (user == null) {
            model.addAttribute("errorTitle", "Error al editar el curso");
            model.addAttribute("errorMessage", "Usuario no encontrado");
            return "error";
        } else if (!courseService.isUserInstructor(id, user.id()) && !request.isUserInRole("ADMIN")) {
            model.addAttribute("errorTitle", "Error al editar el curso");
            model.addAttribute("errorMessage", "El usuario no tiene permisos");
            return "error";
        }
        CourseDTO course = courseService.getCourse(id);
        if (course != null) {
            model.addAttribute("course",course);
            return "edit_course";
        } else {
            model.addAttribute("errorTitle", "Error al editar el curso");
            model.addAttribute("errorMessage", "Curso no encontrado");
            return "error";
        }
    }

    @PostMapping("/updateCourse")
	public String editCourseProcess(Model model, long courseId, NewCourseRequestDTO newCourseRequestDTO) throws IOException, SQLException {

		courseService.updateCourse(courseId, newCourseRequestDTO.title(), newCourseRequestDTO.description(), newCourseRequestDTO.topic(), newCourseRequestDTO.imageFile(), newCourseRequestDTO.notes());

		return "redirect:/course/" + courseId;
	}

    // Show course
    @GetMapping("/course/{id}")
    public String showCourse(@PathVariable Long id, Model model, HttpServletRequest request) {
        CourseDTO course = courseService.getCourse(id);
        String teacher = course.instructor().firstName() + " " + course.instructor().lastName();
        model.addAttribute("pagetitle", "Curso");
        model.addAttribute("teacher", teacher);
        model.addAttribute("course", course);
        Collection<ReviewDTO> parentReviews = reviewService.findParentReviewsByCourse(id);
        model.addAttribute("reviews", parentReviews);
        // coments not configurated yet
        System.out.println(parentReviews);
        if (request.getUserPrincipal() != null) {
            UserDTO user = userService.findByEmail(request.getUserPrincipal().getName());
            if (user != null) {
                Long idUser = user.id();
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
    public String showCourses(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) { // verify if user is logged in
            UserDTO user = userService.findByEmail(principal.getName());
            if (user != null) {
                model.addAttribute("isLoggedIn", true);
                Collection<CourseDTO> courses = courseService.getTopRatedCoursesByTopic(user.topic());
                model.addAttribute("recomendCourses", courses);
            } else {
                model.addAttribute("isLoggedIn", false);
                model.addAttribute("recomendCourses", courseService.findTop4ByOrderByRatingDesc());
            }
        } else {
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("recomendCourses", courseService.findTop4ByOrderByRatingDesc());
        }

        model.addAttribute("pagetitle", "Inicio");
        return "index";
    }

    @GetMapping("/getCourses")
    public String getCourses(Model model, HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<CourseDTO> coursesPage = courseService.findAllByOrderByRatingDesc(pageable);

        model.addAttribute("courses", coursesPage.getContent());

        return "coursesPage";
    }

    @GetMapping("/getTaughtCourses")
    public String getTaughtCourses(Model model, HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Principal principal = request.getUserPrincipal();
        Page<CourseDTO> coursesPage = courseService.findByInstructor(userService.findByEmail(principal.getName()),
                pageable);

        model.addAttribute("taughtCourses", coursesPage.getContent());

        return "taughtCourses";
    }

    @GetMapping("/getEnrollCourses")
    public String getEnrollCourses(Model model, HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Principal principal = request.getUserPrincipal();
        Page<CourseDTO> coursesPage = enrollmentService.getCoursesByUser(userService.findByEmail(principal.getName()), pageable);

        model.addAttribute("taughtCourses", coursesPage.getContent());

        return "taughtCourses";
    }

    @GetMapping("/getCoursesByTopic")
    public String getCoursesByTopic(Model model,
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String topic) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<CourseDTO> coursesPage = courseService.findByTopicOrderByRatingDesc(topic, pageable);

        model.addAttribute("courses", coursesPage.getContent());

        return "coursesPage";
    }

    @GetMapping("/deleteCourse/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/";
    }

    // Change view to courses by topic
    @GetMapping("/showCourses/{topic}")
    public String showCourses(@PathVariable String topic, Model model) {
        model.addAttribute("pagetitle", "Curso");
        model.addAttribute("topic", topic);
        return "courses";
    }

    @GetMapping("/getCoursesByTitle")
    public String getCoursesByTitle(Model model,
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String title) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<CourseDTO> coursesPage = courseService.searchCourses(title, pageable);

        model.addAttribute("courses", coursesPage.getContent());

        return "coursesPage";
    }

    // Change view to courses by title
    @GetMapping("/findCourses/{title}")
    public String showCoursesTitle(@PathVariable String title, Model model) {
        model.addAttribute("pagetitle", "Curso");
        model.addAttribute("title", title);
        return "coursestitle";
    }

    @GetMapping("/charts")
    public String charts(Model model, HttpServletRequest request) {
        model.addAttribute("pagetitle", "Top 5 categorías más populares");
        return "chart";
    }

    @GetMapping("/mostCoursesCategories")
    public ResponseEntity<List<Object[]>> mostCoursesCategories() {
        return new ResponseEntity<>(courseService.getMostCoursesCategoriesNameAndCount(), HttpStatus.OK);
    }

    @GetMapping("/mostInscribedCategories")
    public ResponseEntity<List<Object[]>> mostInscribedCategories() {
        return new ResponseEntity<>(courseService.getMostInscribedCategoriesNameAndCount(), HttpStatus.OK);
    }
    

}