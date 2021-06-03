package by.vorivoda.matvey.model.dao.entity;

import javax.persistence.*;

@Entity
@Table(name = "USERS_INFO")
public class UserInfo implements DAO {

    @Id
    @Column(name = "ID")
    @GeneratedValue
    private Long id;

    @Column(name = "NAME")
    private String name;
    @Column(name = "SURNAME")
    private String surname;
    @Column(name = "PHONE")
    private String phoneNumber;

    public UserInfo() {
        id = 0L;
        name = surname = phoneNumber = "";
    }

    public UserInfo(String name, String surname) {
        this();
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public UserInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurname() {
        return surname;
    }

    public UserInfo setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserInfo setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
