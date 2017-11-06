package com.example.rouzi.lineacharview.KLineChart;

/**
 * Created by rouzi on 2017/10/30.
 *
 */

public class KData {

    private String date;       //日期
    private float maxValue;    //最高价
    private float minValue;    //最低价
    private float openValue;   //开盘价
    private float closeValue;  //收盘价

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getMinValue() {
        return minValue;
    }

    public float getOpenValue() {
        return openValue;
    }

    public void setOpenValue(float openValue) {
        this.openValue = openValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getCloseValue() {
        return closeValue;
    }

    public void setCloseValue(float closeValue) {
        this.closeValue = closeValue;
    }
}
