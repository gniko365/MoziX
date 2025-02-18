/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author szter
 */
@Embeddable
public class MovieGenresPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "movie_id")
    private int movieId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "genre_id")
    private int genreId;

    public MovieGenresPK() {
    }

    public MovieGenresPK(int movieId, int genreId) {
        this.movieId = movieId;
        this.genreId = genreId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) movieId;
        hash += (int) genreId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MovieGenresPK)) {
            return false;
        }
        MovieGenresPK other = (MovieGenresPK) object;
        if (this.movieId != other.movieId) {
            return false;
        }
        if (this.genreId != other.genreId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.mozixx.model.MovieGenresPK[ movieId=" + movieId + ", genreId=" + genreId + " ]";
    }
    
}
