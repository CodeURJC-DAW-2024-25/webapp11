package es.daw.demo.service;
import jakarta.annotation.PostConstruct;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

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

        User user1 = new User("Pepe", "García", "user1@gmail.com", passwordEncoder.encode("1234"), "Desarrollo web", "USER");
        User user2 = new User("Ana", "López", "admin@gmail.com", passwordEncoder.encode("1234"), "Desarrollo web", "ADMIN");

        //userService.save(user1);
        //userService.save(user2);
        setProfileImage(user1, "/static/images/perfil_1.jpg");
        setProfileImage(user2, "/static/images/perfil_2.jpg");
        userService.save(user1);
        userService.save(user2);

        
        Course course1 = new Course("Desarrollo Android desde cero", "Aprende a desarrollar aplicaciones móviles para Android.", "Desarrollo móvil", user1);
        Course course2 = new Course("iOS para principiantes", "Introducción al desarrollo de apps para iPhone y iPad.", "Desarrollo móvil", user1);
        Course course3 = new Course("Flutter y Dart", "Crea aplicaciones multiplataforma con Flutter y Dart.", "Desarrollo móvil", user1);
        Course course4 = new Course("React Native avanzado", "Domina React Native para crear aplicaciones móviles híbridas.", "Desarrollo móvil", user1);

        Course course5 = new Course("Introducción al desarrollo de videojuegos", "Aprende los fundamentos del desarrollo de videojuegos.", "Desarrollo de videojuegos", user1);
        Course course6 = new Course("Unity desde cero", "Crea videojuegos en 2D y 3D con Unity.", "Desarrollo de videojuegos", user1);
        Course course7 = new Course("Desarrollo de videojuegos con Unreal Engine", "Aprende a crear juegos con Unreal Engine.", "Desarrollo de videojuegos", user1);
        Course course8 = new Course("Programación de videojuegos con Godot", "Explora el desarrollo de videojuegos con el motor Godot.", "Desarrollo de videojuegos", user1);

        Course course9 = new Course("Inicia tu emprendimiento", "Conoce los primeros pasos para lanzar tu negocio.", "Emprendimiento", user1);
        Course course10 = new Course("Estrategias de crecimiento para startups", "Cómo escalar tu negocio emergente.", "Emprendimiento", user1);
        Course course11 = new Course("Modelos de negocio innovadores", "Descubre diferentes modelos de negocio para emprender.", "Emprendimiento", user1);
        Course course12 = new Course("Marketing para emprendedores", "Aprende estrategias de marketing para hacer crecer tu negocio.", "Emprendimiento", user1);

        Course course13 = new Course("Finanzas personales", "Aprende a gestionar tus finanzas y ahorrar inteligentemente.", "Finanzas", user1);
        Course course14 = new Course("Inversiones para principiantes", "Conoce los fundamentos de la inversión.", "Finanzas", user1);
        Course course15 = new Course("Criptomonedas y blockchain", "Explora el mundo de las criptomonedas y la tecnología blockchain.", "Finanzas", user1);
        Course course16 = new Course("Gestión financiera empresarial", "Cómo manejar las finanzas de una empresa.", "Finanzas", user1);

        Course course17 = new Course("Introducción al marketing digital", "Aprende las bases del marketing en línea.", "Marketing Digital", user1);
        Course course18 = new Course("SEO y posicionamiento web", "Optimiza tu sitio web para los motores de búsqueda.", "Marketing Digital", user1);
        Course course19 = new Course("Publicidad en redes sociales", "Crea campañas publicitarias en Facebook, Instagram y más.", "Marketing Digital", user1);
        Course course20 = new Course("Email marketing efectivo", "Diseña estrategias de email marketing para captar clientes.", "Marketing Digital", user1);

        Course course21 = new Course("Liderazgo efectivo", "Desarrolla habilidades de liderazgo para guiar equipos.", "Liderazgo", user1);
        Course course22 = new Course("Gestión de equipos", "Aprende a gestionar equipos de trabajo de manera eficiente.", "Liderazgo", user1);
        Course course23 = new Course("Liderazgo en tiempos de crisis", "Cómo ser un líder en momentos difíciles.", "Liderazgo", user1);
        Course course24 = new Course("Toma de decisiones estratégicas", "Mejora tu capacidad de tomar decisiones acertadas.", "Liderazgo", user1);

        Course course25 = new Course("Comunicación efectiva", "Desarrolla habilidades para comunicarte con claridad.", "Comunicación", user1);
        Course course26 = new Course("Hablar en público", "Mejora tu confianza y técnicas para hablar en público.", "Comunicación", user1);
        Course course27 = new Course("Negociación y persuasión", "Aprende técnicas de negociación y persuasión efectivas.", "Comunicación", user1);
        Course course28 = new Course("Comunicación en el ámbito laboral", "Mejora la comunicación dentro de tu entorno profesional.", "Comunicación", user1);

        Course course29 = new Course("Desarrollo Web Completo", "Aprende desarrollo web desde cero con HTML, CSS, JavaScript, Node.js, y más.", "Desarrollo web",user1);
        Course course30 = new Course("React JS desde Cero", "Domina React JS y crea aplicaciones web modernas y reactivas. Incluye proyectos prácticos.", "Desarrollo web", user2);
        Course course31 = new Course("PHP y MySQL Profesional", "Aprende a crear aplicaciones web dinámicas con PHP y MySQL. Incluye integración con APIs.", "Desarrollo web",user1);
        Course course32 = new Course("Python desde cero", "Domina Python desde cero. Incluye proyectos prácticos.", "Desarrollo web", user2);
        Course course33 = new Course("Java desde", "Aprende a crear aplicaciones con Java.", "Desarrollo web",user1);
        
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

        setCourseNotes(course1, "/static/images/ejemplo.pdf");
        

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

    }

    public void setCourseImage(Course course, String classpathResource) throws IOException {
		course.setImage(true);
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
}
