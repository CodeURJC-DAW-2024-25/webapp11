package es.daw.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import es.daw.demo.service.EmailService;
import es.daw.demo.service.ReviewService;
import es.daw.demo.service.UserService;
import es.daw.demo.dto.ReviewDTO;
import es.daw.demo.dto.UserDTO;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserWebController {

    @Autowired 
    private ReviewService reviewService;
    
    @Autowired 
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("user", userService.findByEmail(principal.getName()));
        } else {
            model.addAttribute("isLoggedIn", false);
        }
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
        UserDTO user = new UserDTO(null, firstName, lastName, email, topic, List.of("USER"));
        userService.createUser(user, profileImage, password);
        return "redirect:/";
    }

    // Change view to the sign up page
    @GetMapping("/signUp")
    public String showSignUpPage() {
        return "signup"; 
    }

    // Upload a profile image
    @GetMapping("/user/{id}/image")
    public ResponseEntity<Object> getProfileImage(@PathVariable long id) throws SQLException {
        Resource profileImage = userService.getUserImage(id);

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
				.body(profileImage);
    }
    

    // Change view to the profile page
    @GetMapping("/profile")
    public String showProfilePage(Model model, HttpServletRequest request) throws InterruptedException {
        Principal principal = request.getUserPrincipal();   
        if (principal != null) {
            model.addAttribute("pagetitle", "Perfil");
            if (request.isUserInRole("ADMIN")) {
                Collection<ReviewDTO> pendingReviews = reviewService.findByPendingTrue();
                model.addAttribute("reviews", pendingReviews);
                return "admin";
            } else {
                return "profile";
            }
        } else {
            model.addAttribute("errorTitle", "Error al acceder al perfil");
            model.addAttribute("errorMessage", "No se ha iniciado sesión");
            return "error";
        }
    }

    //List users
    @GetMapping("/admin/users")
    public String listUsers(@RequestParam(required = false) String name, Model model) {
        Collection<UserDTO> users;
        if (name != null && !name.isEmpty()) {
            users = userService.findByFirstNameContainingIgnoreCase(name);
        } else {
            users = userService.findAll();
        }
        model.addAttribute("users", users);
        model.addAttribute("pagetitle", "Perfil");
        return "admin";

    }

    // Delete user
    @PostMapping("/admin/users/delete/{id}")
    public String banearUsuario(@PathVariable Long id, Model model) {
        UserDTO user = userService.findById(id);
        for (ReviewDTO review: reviewService.findReviewsByUser(id)) {
            reviewService.deleteReview(review.id());
        }
        // Send email of notification
        String subject = "Notificación: Tu cuenta ha sido eliminada";
        String message = "Estimado usuario,\n\nTu cuenta ha sido eliminada de forma permanente. Si crees que esto es un error, por favor contacta con el soporte.";

        emailService.sendEmail(user.email(), subject, message);
        userService.deleteById(id);
        model.addAttribute("pagetitle", "Perfil");
        return "admin";
    }

    // Update user
    @PostMapping("/user/{userID}/edit")
    public String updateUser(HttpServletRequest request,
                             @PathVariable Long userID, Model model,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             //@RequestParam String email,
                             @RequestParam String topic,
                             @RequestParam String currentPassword,
                             @RequestParam String newPassword,
                             @RequestParam String confirmPassword,
                             @RequestParam(required = false) MultipartFile imageFile) throws IOException, SQLException {

        UserDTO user = userService.findById(userID);
        String email = user.email();
        if (user != null) {
            String password = "";
            // Verify password and new password
            if (newPassword.equals(confirmPassword)) {
                if(!newPassword.isEmpty()){
                    password = passwordEncoder.encode(newPassword);
                }
                user = userService.updateUser(userID, firstName, lastName, email, topic, imageFile, password);
                System.out.println("User updated");
                System.out.println(user.email());
            }
            else {
                model.addAttribute("errorTitle", "Error al actualizar el perfil");
                model.addAttribute("errorMessage", "Las contraseñas no coinciden");
                return "error";
            }
            

            // Redirect to profile
            return "redirect:/profile";
        } else {
            return "error";
        }
    }

    @GetMapping("/deleteAccount/{userID}")
    public String deleteAccount(HttpServletRequest request,
                             @PathVariable Long userID, Model model) throws IOException, SQLException, ServletException {
        
        UserDTO user = userService.findById(userID);
        for (ReviewDTO review: reviewService.findReviewsByUser(userID)) {
            reviewService.deleteReview(review.id());
        }
        // Send email of notification
        String subject = "Notificación: Tu cuenta ha sido eliminada";
        String message = "Estimado usuario,\n\nTal y como solicitó, su cuenta ha sido eliminada de forma permanente.";
                        
        emailService.sendEmail(user.email(), subject, message);
        userService.deleteById(userID);
        //model.addAttribute("pagetitle", "Perfil");

        request.logout();

        return "redirect:/";
    }
    
}
