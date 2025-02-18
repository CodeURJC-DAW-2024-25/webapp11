package es.daw.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import es.daw.demo.service.UserService;
import es.daw.demo.model.User;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import java.sql.SQLException;
import java.util.Optional;


import jakarta.servlet.http.HttpSession;


@Controller
public class UserController {
    
    @Autowired 
    private UserService userService;

    // Create a new user
    @PostMapping("/newUser")
    public String newUser(@RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String email,
                          @RequestParam MultipartFile profileImage,
                          @RequestParam String topic,
                          @RequestParam String password,
                          @RequestParam String repeatPassword,
                          Model model,
                          HttpSession session
                        ) throws Exception {
        // Check if the passwords match
        if (!password.equals(repeatPassword)) {
            model.addAttribute("errorTitle", "Error al crear la cuenta");
            model.addAttribute("errorMessage", "Las contraseñas no coinciden");
            return "error";
        }

        // Check if the user already exists
        if (userService.existsByEmail(email)) {
            model.addAttribute("errorTitle", "Error al crear la cuenta");
            model.addAttribute("errorMessage", "El usuario ya existe");
            return "error";
        }
        User user = new User(firstName, lastName, email, password, topic);
        userService.save(user, profileImage);

        //Save user in session
        session.setAttribute("loggedInUser", user);

        model.addAttribute("pagetitle", "Perfil");
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("topic", topic);
        model.addAttribute("user", user);
        //Redirección al índice no funciona todavía, falta añadir los atributos al modelo
        return "profile";
    }

    // Change view to the sign up page
    @GetMapping("/signUp")
    public String showSignUpPage() {
        return "signup"; 
    }

    // Change view to the login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; 
    }

    // Upload a profile image
    @GetMapping("/profileImage/{id}")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {
        Optional<User> user = userService.findById(id);
		if (user.isPresent() && user.get().getProfileImage() != null) {

			Resource file = new InputStreamResource(user.get().getProfileImage().getBinaryStream());

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
					.contentLength(user.get().getProfileImage().length()).body(file);

		} else {
			return ResponseEntity.notFound().build();
		}
    }
    





    /* 
    // Find a user by email and Password
    @PostMapping("/findUser")
    public String findUser(@RequestParam String email,
                           @RequestParam String password,
                           HttpSession session,
                           Model model) {
        User user = userRepository.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("errorTitle", "Error al iniciar sesión");
            model.addAttribute("errorMessage", "Email o contraseña incorrectos");
            return "error";
        }
        session.setAttribute("user", user);
        return "index";
    }


    //Edit user
    @PostMapping("/editUser")
    public String editUser (Model model, User editedUser) {
        Optional<User> userOptional = userRepository.findById(editedUser.getId());
        if (userOptional.isPresent()) {
            userRepository.save(editedUser);
            model.addAttribute("user", editedUser);
            return "profile";
        } else {
            model.addAttribute("errorTitle", "Error al editar el usuario");
            model.addAttribute("errorMessage", "El usuario no existe");
            return "error";
        }
    }

    //Delete user
    @PostMapping("/deleteUser")
    public String deleteUser (Model model, User deletedUser) {
        Optional<User> userOptional = userRepository.findById(deletedUser.getId());
        if (userOptional.isPresent()) {
            userRepository.delete(deletedUser);
            model.addAttribute("errorTitle", "Usuario eliminado");
            model.addAttribute("errorMessage", "El usuario ha sido eliminado");
            return "error";
        } else {
            model.addAttribute("errorTitle", "Error al eliminar el usuario");
            model.addAttribute("errorMessage", "El usuario no existe");
            return "error";
        }
    }*/
}
