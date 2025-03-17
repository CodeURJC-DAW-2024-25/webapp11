package es.daw.demo.DTO;

public class ReviewDTO {
    private long id;
    private String text;
    private Boolean pending;
    private Long userId;
    private Long courseId;
    private Long parentId;

    public ReviewDTO() {
        // Constructor vacío para serialización/deserialización
    }

    public ReviewDTO(long id, String text, Boolean pending, Long userId, Long courseId, Long parentId) {
        this.id = id;
        this.text = text;
        this.pending = pending;
        this.userId = userId;
        this.courseId = courseId;
        this.parentId = parentId;
    }

    // Getters y Setters
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

    public Boolean getPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
