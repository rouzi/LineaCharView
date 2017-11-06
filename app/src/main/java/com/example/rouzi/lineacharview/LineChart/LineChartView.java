package com.example.rouzi.lineacharview.LineChart;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.rouzi.lineacharview.Util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

/**
 * Created by rouzi on 2017/6/16
 * 折线图.
 */

public class LineChartView extends View {
    private static final int defaultWidth = 500;
    private static final int defaultHeight = 500;

    private int top, left, right, bottom;  //坐标轴上下左右四个点

    private Paint axisPaint;  //坐标轴画笔
    private Paint linePaint;  //折线图画笔
    private Paint scalePaint; //刻度线画笔
    private Paint textPaint;  //文字画笔

    private Paint controlPaint;
    private Paint controlPointPaint;

    private Path axisPath;    //坐标轴线条
    private Path linePath;    //折线图线条
    private Path scalePath;   //刻度线线条

    private Context context;
    private List<Point> points;
    private Point maxPoint;

    private boolean isShowXText = false;   //是否显示X轴文字
    private boolean isShowYText = false;   //是否显示Y轴文字
    private boolean isShowScaleLine = false;  //是否显示x，y轴刻度线

    private float specX = 0f;
    private float specY = 0f;

    private float phaseY = 1f;
    private int xCount = 0;

    private Paint indicatorPaint;  //指示线画笔
    private Paint indicateValuePaint; //绘制x和y值画笔
    private Paint indicateRectPaint;  //绘制x和y值显示区域的画笔

    private boolean isTouch = false;
    private float[] indicates = new float[8];  //横竖指示线的坐标
    private RectF rectY;    //显示y值的矩形区域
    private RectF rectX;    //显示x值的矩形区域
    private Point touchedPoint;   //触摸到的点
    private Location xValueLocation;  //绘制x值的坐标
    private Location yValueLocation;  //绘制y值的坐标

    private boolean isShowControlLine = false;
    private boolean isShowPoint = false;

    public enum Type{
        Normal,Bezier,Corner
    }

    private Type lineType = Type.Normal;

    public LineChartView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init(){
        axisPath = new Path();
        linePath = new Path();
        scalePath = new Path();

        axisPaint = new Paint();
        axisPaint.setColor(Color.BLUE);
        axisPaint.setStrokeWidth(3);
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(3);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

        scalePaint = new Paint();
        scalePaint.setColor(Color.LTGRAY);
        scalePaint.setStrokeWidth(2);
        scalePaint.setAntiAlias(true);
        scalePaint.setStyle(Paint.Style.STROKE);
        scalePaint.setPathEffect(new DashPathEffect(new float[]{8f,2f}, 1f));

        indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint.setColor(Color.RED);
        indicatorPaint.setStrokeWidth(2);
        indicatorPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(1);
        textPaint.setTextSize(Util.sp2px(context,10));

        indicateValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicateValuePaint.setColor(Color.WHITE);
        indicateValuePaint.setStyle(Paint.Style.FILL);
        indicateValuePaint.setStrokeWidth(1);
        indicateValuePaint.setTextSize(Util.sp2px(context,10));

        indicateRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicateRectPaint.setColor(Color.GRAY);
        indicateRectPaint.setStyle(Paint.Style.FILL);

        controlPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        controlPaint.setStyle(Paint.Style.STROKE);
        controlPaint.setColor(Color.RED);
        controlPaint.setStrokeWidth(3);

        controlPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        controlPointPaint.setStyle(Paint.Style.FILL);
        controlPointPaint.setColor(Color.RED);
        controlPointPaint.setStrokeWidth(1);

        rectX = new RectF();
        rectY = new RectF();
        xValueLocation = new Location();
        yValueLocation = new Location();
    }

    public void setPoints(List<Point> points) {
        this.points = points;
        maxPoint = Collections.max(points);
        xCount = points.size();
        invalidate();
    }

    public void setShowXText(boolean showXText) {
        isShowXText = showXText;
        invalidate();
    }

    public void setShowYText(boolean showYText) {
        isShowYText = showYText;
        invalidate();
    }

    public void setShowScaleLine(boolean showScaleLine) {
        isShowScaleLine = showScaleLine;
    }

    public void setShowControlLine(boolean showControlLine) {
        isShowControlLine = showControlLine;
        invalidate();
    }

    public boolean isShowControlLine() {
        return isShowControlLine;
    }

    public boolean isShowPoint() {
        return isShowPoint;
    }

    public void setShowPoint(boolean showPoint) {
        isShowPoint = showPoint;
        invalidate();
    }

