package es.daw.demo.service;
import jakarta.annotation.PostConstruct;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.sql.Blob;

import es.daw.demo.model.Course;
import es.daw.demo.model.User;
import es.daw.demo.repository.CourseRepository;

@Service
public class DataBaseInitializer{

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserService userService;

    @Autowired
	private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeDatabase() throws IOException {

        User user1 = new User("Pepe", "García", "user1@gmail.com", passwordEncoder.encode("1234"), "Desarrollo web", false, "USER");
        User user2 = new User("Ana", "López", "admin@gmail.com", passwordEncoder.encode("1234"), "Desarrollo web", false, "ADMIN");

        //userService.save(user1);
        //userService.save(user2);
        setProfileImage(user1, "/static/images/perfil_1.jpg");
        setProfileImage(user2, "/static/images/perfil_2.jpg");
        userService.save(user1);
        userService.save(user2);

        Course course1 = new Course("Desarrollo Web Completo", "Aprende desarrollo web desde cero con HTML, CSS, JavaScript, Node.js, y más.", "Desarrollo web",user1);
        Course course2 = new Course("React JS desde Cero", "Domina React JS y crea aplicaciones web modernas y reactivas. Incluye proyectos prácticos.", "Desarrollo web", user2);
        Course course3 = new Course("PHP y MySQL Profesional", "Aprende a crear aplicaciones web dinámicas con PHP y MySQL. Incluye integración con APIs.", "Desarrollo web",user1);
        Course course4 = new Course("Python desde cero", "Domina Python desde cero. Incluye proyectos prácticos.", "Desarrollo web", user2);
        Course course5 = new Course("Java desde", "Aprende a crear aplicaciones con Java.", "Desarrollo web",user1);
        
        setCourseImage(course1, "/static/images/img1.png");
        setCourseImage(course2, "/static/images/img2.png");
        setCourseImage(course3, "/static/images/img3.png");
        setCourseImage(course4, "/static/images/img2.png");
        setCourseImage(course5, "/static/images/img3.png");
        
        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
        courseRepository.save(course4);
        courseRepository.save(course5);


    }

    public void setCourseImage(Course course, String classpathResource) throws IOException {
		course.setImage(true);
		Resource image = new ClassPathResource(classpathResource);
		course.setImageFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
	}

    public void setProfileImage(User user, String classpathResource) throws IOException {
		Resource image = new ClassPathResource(classpathResource);
		user.setProfileImage(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
	}
}
