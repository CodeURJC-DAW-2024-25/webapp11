package es.daw.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import es.daw.demo.service.EmailService;
import es.daw.demo.service.EnrollmentService;
import es.daw.demo.service.ReviewService;
import es.daw.demo.service.UserService;
import es.daw.demo.model.Course;
import es.daw.demo.model.Review;
import es.daw.demo.model.User;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class UserController {

    @Autowired 
    private ReviewService reviewService;
    
    @Autowired 
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EnrollmentService enrollmentService;


    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("user", userService.findByEmail(principal.getName()).get());
        } else {
            model.addAttribute("isLoggedIn", false);
        }
    }

    @GetMapping("/error-login")
    public String loginError(Model model) {
        model.addAttribute("errorTitle", "Error de Autenticación");
        model.addAttribute("errorMessage", "Usuario o contraseña incorrectos");
        return "/error";
    }



    // Create a new user
    @PostMapping("/newUser")
    public String newUser(@RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String email,
                          @RequestParam MultipartFile profileImage,
                          @RequestParam String topic,
                          @RequestParam String password,
                          @RequestParam String repeatPassword,
                          Model model
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
        password = passwordEncoder.encode(password);
        User user = new User(firstName, lastName, email, password, topic, "USER");
        userService.save(user, profileImage);

        //Save user in session
        //session.setAttribute("loggedInUser", user);

        //model.addAttribute("pagetitle", "Perfil");
        //model.addAttribute("isLoggedIn", true);
        //model.addAttribute("topic", topic);
        //model.addAttribute("user", user);
        //No se deberia de hacer un redirect a la pagina de index?
        return "redirect:/";
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
    

    // Change view to the profile page
    @GetMapping("/profile")
    public String showProfilePage(Model model, HttpServletRequest request) throws InterruptedException {
        Principal principal = request.getUserPrincipal();   
        if (principal != null) {
            model.addAttribute("pagetitle", "Perfil");
            if (request.isUserInRole("ADMIN")) {
                List<Review> pendingReviews = reviewService.findByPendingTrue();
                model.addAttribute("reviews", pendingReviews);
                return "admin";
            } else {
                List <Course> courses = enrollmentService.getCoursesByUser(userService.findByEmail(principal.getName()).get());
                System.out.println("Este es un mensaje de info");
                System.out.println(courses);
                model.addAttribute("enrolledCourses", enrollmentService.getCoursesByUser(userService.findByEmail(principal.getName()).get()));
                return "profile";
            }
        } else {
            model.addAttribute("errorTitle", "Error al acceder al perfil");
            model.addAttribute("errorMessage", "No se ha iniciado sesión");
            return "error";
        }
    }

    @GetMapping("/admin/users")
    public String listUsers(@RequestParam(required = false) String name, Model model) {
        List<User> users;
        if (name != null && !name.isEmpty()) {
            users = userService.findByFirstNameContainingIgnoreCase(name);
        } else {
            users = userService.findAll();
        }
        model.addAttribute("users", users);
        model.addAttribute("pagetitle", "Perfil");
        return "admin";

    }

    @PostMapping("/admin/users/delete/{id}")
    public String banearUsuario(@PathVariable Long id, Model model) {
        Optional <User> usuario = userService.findById(id);

        // Enviar el correo de notificación
        String subject = "Notificación: Tu cuenta ha sido eliminada";
        String message = "Estimado usuario,\n\nTu cuenta ha sido eliminada de forma permanente. Si crees que esto es un error, por favor contacta con el soporte.";

        emailService.sendEmail(usuario.get().getEmail(), subject, message);
        userService.deleteById(id);
        model.addAttribute("pagetitle", "Perfil");
        return "admin";
    }

    public String postMethodName(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }
    /*
    @GetMapping("/deleteAccount/{id}")
    public String deleteUser(Model model,@PathVariable("id") Long userID) {

        Optional<User> optionalUser = userService.findById(userID);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Course> userProducts = courseService.findByInstructor(user);
            for (Course eachProduct : userProducts) {
                courseService.deleteById(eachProduct.getId());
            }
            List<Review> userReview = reviewService.findReviewsByUser(userID);
            for (Review eachReview : userReview) {
                reviewService.deleteReview(eachReview.getId());
            }
        }

        userService.deleteById(userID);

        return "redirect:/index";
    }*/

    @PostMapping("/updateUser/{userID}")
    public String updateUser(HttpServletRequest request,
                             @PathVariable Long userID, Model model,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String currentPassword,
                             @RequestParam String newPassword,
                             @RequestParam String confirmPassword,
                             @RequestParam(required = false) MultipartFile imageFile) throws IOException, SQLException {

        // Obtener el token CSRF
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        // Agregar el token al modelo
        model.addAttribute("token", csrfToken.getToken());

        Optional<User> optionalUser = userService.findById(userID);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
			// Verify password and new password
			if (!newPassword.isEmpty() && newPassword.equals(confirmPassword) && passwordEncoder.matches(currentPassword, user.getPassword())) {
				user.setPassword(passwordEncoder.encode(newPassword));
			}
			// Update user
			if (!lastName.isEmpty()) {
				user.setLastName(lastName);
			}
			// Verify and update image
			if (imageFile.getOriginalFilename() != "" && !imageFile.isEmpty()) {
				user.setProfileImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
			}

			if (!firstName.isEmpty() && !userService.findByFirstName(firstName).isPresent()) {
				user.setFirstName(firstName);
				userService.save(user);
				return "redirect:/profile";
			}

			// Save updated user
			userService.save(user);

			// Redirect to profile
			return "redirect:/profile";
		} else {
			return "error";
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
