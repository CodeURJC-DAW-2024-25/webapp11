package es.daw.demo.service;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.sql.Blob;

import es.daw.demo.model.Course;
import es.daw.demo.model.User;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.repository.UserRepository;

@Service
public class DataBaseInitializer{

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    //@PostConstruct Añadir cuando esté todo implementado
    public void initializeDatabase() throws IOException {

        User user1 = new User("Pepe", "García", "pepegarcia@gmail.com", "1234", "Informática");
        User user2 = new User("Juan", "López", "juanlopez@gmail.com", "2345", "Informática");

        userRepository.save(user1);
        userRepository.save(user2);

        Resource image1 = new ClassPathResource("/static/images/img1.png");
        Resource image2 = new ClassPathResource("/static/images/img2.jpg");
        Resource image3 = new ClassPathResource("/static/images/img3.png");

        Blob imgU1 = BlobProxy.generateProxy(image1.getInputStream(), image1.contentLength());
        Blob imgU2 = BlobProxy.generateProxy(image2.getInputStream(), image2.contentLength());
        Blob imgU3 = BlobProxy.generateProxy(image3.getInputStream(), image3.contentLength());
        
        courseRepository.save(new Course("Desarrollo Web Completo", "Aprende desarrollo web desde cero con HTML, CSS, JavaScript, Node.js, y más.", "Informática", imgU1, user1, 4));
        courseRepository.save(new Course("React JS desde Cero", "Domina React JS y crea aplicaciones web modernas y reactivas. Incluye proyectos prácticos.", "Informática", imgU2, user2, 4));
        courseRepository.save(new Course("PHP y MySQL Profesional", "Aprende a crear aplicaciones web dinámicas con PHP y MySQL. Incluye integración con APIs.", "Informática", imgU3, user1, 4));
    }
}
