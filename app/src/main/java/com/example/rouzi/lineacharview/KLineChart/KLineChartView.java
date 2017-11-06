package com.example.rouzi.lineacharview.KLineChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.rouzi.lineacharview.Util;

import java.util.List;

/**
 * Created by rouzi on 2017/10/30.
 * k线图
 */

public class KLineChartView extends View {

    private int left, top, right, bottom; //k线图区域
    private int bgTop, bgBottom;  //边框区域

    private Context context;

    private Paint mPaint;
    private Paint linePaint;
    private Paint kPaint;
    private Paint textPaint;

    private Path linePath;

    private List<KData> kDataList;
    private float maxValue;
    private float minValue;

    private Paint indicatePaint;
    private Paint indicateRectPaint;
    private Paint indicateValuePaint;

    private boolean isTouch = false;
    private float xTouch;
    private float yTouch;


    public KLineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.LTGRAY);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);
        linePaint.setPathEffect(new DashPathEffect(new float[]{8f,2f}, 1f));

        linePath = new Path();

        kPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        kPaint.setStrokeWidth(3);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStrokeWidth(2);
        textPaint.setTextSize(Util.sp2px(context,10));
        textPaint.setColor(Color.LTGRAY);
        textPaint.setStyle(Paint.Style.FILL);

        indicatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatePaint.setColor(Color.DKGRAY);
        indicatePaint.setStrokeWidth(2);
        indicatePaint.setStyle(Paint.Style.STROKE);

        indicateRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicateRectPaint.setColor(Color.DKGRAY);
        indicateRectPaint.setStyle(Paint.Style.FILL);

        indicateValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicateValuePaint.setStyle(Paint.Style.FILL);
        indicateValuePaint.setTextSize(Util.sp2px(context, 10));
        indicateValuePaint.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if(widthMode == MeasureSpec.EXACTLY){
            width = widthSize;
        }else if(widthMode == MeasureSpec.AT_MOST){
            width = Math.min(500,widthSize);
        }else{
            width = 500;
        }

        int height;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }else if(heightMode == MeasureSpec.AT_MOST){
            height = Math.min(400, heightSize);
        }else{
            height = 300;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int marginV = Util.dip2px(context, 5);
        int marginH = Util.dip2px(context, 10);
        left = marginV;
        top = marginH * 2;
        right = w - marginV;
        bottom = h - marginH * 3;
        bgTop = marginH;
        bgBottom = h - marginH * 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(left, bgTop, right, bgBottom, mPaint);

        float specH = (bottom - top)/(maxValue - minValue);
        float specW = (right - left)/kDataList.size();

        if(kDataList != null && kDataList.size() > 0){
            drawBackground(canvas, specH, specW);
            drawKlineChart(canvas, specH, specW);

            if(isTouch){
                drawIndicator(canvas, specH, specW);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xTouch = event.getX();
        yTouch = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                break;
        }
        invalidate();
        return true;
    }

    //画手指触摸时显示的指示线和数据
    private void drawIndicator(Canvas canvas, float sh, float sw){
        float xTouchMin = left + sw/2;
        float xTouchMax = left + sw/2 + sw * kDataList.size();
        xTouch = xTouch < xTouchMin ? xTouchMin : xTouch;
        xTouch = xTouch > xTouchMax ? xTouchMax : xTouch;

        int position = (int)((xTouch - left)/sw);
        float x = left + sw * position + sw/2;
        canvas.drawLine(x, bgTop, x, bgBottom, indicatePaint);
        canvas.drawLine(left, yTouch, right, yTouch, indicatePaint);
    }

    //画网格线
    private void drawBackground(Canvas canvas, float sh, float sw){
        linePath.reset();

        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textHeight = Math.abs(fm.ascent);
        float specY = (maxValue - minValue) / 4;
        for(int i=0;i<5;i++){
            float y = bottom - specY * i * sh;
            String valueY = Util.formatMoney(minValue + specY * i);

            if(i == 0){
                canvas.drawText(valueY, left + 2, y - 4, textPaint);
            }

            if(i == 4){
                canvas.drawText(valueY, left + 2, y + textHeight, textPaint);
            }

            linePath.moveTo(left, y);
            linePath.lineTo(right, y);
            canvas.drawPath(linePath, linePaint);
            linePath.reset();
        }

        int specX = kDataList.size()/3;
        for(int i=1;i<=2;i++){
            float x = left + sw * specX * i + sw/2;

            String date = kDataList.get(specX * i).getDate();
            float dateWidth = textPaint.measureText(date);
            canvas.drawText(date,x - dateWidth/2, bgBottom + textHeight, textPaint);

            linePath.moveTo(x, bgBottom);
            linePath.lineTo(x, bgTop);
            canvas.drawPath(linePath, linePaint);
            linePath.reset();
        }
    }

    //画k线图
    private void drawKlineChart(Canvas canvas, float sh, float sw){
        for(int i=0;i<kDataList.size();i++){
            KData data = kDataList.get(i);
            float x = left + sw * i + sw/2;
            float maxY = bottom - (data.getMaxValue() - minValue)*sh;
            float minY = bottom - (data.getMinValue() - minValue)*sh;
            float openY = bottom - (data.getOpenValue() - minValue)*sh;
            float closeY = bottom - (data.getCloseValue() - minValue)*sh;

            float left = x - sw/2 + 4;
            float right = x + sw/2 - 4;
            float top;
            float bottom;

            if(closeY <= openY){
                kPaint.setColor(Color.RED);
                kPaint.setStyle(Paint.Style.STROKE);
                top = closeY;
                bottom = openY;

                canvas.drawLine(x, maxY, x, closeY, kPaint);
                canvas.drawLine(x, minY, x, openY, kPaint);
            }else{
                kPaint.setColor(Color.GREEN);
                kPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                top = openY;
                bottom = closeY;

                canvas.drawLine(x, maxY, x, openY, kPaint);
                canvas.drawLine(x, minY, x, closeY, kPaint);
            }

            canvas.drawRect(left, top, right, bottom, kPaint);
        }
    }

    public void setkDataList(List<KData> kDataList) {
        if(kDataList != null && kDataList.size() > 0){
            this.kDataList = kDataList;
            setMaxAndMinValue();
            invalidate();
        }
    }

    private void setMaxAndMinValue(){
        KData maxData = kDataList.get(0);
        KData minData = kDataList.get(0);
        for(int i=1;i<kDataList.size();i++){
            KData data = kDataList.get(i);
            maxData = data.getMaxValue() >= maxData.getMaxValue() ? data : maxData;
            minData = data.getMinValue() <= minData.getMinValue() ? data : minData;
        }
        maxValue = maxData.getMaxValue();
        minValue = minData.getMinValue();
    }

}
