package model;

import java.lang.annotation.Inherited;
import java.sql.Blob;
import java.util.List;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.*;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Generación automática del ID
    private Long id;

    @Column(nullable = false) // El autor no puede ser nulo
    private String author;

    @Column(nullable = false, length = 1000) // El contenido no puede ser nulo y tiene un límite de 1000 caracteres
    private String content;

    @Temporal(TemporalType.TIMESTAMP) // Guarda la fecha y hora del comentario
    private Date createdAt;

    @ManyToOne // Muchos comentarios pueden pertenecer a un solo curso
    @JoinColumn(name = "course_id", nullable = false) // Clave foránea que enlaza con Course
    private Course course;

    public Comment() {
        this.createdAt = new Date(); // Se asigna la fecha actual al crear un comentario
    }

    public Comment(String author, String content, Course course) {
        this.author = author;
        this.content = content;
        this.createdAt = new Date();
        this.course = course;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
