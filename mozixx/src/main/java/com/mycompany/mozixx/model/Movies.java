/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author User
 */
@Entity
@Table(name = "movies")
@NamedQueries({
    @NamedQuery(name = "Movies.findAll", query = "SELECT m FROM Movies m"),
    @NamedQuery(name = "Movies.findByMovieId", query = "SELECT m FROM Movies m WHERE m.movieId = :movieId"),
    @NamedQuery(name = "Movies.findByReleaseYear", query = "SELECT m FROM Movies m WHERE m.releaseYear = :releaseYear"),
    @NamedQuery(name = "Movies.findByMovieName", query = "SELECT m FROM Movies m WHERE m.movieName = :movieName"),
    @NamedQuery(name = "Movies.findByLength", query = "SELECT m FROM Movies m WHERE m.length = :length"),
    @NamedQuery(name = "Movies.findByCover", query = "SELECT m FROM Movies m WHERE m.cover = :cover"),
    @NamedQuery(name = "Movies.findByTrailerLink", query = "SELECT m FROM Movies m WHERE m.trailerLink = :trailerLink")})
public class Movies implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "movie_id")
    private Integer movieId;
    @Column(name = "release_year")
    private Integer releaseYear;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 99)
    @Column(name = "movie_name")
    private String movieName;
    @Column(name = "Length")
    private Integer length;
    @Size(max = 255)
    @Column(name = "cover")
    private String cover;
    @Size(max = 255)
    @Column(name = "trailer_link")
    private String trailerLink;
    @JoinTable(name = "movie_directors", joinColumns = {
        @JoinColumn(name = "movie_id", referencedColumnName = "movie_id")}, inverseJoinColumns = {
        @JoinColumn(name = "director_id", referencedColumnName = "director_id")})
    @ManyToMany
    private Collection<Directors> directorsCollection;
    @OneToMany(mappedBy = "movieId")
    private Collection<Ratings> ratingsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "movies")
    private Collection<MovieGenres> movieGenresCollection;

    public Movies() {
    }

    public Movies(Integer movieId) {
        this.movieId = movieId;
    }

    public Movies(Integer movieId, String movieName) {
        this.movieId = movieId;
        this.movieName = movieName;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTrailerLink() {
        return trailerLink;
    }

    public void setTrailerLink(String trailerLink) {
        this.trailerLink = trailerLink;
    }

    public Collection<Directors> getDirectorsCollection() {
        return directorsCollection;
    }

    public void setDirectorsCollection(Collection<Directors> directorsCollection) {
        this.directorsCollection = directorsCollection;
    }

    public Collection<Ratings> getRatingsCollection() {
        return ratingsCollection;
    }

    public void setRatingsCollection(Collection<Ratings> ratingsCollection) {
        this.ratingsCollection = ratingsCollection;
    }

    public Collection<MovieGenres> getMovieGenresCollection() {
        return movieGenresCollection;
    }

    public void setMovieGenresCollection(Collection<MovieGenres> movieGenresCollection) {
        this.movieGenresCollection = movieGenresCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (movieId != null ? movieId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movies)) {
            return false;
        }
        Movies other = (Movies) object;
        if ((this.movieId == null && other.movieId != null) || (this.movieId != null && !this.movieId.equals(other.movieId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.mozixx.model.Movies[ movieId=" + movieId + " ]";
    }
    
}
