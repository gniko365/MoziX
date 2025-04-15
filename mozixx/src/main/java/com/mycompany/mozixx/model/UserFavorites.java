/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author szter
 */
@Entity
@Table(name = "user_favorites")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserFavorites.findAll", query = "SELECT u FROM UserFavorites u"),
    @NamedQuery(name = "UserFavorites.findByFavoriteId", query = "SELECT u FROM UserFavorites u WHERE u.favoriteId = :favoriteId"),
    @NamedQuery(name = "UserFavorites.findByAddedAt", query = "SELECT u FROM UserFavorites u WHERE u.addedAt = :addedAt")})
public class UserFavorites implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "favorite_id")
    private Integer favoriteId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "added_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedAt;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users userId;
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id")
    @ManyToOne(optional = false)
    private Movies movieId;

    public UserFavorites() {
    }

    public UserFavorites(Integer favoriteId) {
        this.favoriteId = favoriteId;
    }

    public UserFavorites(Integer favoriteId, Date addedAt) {
        this.favoriteId = favoriteId;
        this.addedAt = addedAt;
    }

    public Integer getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(Integer favoriteId) {
        this.favoriteId = favoriteId;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    public Movies getMovieId() {
        return movieId;
    }

    public void setMovieId(Movies movieId) {
        this.movieId = movieId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (favoriteId != null ? favoriteId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserFavorites)) {
            return false;
        }
        UserFavorites other = (UserFavorites) object;
        if ((this.favoriteId == null && other.favoriteId != null) || (this.favoriteId != null && !this.favoriteId.equals(other.favoriteId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.mozixx.model.UserFavorites[ favoriteId=" + favoriteId + " ]";
    }
    
}
