package by.vorivoda.matvey.model.dao.entity;

import by.vorivoda.matvey.security.SecurityToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="USERS")
public class User implements DAO, UserDetails {

    @Id
    @Column(name = "ID")
    @GeneratedValue
    private Long id;

    @Column(name = "USERNAME")
    private String username;
    @Column(name = "ALTERNATIVE_USERNAME")
    private String alternativeUsername;
    @Column(name = "PASSWORD")
    private String password;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_INFO_ID")
    private UserInfo userInfo;
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "STORAGE_INFO_ID")
    private StorageInfo storageInfo;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "TOKEN_ID")
    private SecurityToken token;

    public User() {
        id = 0L;
        username = alternativeUsername = password = "";
        setUserInfo(new UserInfo());
        setStorageInfo(new StorageInfo());
    }

    public User(String username, String password) {
        this();
        setUsername(username);
        setPassword(password);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return new HashSet<>();
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public StorageInfo getStorageInfo() {
        return storageInfo;
    }

    public void setStorageInfo(StorageInfo storageInfo) {
        this.storageInfo = storageInfo;
    }

    public String getAlternativeUsername() {
        return alternativeUsername;
    }

    public void setAlternativeUsername(String alternativeUsername) {
        this.alternativeUsername = alternativeUsername;
    }

    public SecurityToken getToken() {
        return token;
    }

    public void setToken(SecurityToken token) {
        token.setUser(this);
        this.token = token;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Username: " + username + ", Password: " + password;
    }
}
