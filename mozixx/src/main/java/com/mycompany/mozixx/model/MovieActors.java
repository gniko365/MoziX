/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.model;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author szter
 */
@Entity
@Table(name = "movie_actors")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MovieActors.findAll", query = "SELECT m FROM MovieActors m"),
    @NamedQuery(name = "MovieActors.findByMovieId", query = "SELECT m FROM MovieActors m WHERE m.movieActorsPK.movieId = :movieId"),
    @NamedQuery(name = "MovieActors.findByActorId", query = "SELECT m FROM MovieActors m WHERE m.movieActorsPK.actorId = :actorId")})
public class MovieActors implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MovieActorsPK movieActorsPK;

    public MovieActors() {
    }

    public MovieActors(MovieActorsPK movieActorsPK) {
        this.movieActorsPK = movieActorsPK;
    }

    public MovieActors(int movieId, int actorId) {
        this.movieActorsPK = new MovieActorsPK(movieId, actorId);
    }

    public MovieActorsPK getMovieActorsPK() {
        return movieActorsPK;
    }

    public void setMovieActorsPK(MovieActorsPK movieActorsPK) {
        this.movieActorsPK = movieActorsPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (movieActorsPK != null ? movieActorsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MovieActors)) {
            return false;
        }
        MovieActors other = (MovieActors) object;
        if ((this.movieActorsPK == null && other.movieActorsPK != null) || (this.movieActorsPK != null && !this.movieActorsPK.equals(other.movieActorsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.mozixx.model.MovieActors[ movieActorsPK=" + movieActorsPK + " ]";
    }
    
}
