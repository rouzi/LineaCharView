package com.example.rouzi.lineacharview;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.rouzi.lineacharview.CircleWave.CircleWaveView;
import com.example.rouzi.lineacharview.CircleWave.CircleWaveView1;
import com.example.rouzi.lineacharview.CircleWave.CircleWaveViewShader;

/**
 * Created by rouzi on 2017/9/26.
 *
 */

public class CircleWaveActivity extends AppCompatActivity {

    private CircleWaveView circleWaveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_wave);

        circleWaveView = (CircleWaveView) findViewById(R.id.circle_view);
        circleWaveView.waveAnimate();


//        CircleWaveView1 circleWaveView1 = (CircleWaveView1) findViewById(R.id.circle_view1);
//        circleWaveView1.setWaterLevelRatio(0.6f);
//        circleWaveView1.setBorder(1, ContextCompat.getColor(this,R.color.circle_bg_color));
//        circleWaveView1.setShowWave(true);
//        circleWaveView1.animStart();

//        CircleWaveViewShader circleWaveView2 = (CircleWaveViewShader) findViewById(R.id.circle_view2);
    }


}
