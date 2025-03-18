package es.daw.demo.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.daw.demo.model.User;
import es.daw.demo.repository.UserRepository;
import es.daw.demo.dto.UserDTO;
import es.daw.demo.dto.UserMapper;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public void createUser(UserDTO user, MultipartFile imageFile, String password) throws IOException{
		if (user.id() != null) {
            throw new IllegalArgumentException();
        }

        User newUser = toDomain(user);
        newUser.setPassword(password);
        if (!imageFile.isEmpty()) {
            newUser.setProfileImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }
        newUser.setRoles(Collections.singletonList("USER"));
        userRepository.save(newUser);
	}
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserDTO findByEmail(String email) {
        return toDTO(userRepository.findByEmail(email).orElseThrow());
    }

    public UserDTO findByFirstName(String firstName) {
        return toDTO(userRepository.findByFirstName(firstName).orElseThrow());
    }

    public UserDTO findById (long id) {
        return toDTO(userRepository.findById(id).orElseThrow());
    }

    public Collection <UserDTO> findAll () {
        return toDTOs(userRepository.findAll());
    }

    public Collection <UserDTO> findByFirstNameContainingIgnoreCase (String firstName) {
        return toDTOs(userRepository.findByFirstNameContainingIgnoreCase (firstName));
    }

    public void deleteById (Long id) {
        userRepository.deleteById(id);
    }

    public Resource getUserImage(Long courseId) {
        User user = userRepository.findById(courseId).orElseThrow();
        try {
            return new InputStreamResource(user.getProfileImage().getBinaryStream());
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user image", e);
        }
    }

    public void updateUser(Long id, String firstName, String lastName, String email, String topic, MultipartFile imageFile, String password) throws IOException {
        User user = userRepository.findById(id).orElseThrow();
        if (user == null) {
            throw new IllegalArgumentException();
        }
        // Update user
        if (!firstName.isEmpty()) {
            user.setFirstName(firstName);
        }
        if (!lastName.isEmpty()) {
            user.setLastName(lastName);
        }
        if (!email.isEmpty()) {
            user.setEmail(email);
        }
        if (!topic.isEmpty()) {
            user.setTopic(topic);
        }
        if (!password.isEmpty()) {
            user.setPassword(password);
        }
        // Verify and update image
        if (imageFile.getOriginalFilename() != "" && !imageFile.isEmpty()) {
            user.setProfileImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }

        // Save updated user
        userRepository.save(user);
    }

    private User toDomain(UserDTO user) {
        return userMapper.toDomain(user);
    }

    private UserDTO toDTO(User user) {
        return userMapper.toDTO(user);
    }

    private Collection<UserDTO> toDTOs(Collection<User> users) {
        return userMapper.toDTO(users);
    }
}
