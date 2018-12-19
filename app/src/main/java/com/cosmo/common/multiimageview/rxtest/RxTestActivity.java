package com.cosmo.common.multiimageview.rxtest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cosmo.library.utils.RxUtils;


/**
 * Author:ruchao.jiang
 * Created: 2018/12/19 10:13
 * Email:ruchao.jiang@uama.com.cn
 */
public class RxTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setBackgroundColor(Color.WHITE);
        final Button button = new Button(this);
        button.setText("获取验证码");
        button.setBackgroundColor(Color.YELLOW);
        button.setTextColor(Color.WHITE);
        button.setPadding(20,20,20,20);
        linearLayout.addView(button);
        setContentView(linearLayout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setClickable(false);
                RxUtils.countDown(10l, new RxUtils.CountListener() {
                    @Override
                    public void accept(long count) {
                        button.setBackgroundColor(Color.DKGRAY);
                        button.setText(count+"秒后重新获取");
                    }

                    @Override
                    public void complete() {
                        button.setClickable(true);
                        button.setBackgroundColor(Color.YELLOW);
                        button.setText("重新获取验证码");
                    }
                });
            }
        });
    }
}
