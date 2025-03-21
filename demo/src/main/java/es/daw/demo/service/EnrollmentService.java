package es.daw.demo.service;

import es.daw.demo.dto.CourseDTO;
import es.daw.demo.dto.EnrollmentDTO;
import es.daw.demo.dto.EnrollmentMapper;
import es.daw.demo.dto.CourseMapper;
import es.daw.demo.dto.UserDTO;
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

import java.util.Collection;
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

    @Autowired
    private EnrollmentMapper enrollmentMapper;

    @Autowired
    private CourseMapper courseMapper;

    public EnrollmentDTO createEnrollment(EnrollmentDTO enrollmentDTO) {
        Enrollment enrollment = toDomain(enrollmentDTO);
        return toDTO(enrollmentRepository.save(enrollment));
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
        List<Enrollment> existingEnrollments = enrollmentRepository.findByUser_Id(userId);
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
        String mostFrequentTopic = enrollmentRepository.findMostFrequentTopicByUser(user.getId());
        
        if (mostFrequentTopic != null) {
            user.setTopic(mostFrequentTopic);
            userRepository.save(user);
        }
    }
    
    

    public void cancelEnrollment(Long enrollmentId) {
        // search the enrollment
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        // delete the enrollment
        enrollmentRepository.delete(enrollment);
    }

    public Collection<EnrollmentDTO> findEnrollmentsByUser(Long userId) {
        return toDTOs(enrollmentRepository.findByUser_Id(userId));
    }

    public Collection<EnrollmentDTO> findEnrollmentsByCourse(Long courseId) {
        return toDTOs(enrollmentRepository.findByCourse_Id(courseId));
    }

    public EnrollmentDTO findById(Long enrollmentId) {
        return toDTO(enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada")));
    }

    public Boolean isUserEnrolledInCourse(Long userId, Long courseId) {
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }
    

    public String getMostFrequentTopic(UserDTO user) {
        String mostFrequentTopic = enrollmentRepository.findMostFrequentTopicByUser(user.id());
        return (mostFrequentTopic != null) ? mostFrequentTopic : user.topic();
    }
    
    public void updateRating(Long enrollmentId, int rating) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        enrollment.setRating(rating);
        enrollmentRepository.save(enrollment);
    }

    public EnrollmentDTO findByUserAndCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        return toDTO(enrollmentRepository.findByUserAndCourse(user, course));
    }

    public Page<CourseDTO> getCoursesByUser(UserDTO user, Pageable pageable) {

        return enrollmentRepository.findByUser_Id(user.id(), pageable).map(courseMapper::toDTO);
    }

    public Collection<EnrollmentDTO> findByUser(UserDTO user) {
        return toDTOs(enrollmentRepository.findByUser_Id(user.id()));
    }

    public Collection<EnrollmentDTO> findByCourse(CourseDTO course) {
        return toDTOs(enrollmentRepository.findByCourse_Id(course.id()));
    }

    public void delete(EnrollmentDTO enrollmentDTO) {
        Enrollment enrollment = toDomain(enrollmentDTO);
        enrollmentRepository.delete(enrollment);
    }

    public List<Object[]> getMostPunctuation(Long course_id){
        return enrollmentRepository.getMostPunctuation(course_id);
    }

    private EnrollmentDTO toDTO(Enrollment enrollment) {
        return enrollmentMapper.toDTO(enrollment);
    }

    private Enrollment toDomain(EnrollmentDTO enrollmentDTO) {
        return enrollmentMapper.toDomain(enrollmentDTO);
    }

    private Collection<EnrollmentDTO> toDTOs(Collection<Enrollment> enrollments) {
        return enrollments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}