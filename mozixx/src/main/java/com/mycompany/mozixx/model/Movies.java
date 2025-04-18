package com.mycompany.mozixx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

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

    @OneToMany(mappedBy = "movieId")
    @JsonIgnore
    private Collection<Ratings> ratings;
    
    @ManyToMany(mappedBy = "favoriteMovies")
    @JsonIgnore
    private Set<Users> favoritedByUsers;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "movieId")
    private Collection<UserFavorites> userFavoritesCollection;

    public Movies() {
    }

    public Movies(Integer movieId) {
        this.movieId = movieId;
    }

    public Movies(Integer movieId, String movieName) {
        this.movieId = movieId;
        this.movieName = movieName;
    }

    // Getterek és setterek
    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Object id) {
        this.movieId = id instanceof Integer ? (Integer)id : 
                      id instanceof String ? Integer.parseInt((String)id) : null;
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

    public void setLength(Object length) {
        this.length = length instanceof Integer ? (Integer)length : 
                      length instanceof String ? Integer.parseInt((String)length) : null;
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

    @XmlTransient
    public Collection<UserFavorites> getUserFavoritesCollection() {
        return userFavoritesCollection;
    }

    public void setUserFavoritesCollection(Collection<UserFavorites> userFavoritesCollection) {
        this.userFavoritesCollection = userFavoritesCollection;
    }

    public Collection<Ratings> getRatings() {
        return ratings;
    }

    public void setRatings(Collection<Ratings> ratings) {
        this.ratings = ratings;
    }

    public Set<Users> getFavoritedByUsers() {
        return favoritedByUsers;
    }

    public void setFavoritedByUsers(Set<Users> favoritedByUsers) {
        this.favoritedByUsers = favoritedByUsers;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (movieId != null ? movieId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
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

    public static ArrayList<Movies> getMovies() {
        EntityManager em = null;
        try {
            em = Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT").createEntityManager();
            StoredProcedureQuery spq = em.createStoredProcedureQuery("GetMovies", Movies.class);
            spq.execute();
            return new ArrayList<>(spq.getResultList());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}