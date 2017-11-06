package com.example.rouzi.lineacharview.PieChart;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rouzi on 2017/10/19.
 * 饼状图
 */

public class PieChartView extends View {
    private float total  = 0;
    private float swapAngle = 0;

    private float radius;
    private float radiusInside = 0;

    private float cx;
    private float cy;

    private RectF rect1;
    private RectF rect2;
    private RectF rect3;
    private RectF rect2New;

    private Paint mPaint;
    private Paint centerPaint;
    private Paint animPaint;
    private Paint wPaint;

    private Path arcPath;

    private Region mRegion;
    private int clickRegion = -1;
    private List<Region> regions;
    private List<PieData> pieDataList;

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setColor(Color.WHITE);

        wPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wPaint.setColor(Color.WHITE);
        wPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        wPaint.setStrokeWidth(2);

        animPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        animPaint.setStyle(Paint.Style.FILL);
        animPaint.setColor(Color.WHITE);

        arcPath = new Path();
        regions = new ArrayList<>();
        rect2New = new RectF();
        rect3 = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int minSize = Math.min(width, height);
        setMeasuredDimension(minSize, minSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = w/2;
        cy = h/2;
        rect1 = new RectF(19, 19, w-19, h-19);
        rect2 = new RectF(20, 20, w-20, h-20);
        mRegion = new Region(20, 20, w-20, h-20);
        radius = (w - 40)/2;
        radiusInside = radius/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(pieDataList != null && pieDataList.size() > 0){
            float startAngle = 0;
            for(int i=0;i<pieDataList.size();i++){
                PieData pieData = pieDataList.get(i);
                float swapAngle;
                if(i == pieDataList.size() - 1){
                    swapAngle = 360 - startAngle;
                }else{
                    swapAngle = (pieData.getIncome()/total) * 360;
                }
                mPaint.setColor(pieData.getColor());

                if(pieData.isOut()){
                    float middleAngle = startAngle + swapAngle/2;
                    float newCX = cx + (float) Math.sin(Math.toRadians(middleAngle)) * 20;
                    float newCY = cy - (float) Math.cos(Math.toRadians(middleAngle)) * 20;
                    rect2New.left = newCX - radius;
                    rect2New.top = newCY - radius;
                    rect2New.right = newCX + radius;
                    rect2New.bottom = newCY + radius;

                    arcPath.moveTo(newCX, newCY);
                    arcPath.lineTo(newCX + (float) Math.sin(Math.toRadians(startAngle)) * radius, newCY - (float) Math.cos(Math.toRadians(startAngle)) * radius);
                    arcPath.addArc(rect2New, startAngle - 90,swapAngle);
                    arcPath.lineTo(newCX, newCY);
                    canvas.drawPath(arcPath, mPaint);

                    if(!isSolid){
                        rect3.left = newCX - radiusInside;
                        rect3.top = newCY - radiusInside;
                        rect3.right = newCX + radiusInside;
                        rect3.bottom = newCY + radiusInside;
                        canvas.drawArc(rect3, startAngle - 90, swapAngle, true, wPaint);
                    }
                }else{
                    arcPath.moveTo(cx, cy);
                    arcPath.lineTo(cx + (float) Math.sin(Math.toRadians(startAngle)) * radius, cy - (float) Math.cos(Math.toRadians(startAngle)) * radius);
                    arcPath.addArc(rect2, startAngle - 90, swapAngle);
                    arcPath.lineTo(cx, cy);
                    canvas.drawPath(arcPath, mPaint);
                }

                if(!isSolid){
                    canvas.drawCircle(cx, cy, radiusInside, centerPaint);
                }

                regions.get(i).setPath(arcPath, mRegion);
                startAngle += swapAngle;
                arcPath.reset();
            }

            canvas.drawArc(rect1, swapAngle - 90, 360 - swapAngle, true, animPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            for (int i=0;i<regions.size();i++){
                Region region = regions.get(i);
                if(region.contains((int)event.getX(), (int)event.getY())){
                    if(clickRegion == i){
                        pieDataList.get(i).setOut(false);
                        clickRegion = -1;
                        invalidate();
                        return true;
                    }

                    if(clickRegion != -1){
                        pieDataList.get(clickRegion).setOut(false);
                    }
                    pieDataList.get(i).setOut(true);
                    clickRegion = i;

                    invalidate();
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void animStart(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "swapAngle", 0, 360);
        animator.setDuration(2000);
        animator.start();
    }

    public float getSwapAngle() {
        return swapAngle;
    }

    public void setSwapAngle(float swapAngle) {
        this.swapAngle = swapAngle;
        invalidate();
    }

    public List<PieData> getPieDataList() {
        return pieDataList;
    }

    public void setPieDataList(List<PieData> pieDataList) {
        total = 0;
        regions.clear();
        this.pieDataList = pieDataList;
        for(PieData data:pieDataList){
            total += data.getIncome();
            Region region = new Region();
            regions.add(region);
        }
        invalidate();
    }

    private boolean isSolid = false;

    public boolean isSolid() {
        return isSolid;
    }

    public void setSolid(boolean solid) {
        isSolid = solid;
        invalidate();
    }
}
