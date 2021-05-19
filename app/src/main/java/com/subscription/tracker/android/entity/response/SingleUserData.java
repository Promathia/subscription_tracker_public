package com.subscription.tracker.android.entity.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class SingleUserData implements Serializable {

    private int id;
    private String externalId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Date birthDate;
    private RegistrationType registrationType;
    private boolean isFilled;
    private List<UserClub> userClubs;

    public SingleUserData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public RegistrationType getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(RegistrationType registrationType) {
        this.registrationType = registrationType;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    public List<UserClub> getUserClubs() {
        return userClubs;
    }

    public void setUserClubs(List<UserClub> userClubs) {
        this.userClubs = userClubs;
    }
}
