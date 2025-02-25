package es.daw.demo.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.daw.demo.model.User;
import es.daw.demo.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public void save(User user) {
        userRepository.save(user);
    }

    public void save(User user, MultipartFile imageFile) throws IOException{
		if(!imageFile.isEmpty()) {
			user.setProfileImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
		}
		this.save(user);
	}
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByFirstName(String firstName) {
        return userRepository.findByFirstName(firstName);
    }

    public Optional<User> findById (long id) {
        return userRepository.findById(id);
    }

    public List <User> findAll () {
        return userRepository.findAll();
    }

    public List <User> findByFirstNameContainingIgnoreCase (String firstName) {
        return userRepository.findByFirstNameContainingIgnoreCase (firstName);
    }

    public void deleteById (Long id) {
        userRepository.deleteById(id);
    }
}
