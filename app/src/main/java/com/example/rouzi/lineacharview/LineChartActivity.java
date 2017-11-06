package com.example.rouzi.lineacharview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.rouzi.lineacharview.LineChart.LineChartView;
import com.example.rouzi.lineacharview.LineChart.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rouzi on 2017/9/22.
 *
 */

public class LineChartActivity extends AppCompatActivity implements View.OnClickListener{

    private LineChartView lineChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        lineChartView = (LineChartView) findViewById(R.id.line_chart);
        lineChartView.setShowScaleLine(true);
        lineChartView.setShowXText(true);
        lineChartView.setShowYText(true);
        setData();

        findViewById(R.id.btn_anim_x).setOnClickListener(this);
        findViewById(R.id.btn_anim_y).setOnClickListener(this);
        findViewById(R.id.btn_anim_xy).setOnClickListener(this);
        findViewById(R.id.btn_normal).setOnClickListener(this);
        findViewById(R.id.btn_bezier).setOnClickListener(this);
        findViewById(R.id.btn_corner).setOnClickListener(this);
        findViewById(R.id.btn_control_line).setOnClickListener(this);
        findViewById(R.id.btn_show_point).setOnClickListener(this);
    }

    private void setData(){
        List<Point> points = new ArrayList<>();
        for(int i=1;i<=25;i++){
            Point point = new Point();
            float income = (float) Math.random() * 100;
            point.setIncome(income);
            point.setDate("06-" + i);
            points.add(point);
        }
        lineChartView.setPoints(points);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_anim_x:
                lineChartView.animX(3000);
                break;
            case R.id.btn_anim_y:
                lineChartView.animY(3000);
                break;
            case R.id.btn_anim_xy:
                lineChartView.animX(3000);
                lineChartView.animY(3000);
                break;
            case R.id.btn_normal:
                lineChartView.setLineType(LineChartView.Type.Normal);
                break;
            case R.id.btn_bezier:
                lineChartView.setLineType(LineChartView.Type.Bezier);
                break;
            case R.id.btn_corner:
                lineChartView.setLineType(LineChartView.Type.Corner);
                break;
            case R.id.btn_control_line:
                lineChartView.setShowControlLine(!lineChartView.isShowControlLine());
                break;
            case R.id.btn_show_point:
                lineChartView.setShowPoint(!lineChartView.isShowPoint());
                break;
        }
    }
}
