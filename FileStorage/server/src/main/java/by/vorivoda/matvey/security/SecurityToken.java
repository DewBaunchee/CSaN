package by.vorivoda.matvey.security;

import by.vorivoda.matvey.model.dao.entity.User;
import by.vorivoda.matvey.model.dao.entity.DAO;
import by.vorivoda.matvey.model.GlobalConstants;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "TOKENS")
public class SecurityToken implements DAO {

    @Id
    @Column(name = "ID")
    @GeneratedValue
    private Long id;
    @Column(name = "TOKEN")
    private String value;
    @Column(name = "EXPIRED_DATE")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date expiredDate;

    @OneToOne(optional = false, mappedBy = "token")
    private User user;

    public SecurityToken() {}

    public SecurityToken(String value) {
        this.value = value;
        refreshToken();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String token) {
        this.value = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void refreshToken() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, GlobalConstants.TOKEN_LIFETIME_IN_MINUTES);
        expiredDate = calendar.getTime();
    }

    public boolean isExpired() {
        return new Date().getTime() > expiredDate.getTime();
    }
}
