package es.daw.demo.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.daw.demo.dto.CourseDTO;
import es.daw.demo.dto.UserDTO;
import es.daw.demo.dto.CourseMapper;
import es.daw.demo.dto.EnrollmentDTO;
import es.daw.demo.model.Course;
import es.daw.demo.repository.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;



@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private EnrollmentService enrollmentService;

    public CourseDTO createCourse(CourseDTO courseDTO, MultipartFile imageFile, MultipartFile noteFile) throws IOException {
        if (courseDTO.id() != null) {
            throw new IllegalArgumentException();
        }

        Course course = toDomain(courseDTO);
        if (imageFile != null && !imageFile.isEmpty()) {
            course.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }
        if (noteFile != null && !noteFile.isEmpty()) {
            course.setNoteFile(BlobProxy.generateProxy(noteFile.getInputStream(), noteFile.getSize()));
        }
        courseRepository.save(course);
        return toDTO(course);
    }

    public CourseDTO getCourse(long id) {
        return toDTO(courseRepository.findById(id));
    }

    public Page<CourseDTO> getCoursesOrderedByRating(Pageable pageable) {
        // Return courses by rating and paged
        return courseRepository.findAllByOrderByRatingDesc(pageable).map(this::toDTO);
    }

    public Collection<CourseDTO> findTop4ByOrderByRatingDesc() {
        return toDTOs(courseRepository.findTop4ByOrderByRatingDesc());
    }

    public Collection<CourseDTO> findByTitle(String title) {
        return toDTOs(courseRepository.findByTitle(title));
    }

    public Collection<CourseDTO> findByInstructor(UserDTO user) {
        return toDTOs(courseRepository.findByInstructor_Id(user.id()));
    }

    public void deleteCourse(long id) {
        courseRepository.deleteById(id);
    }

    public boolean isUserInstructor(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        return course != null && course.getInstructor() != null && course.getInstructor().getId() == userId;
    }

    public Collection<CourseDTO> getTopRatedCoursesByTopic(String topic) {
        return toDTOs(courseRepository.findTop4ByTopicOrderByRatingDesc(topic));
    }

    public Resource getCourseImage(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        try {
            return new InputStreamResource(course.getImageFile().getBinaryStream());
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving course image", e);
        }
    }
    
    public Resource getCourseNotes (Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        try {
            return new InputStreamResource(course.getNotes().getBinaryStream());
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving course notes", e);
        }
    }

    public void updateCourse(Long id, String title, String description, String topic, MultipartFile imageFile, MultipartFile noteFile) throws IOException {
        Course course = courseRepository.findById(id).orElseThrow();
        if (course == null) {
            throw new IllegalArgumentException();
        }
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

        if (!noteFile.isEmpty()) {
            course.setNotes(BlobProxy.generateProxy(noteFile.getInputStream(), noteFile.getSize()));
        }

        // Save updated course
        courseRepository.save(course);
    }

    public Page<CourseDTO> findAllByOrderByRatingDesc(Pageable pageable) {
        return courseRepository.findAllByOrderByRatingDesc(pageable).map(this::toDTO);
    }

    public Page<CourseDTO> findByInstructor(UserDTO user, Pageable pageable) {
        return courseRepository.findByInstructor_Id(user.id(), pageable).map(this::toDTO);
    }

    public Page<CourseDTO> findByTopicOrderByRatingDesc(String topic, Pageable pageable) {
        return courseRepository.findByTopicOrderByRatingDesc(topic, pageable).map(this::toDTO);
    }

    public Page<CourseDTO> searchCourses(String title, Pageable pageable) {
        return courseRepository.searchCourses(title, pageable).map(this::toDTO);
    }

    public List<Object[]> getMostCoursesCategoriesNameAndCount() {
        return courseRepository.getMostCoursesCategoriesNameAndCount();
    }

    public List<Object[]> getMostInscribedCategoriesNameAndCount() {
        return courseRepository.getMostInscribedCategoriesNameAndCount();
    }

    public void updateCourseRating (Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        Collection<EnrollmentDTO> enrollments = enrollmentService.findEnrollmentsByCourse(courseId);

        int totalRating = enrollments.stream()
                                     .mapToInt(enrollmentDTO -> enrollmentDTO.rating())
                                     .sum();
        int newAverageRating = enrollments.isEmpty() ? 0 : totalRating / enrollments.size();

        course.setRating(newAverageRating);
        courseRepository.save(course);
    }

    public CourseDTO findById(Long courseId) {
        return toDTO(courseRepository.findById(courseId).orElseThrow());
    }

    private Course toDomain(CourseDTO courseDTO) {
        return courseMapper.toDomain(courseDTO);
    }

    private CourseDTO toDTO(Course course) {
        return courseMapper.toDTO(course);
    }

    private Collection<CourseDTO> toDTOs(Collection<Course> courses) {
        return courseMapper.toDTOs(courses);
    }

    

}
