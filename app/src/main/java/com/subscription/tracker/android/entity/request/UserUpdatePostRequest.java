package com.subscription.tracker.android.entity.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdatePostRequest implements Serializable {

    private String initiatorId;
    private String userName;
    private String userSurname;
    private String userEmail;
    private String userPhone;
    private String userBirthdate;
    private String userRole;

    public String getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(String initiatorId) {
        this.initiatorId = initiatorId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserBirthdate() {
        return userBirthdate;
    }

    public void setUserBirthdate(String userBirthdate) {
        this.userBirthdate = userBirthdate;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return (userRole == null || Integer.parseInt(userRole) == 0)
                && (userName == null || userName.isEmpty())
                && (userSurname == null || userSurname.isEmpty())
                && (userEmail == null || userEmail.isEmpty())
                && (userPhone == null || userPhone.isEmpty())
                && (userBirthdate == null || userBirthdate.isEmpty());
    }
}
