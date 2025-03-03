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
public class MovieActorsPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "movie_id")
    private int movieId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "actor_id")
    private int actorId;

    public MovieActorsPK() {
    }

    public MovieActorsPK(int movieId, int actorId) {
        this.movieId = movieId;
        this.actorId = actorId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getActorId() {
        return actorId;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) movieId;
        hash += (int) actorId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MovieActorsPK)) {
            return false;
        }
        MovieActorsPK other = (MovieActorsPK) object;
        if (this.movieId != other.movieId) {
            return false;
        }
        if (this.actorId != other.actorId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.mozixx.model.MovieActorsPK[ movieId=" + movieId + ", actorId=" + actorId + " ]";
    }
    
}
