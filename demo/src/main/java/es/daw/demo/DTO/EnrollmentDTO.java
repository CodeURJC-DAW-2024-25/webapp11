package es.daw.demo.DTO;

import java.util.Date;

public class EnrollmentDTO {
    private long id;
    private Long userId;
    private Long courseId;
    private Date date;
    private int rating;

    public EnrollmentDTO() {
        // Empty constructor for serialization/deserialization
    }

    public EnrollmentDTO(long id, Long userId, Long courseId, Date date, int rating) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.date = date;
        this.rating = rating;
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
