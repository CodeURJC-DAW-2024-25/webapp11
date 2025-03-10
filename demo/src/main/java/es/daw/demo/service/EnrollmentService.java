package es.daw.demo.service;

import es.daw.demo.model.Course;
import es.daw.demo.model.Enrollment;
import es.daw.demo.model.User;
import es.daw.demo.repository.EnrollmentRepository;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
        // Count how many courses are for each category
        List<Enrollment> enrollments = enrollmentRepository.findByUser(user);

        Map<String, Long> topicCount = enrollments.stream()
                .collect(Collectors.groupingBy(e -> e.getCourse().getTopic(), Collectors.counting()));

        // Get topic with the highest count of courses
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
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        // delet the enrollment
        enrollmentRepository.delete(enrollment);
    }

    public List<Enrollment> findEnrollmentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return enrollmentRepository.findByUser(user);
    }

    public List<Enrollment> findEnrollmentsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

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
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        return enrollmentRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
    }

    public Page<Course> getCoursesByUser(User user, Pageable pageable) {
        List<Course> courses = enrollmentRepository.findByUser(user)
                .stream()
                .map(Enrollment::getCourse)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), courses.size());
        
        List<Course> pagedCourses = courses.subList(start, end);

        return new PageImpl<>(pagedCourses, pageable, courses.size());
    }

    public List<Enrollment> findByUser(User user) {
        return enrollmentRepository.findByUser(user);
    }

    public List<Enrollment> findByCourse(Course course) {
        return enrollmentRepository.findByCourse(course);
    }

    public void delete(Enrollment enrollment) {
        enrollmentRepository.delete(enrollment);
    }

    public List<Object[]> getMostPunctuation(Long course_id){
        return enrollmentRepository.getMostPunctuation(course_id);
    }
}