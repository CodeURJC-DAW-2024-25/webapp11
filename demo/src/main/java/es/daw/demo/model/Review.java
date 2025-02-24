package es.daw.demo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String text;
    private Boolean pending;
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    private Course course;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    private List<Review> hijos;

    @ManyToOne
    private Review parent;

    public Review() {
        // Used by JPA
    }

    public Review(String text, User user, Course course, Review parent) {
        super();
        this.text = text;
        this.user = user;
        this.course = course;
        this.parent = parent;
        this.pending = false;
    }

    // Getters and setters
    public Boolean getState () {
        return pending;
    }

    public void setState (Boolean pending) {
        this.pending = pending;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Review> getHijos() {
        return hijos;
    }

    public void setHijos(List<Review> hijos) {
        this.hijos = hijos;
    }

    public Review getParent() {
        return parent;
    }

    public void setParent(Review parent) {
        this.parent = parent;
    }

    public void addHijo(Review hijo) {
        if (hijos == null) {
            hijos = new ArrayList<>();
        }
        hijos.add(hijo);
    }
    /*
     * Creo que va en el service
     * public void removeHijo(Comment hijo) {
     * if (hijos != null) {
     * hijos.remove(hijo);
     * }
     * }
     * 
     * public void remove() {
     * if (parent != null) {
     * parent.removeHijo(this);
     * }
     * 
     * }
     */
}