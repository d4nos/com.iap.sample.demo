package com.iap.sample.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.iap.sample.demo.calc.calcscreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.bt_show_iap).setOnClickListener(View.OnClickListener {
            calcscreen.open(
                this
            )
        })
    }
}