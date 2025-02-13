package es.daw.demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;


@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String topic;
    //private Boolean admin = false;
    @Lob
    private byte[] profileImage;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user") 
    private List<Enrollment> enrollement = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "instructor")
    private List<Course> taughtCourses = new ArrayList<>();

    protected User() {
        //Used by JPA
    }

    public User(String firstName, String lastName, String email, String password, String topic, byte[] profileImage) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.topic = topic;
        this.profileImage = profileImage;
    }







    // Getters and setters
    public long getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getTopic() {
        return topic;
    }
    public byte[] getProfileImage() {
        return profileImage;
    }
    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
    public void setId(long id) {
        this.id = id;
    }
    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }
    public void setEnrolledCourses(Course course) {
        this.enrolledCourses.add(course);
    }
    public List<Course> getTaughtCourses() {
        return taughtCourses;
    }
    public void setTaughtCourses(Course course) {
        this.taughtCourses.add(course);
    }
}
