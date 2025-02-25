package es.daw.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import es.daw.demo.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional <User> findByFirstName (String firstName);
    //User findById (long id);
    boolean existsByEmail(String email); // Check if the user exists by email
    List<User> findByFirstNameContainingIgnoreCase(String firstNname);
}
