package com.subscription.tracker.android.services;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

public class EncryptionService {

    private final static String ENCODER_SALT_START = "{android}";
    private final static String ENCODER_SALT_END = "{android}";

    public String encryptString(final String data) {
        final String salted = String.format("%s%s%s", ENCODER_SALT_START, data, ENCODER_SALT_END);
        return android.util.Base64.encodeToString(salted.getBytes(), Base64.DEFAULT);
    }

    public String decryptString(final String data) {
        final String decode = new String((Base64.decode(data.getBytes(), Base64.DEFAULT)), StandardCharsets.UTF_8);
        return decode.replace(ENCODER_SALT_START, "").replace(ENCODER_SALT_END, "");
    }

}
