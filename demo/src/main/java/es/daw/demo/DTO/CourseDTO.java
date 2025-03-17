package es.daw.demo.dto;

import java.sql.Blob;

public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private String topic;
    private boolean image;
    private Blob imageFile;
    private Blob notes;
    private Long instructorId;
    private int rating;

    public CourseDTO() {
        // Empty constructor
    }

    public CourseDTO(Long id, String title, String description, String topic, boolean image, Blob imageFile, Blob notes,
            Long instructorId, int rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.image = image;
        this.imageFile = imageFile;
        this.notes = notes;
        this.instructorId = instructorId;
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

    public boolean isImage() {
        return image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public Blob getImageFile() {
        return imageFile;
    }

    public void setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public Blob getNotes() {
        return notes;
    }

    public void setNotes(Blob notes) {
        this.notes = notes;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
