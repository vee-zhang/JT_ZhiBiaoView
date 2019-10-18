package com.william.zhibiaofenxi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.william.zhibiaoview.DashBoardView;
import com.william.zhibiaoview.StepView;
import com.william.zhibiaoview.ZheXianView;
import com.william.zhibiaoview.ZheXianView.Data;
import com.william.zhibiaoview.ZhiBiaoView;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private StepView stepView;
    int index;

    private SeekBar seekBar;
    private DashBoardView dash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepView = findViewById(R.id.step);


        final ZhiBiaoView view = findViewById(R.id.zhibiao);
        Button btn = findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepView.setSelectedIndex(++index);
            }
        });

        ZheXianView zxv = findViewById(R.id.zhexian);
        LinkedList<Data> dataList = new LinkedList<>();
        dataList.add(new Data("06.20", 80f));
        dataList.add(new Data("06.20", 82f));
        dataList.add(new Data("06.20", 85f));
        dataList.add(new Data("06.20", 84f));
        dataList.add(new Data("06.20", 0f));
        dataList.add(new Data("06.20", 88));
        dataList.add(new Data("06.20", 98f));

        zxv.initData(dataList);

        dash = findViewById(R.id.dash);
        seekBar = findViewById(R.id.seek);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dash.setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
