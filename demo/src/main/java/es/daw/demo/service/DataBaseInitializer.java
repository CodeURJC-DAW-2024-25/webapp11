package es.daw.demo.service;
import jakarta.annotation.PostConstruct;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import es.daw.demo.model.Course;
import es.daw.demo.model.User;
import es.daw.demo.model.Enrollment;
import es.daw.demo.model.Review;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.repository.EnrollmentRepository;
import es.daw.demo.repository.ReviewRepository;
import es.daw.demo.repository.UserRepository;

@Service
public class DataBaseInitializer{

    private final CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
	private PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    DataBaseInitializer(CourseService courseService) {
        this.courseService = courseService;
    }
    @PostConstruct
    public void initializeDatabase() throws IOException {

        User user1 = new User("Pepe", "García", "user1@gmail.com", passwordEncoder.encode("1234"), "Desarrollo web", "USER");
        User user2 = new User("Juan", "García", "user2@gmail.com", passwordEncoder.encode("1234"), "Desarrollo móvil", "USER");
        User user3 = new User("Antonio", "García", "user3@gmail.com", passwordEncoder.encode("1234"), "Finanzas", "USER");
        User user4 = new User("Pedro", "García", "user4@gmail.com", passwordEncoder.encode("1234"), "Liderazgo", "USER");
        User user5 = new User("Felipe", "García", "user5@gmail.com", passwordEncoder.encode("1234"), "Comunicación", "USER");
        User user6 = new User("María", "García", "user6@gmail.com", passwordEncoder.encode("1234"), "Emprendimiento", "USER");
        User user7 = new User("Inés", "García", "user7@gmail.com", passwordEncoder.encode("1234"), "Desarrollo móvil", "USER");
        User user8 = new User("Irene", "García", "user8@gmail.com", passwordEncoder.encode("1234"), "Desarrollo web", "USER");
        User user9 = new User("Natalia", "García", "user9@gmail.com", passwordEncoder.encode("1234"), "Desarrollo web", "USER");
        User admin = new User("Ana", "López", "admin@gmail.com", passwordEncoder.encode("1234"), "Desarrollo web", "ADMIN");

    
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);
        userRepository.save(user8);
        userRepository.save(user9);
        userRepository.save(admin);

        
        Course course1 = new Course("Desarrollo Android desde cero", "Aprende a desarrollar aplicaciones móviles para Android.", "Desarrollo móvil", user1);
        Course course2 = new Course("iOS para principiantes", "Introducción al desarrollo de apps para iPhone y iPad.", "Desarrollo móvil", user2);
        Course course3 = new Course("Flutter y Dart", "Crea aplicaciones multiplataforma con Flutter y Dart.", "Desarrollo móvil", user3);
        Course course4 = new Course("React Native avanzado", "Domina React Native para crear aplicaciones móviles híbridas.", "Desarrollo móvil", user4);

        Course course5 = new Course("Introducción al desarrollo de videojuegos", "Aprende los fundamentos del desarrollo de videojuegos.", "Desarrollo de videojuegos", user5);
        Course course6 = new Course("Unity desde cero", "Crea videojuegos en 2D y 3D con Unity.", "Desarrollo de videojuegos", user6);
        Course course7 = new Course("Desarrollo de videojuegos con Unreal Engine", "Aprende a crear juegos con Unreal Engine.", "Desarrollo de videojuegos", user7);
        Course course8 = new Course("Programación de videojuegos con Godot", "Explora el desarrollo de videojuegos con el motor Godot.", "Desarrollo de videojuegos", user8);

        Course course9 = new Course("Inicia tu emprendimiento", "Conoce los primeros pasos para lanzar tu negocio.", "Emprendimiento", user9);
        Course course10 = new Course("Estrategias de crecimiento para startups", "Cómo escalar tu negocio emergente.", "Emprendimiento", user1);
        Course course11 = new Course("Modelos de negocio innovadores", "Descubre diferentes modelos de negocio para emprender.", "Emprendimiento", user2);
        Course course12 = new Course("Marketing para emprendedores", "Aprende estrategias de marketing para hacer crecer tu negocio.", "Emprendimiento", user3);

        Course course13 = new Course("Finanzas personales", "Aprende a gestionar tus finanzas y ahorrar inteligentemente.", "Finanzas", user4);
        Course course14 = new Course("Inversiones para principiantes", "Conoce los fundamentos de la inversión.", "Finanzas", user5);
        Course course15 = new Course("Criptomonedas y blockchain", "Explora el mundo de las criptomonedas y la tecnología blockchain.", "Finanzas", user6);
        Course course16 = new Course("Gestión financiera empresarial", "Cómo manejar las finanzas de una empresa.", "Finanzas", user7);

