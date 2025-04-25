package com.mycompany.mozixx.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.ParameterMode;
import javax.persistence.Persistence;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findByUserId", query = "SELECT u FROM Users u WHERE u.userId = :userId"),
    @NamedQuery(name = "Users.findByUsername", query = "SELECT u FROM Users u WHERE u.username = :username"),
    @NamedQuery(name = "Users.findByEmail", query = "SELECT u FROM Users u WHERE u.email = :email"),
    @NamedQuery(name = "Users.findByPassword", query = "SELECT u FROM Users u WHERE u.password = :password"),
    @NamedQuery(name = "Users.findByRegistrationDate", query = "SELECT u FROM Users u WHERE u.registrationDate = :registrationDate"),
    @NamedQuery(name = "Users.findByRole", query = "SELECT u FROM Users u WHERE u.role = :role")})
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "userId"
    )
public class Users implements Serializable {

    public enum Role {
        user, admin;

        public static Role fromString(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "user_id")
    private Integer userId;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "username")
    private String username;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "email")
    private String email;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password")
    private String password;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "registration_date")
    @Temporal(TemporalType.DATE)
    private Date registrationDate;
    
    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "ENUM('user','admin')")
    private Role role;

    @OneToMany(mappedBy = "userId")
    @JsonIgnore
    private Collection<Ratings> ratingsCollection;
    
    @OneToMany
    @JoinTable(
        name = "user_favorites",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    @JsonIgnore
    private Set<Movies> favoriteMovies;

    @Transient
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("mozixx-1.0-SNAPSHOT");
    
    @Transient
    private boolean isAdmin;
    
    public Users() {
    }

    public Users(Integer userId) {
        EntityManager em = emf.createEntityManager();
        try {
            Users u = em.find(Users.class, userId);
            if (u != null) {
                this.userId = u.getUserId();
                this.email = u.getEmail();
                this.username = u.getUsername();
                this.role = u.getRole();
                this.registrationDate = u.getRegistrationDate();
            }
        } finally {
            em.close();
        }
    }

    public Users(Integer userId, String email, String username, String password, Role role, Date registrationDate) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.registrationDate = registrationDate;
    }
    
    public Users(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = Role.user;
        this.registrationDate = new Date();
    }

    // Getters and setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        return !((this.userId == null && other.userId != null) || 
                (this.userId != null && !this.userId.equals(other.userId)));
    }

    @Override
    public String toString() {
        return "Users{" +
               "userId=" + userId + 
               ", username='" + username + '\'' +
               ", role=" + role +
               '}';
    }

    public Users loginUser(String email, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            StoredProcedureQuery spq = em.createStoredProcedureQuery("login");
            spq.registerStoredProcedureParameter("emailIN", String.class, ParameterMode.IN);
            spq.registerStoredProcedureParameter("passwordIN", String.class, ParameterMode.IN);
            spq.setParameter("emailIN", email);
            spq.setParameter("passwordIN", password);
            spq.execute();

            List<Object[]> resultList = spq.getResultList();
            if (resultList.isEmpty()) {
                return null;
            }

            Object[] o = resultList.get(0);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return new Users(
                Integer.valueOf(o[0].toString()),
                o[1].toString(),
                o[2].toString(),
                o[3].toString(),
                Role.valueOf(o[4].toString()),
                (o.length > 5 && o[5] != null) ? formatter.parse(o[5].toString()) : null
            );

        } catch (NumberFormatException | ParseException e) {
            System.err.println("Hiba: " + e.getLocalizedMessage());
            return null;
        } finally {
            em.close();
        }
    }

    // Other methods
    public Integer getId() {
        return userId;
    }

    public boolean isAdmin() {
        return this.role == Role.admin;
    }

    public Date getRegistration_date() {
        return registrationDate;
    }
}

