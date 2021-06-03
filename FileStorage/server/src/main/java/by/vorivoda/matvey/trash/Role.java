/*
package by.vorivoda.matvey.security;

import by.vorivoda.matvey.entities.User;
import by.vorivoda.matvey.entities.DAO;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "ROLES")
public class Role implements DAO, GrantedAuthority {

    public static final Role USER = new Role(1, "ROLE_USER");

    @Id
    @Column(name = "ID")
    @GeneratedValue
    private Integer id;

    private String name;
    @Transient
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role() {

    }

    public Role(Integer id) {
        setId(id);
    }

    public Role(Integer id, String name) {
        setId(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public String getAuthority() {
        return getName();
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
*/
