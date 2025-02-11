
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


    private String instructoriD;

    @Min(0)
    @Max(5)
    private int rating;

    @OneToMany(cascade = CascadeType.ALL)
	private List<Comment> comments = new ArrayList<>();

}