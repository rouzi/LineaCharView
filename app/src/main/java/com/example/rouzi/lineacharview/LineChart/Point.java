package com.example.rouzi.lineacharview.LineChart;

import android.support.annotation.NonNull;

/**
 * Created by rouzi on 2017/6/28.
 *
 */

public class Point  implements Comparable<Point>{

    private float income;

    private String date;

    public Point(){}

    public Point(float income, String date) {
        this.income = income;
        this.date = date;
    }

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(@NonNull Point o) {
        if(income > o.income){
            return 1;
        }
        if(income < o.income){
            return -1;
        }
        return 0;
    }
}
