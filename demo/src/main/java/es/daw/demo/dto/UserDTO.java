package es.daw.demo.DTO;

import java.sql.Blob;
import java.util.List;

public class UserDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String topic;
    private List<String> roles;
    private Blob profileImage;

    public UserDTO() {
        // Constructor vacío para serialización/deserialización
    }

    public UserDTO(long id, String firstName, String lastName, String email, String topic, List<String> roles,
            Blob profileImage) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.topic = topic;
        this.roles = roles;
        this.profileImage = profileImage;
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Blob getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Blob profileImage) {
        this.profileImage = profileImage;
    }
}
