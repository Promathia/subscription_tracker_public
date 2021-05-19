package com.subscription.tracker.android.services.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class ImageUtils {

    private ImageUtils() {}

    public static Drawable resolveImageResource(final Context context,
                                                final String imageName) {
        final String uri = String.format("@drawable/%s", imageName);
        final int imageResource = context
                .getResources()
                .getIdentifier(uri, null, context.getPackageName());
        return context.getResources().getDrawable(imageResource);
    }

}
