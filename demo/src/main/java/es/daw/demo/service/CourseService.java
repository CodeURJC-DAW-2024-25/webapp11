package es.daw.demo.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.daw.demo.model.Course;
import es.daw.demo.model.Enrollment;
import es.daw.demo.model.User;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.repository.EnrollmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public void save(Course c) {
        courseRepository.save(c);
    }

    public void save(Course course, MultipartFile imageFile, MultipartFile noteFile) throws IOException {
        if (!imageFile.isEmpty()) {
            course.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }
        if (!noteFile.isEmpty()) {
            course.setNoteFile(BlobProxy.generateProxy(noteFile.getInputStream(), noteFile.getSize()));
        } else {
        }
        this.save(course);
    }

    public Optional<Course> findById(long id) {
        return courseRepository.findById(id);
    }

    public Page<Course> getCoursesOrderedByRating(Pageable pageable) {
        // Return courses by rating and paged
        return courseRepository.findAllByOrderByRatingDesc(pageable);
    }

    public List<Course> findTop4ByOrderByRatingDesc() {
        return courseRepository.findTop4ByOrderByRatingDesc();
    }

    public List<Course> findByTitle(String title) {
        return courseRepository.findByTitle(title);
    }

    public List<Course> findByInstructor(User user) {
        return courseRepository.findByInstructor(user);
    }

    public void deleteById(long id) {
        courseRepository.deleteById(id);
    }

    public boolean isUserInstructor(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        return course != null && course.getInstructor() != null && course.getInstructor().getId() == userId;
    }

    public List<Course> getTopRatedCoursesByTopic(String topic) {
        return courseRepository.findTop4ByTopicOrderByRatingDesc(topic);
    }

    public void updateCourseRating(Long courseId) {
        // Obtener el curso
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    
        // Obtener el promedio directamente de la base de datos
        Double averageRating = enrollmentRepository.findAverageRatingByCourseId(courseId);
    
        // Si no hay calificaciones, establecer en 0; si hay, redondear a entero
        course.setRating((averageRating != null) ? averageRating.intValue() : 0);
    
        // Guardar cambios
        courseRepository.save(course);
    }
    

    public Page<Course> findAllByOrderByRatingDesc(Pageable pageable) {
        return courseRepository.findAllByOrderByRatingDesc(pageable);
    }

    public Page<Course> findByInstructor(User user, Pageable pageable) {
        return courseRepository.findByInstructor(user, pageable);
    }

    public Page<Course> findByTopicOrderByRatingDesc(String topic, Pageable pageable) {
        return courseRepository.findByTopicOrderByRatingDesc(topic, pageable);
    }

    public Page<Course> searchCourses(String title, Pageable pageable) {
        return courseRepository.searchCourses(title, pageable);
    }

    public List<Object[]> getMostCoursesCategoriesNameAndCount() {
        return courseRepository.getMostCoursesCategoriesNameAndCount();
    }

    public List<Object[]> getMostInscribedCategoriesNameAndCount() {
        return courseRepository.getMostInscribedCategoriesNameAndCount();
    }
}
