
package es.daw.demo.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String topic;

    @Lob
    private Blob image;

    @Lob
    private Blob notes;

    @ManyToOne
    private User instructor;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
    private List<Enrollment> enrollment = new ArrayList<>();

    private int rating;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Review> comments = new ArrayList<>();

    public Course() {
        // Used by JPA
    }

    public Course(String title, String description, String topic, Blob image, Blob notes, User instructor, int rating) {
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.image = image;
        this.notes = notes;
        this.instructor = instructor;
        this.rating = rating;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public Blob getNotes() {
        return notes;
    }

    public void setNotes(Blob notes) {
        this.notes = notes;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public List<Review> getComments() {
        return comments;
    }

    public void setComments(List<Review> comments) {
        this.comments = comments;
    }

    public void addComment(Review comment) {
        this.comments.add(comment);
        comment.setCourse(this);
    }

    public void removeComment(Review comment) {
        this.comments.remove(comment);
        comment.setCourse(null);
    }

}