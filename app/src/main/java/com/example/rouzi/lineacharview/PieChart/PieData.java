package com.example.rouzi.lineacharview.PieChart;

/**
 * Created by rouzi on 2017/10/19.
 *
 */

public class PieData {

    private float income;

    private int color;

    private boolean isOut = false;

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isOut() {
        return isOut;
    }

    public void setOut(boolean out) {
        isOut = out;
    }
}
