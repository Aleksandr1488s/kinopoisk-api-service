package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "films")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "film_id", unique = true,nullable = false)
    private Long filmId;

    @Column(name = "film_name")
    private String filmName;

    private Integer year;
    private  Double rating;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public Long getFilmId() {return filmId;}

    public void setFilmId(Long filmId) {this.filmId = filmId;}

    public String getFilmName() {return filmName;}

    public void setFilmName(String filmName) {this.filmName = filmName;}

    public Integer getYear() {return year;}

    public void setYear(Integer years) {this.year = years;}

    public Double getRating() {return rating;}

    public void setRating(Double rating) {this.rating = rating;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}
}
