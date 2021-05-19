package com.subscription.tracker.android.entity.response;

import java.io.Serializable;
import java.util.Date;

public class UserSubscription implements Serializable {

    private Integer id;
    private Integer userId;
    private Integer visitsLimit;
    private Integer visitCounter;
    private Date deadline;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getVisitsLimit() {
        return visitsLimit;
    }

    public void setVisitsLimit(Integer visitsLimit) {
        this.visitsLimit = visitsLimit;
    }

    public Integer getVisitCounter() {
        return visitCounter;
    }

    public void setVisitCounter(Integer visitCounter) {
        this.visitCounter = visitCounter;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}
