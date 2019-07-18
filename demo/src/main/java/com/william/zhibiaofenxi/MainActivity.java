package com.william.zhibiaofenxi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.william.zhibiaoview.ZheXianView;
import com.william.zhibiaoview.ZheXianView.Data;
import com.william.zhibiaoview.ZhiBiaoView;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final ZhiBiaoView view = findViewById(R.id.zhibiao);
        Button btn = findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setNormalScore(5);
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
    }
}
