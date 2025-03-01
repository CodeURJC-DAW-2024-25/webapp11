package es.daw.demo.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import es.daw.demo.repository.CourseRepository;
import es.daw.demo.model.Course;
import es.daw.demo.model.User;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class CourseStatisticsAuthorizationManager implements AuthorizationManager<HttpServletRequest> {

    private final CourseRepository courseRepository;

    public CourseStatisticsAuthorizationManager(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, HttpServletRequest request) {
        Authentication auth = authentication.get();

        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        // Extraer el ID del curso de la URL "/course/{id}/statistics"
        String path = request.getRequestURI();
        String[] segments = path.split("/");

        if (segments.length < 3) {
            return new AuthorizationDecision(false); // Ruta incorrecta
        }

        Long courseId;
        try {
            courseId = Long.parseLong(segments[2]); // Extraer el ID del curso
        } catch (NumberFormatException e) {
            return new AuthorizationDecision(false);
        }

        // Obtener el usuario autenticado
        Object principal = auth.getPrincipal();
        if (!(principal instanceof User)) {
            return new AuthorizationDecision(false);
        }

        User user = (User) principal;

        // Si el usuario es ADMIN, siempre tiene acceso
        if (user.getRoles().contains("ROLE_ADMIN")) {
            return new AuthorizationDecision(true);
        }

        // Buscar el curso en la base de datos
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isEmpty()) {
            return new AuthorizationDecision(false);
        }

        Course course = optionalCourse.get();

        // Verificar si el usuario es el profesor del curso
        boolean isProfessor = course.getInstructor() != null &&
                Long.valueOf(user.getId()).equals(course.getInstructor().getId());

        return new AuthorizationDecision(isProfessor);
    }
}
