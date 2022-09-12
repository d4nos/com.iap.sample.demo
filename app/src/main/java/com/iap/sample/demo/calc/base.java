package com.iap.sample.demo.calc;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class base extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(onLayout());
        this.initViews(savedInstanceState);

    }

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract int onLayout();

}
