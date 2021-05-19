package com.subscription.tracker.android.entity.response;

import java.io.Serializable;

public class Subscription implements Serializable {

    private Integer id;
    private Integer visitsLimit;
    private Integer termDays;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVisitsLimit() {
        return visitsLimit;
    }

    public void setVisitsLimit(Integer visitsLimit) {
        this.visitsLimit = visitsLimit;
    }

    public Integer getTermDays() {
        return termDays;
    }

    public void setTermDays(Integer termDays) {
        this.termDays = termDays;
    }
}
