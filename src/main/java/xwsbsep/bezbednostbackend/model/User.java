package xwsbsep.bezbednostbackend.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="USERS")
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Column(unique = true)
    private String username;

    private String password;

    private UserRole userRole;

    private String country;

    private String organization;

    public User() {
    }

    public User(String name, String username, String password, UserRole userRole, String country, String organization) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.country = country;
        this.organization = organization;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
