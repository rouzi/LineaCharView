package com.example.rouzi.lineacharview.CircleWave;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.rouzi.lineacharview.R;

/**
 * Created by rouzi on 2017/9/26.
 * 能量球
 */

public class CircleWaveView extends View {

    private int width;
    private int height;

    private float circleRadius;
    private float circleDiameter;
    private float circleX;
    private float circleY;

    private float waveHeightPercent = 0.7f;
    private float waveTranslateValue = 0f;
    private float amplitudePercent = 1f;
    private float waveAmplitude;

    private Path wavePathFront;
    private Path wavePathBehind;
    private Paint wavePaint;

    private Paint circlePaint;
    private Paint bgPaint;

    private Xfermode xfermode;
    private Bitmap circleBitmap;
    private Context context;

    private int waveCount = 4;

    private AnimatorSet animatorSet;

    public CircleWaveView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CircleWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init(){
        wavePathFront = new Path();
        wavePathBehind = new Path();

        wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setStyle(Paint.Style.FILL);

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(Color.WHITE);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.WHITE);

        animatorSet = new AnimatorSet();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int minSize = Math.min(widthSize, heightSize);
        setMeasuredDimension(minSize, minSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        circleDiameter = Math.min(w, h);
        circleRadius = circleDiameter/2;
        circleX = w/2;
        circleY = h/2;

        width = w;
        height = h;
        waveAmplitude = circleDiameter/10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float levelHeight = (1 - waveHeightPercent) * height;
        float specWidth = width/waveCount;
        float translateX = width * waveTranslateValue;

        canvas.drawCircle(circleX, circleY, circleRadius, bgPaint);

        int saveCount = canvas.saveLayer(0, 0, width, height, wavePaint, Canvas.ALL_SAVE_FLAG);
        setFrontWavePath(levelHeight, specWidth, translateX);
        setBehindWavePath(levelHeight, specWidth, translateX);

        wavePaint.setColor(ContextCompat.getColor(context, R.color.behind_color));
        canvas.drawPath(wavePathBehind, wavePaint);

        wavePaint.setColor(ContextCompat.getColor(context, R.color.front_color));
        canvas.drawPath(wavePathFront, wavePaint);

        circlePaint.setXfermode(xfermode);
        if(circleBitmap == null){
            circleBitmap = createCircleBitmap();
        }
        canvas.drawBitmap(circleBitmap,circleX - circleRadius, 0, circlePaint);
        circlePaint.setXfermode(null);
        canvas.restoreToCount(saveCount);
    }

    private Bitmap createCircleBitmap(){
        Bitmap bitmap = Bitmap.createBitmap((int)circleDiameter, (int)circleDiameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(circleRadius, circleRadius,circleRadius, paint);
        return bitmap;
    }

    private void setFrontWavePath(float levelHeight, float specWidth, float translateX){
        wavePathFront.reset();

        wavePathFront.moveTo(0 - translateX, height);
        wavePathFront.lineTo(0 - translateX, levelHeight);
        for(int i=1;i<=waveCount;i++){
            float controlX = specWidth * (i*2 - 1) - translateX;
            float controlY = i%2 != 0 ? levelHeight - waveAmplitude * amplitudePercent : levelHeight + waveAmplitude * amplitudePercent;
            float toX = specWidth * (2 * i) - translateX;
            wavePathFront.quadTo(controlX, controlY, toX, levelHeight);
        }
        wavePathFront.lineTo(specWidth * waveCount + translateX, height);
        wavePathFront.close();
    }

    private void setBehindWavePath(float levelHeight, float specWidth, float translateX){
        wavePathBehind.reset();

        wavePathBehind.moveTo(specWidth * waveCount + translateX, height);
        wavePathBehind.lineTo(specWidth * waveCount + translateX, levelHeight);
        for(int i=1;i<=waveCount;i++){
            float controlX = (waveCount - (2 * i - 1)) * specWidth + translateX;
            float controlY = i%2 != 0 ? levelHeight + waveAmplitude * amplitudePercent * 0.7f : levelHeight - waveAmplitude * amplitudePercent * 0.7f;
            float toX = (waveCount - 2 * i) * specWidth + translateX;
            wavePathBehind.quadTo(controlX, controlY, toX, levelHeight);
        }
        wavePathBehind.lineTo(translateX - specWidth * waveCount, height);
        wavePathBehind.close();
    }

    public void waveAnimate(){
        ObjectAnimator transAnim = ObjectAnimator.ofFloat(this, "waveTranslateValue", 0, 1);
        transAnim.setInterpolator(new LinearInterpolator());
        transAnim.setDuration(1000);
        transAnim.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator upAnim = ObjectAnimator.ofFloat(this, "waveHeightPercent", 0, waveHeightPercent);
        upAnim.setDuration(3000);

        animatorSet.playTogether(transAnim, upAnim);
        animatorSet.start();
    }

    public float getWaveTranslateValue() {
        return waveTranslateValue;
    }

    public void setWaveTranslateValue(float waveTranslateValue) {
        this.waveTranslateValue = waveTranslateValue;
        invalidate();
    }

    public float getAmplitudePercent() {
        return amplitudePercent;
    }

    public void setAmplitudePercent(float amplitudePercent) {
        this.amplitudePercent = amplitudePercent;
    }

    public float getWaveHeightPercent() {
        return waveHeightPercent;
    }

    public void setWaveHeightPercent(float waveHeightPercent) {
        this.waveHeightPercent = waveHeightPercent;
        invalidate();
    }
}
