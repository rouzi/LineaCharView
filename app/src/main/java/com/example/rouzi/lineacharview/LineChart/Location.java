package com.example.rouzi.lineacharview.LineChart;

/**
 * Created by rouzi on 2017/8/29.
 */

public class Location {

    public float x;

    public float y;

    public boolean isPeak = false;

    public Location(){

    }

    public Location(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isPeak() {
        return isPeak;
    }

    public void setPeak(boolean peak) {
        isPeak = peak;
    }
}
