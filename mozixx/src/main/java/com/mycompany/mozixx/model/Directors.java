/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mozixx.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author User
 */
@Entity
@Table(name = "directors")
@NamedQueries({
    @NamedQuery(name = "Directors.findAll", query = "SELECT d FROM Directors d"),
    @NamedQuery(name = "Directors.findByDirectorId", query = "SELECT d FROM Directors d WHERE d.directorId = :directorId"),
    @NamedQuery(name = "Directors.findByName", query = "SELECT d FROM Directors d WHERE d.name = :name"),
    @NamedQuery(name = "Directors.findByDirectorImage", query = "SELECT d FROM Directors d WHERE d.directorImage = :directorImage"),
    @NamedQuery(name = "Directors.findByBirthDate", query = "SELECT d FROM Directors d WHERE d.birthDate = :birthDate")})
public class Directors implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "director_id")
    private Integer directorId;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "director_image")
    private String directorImage;
    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    @ManyToMany(mappedBy = "directorsCollection")
    private Collection<Movies> moviesCollection;

    public Directors() {
    }

    public Directors(Integer directorId) {
        this.directorId = directorId;
    }

    public Integer getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Integer directorId) {
        this.directorId = directorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectorImage() {
        return directorImage;
    }

    public void setDirectorImage(String directorImage) {
        this.directorImage = directorImage;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Collection<Movies> getMoviesCollection() {
        return moviesCollection;
    }

    public void setMoviesCollection(Collection<Movies> moviesCollection) {
        this.moviesCollection = moviesCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (directorId != null ? directorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Directors)) {
            return false;
        }
        Directors other = (Directors) object;
        if ((this.directorId == null && other.directorId != null) || (this.directorId != null && !this.directorId.equals(other.directorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.mozixx.model.Directors[ directorId=" + directorId + " ]";
    }
    
}
