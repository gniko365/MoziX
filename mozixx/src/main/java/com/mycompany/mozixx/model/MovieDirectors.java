/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.model;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author szter
 */
@Entity
@Table(name = "movie_directors")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MovieDirectors.findAll", query = "SELECT m FROM MovieDirectors m"),
    @NamedQuery(name = "MovieDirectors.findByMovieId", query = "SELECT m FROM MovieDirectors m WHERE m.movieDirectorsPK.movieId = :movieId"),
    @NamedQuery(name = "MovieDirectors.findByDirectorId", query = "SELECT m FROM MovieDirectors m WHERE m.movieDirectorsPK.directorId = :directorId")})
public class MovieDirectors implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MovieDirectorsPK movieDirectorsPK;
    @JoinColumn(name = "director_id", referencedColumnName = "director_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Directors directors;
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Movies movies;

    public MovieDirectors() {
    }

    public MovieDirectors(MovieDirectorsPK movieDirectorsPK) {
        this.movieDirectorsPK = movieDirectorsPK;
    }

    public MovieDirectors(int movieId, int directorId) {
        this.movieDirectorsPK = new MovieDirectorsPK(movieId, directorId);
    }

    public MovieDirectorsPK getMovieDirectorsPK() {
        return movieDirectorsPK;
    }

    public void setMovieDirectorsPK(MovieDirectorsPK movieDirectorsPK) {
        this.movieDirectorsPK = movieDirectorsPK;
    }

    public Directors getDirectors() {
        return directors;
    }

    public void setDirectors(Directors directors) {
        this.directors = directors;
    }

    public Movies getMovies() {
        return movies;
    }

    public void setMovies(Movies movies) {
        this.movies = movies;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (movieDirectorsPK != null ? movieDirectorsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MovieDirectors)) {
            return false;
        }
        MovieDirectors other = (MovieDirectors) object;
        if ((this.movieDirectorsPK == null && other.movieDirectorsPK != null) || (this.movieDirectorsPK != null && !this.movieDirectorsPK.equals(other.movieDirectorsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.mozixx.model.MovieDirectors[ movieDirectorsPK=" + movieDirectorsPK + " ]";
    }
    
}
