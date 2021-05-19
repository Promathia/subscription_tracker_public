package com.subscription.tracker.android.entity.response;

import java.io.Serializable;

public class RegistrationType implements Serializable {

    private int id;
    private String registrationTypeName;

    public RegistrationType() {
    }

    public RegistrationType(int id, String registrationTypeName) {
        this.id = id;
        this.registrationTypeName = registrationTypeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegistrationTypeName() {
        return registrationTypeName;
    }

    public void setRegistrationTypeName(String registrationTypeName) {
        this.registrationTypeName = registrationTypeName;
    }

    public enum Type {
        GOOGLE {
            @Override
            public RegistrationType getType() {
                return new RegistrationType(1, "GOOGLE");
            }
        },
        PHONE {
            @Override
            public RegistrationType getType() {
                return new RegistrationType(2, "PHONE");
            }
        };
        public abstract RegistrationType getType();
    }

}
