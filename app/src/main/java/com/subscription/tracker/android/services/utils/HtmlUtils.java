package com.subscription.tracker.android.services.utils;

import android.text.Html;
import android.text.Spanned;

public class HtmlUtils {

    private HtmlUtils() {}

    public static Spanned getSpannedFromHtmlWithAPI(final String message) {
        int apiLevel = android.os.Build.VERSION.SDK_INT;
        if (apiLevel >= 24) {
            return Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(message);
        }
    }

}
