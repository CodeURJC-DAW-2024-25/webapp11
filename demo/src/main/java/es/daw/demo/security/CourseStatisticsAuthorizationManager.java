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

        // Extract course ID from the following URL "/course/{id}/statistics"
        String path = request.getRequestURI();
        String[] segments = path.split("/");

        if (segments.length < 3) {
            return new AuthorizationDecision(false); // incorrect path
        }

        Long courseId;
        try {
            courseId = Long.parseLong(segments[2]); // Extract course ID
        } catch (NumberFormatException e) {
            return new AuthorizationDecision(false);
        }

        // Get authenticated user
        Object principal = auth.getPrincipal();
        if (!(principal instanceof User)) {
            return new AuthorizationDecision(false);
        }

        User user = (User) principal;

        // If user is admin, he will always access
        if (user.getRoles().contains("ROLE_ADMIN")) {
            return new AuthorizationDecision(true);
        }

        // Search course in the database
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isEmpty()) {
            return new AuthorizationDecision(false);
        }

        Course course = optionalCourse.get();

        // Verify if the user is the teacher of the course
        boolean isProfessor = course.getInstructor() != null &&
                Long.valueOf(user.getId()).equals(course.getInstructor().getId());

        return new AuthorizationDecision(isProfessor);
    }
}
