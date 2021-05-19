package com.subscription.tracker.android;

import java.text.SimpleDateFormat;

public abstract class Constants {

    public static final int GOOGLE_SIGN_IN_ACTIVITY_CODE = 1;
    public static final String MOBILE_PARAM = "mobile";
    public static final String USER_DTO_EXTRA_PARAM = "USER_DTO";

    public static final String API_URI = "http://ec2-3-122-245-124.eu-central-1.compute.amazonaws.com:8080/api/v1";

    public final static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");

    public static final int VISITOR_CLOSE_DEADLINE_DAY_VALUE = 3;
}
