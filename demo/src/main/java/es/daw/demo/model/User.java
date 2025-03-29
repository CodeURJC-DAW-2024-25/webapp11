package es.daw.demo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.sql.Blob;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
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
    @ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;

    @Lob
    private Blob profileImage;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user") 
    private List<Enrollment> enrollements = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "instructor")
    private List<Course> taughtCourses = new ArrayList<>();

    protected User() {
        //Used by JPA
    }

    public User(String firstName, String lastName, String email, String password, String topic, String... roles) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.topic = topic;
        if (roles == null) {
            this.setRoles(Collections.singletonList("USER"));
        } else {
            this.roles = List.of(roles);
        }
        
    }

    @ManyToMany
    @JoinTable(name = "user_inscribed_course",
            joinColumns = @JoinColumn(name = "firstName"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> inscribedCourses = new ArrayList<>();


    // Getters and setters

    public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

    public void setProfileImage(Blob imageFile) {
		this.profileImage = imageFile;
	}

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
    public Blob getProfileImage() {
        return profileImage;
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

    public List<Course> getTaughtCourses() {
        return taughtCourses;
    }
    public void setTaughtCourses(Course course) {
        this.taughtCourses.add(course);
    }
}
