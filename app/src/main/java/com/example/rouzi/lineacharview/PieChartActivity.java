package com.example.rouzi.lineacharview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.rouzi.lineacharview.PieChart.PieChartView;
import com.example.rouzi.lineacharview.PieChart.PieData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rouzi on 2017/10/12.
 *
 */

public class PieChartActivity extends AppCompatActivity {

    private PieChartView pieChartView;
    private int[] colors;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);

        colors = new int[]{Color.RED, Color.YELLOW, Color.GREEN, ContextCompat.getColor(this, R.color.color_4)};
        pieChartView = (PieChartView) findViewById(R.id.pie_chart_view);
        pieChartView.setPieDataList(initPieData());
        pieChartView.animStart();

        findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pieChartView.setPieDataList(initPieData());
                pieChartView.animStart();
            }
        });

        findViewById(R.id.btn_solid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pieChartView.setSolid(!pieChartView.isSolid());
            }
        });
    }

    private List<PieData> initPieData(){
        List<PieData> pieDatas = new ArrayList<>();
        for(int i=0;i<4;i++){
            PieData pieData = new PieData();
            pieData.setIncome((float) Math.random() * 100);
            pieData.setColor(colors[i]);
            pieDatas.add(pieData);
        }
        return pieDatas;
    }
}
