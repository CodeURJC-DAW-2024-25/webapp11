package es.daw.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import es.daw.demo.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    List <User> findByFirstName (String firstName);
    User findById (long id);
}
