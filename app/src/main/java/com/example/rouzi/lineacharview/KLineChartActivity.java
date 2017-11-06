package com.example.rouzi.lineacharview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.rouzi.lineacharview.KLineChart.KData;
import com.example.rouzi.lineacharview.KLineChart.KLineChartView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rouzi on 2017/10/31.
 *
 */

public class KLineChartActivity extends AppCompatActivity {

    private KLineChartView kLineChartView;
    private List<KData> kDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_k_chart);

        kLineChartView = (KLineChartView) findViewById(R.id.k_chart_view);
        kDataList = new ArrayList<>();
        setData();

        findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
            }
        });
    }

    private void setData(){
        kDataList.clear();
        Random random = new Random();
        int n = random.nextInt(10) + 30;

        for(int i=0;i<n;i++){
            KData data = new KData();
            data.setDate("08-" + i);

            float openValue = (float) Math.random()*100 + 3300;
            float closeValue = (float) Math.random()*100 + 3300;
            float maxValue = Math.max(openValue, closeValue) + (float) Math.random()*50;
            float minValue = Math.min(openValue, closeValue) - (float) Math.random()*50;

            data.setOpenValue(openValue);
            data.setCloseValue(closeValue);
            data.setMinValue(minValue);
            data.setMaxValue(maxValue);
            kDataList.add(data);
        }

        kLineChartView.setkDataList(kDataList);
    }
}
