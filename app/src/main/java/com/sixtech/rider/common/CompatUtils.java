package com.sixtech.rider.common;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.TypefaceSpan;

import com.sixtech.rider.R;

public class CompatUtils {

    public static Typeface getTypeFace (Context context, int resId) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return context.getResources().getFont(resId);
        }
        return Typeface.DEFAULT;
    }

    public static TypefaceSpan getTypefaceSpan(Typeface tf) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            return new TypefaceSpan(tf);
        } else {
            // for below pie devices
            return new CustomTypefaceSpan("", tf);
        }
    }
}