        Course course17 = new Course("Introducción al marketing digital", "Aprende las bases del marketing en línea.", "Marketing Digital", user8);
        Course course18 = new Course("SEO y posicionamiento web", "Optimiza tu sitio web para los motores de búsqueda.", "Marketing Digital", user9);
        Course course19 = new Course("Publicidad en redes sociales", "Crea campañas publicitarias en Facebook, Instagram y más.", "Marketing Digital", user1);
        Course course20 = new Course("Email marketing efectivo", "Diseña estrategias de email marketing para captar clientes.", "Marketing Digital", user2);

        Course course21 = new Course("Liderazgo efectivo", "Desarrolla habilidades de liderazgo para guiar equipos.", "Liderazgo", user3);
        Course course22 = new Course("Gestión de equipos", "Aprende a gestionar equipos de trabajo de manera eficiente.", "Liderazgo", user4);
        Course course23 = new Course("Liderazgo en tiempos de crisis", "Cómo ser un líder en momentos difíciles.", "Liderazgo", user5);
        Course course24 = new Course("Toma de decisiones estratégicas", "Mejora tu capacidad de tomar decisiones acertadas.", "Liderazgo", user6);

        Course course25 = new Course("Comunicación efectiva", "Desarrolla habilidades para comunicarte con claridad.", "Comunicación", user7);
        Course course26 = new Course("Hablar en público", "Mejora tu confianza y técnicas para hablar en público.", "Comunicación", user8);
        Course course27 = new Course("Negociación y persuasión", "Aprende técnicas de negociación y persuasión efectivas.", "Comunicación", user9);
        Course course28 = new Course("Comunicación en el ámbito laboral", "Mejora la comunicación dentro de tu entorno profesional.", "Comunicación", user1);

        Course course29 = new Course("Desarrollo Web Completo", "Aprende desarrollo web desde cero con HTML, CSS, JavaScript, Node.js, y más.", "Desarrollo web",user2);
        Course course30 = new Course("React JS desde Cero", "Domina React JS y crea aplicaciones web modernas y reactivas. Incluye proyectos prácticos.", "Desarrollo web", user3);
        Course course31 = new Course("PHP y MySQL Profesional", "Aprende a crear aplicaciones web dinámicas con PHP y MySQL. Incluye integración con APIs.", "Desarrollo web",user4);
        Course course32 = new Course("Python desde cero", "Domina Python desde cero. Incluye proyectos prácticos.", "Desarrollo web", user5);
        Course course33 = new Course("Java desde", "Aprende a crear aplicaciones con Java.", "Desarrollo web",user6);
        
        

        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
        courseRepository.save(course4);
        courseRepository.save(course5);
        courseRepository.save(course6);
        courseRepository.save(course7);
        courseRepository.save(course8);
        courseRepository.save(course9);
        courseRepository.save(course10);
        courseRepository.save(course11);
        courseRepository.save(course12);
        courseRepository.save(course13);
        courseRepository.save(course14);
        courseRepository.save(course15);
        courseRepository.save(course16);
        courseRepository.save(course17);
        courseRepository.save(course18);
        courseRepository.save(course19);
        courseRepository.save(course20);
        courseRepository.save(course21);
        courseRepository.save(course22);
        courseRepository.save(course23);
        courseRepository.save(course24);
        courseRepository.save(course25);
        courseRepository.save(course26);
        courseRepository.save(course27);
        courseRepository.save(course28);
        courseRepository.save(course29);
        courseRepository.save(course30);
        courseRepository.save(course31);
        courseRepository.save(course32);
        courseRepository.save(course33);
        
        Enrollment enrollment1 = new Enrollment(user4, course1);
        Enrollment enrollment2 = new Enrollment(user3, course1);
        Enrollment enrollment3 = new Enrollment(user2, course1);
        Enrollment enrollment4 = new Enrollment(user5, course1);
        Enrollment enrollment5 = new Enrollment(user1, course2);
        Enrollment enrollment6 = new Enrollment(user5, course2);
        Enrollment enrollment7 = new Enrollment(user6, course2);
        Enrollment enrollment8 = new Enrollment(user7, course2);
        Enrollment enrollment9 = new Enrollment(user1, course7);
        Enrollment enrollment10 = new Enrollment(user5, course7);
        Enrollment enrollment11 = new Enrollment(user6, course7);
        Enrollment enrollment12 = new Enrollment(user3, course7);
        enrollment1.setRating(4);
        enrollment2.setRating(2);
        enrollment3.setRating(3);
        enrollment4.setRating(4);
        enrollment5.setRating(4);
        enrollment6.setRating(1);
        enrollment7.setRating(4);
        enrollment8.setRating(3);
        enrollment9.setRating(4);
        enrollment10.setRating(2);
        enrollment11.setRating(3);
        enrollment12.setRating(1);

