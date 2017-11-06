package com.example.rouzi.lineacharview.CircleWave;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.example.rouzi.lineacharview.R;

/**
 * Created by rouzi on 2017/10/16.
 *
 */

public class CircleWaveViewShader extends View {
    private int width;
    private int height;

    private float circleRadius;
    private float circleX;
    private float circleY;

    private float waveHeightPercentDefault = 0.5f;
    private float waveTranslateValue = 0f;
    private float waveHeightPercent = 0f;
    private float amplitudePercent = 1f;
    private float waveAmplitude;

    private Paint mPaint;
    private Paint bgPaint;

    private Path wavePathFront;
    private Path wavePathBehind;

    private BitmapShader bitmapShader;
    private Matrix shaderMatrix;

    private int waveCount = 4;
    private Context context;

    public CircleWaveViewShader(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CircleWaveViewShader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.WHITE);

        wavePathFront = new Path();
        wavePathBehind = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, height;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if(widthMode == MeasureSpec.EXACTLY){
            width = widthSize;
        }else if(widthMode == MeasureSpec.AT_MOST){
            width = Math.min(300,widthSize);
        }else{
            width = 300;
        }

        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }else if(heightMode == MeasureSpec.AT_MOST){
            height = Math.min(300,heightSize);
        }else{
            height = 300;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int circleDiameter = Math.min(w, h);
        circleRadius = circleDiameter/2;
        circleX = w/2;
        circleY = h/2;

        width = w;
        height = h;
        waveAmplitude = circleDiameter/10;

        createShader();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(circleX, circleY, circleRadius, bgPaint);
        canvas.drawCircle(circleX, circleY, circleRadius, mPaint);
    }

    private void createShader(){
        float levelHeight = (1 - waveHeightPercentDefault) * height;
        float specWidth = width/waveCount;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        setFrontWavePath(levelHeight, specWidth);
        setBehindWavePath(levelHeight, specWidth);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(ContextCompat.getColor(context, R.color.behind_color));
        canvas.drawPath(wavePathBehind, paint);

        paint.setColor(ContextCompat.getColor(context, R.color.front_color));
        canvas.drawPath(wavePathFront, paint);

        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(bitmapShader);
    }

    private void setFrontWavePath(float levelHeight, float specWidth){
        wavePathFront.reset();

        wavePathFront.moveTo(0, height);
        wavePathFront.lineTo(0, levelHeight);
        for(int i=1;i<=waveCount;i++){
            float controlX = specWidth * (i*2 - 1);
            float controlY = i%2 != 0 ? levelHeight - waveAmplitude * amplitudePercent : levelHeight + waveAmplitude * amplitudePercent;
            float toX = specWidth * (2 * i);
            wavePathFront.quadTo(controlX, controlY, toX, levelHeight);
        }
        wavePathFront.lineTo(specWidth * waveCount, height);
        wavePathFront.close();
    }

    private void setBehindWavePath(float levelHeight, float specWidth){
        wavePathBehind.reset();

        wavePathBehind.moveTo(specWidth * waveCount, height);
        wavePathBehind.lineTo(specWidth * waveCount, levelHeight);
        for(int i=1;i<=waveCount;i++){
            float controlX = (waveCount - (2 * i - 1)) * specWidth;
            float controlY = i%2 != 0 ? levelHeight + waveAmplitude * amplitudePercent * 0.7f : levelHeight - waveAmplitude * amplitudePercent * 0.7f;
            float toX = (waveCount - 2 * i) * specWidth;
            wavePathBehind.quadTo(controlX, controlY, toX, levelHeight);
        }
        wavePathBehind.lineTo(0 - specWidth * waveCount, height);
        wavePathBehind.close();
    }

    public float getWaveTranslateValue() {
        return waveTranslateValue;
    }

    public void setWaveTranslateValue(float waveTranslateValue) {
        this.waveTranslateValue = waveTranslateValue;
    }
}
