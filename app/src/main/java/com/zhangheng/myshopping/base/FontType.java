package com.zhangheng.myshopping.base;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;


public class FontType {
    private static HashMap<String, Typeface> storage;
    public static Typeface getFont(Context context, String fullName) {
        if (storage == null) {
            storage = new HashMap<String, Typeface>();
        }
        synchronized (storage) {
            Typeface font = storage.get(fullName);
            if (font == null) {
                font = Typeface.createFromAsset(context.getAssets(), fullName);
                if (font != null) {
                    storage.put(fullName, font);
                    return font;
                }
            } else {
                return font;
            }
        }
        return Typeface.DEFAULT;
    }
}
