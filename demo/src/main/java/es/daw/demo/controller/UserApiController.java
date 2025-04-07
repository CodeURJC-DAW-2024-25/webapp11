package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.io.Resource;
import es.daw.demo.service.EmailService;
import es.daw.demo.service.UserService;
import es.daw.demo.dto.UserDTO;
import es.daw.demo.dto.UserSignUpDTO;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
@RestController
@RequestMapping("/api/v1/users")
public class UserApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserSignUpDTO user) throws Exception {
        if (userService.existsByEmail(user.email())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String password = passwordEncoder.encode(user.password());
        UserDTO userDTO = new UserDTO(user.id(), user.firstName(), user.lastName(), user.email(), user.topic(), user.roles());
        userDTO = userService.createUser(userDTO, null, password);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(userDTO.id()).toUri();
        return ResponseEntity.created(location).body(userDTO);
    }

    @PostMapping("/{id}/image")
	public ResponseEntity<Object> createUserImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
		userService.createUserImage(id, imageFile.getInputStream(), imageFile.getSize());
        URI location = fromCurrentRequest().build().toUri();
		return ResponseEntity.created(location).build();
	}

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getProfileImage(@PathVariable long id) throws SQLException {
        Resource profileImage = userService.getUserImage(id);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(profileImage);
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getUsers(@RequestParam(required = false) String name) {
        List<UserDTO> users = (name != null && !name.isEmpty()) ?
                (List<UserDTO>) userService.findByFirstNameContainingIgnoreCase(name) : (List<UserDTO>) userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        if (user != null) {
            emailService.sendEmail(user.email(), "Cuenta eliminada", "Tu cuenta ha sido eliminada");
            userService.deleteById(id);
            return ResponseEntity.ok("Usuario eliminado exitosamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                            @RequestBody UserDTO UpdatedUser,
                                            @RequestParam(required = false) String password) throws IOException {
        UserDTO user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userService.updateUser(id, UpdatedUser.firstName(), UpdatedUser.lastName(), UpdatedUser.email(), UpdatedUser.topic(), null, passwordEncoder.encode(password));
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}/image")
	public ResponseEntity<Object> updateUserImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
		userService.createUserImage(id, imageFile.getInputStream(), imageFile.getSize());
        URI location = fromCurrentRequest().build().toUri();
		return ResponseEntity.created(location).build();
	}
}
