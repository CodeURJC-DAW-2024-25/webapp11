package es.daw.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.model.Course;
import java.util.Optional;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    // new course
    @PostMapping("/newCourse")
    public String newCourse(@RequestParam String title,
            @RequestParam String description,
            @RequestParam String topic,
            @RequestParam String instructorId,
            Model model) {
        Course newCourse = new Course(title, description, topic, null, null, instructorId, 0);
        courseRepository.save(newCourse);
        model.addAttribute("message", "Course created successfully");
        return "index"; // go back to principle page
    }

    // search courses by title
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
}