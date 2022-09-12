package com.iap.sample.demo.calc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.iap.sample.demo.R;

import java.util.Locale;


public class calcscreen extends base {

    public static void open(Context context) {
        context.startActivity(new Intent(context, calcscreen.class));
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        util.getInstance().initBilling(this);

        initToolbar();

        setTitle("Donate for Quick Calculator");
        util.getInstance().initBilling(this, new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                runOnUiThread(() -> initD());
            }
        });
    }

    private void initToolbar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private View findViewByName(String viewIdString) {
        return findViewById(getResources().getIdentifier(viewIdString, "id", getPackageName()));
    }

    private void initD() {
        util util = com.iap.sample.demo.calc.util.getInstance();
        for (int i = 1; i <= 4; i++) {
            try {
                View layoutPack = findViewByName("layout_calc_donate_" + i);
                String packString = com.iap.sample.demo.calc.util.getPack(i);
                Log.i("CALC:", "init: " + packString);
                layoutPack.setSelected(true);
                layoutPack.setOnClickListener(view -> {
                    util.consume(packString);
                    util.purchase(this, packString);
                });

                String titleViewIdString = String.format(Locale.US, "tv_calc_donate_%d_price", i);
                TextView tvPrice = (TextView) findViewByName(titleViewIdString);
                tvPrice.setSelected(layoutPack.isSelected());
                String price = com.iap.sample.demo.calc.util.getInstance().getPrice(packString);
                tvPrice.setText(price);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected int onLayout() {
        return R.layout.activity_calc_donate;
    }
}