    public void setLineType(Type type){
        lineType = type;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        // 获取宽度测量规格中的mode,MeasureSpec类中的三个Mode常量值:UNSPECIFIED,EXACTLY,AT_MOST
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 获取宽度测量规格中的size
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if(widthMode == MeasureSpec.EXACTLY){         //父容器决定控件的大小，忽略其本身的大小，当width或height设为match_parent或者固定大小时，模式为EXACTLY,控件去占据父容器的剩余空间
            width = widthSize;
        }else if(widthMode == MeasureSpec.AT_MOST){   //控件最大可以达到的指定大小，当width或height设置为wrap_content，模式为AT_MOST
            width = Math.min(defaultWidth,widthSize);
        }else{                                       //UNSPECIFIED : 父容器不对控件施加任何约束，控件可以是任意大小
            width = defaultWidth;
        }

        int height;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }else if(heightMode == MeasureSpec.AT_MOST){
            height = Math.min(defaultHeight, heightSize);
        }else{
            height = defaultHeight;
        }

        setMeasuredDimension(width, height);   //设置大小
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        left = Util.dip2px(context, 40);
        top = Util.dip2px(context, 20);
        right = w - Util.dip2px(context, 10);
        bottom = h - Util.dip2px(context, 20);

        if(points != null && points.size() > 0){
            specY = (bottom - top) / maxPoint.getIncome();
            specX = (right - left) / (points.size() - 1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        axisPath.reset();
        axisPath.moveTo(left, top);
        axisPath.lineTo(left, bottom);
        axisPath.lineTo(right, bottom);
        canvas.drawPath(axisPath, axisPaint);

        drawLines(canvas);

        if(isTouch){
            drawIndicateLines(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                setIndicators(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                setIndicators(x, y);
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                break;
        }
        invalidate();
        return true;
    }

    private void drawLines(Canvas canvas){
        if(points != null && points.size() > 0){
            drawScaleLine(canvas);
            if(lineType == Type.Normal){
                drawDataLine(canvas, points);
            }else if(lineType == Type.Bezier){
                drawBezierLine(canvas, points);
            }else{
                drawHBezierLine(canvas, points);
            }
        }
    }

    //画刻度线和文字
    private void drawScaleLine(Canvas canvas){
        scalePath.reset();
        float specX = (right - left) / (points.size() - 1);

        float xDivider = specX * 4;
        float yDivider = (bottom - top) / 5;

        if(isShowScaleLine){
            //x轴刻度线
            for(int x = 1;x <= points.size()/4;x++){
                scalePath.moveTo(left + xDivider * x, bottom);
                scalePath.lineTo(left + xDivider * x, top);
            }
            //y轴刻度线
            for(int y=1;y<=5;y++){
                scalePath.moveTo(left, bottom - yDivider * y);
                scalePath.lineTo(right, bottom - yDivider * y);
            }
            canvas.drawPath(scalePath, scalePaint);
        }

        Paint.FontMetrics fm = textPaint.getFontMetrics();
        if(isShowXText){     //绘制x轴文字
            float h1 = fm.descent - fm.ascent; //x轴文字高度，相对x轴向下偏移的值
            for(int x1 = 0;(x1 * 4) < points.size();x1++){
                String xStr = points.get(x1 * 4).getDate();
                float xw = textPaint.measureText(xStr);  //x轴文字的宽度，为了计算文字宽度的中心点
                canvas.drawText(points.get(x1 * 4).getDate(), left + xDivider * x1 - xw/2,bottom + h1, textPaint);
            }
        }

        if(isShowYText){    //绘制y轴数值
            float h2 = Math.abs(fm.descent + fm.ascent);  //y轴文字相对于文字baseline的高度，为了计算高度的中心点
            float avIncome = maxPoint.getIncome() / 5;
            for(int y1 = 1;y1 <=5 ;y1++){
                String income = new DecimalFormat("0.00").format(avIncome * y1);
                float textWidth = textPaint.measureText(income);  //y轴文字宽度，相对y轴向左偏移的值
                canvas.drawText(income,left - textWidth - 5,bottom - yDivider * y1 + h2/2, textPaint);
            }
        }
    }

    //描点连线
    private void drawDataLine(Canvas canvas, List<Point> pointList){
        linePath.reset();
        for(int i=0;i<xCount;i++){
            Point point = pointList.get(i);
            Location location = getLocation(point, i);

            if(i == 0){
                linePath.moveTo(location.x, location.y);
            }else{
                linePath.lineTo(location.x, location.y);
            }

            if(isShowPoint){
                canvas.drawCircle(location.x, location.y, 5f, textPaint);
            }

        }
        canvas.drawPath(linePath, linePaint);
    }

    private void drawBezierLine(Canvas canvas, List<Point> pointList){
        linePath.reset();
        Path controlPath = new Path();
        float radius = 0.13f;
        for(int i=0;i<xCount;i++){
            Point point = pointList.get(i);
            Location current = getLocation(point, i);
            Location preLast = i >= 2 ? getLocation(pointList.get(i-2), i-2) : current;
            Location last = i >= 1 ? getLocation(pointList.get(i-1), i-1) : current;
            Location next = i == xCount -1 ? current : getLocation(pointList.get(i+1), i+1);

            if(i == 0){
                linePath.moveTo(current.x, current.y);
                controlPath.moveTo(current.x, current.y);
            }else{
                float firstDiffX = current.x - preLast.x;
                float firstDiffY = current.y - preLast.y;
                float secondDiffX = next.x - last.x;
                float secondDiffY = next.y - last.y;

                float firstControlX = last.x + radius * firstDiffX;
                float firstControlY = last.y + radius * firstDiffY;
                float secondControlX = current.x - radius * secondDiffX;
                float secondControlY = current.y - radius * secondDiffY;

                firstControlY = firstControlY < top ? top : firstControlY;
                firstControlY = firstControlY > bottom ? bottom : firstControlY;
                secondControlY = secondControlY < top ? top : secondControlY;
                secondControlY = secondControlY > bottom ? bottom : secondControlY;

                linePath.cubicTo(firstControlX, firstControlY, secondControlX, secondControlY, current.x, current.y);

                if(isShowControlLine){
                    controlPath.lineTo(firstControlX, firstControlY);
                    controlPath.lineTo(secondControlX, secondControlY);
                    canvas.drawCircle(firstControlX, firstControlY, 5, controlPointPaint);
                    canvas.drawCircle(secondControlX, secondControlY, 5, controlPointPaint);
                }
            }

            if(isShowPoint){
                canvas.drawCircle(current.x, current.y, 5f, textPaint);
            }
        }

        canvas.drawPath(linePath, linePaint);

        if(isShowControlLine){
            canvas.drawPath(controlPath, controlPaint);
        }
    }

    private void drawHBezierLine(Canvas canvas, List<Point> pointList){
        linePath.reset();
        Path controlPath = new Path();
        float radius1 = 0.5f;
        float radius2;
        for(int i=0;i<xCount;i++){
            Location current = getLocation(pointList.get(i), i);
            Location preLast = i >= 2 ? getLocation(pointList.get(i-2), i-2) : current;
            Location last = i >= 1 ? getLocation(pointList.get(i-1), i-1) : current;
            Location next = i < xCount - 1 ? getLocation(pointList.get(i+1), i+1) : current;
            if(i == 0){
                linePath.moveTo(current.x, current.y);
                controlPath.moveTo(current.x, current.y);
            }else{
                if(Math.abs(current.y - last.y) <= 8 * specY){
                    radius2 = 0.06f;
                }else{
                    radius2 = 0.12f;
                }

                float firstX;
                float firstY;
                float secondX;
                float secondY;

                float firstDiffX = current.x - preLast.x;
                float firstDiffY = current.y - preLast.y;
                float secondDiffX = next.x - last.x;
                float secondDiffY = next.y - last.y;

                if(last.isPeak() && current.isPeak()){
                    firstX = current.x - radius1 * specX;
                    firstY = last.y;
                    secondX = last.x + radius1 * specX;
                    secondY = current.y;
                }else if(!last.isPeak() && current.isPeak()){
                    firstX = last.x + radius2 * firstDiffX;
                    firstY = last.y + radius2 * firstDiffY;
                    secondX = last.x + radius1 * specX;
                    secondY = current.y;
                }else if(last.isPeak() && !current.isPeak()){
                    firstX = current.x - radius1 * specX;
                    firstY = last.y;
                    secondX = current.x - radius2 * secondDiffX;
                    secondY = current.y - radius2 * secondDiffY;
                }else{
                    firstX = last.x + radius2 * firstDiffX;
                    firstY = last.y + radius2 * firstDiffY;
                    secondX = current.x - radius2 * secondDiffX;
                    secondY = current.y - radius2 * secondDiffY;
                }

                firstY = firstY < top ? top : firstY;
                firstY = firstY > bottom ? bottom : firstY;
                secondY = secondY < top ? top : secondY;
                secondY = secondY > bottom ? bottom : secondY;

                linePath.cubicTo(firstX, firstY, secondX, secondY, current.x, current.y);

                if(isShowControlLine){
                    controlPath.lineTo(firstX, firstY);
                    controlPath.lineTo(secondX, secondY);
                    canvas.drawCircle(firstX, firstY, 5, controlPointPaint);
                    canvas.drawCircle(secondX, secondY, 5, controlPointPaint);
                }
            }

            if(isShowPoint){
                canvas.drawCircle(current.x, current.y, 5f, textPaint);
            }

        }
        canvas.drawPath(linePath, linePaint);

        if(isShowControlLine){
            canvas.drawPath(controlPath, controlPaint);
        }
    }

    private void drawDataLine2(Canvas canvas, List<Point> points){
        for(int i=0;i<xCount;i++){
            Point point1 = points.get(i == 0? 0: i-1);
            Point point2 = points.get(i);
            Location location1 = getLocation(point1,i == 0?0: i-1 );
            Location location2 = getLocation(point2, i);
            canvas.drawCircle(location2.x, location2.y, 5f, textPaint);

            if(i > 0){
                canvas.drawLine(location1.x, location1.y, location2.x,location2.y, linePaint);
            }
        }
    }

    private void drawDataLines(Canvas canvas, List<Point> points){
        float[] mLineBuffer = new float[4];
        if(xCount > 1){
            mLineBuffer = new float[xCount * 4];
        }
        int j = 0;
        for(int i=0;i<xCount;i++){
            Point point1 = points.get(i == 0? 0: i-1);
            Point point2 = points.get(i);
            Location location1 = getLocation(point1,i == 0 ? 0: i-1 );
            Location location2 = getLocation(point2, i);

            int x1 = j++;
            mLineBuffer[x1] = location1.x;
            int x2 = j++;
            mLineBuffer[x2] = location1.y ;
            int x3 = j++;
            mLineBuffer[x3] = location2.x;
            int x4 = j++;
            mLineBuffer[x4] = location2.y ;

            canvas.drawCircle(location2.x, location2.y, 5f, textPaint);
        }

        canvas.drawLines(mLineBuffer, linePaint);
    }

    //绘制手触摸的时候的指示线和对应的值
    private void drawIndicateLines(Canvas canvas){
        canvas.drawLines(indicates, indicatorPaint);
        canvas.drawRect(rectX, indicateRectPaint);
        canvas.drawRect(rectY, indicateRectPaint);
        canvas.drawText(touchedPoint.getDate(), xValueLocation.x, xValueLocation.y, indicateValuePaint);
        String income =  new DecimalFormat("0.00").format(touchedPoint.getIncome());
        canvas.drawText(income, yValueLocation.x, yValueLocation.y, indicateValuePaint);
    }

    private void setIndicators(float x, float y){
        if(x < left){
            x = left;
        }
        if(x > right){
            x = right;
        }
        int position = new BigDecimal((x-left)/specX).setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
        Point point = points.get(position);
        touchedPoint = point;
        Location location = getLocation(point, position);

        //计算手指触摸时，两条指示线的坐标并赋值
        indicates[0] = left;
        indicates[1] = location.y;
        indicates[2] = right;
        indicates[3] = location.y;
        indicates[4] = location.x;
        indicates[5] = top;
        indicates[6] = location.x;
        indicates[7] = bottom;

        Paint.FontMetrics fm = indicateValuePaint.getFontMetrics();
        //计算两条指示线在坐标轴上显示值的矩形区域
        String date = point.getDate();
        String income =  new DecimalFormat("0.00").format(point.getIncome());
        float dateWidth = indicateValuePaint.measureText(date);
        float incomeWidth = indicateValuePaint.measureText(income);
        float height = fm.bottom - fm.top;
        //绘制x值的矩形区域
        rectX.left = location.x - dateWidth/2 - 5;
        rectX.top = bottom;
        rectX.right = location.x + dateWidth/2 + 5;
        rectX.bottom = bottom + height;
        //绘制y值的矩形区域
        rectY.left = left - incomeWidth - 10;
        rectY.top = location.y - height/2;
        rectY.right = left;
        rectY.bottom = location.y + height/2;

        //计算绘制x，y值的坐标
        xValueLocation.x = rectX.left + 5;
        xValueLocation.y = rectX.bottom - fm.descent;
        yValueLocation.x = rectY.left + 5;
        yValueLocation.y = rectY.bottom - fm.descent;
    }

    public void animX(long duration){
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "xCount", 0, points.size());
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        animator.start();
    }

    public void animY(long duration){
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "phaseY", 0f, 1f);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        animator.start();
    }

    private Location getLocation(Point point, int n){
        if(specY == 0 || specX == 0){
            specY = (bottom - top) / maxPoint.getIncome();
            specX = (right - left) / (points.size() - 1);
        }

        Location location = new Location();
        location.x = n * specX + left;
        location.y = bottom  - point.getIncome() * specY * phaseY;

        Point last = n >=1 ? points.get(n-1) : point;
        Point next = n < points.size() -1 ? points.get(n+1) : point;
        if((point.getIncome() <= last.getIncome() && point.getIncome() <= next.getIncome()) ||
                (point.getIncome() >= last.getIncome() && point.getIncome() >= next.getIncome())){
            location.setPeak(true);
        }

        return location;
    }

    public float getPhaseY() {
        return phaseY;
    }

    public void setPhaseY(float phaseY) {
        this.phaseY = phaseY;
    }

    public int getXCount() {
        return xCount;
    }

    public void setXCount(int xCount) {
        this.xCount = xCount;
    }
}
