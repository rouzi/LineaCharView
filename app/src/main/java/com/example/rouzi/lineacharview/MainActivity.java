package com.example.rouzi.lineacharview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_line_chart).setOnClickListener(this);
        findViewById(R.id.btn_circle_view).setOnClickListener(this);
        findViewById(R.id.btn_region_view).setOnClickListener(this);
        findViewById(R.id.btn_k_line).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_line_chart:
                toActivity(LineChartActivity.class);
                break;
            case R.id.btn_circle_view:
                toActivity(CircleWaveActivity.class);
                break;
            case R.id.btn_region_view:
                toActivity(PieChartActivity.class);
                break;
            case R.id.btn_k_line:
                toActivity(KLineChartActivity.class);
                break;
        }
    }

    private void toActivity(Class<?> cls){
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
