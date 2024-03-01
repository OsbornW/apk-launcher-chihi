package com.soya.launcher.utils;

import android.content.Context;
import android.text.TextUtils;

import com.soya.launcher.R;
import com.soya.launcher.enums.Types;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StringUtils {
    public static String firstCharToUpperCase(String text){
        if (TextUtils.isEmpty(text)) return text;
        String[] array = StringUtils.toArrays(text);
        array[0] = array[0].toUpperCase();
        StringBuilder sb = new StringBuilder(array.length);
        for (String item : array){
            sb.append(item);
        }
        return sb.toString();
    }

    public static boolean isEmpty(String...strings){
        for (String string : strings){
            if (TextUtils.isEmpty(string)){
                return true;
            }
        }

        return false;
    }

    public static String[] toArrays(CharSequence text){
        int[] codePoints = text.codePoints().toArray();
        String[] words = new String[codePoints.length];
        for (int i = 0; i < codePoints.length; i++){
            int code = codePoints[i];
            words[i] = new String(Character.toChars(code));
        }
        return words;
    }

    public static String numberToText(float number){
        if (number < 1000){
            return String.valueOf(number);
        }else {
            float value = number / 10000f;
            if (value < 1){
                return round(value * 10f, 2)+"千";
            }else {
                return round(value, 2)+"万";
            }
        }
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String getMoviceCollectionTitle(Context context, int type){
        String title = "";
        switch (type){
            case Types.TYPE_NETFLIX:
                title = context.getString(R.string.netflix);
                break;
            case Types.TYPE_YOUTUBE:
                title = context.getString(R.string.youtube);
                break;
            case Types.TYPE_DISNEY:
                title = context.getString(R.string.disney);
                break;
            case Types.TYPE_MAX:
                title = context.getString(R.string.max);
                break;
            case Types.TYPE_HULU:
                title = context.getString(R.string.hulu);
                break;
            case Types.TYPE_PRIME_VIDEO:
                title = context.getString(R.string.prime_video);
                break;
        }
        return title;
    }

    public static String dayToWeek(Context context, int type){
        String name = context.getString(R.string.sunday);
        switch (type){
            case 1:
                name = context.getString(R.string.monday);
                break;
            case 2:
                name = context.getString(R.string.tuesday);
                break;
            case 3:
                name = context.getString(R.string.wednesday);
                break;
            case 4:
                name = context.getString(R.string.thursday);
                break;
            case 5:
                name = context.getString(R.string.friday);
                break;
            case 6:
                name = context.getString(R.string.saturday);
                break;
            case 7:
                name = context.getString(R.string.sunday);
                break;
        }
        return name;
    }
}
