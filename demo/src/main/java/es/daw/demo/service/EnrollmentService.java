package es.daw.demo.service;

import es.daw.demo.model.Course;
import es.daw.demo.model.Enrollment;
import es.daw.demo.model.User;
import es.daw.demo.repository.EnrollmentRepository;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    public Enrollment save(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    public String enrollUserToCourse(Long userId, Long courseId) {
        // check user
        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            return "Usuario no encontrado";
        }

        // check course
        Course course = courseRepository.findById(courseId)
                .orElse(null);
        if (course == null) {
            return "Curso no encontrado";
        }

        // check if the user is already enrolled in the course
        List<Enrollment> existingEnrollments = enrollmentRepository.findByUser(user);
        for (Enrollment enrollment : existingEnrollments) {
            if (enrollment.getCourse().equals(course)) {
                return "Ya está inscrito en este curso";
            }
        }

        // enrollment creation
        Enrollment enrollment = new Enrollment(user, course);

        // Update the topic of the user
        updateUserTopic(user);

        // save the enrollment
        enrollmentRepository.save(enrollment);
        return "success"; // Indicating successful enrollment
    }

    private void updateUserTopic(User user) {
        // Contar cuántos cursos tiene en cada categoría
        List<Enrollment> enrollments = enrollmentRepository.findByUser(user);
        
        Map<String, Long> topicCount = enrollments.stream()
            .collect(Collectors.groupingBy(e -> e.getCourse().getTopic(), Collectors.counting()));

        // Obtener el tema con mayor cantidad de cursos
        String mostFrequentTopic = topicCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);

        if (mostFrequentTopic != null) {
            user.setTopic(mostFrequentTopic);
            userRepository.save(user);
        }
    }

    public void cancelEnrollment(Long enrollmentId) {
        // search the enrollment
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        // delet the enrollment
        enrollmentRepository.delete(enrollment);
    }

    public List<Enrollment> findEnrollmentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return enrollmentRepository.findByUser(user);
    }

    public List<Enrollment> findEnrollmentsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return enrollmentRepository.findByCourse(course);
    }

    public Optional<Enrollment> findById(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId);
    }

    public Boolean isUserEnrolledInCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            return false;
        }

        Course course = courseRepository.findById(courseId)
                .orElse(null);
        if (course == null) {
            return false;
        }

        List<Enrollment> existingEnrollments = enrollmentRepository.findByUser(user);
        for (Enrollment enrollment : existingEnrollments) {
            if (enrollment.getCourse().equals(course)) {
                return true;
            }
        }
        return false;
    }

    public String getMostFrequentTopic(User user) {
        List<Enrollment> enrollments = enrollmentRepository.findByUser(user);
        
        if (enrollments.isEmpty()) {
            return user.getTopic();
        }

        Map<String, Long> topicCount = enrollments.stream()
            .map(e -> e.getCourse().getTopic())
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return topicCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    public Enrollment findByUserAndCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return enrollmentRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
    }

    public List<Course> getCoursesByUser(User user) {
        return enrollmentRepository.findByUser(user)
                .stream()
                .map(Enrollment::getCourse)
                .collect(Collectors.toList());
    }
}