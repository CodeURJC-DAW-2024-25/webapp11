package es.daw.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;

import es.daw.demo.service.CourseService;
import es.daw.demo.service.EnrollmentService;
import es.daw.demo.service.ReviewService;
import es.daw.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import es.daw.demo.model.User;
import es.daw.demo.model.Course;
import es.daw.demo.model.Review;

import java.util.List;
import java.util.Optional;
import java.security.Principal;
import java.sql.SQLException;

@Controller
public class CourseController {

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

    // Change view to the new course page
    @GetMapping("/createCourse")
    public String showNewCoursePage(Model model) {
        return "new_course";
    }

    // Change view to edit course
    @GetMapping("/editCourse/{id}")
    public String editCourse(Model model, @PathVariable Long id) {
        model.addAttribute("id", id);
        return "edit_course";
    }

    @PostMapping("/updateCourse/{id}")
    public String updateCourse( @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String topic,
            @RequestParam MultipartFile imageFile,
            @RequestParam MultipartFile notes,
            Model model,
            HttpServletRequest request) throws Exception {

        // Obtener el token CSRF
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        // Agregar el token al modelo
        model.addAttribute("token", csrfToken.getToken());

        Optional<Course> optionalCourse = courseService.findById(id);

        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
			// Update course
			if (!title.isEmpty()) {
				course.setTitle(title);
			}
            if (!description.isEmpty()) {
				course.setDescription(description);
			}
            if (!topic.isEmpty()) {
				course.setTopic(topic);
			}
			// Verify and update image
			if (imageFile.getOriginalFilename() != "" && !imageFile.isEmpty()) {
				course.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
			}

            if(!notes.isEmpty()) {
                course.setNotes(BlobProxy.generateProxy(notes.getInputStream(), notes.getSize()));
            }

			// Save updated course
			courseService.save(course);

			return "redirect:/showCourse/" + course.getId();
		} else {
			return "error";
		}
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
    public String getIndex(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) { // Verifica si el usuario está autenticado
            Optional<User> user = userService.findByEmail(principal.getName());
            if (user.isPresent()) {
                model.addAttribute("isLoggedIn", true);
                List<Course> courses = courseService.getTopRatedCoursesByTopic(user.get().getTopic());
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

        Page<Course> coursesPage = courseService.findAllByOrderByRatingDesc(pageable);

        model.addAttribute("courses", coursesPage.getContent());

        return "coursesPage";
    }

    @GetMapping("/getTaughtCourses")
    public String getTaughtCourses(Model model, HttpServletRequest request, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Principal principal = request.getUserPrincipal();
        Page<Course> coursesPage = courseService.findByInstructor(userService.findByEmail(principal.getName()).get(), pageable);

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
        Page<Course> coursesPage = courseService.findByTopicOrderByRatingDesc(topic, pageable);

        model.addAttribute("courses", coursesPage.getContent());

        return "coursesPage";
    }

    @GetMapping("/deleteCourse/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteById(id);
        return "redirect:/";
    }

    //Change view to courses by topic
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
        Page<Course> coursesPage = courseService.searchCourses(title, pageable);

        model.addAttribute("courses", coursesPage.getContent());

        return "coursesPage";
    }

    //Change view to courses by title
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

    @GetMapping("/mostInscribedCathegories") // should return a json with a list of the most read genres and their count
    public ResponseEntity<List<Object[]>> mostInscribedCathegories() {
        return new ResponseEntity<>(courseService.getMostCoursesCathegoriesNameAndCount(), HttpStatus.OK);
    }

}