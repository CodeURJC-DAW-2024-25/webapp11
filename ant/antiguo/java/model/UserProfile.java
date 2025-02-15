package model;

import java.lang.annotation.Inherited;
import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String email;

    @Lob
    private byte[] profileImage;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userProfile")
    private List<Course> enrolledCourses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "instructor")
    private List<Course> taughtCourses = new ArrayList<>();

    public UserProfile() {
    }

    public UserProfile(String name, String email, byte[] profileImage) {
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(List<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public List<Course> getTaughtCourses() {
        return taughtCourses;
    }

    public void setTaughtCourses(List<Course> taughtCourses) {
        this.taughtCourses = taughtCourses;
    }
}
