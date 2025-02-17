package es.daw.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import es.daw.demo.repository.CourseRepository;
import es.daw.demo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import es.daw.demo.model.User;
import es.daw.demo.model.Course;
import java.util.Optional;
import java.sql.Blob;

@Controller
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    // new course
    @PostMapping("/newCourse")
    public String newCourse(@RequestParam String title,
            @RequestParam String description,
            @RequestParam String topic,
            @RequestParam Long instructorId,
            @RequestParam Blob image,
            @RequestParam Blob notes,
            Model model) {
        Optional<User> instructorOptional = userRepository.findById(instructorId);
        if (!instructorOptional.isPresent()) {
            model.addAttribute("errorTitle", "Error al crear el curso");
            model.addAttribute("errorMessage", "El instructor no existe");
            return "error";
        }
        User instructor = instructorOptional.get();
        Course newCourse = new Course(title, description, topic, image, notes, instructor, 0);
        courseRepository.save(newCourse);
        model.addAttribute("message", "Course created successfully");
        return "index"; // go back to principle page
    }

    // search courses by title ????
    @GetMapping("/searchCourse")
    public String searchCourses(@RequestParam String title, Model model) {
        model.addAttribute("courses", courseRepository.findByTitle(title));
        return "courses";
    }

    // edit course
    @PostMapping("/editCourse")
    public String editCourse(@ModelAttribute Course editedCourse, Model model) {
        Optional<Course> courseOptional = courseRepository.findById(editedCourse.getId());
        if (courseOptional.isPresent()) {
            courseRepository.save(editedCourse);
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
        Optional<Course> courseOptional = courseRepository.findById(id);
        if (courseOptional.isPresent()) {
            courseRepository.deleteById(id);
            model.addAttribute("message", "course deleted successfully");
            return "index";
        } else {
            model.addAttribute("errorTitle", "error deleting course");
            model.addAttribute("errorMessage", "course does not exist");
            return "error";
        }
    }

    // Update course
    @PostMapping("/updateCourse")
    public String updateCourse(Model model, Course updatedCourse, @PathVariable Long id) {
        Course oldCourse = courseRepository.findById(id).orElseThrow();
        updatedCourse.setId(id);

        oldCourse.getComments().forEach(comment -> updatedCourse.addComment(comment));
        return "redirect:/showCourse/" + id;
    }

    // Show course
    @GetMapping("/showCourse/{id}")
    public String showCourse(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id).orElseThrow();
        model.addAttribute("course", course);
        return "course";
    }

    // Show all courses
    @GetMapping("/")
    public String getIndex (Model model, HttpSession session) {
        model.addAttribute("pagetitle", "Inicio");
        model.addAttribute("isLoggedIn", session.getAttribute("user") != null);
        model.addAttribute("allCourses", courseRepository.findAll());
        model.addAttribute("recomendCourses", courseRepository.findTop4ByOrderByRatingDesc());
        model.addAttribute("topic", "Prueba");
        return "index";
    }
}