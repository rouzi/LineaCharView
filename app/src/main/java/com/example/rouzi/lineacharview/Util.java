package com.example.rouzi.lineacharview;

import android.content.Context;
import android.view.View;

import java.text.DecimalFormat;

/**
 * Created by rouzi on 2017/6/20.
 *
 */

public class Util {

    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static String formatMoney(float money){
        return decimalFormat.format(money);
    }
}
