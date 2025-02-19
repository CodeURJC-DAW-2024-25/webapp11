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
import java.util.Optional;

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

    public Enrollment enrollUserToCourse(Long userId, Long courseId) {
        // check user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // check course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // check if the user is already enrolled in the course
        List<Enrollment> existingEnrollments = enrollmentRepository.findByUser(user);
        for (Enrollment enrollment : existingEnrollments) {
            if (enrollment.getCourse().equals(course)) {
                throw new RuntimeException("User already enrolled in the course");
            }
        }
        // enrollment creation
        Enrollment enrollment = new Enrollment(user, course);

        // save the enrollment
        return save(enrollment); // call the save method
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
}
