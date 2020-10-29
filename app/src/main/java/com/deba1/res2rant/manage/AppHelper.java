package com.deba1.res2rant.manage;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppHelper {

    public static boolean validateRegex(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

}