        enrollmentRepository.save(enrollment1);
        enrollmentRepository.save(enrollment2);
        enrollmentRepository.save(enrollment3);
        enrollmentRepository.save(enrollment4);
        enrollmentRepository.save(enrollment5);
        enrollmentRepository.save(enrollment6);
        enrollmentRepository.save(enrollment7);
        enrollmentRepository.save(enrollment8);
        enrollmentRepository.save(enrollment9);
        enrollmentRepository.save(enrollment10);
        enrollmentRepository.save(enrollment11);
        enrollmentRepository.save(enrollment12);

        //updateCourseRatings();
        updateUserTopic(user1);
        updateUserTopic(user2);
        updateUserTopic(user3);
        updateUserTopic(user4);
        updateUserTopic(user5);
        updateUserTopic(user6);
        updateUserTopic(user7);   
        
        Review review1 = new Review("Muy buen curso, lo recomiendo.", user1, course2, null);
        reviewRepository.save(review1);

        setProfileImage(user1, "/static/images/perfil_1.jpg");
        setProfileImage(user2, "/static/images/perfil_1.jpg");
        setProfileImage(user3, "/static/images/perfil_1.jpg");
        setProfileImage(user4, "/static/images/perfil_1.jpg");
        setProfileImage(user5, "/static/images/perfil_1.jpg");
        setProfileImage(user6, "/static/images/perfil_2.jpg");
        setProfileImage(user7, "/static/images/perfil_2.jpg");
        setProfileImage(user8, "/static/images/perfil_2.jpg");
        setProfileImage(user9, "/static/images/perfil_2.jpg");
        setProfileImage(admin, "/static/images/perfil_2.jpg");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);
        userRepository.save(user8);
        userRepository.save(user9);
        userRepository.save(admin);

        setCourseImage(course1, "/static/images/movil1.jpg");
        setCourseImage(course2, "/static/images/movil2.jpg");
        setCourseImage(course3, "/static/images/movil3.jpg");
        setCourseImage(course4, "/static/images/movil4.jpg");

        setCourseImage(course5, "/static/images/video1.jpg");
        setCourseImage(course6, "/static/images/video2.jpg");
        setCourseImage(course7, "/static/images/video3.jpg");
        setCourseImage(course8, "/static/images/video4.jpg");

        setCourseImage(course9, "/static/images/emp1.jpg");
        setCourseImage(course10, "/static/images/emp2.jpg");
        setCourseImage(course11, "/static/images/emp3.jpg");
        setCourseImage(course12, "/static/images/emp4.jpg");

        setCourseImage(course13, "/static/images/fin1.jpg");
        setCourseImage(course14, "/static/images/fin2.jpg");
        setCourseImage(course15, "/static/images/fin3.jpg");
        setCourseImage(course16, "/static/images/fin4.jpg");

        setCourseImage(course17, "/static/images/mark1.jpg");
        setCourseImage(course18, "/static/images/mark2.jpg");
        setCourseImage(course19, "/static/images/mark3.jpg");
        setCourseImage(course20, "/static/images/mark4.jpg");

        setCourseImage(course21, "/static/images/lid1.jpg");
        setCourseImage(course22, "/static/images/lid2.jpg");
        setCourseImage(course23, "/static/images/emp3.jpg");
        setCourseImage(course24, "/static/images/emp4.jpg");

        setCourseImage(course25, "/static/images/movil1.jpg");
        setCourseImage(course26, "/static/images/movil2.jpg");
        setCourseImage(course27, "/static/images/movil3.jpg");
        setCourseImage(course28, "/static/images/movil4.jpg");

        setCourseImage(course29, "/static/images/web1.jpg");
        setCourseImage(course30, "/static/images/web2.jpg");
        setCourseImage(course31, "/static/images/web3.jpg");
        setCourseImage(course32, "/static/images/web4.jpg");
        setCourseImage(course33, "/static/images/web5.jpg");

        setCourseNotes(course1, "/static/images/ejemplo.pdf");
        setCourseNotes(course2, "/static/images/ejemplo.pdf");
        setCourseNotes(course3, "/static/images/ejemplo.pdf");
        setCourseNotes(course4, "/static/images/ejemplo.pdf");
        setCourseNotes(course5, "/static/images/ejemplo.pdf");
        setCourseNotes(course6, "/static/images/ejemplo.pdf");
        setCourseNotes(course7, "/static/images/ejemplo.pdf");
        setCourseNotes(course8, "/static/images/ejemplo.pdf");
        setCourseNotes(course9, "/static/images/ejemplo.pdf");
        setCourseNotes(course10, "/static/images/ejemplo.pdf");
        setCourseNotes(course11, "/static/images/ejemplo.pdf");
        setCourseNotes(course12, "/static/images/ejemplo.pdf");
        setCourseNotes(course13, "/static/images/ejemplo.pdf");
        setCourseNotes(course14, "/static/images/ejemplo.pdf");
        setCourseNotes(course15, "/static/images/ejemplo.pdf");
        setCourseNotes(course16, "/static/images/ejemplo.pdf");
        setCourseNotes(course17, "/static/images/ejemplo.pdf");
        setCourseNotes(course18, "/static/images/ejemplo.pdf");
        setCourseNotes(course19, "/static/images/ejemplo.pdf");
        setCourseNotes(course20, "/static/images/ejemplo.pdf");
        setCourseNotes(course21, "/static/images/ejemplo.pdf");
        setCourseNotes(course22, "/static/images/ejemplo.pdf");
        setCourseNotes(course23, "/static/images/ejemplo.pdf");
        setCourseNotes(course24, "/static/images/ejemplo.pdf");
        setCourseNotes(course25, "/static/images/ejemplo.pdf");
        setCourseNotes(course26, "/static/images/ejemplo.pdf");
        setCourseNotes(course27, "/static/images/ejemplo.pdf");
        setCourseNotes(course28, "/static/images/ejemplo.pdf");
        setCourseNotes(course29, "/static/images/ejemplo.pdf");
        setCourseNotes(course30, "/static/images/ejemplo.pdf");
        setCourseNotes(course31, "/static/images/ejemplo.pdf");
        setCourseNotes(course32, "/static/images/ejemplo.pdf");
        setCourseNotes(course33, "/static/images/ejemplo.pdf");

        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
        courseRepository.save(course4);
        courseRepository.save(course5);
        courseRepository.save(course6);
        courseRepository.save(course7);
        courseRepository.save(course8);
        courseRepository.save(course9);
        courseRepository.save(course10);
        courseRepository.save(course11);
        courseRepository.save(course12);
        courseRepository.save(course13);
        courseRepository.save(course14);
        courseRepository.save(course15);
        courseRepository.save(course16);
        courseRepository.save(course17);
        courseRepository.save(course18);
        courseRepository.save(course19);
        courseRepository.save(course20);
        courseRepository.save(course21);
        courseRepository.save(course22);
        courseRepository.save(course23);
        courseRepository.save(course24);
        courseRepository.save(course25);
        courseRepository.save(course26);
        courseRepository.save(course27);
        courseRepository.save(course28);
        courseRepository.save(course29);
        courseRepository.save(course30);
        courseRepository.save(course31);
        courseRepository.save(course32);
        courseRepository.save(course33);

        updateCourseRatings(); //Change this to update the ratings after all enrollments are saved, check if it works with docker image

    }

    public void setCourseImage(Course course, String classpathResource) throws IOException {
		Resource image = new ClassPathResource(classpathResource);
		course.setImageFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
	}

    public void setCourseNotes(Course course, String classpathResource) throws IOException {
		Resource notes = new ClassPathResource(classpathResource);
		course.setNoteFile(BlobProxy.generateProxy(notes.getInputStream(), notes.contentLength()));
	}

    public void setProfileImage(User user, String classpathResource) throws IOException {
		Resource image = new ClassPathResource(classpathResource);
		user.setProfileImage(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
	}

    private void updateCourseRatings() {
        List<Course> courses = courseRepository.findAll();
        for (Course course : courses) {
            courseService.updateCourseRating(course.getId());
        }
    }
        private void updateUserTopic(User user) {
        List<Enrollment> enrollments = enrollmentRepository.findByUser_Id(user.getId());
        
        Map<String, Long> topicCount = enrollments.stream()
            .collect(Collectors.groupingBy(e -> e.getCourse().getTopic(), Collectors.counting()));

        String mostFrequentTopic = topicCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);

        if (mostFrequentTopic != null) {
            user.setTopic(mostFrequentTopic);
            userRepository.save(user);
        }
    }
}