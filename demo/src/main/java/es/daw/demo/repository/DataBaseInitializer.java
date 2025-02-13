package es.daw.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

@Controller
public class DataBaseInitializer implements CommandLineRunner{
    

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create a new user
        //userRepository.save(new User("John", "Doe", ")
    }
}
