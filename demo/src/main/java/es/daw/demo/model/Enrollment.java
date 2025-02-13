package es.daw.demo.model;

import jakarta.annotation.Generated;
import jakarta.persistence.GenerationType;
import java.util.Date;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;

public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Course course;

    @Temporal(TemporalType.DATE)
    private Date date;

    public Enrollment() {
        // Used by JPA
    }

    public Enrollment(User user, Course course) {
        super();
        this.user = user;
        this.course = course;
        this.date = new Date();
    }
}
