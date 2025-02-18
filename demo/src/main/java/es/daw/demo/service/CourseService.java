package es.daw.demo.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.daw.demo.model.Course;
import es.daw.demo.model.User;
import es.daw.demo.repository.CourseRepository;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public void save(Course course) {
        courseRepository.save(course);
    }

    public void save(Course course, MultipartFile imageFile, MultipartFile noteFile) throws IOException{
		if(!imageFile.isEmpty()) {
			course.setImageFile(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
		}
        if(!noteFile.isEmpty()) {
			course.setImageFile(BlobProxy.generateProxy(noteFile.getInputStream(), noteFile.getSize()));
		}
		this.save(course);
	}

    public Optional<Course> findById (long id) {
        return courseRepository.findById(id);
    }

    public List<Course> findAll () {
        return courseRepository.findAll();
    }

    public List<Course> findTop4ByOrderByRatingDesc () {
        return courseRepository.findTop4ByOrderByRatingDesc();
    }

    public List<Course> findByTitle (String title) {
        return courseRepository.findByTitle(title);
    }

    public void deleteById (long id) {
        courseRepository.deleteById(id);
    }
}
