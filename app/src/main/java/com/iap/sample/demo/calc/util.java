package com.iap.sample.demo.calc;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.iap.sample.demo.BuildConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class util {

    public void success() {
    }

    public static synchronized util getInstance() {
        if (instance == null) {
            instance = new util();
        }
        return instance;
    }

    private util() {
    }

    public static String getPack(int number) {
        return BuildConfig.DEBUG ? IAP_SAMPLE_PACK_T : PREFIX + number;
    }

    public void initBilling(Context context) {
        initBilling(context, null);
    }

    private final PurchasesUpdatedListener updater = (billingResult, purchases) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (int i = 0; i < purchases.size(); i++) {
                handlePurchase(purchases.get(i));
            }
        }
    };


    private void onBillingConnected(@NonNull BillingResult billingResult) {
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            if (stateListener != null) {
                stateListener.onBillingSetupFinished(billingResult);
            }
            return;
        }
        Log.i("CALC: ", IAP_SAMPLE_PACK_1 + " " + IAP_SAMPLE_PACK_2 + " " + IAP_SAMPLE_PACK_3 + " " + IAP_SAMPLE_PACK_4);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(Arrays.asList(IAP_SAMPLE_PACK_1, IAP_SAMPLE_PACK_2, IAP_SAMPLE_PACK_3, IAP_SAMPLE_PACK_4))
                .setType(BillingClient.SkuType.INAPP);
        d.querySkuDetailsAsync(params.build(),
                (billingResult12, skuDetailsList) -> {
                    util.this.skus = skuDetailsList;
                    for (SkuDetails skuDetails : skus) {
                        Log.i("CALC: ", "connected: " + skuDetails.getSku());
                    }
                    calclog.d(billingResult12.getResponseCode());
                    if (stateListener != null) {
                        stateListener.onBillingSetupFinished(billingResult);
                    }
                });
    }


    private Purchase get(String productId) {
        try {
            Purchase.PurchasesResult purchasesResult = d.queryPurchases(BillingClient.SkuType.INAPP);
            for (Purchase purchase :
                    Objects.requireNonNull(purchasesResult.getPurchasesList())) {
                if (purchase.getSkus().contains(productId)) return purchase;
            }
        } catch (Exception exception) {
            calclog.e(exception);
        }
        return null;
    }

    public static void init(Context mainApplication) {
        util.getInstance().initBilling(mainApplication);
    }

    public void initBilling(final Context context, BillingClientStateListener billingClientStateListener) {
        d = BillingClient.newBuilder(context)
                .setListener(updater)
                .enablePendingPurchases()
                .build();
        this.stateListener = billingClientStateListener;
        d.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                calclog.d(billingResult.getResponseCode());
                onBillingConnected(billingResult);
            }

            @Override
            public void onBillingServiceDisconnected() {
                calclog.method();
                if (billingClientStateListener != null) {
                    billingClientStateListener.onBillingServiceDisconnected();
                }
            }
        });
    }


    private void consume(Purchase purchase) {
        if (purchase != null) {
            ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
            d.consumeAsync(consumeParams, (billingResult, s) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    fetch(null);
                }
            });
        }
    }

    public void consume(String productId) {
        Purchase purchase = get(productId);
        consume(purchase);
    }


    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            consume(purchase);
            if (purchase.isAcknowledged()) {
                return;
            }

            AcknowledgePurchaseParams acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
            d.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                success();
            });
        }
    }


    public void fetch(donatecallback callback) {
        d.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult, list) -> {
            try {
                boolean purchased = false;
                for (Purchase purchase : Objects.requireNonNull(list)) {
                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        purchased = true;
                    }
                }
                if (!purchased) {
                    d.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult1, list1) -> {
                        boolean subscribed = false;
                        for (Purchase purchase : Objects.requireNonNull(list1)) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                subscribed = true;
                            }
                        }
                        if (callback != null) {
                            if (subscribed) {
                                callback.purchased();
                            } else {
                                callback.notPurchase();
                            }
                        }
                    });
                    return;
                }
                if (callback != null) {
                    callback.purchased();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void purchase(Activity activity, String productId) {
        try {
            calclog.d(productId);
            if (d == null) {
                initBilling(activity);
            }
            SkuDetails skuDetails = get(skus, productId);
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build();
            d.launchBillingFlow(activity, billingFlowParams);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    private SkuDetails get(List<SkuDetails> skuDetailsListSUB, String productId) {
        try {
            for (SkuDetails skuDetails : skuDetailsListSUB) {
                if (skuDetails.getSku().equals(productId)) {
                    return skuDetails;
                }
            }
        } catch (Exception e) {
            calclog.e(e);
        }
        return null;
    }


    public String getPrice(String productId) {
        Log.i("CALC: ", "price: " + productId);
        String defaultP = "0$";
        if (d == null || !d.isReady()) {
            return defaultP;
        }
        for (SkuDetails skuDetails : skus) {
            if (skuDetails.getSku().equals(productId)) {
                return skuDetails.getPrice();
            }
        }
        return defaultP;
    }

    public static final String IAP_SAMPLE_PACK_T = "android.test.purchased";
    public static final String PREFIX = BuildConfig.DEBUG ? IAP_SAMPLE_PACK_T : "iap_sample_pack_";

    public static final String IAP_SAMPLE_PACK_1 = BuildConfig.DEBUG ? IAP_SAMPLE_PACK_T : PREFIX + "1";
    public static final String IAP_SAMPLE_PACK_2 = BuildConfig.DEBUG ? IAP_SAMPLE_PACK_T : PREFIX + "2";
    public static final String IAP_SAMPLE_PACK_3 = BuildConfig.DEBUG ? IAP_SAMPLE_PACK_T : PREFIX + "3";

    private BillingClient d;
    private BillingClientStateListener stateListener;
    private static util instance;
    private List<SkuDetails> skus = new ArrayList<>();

    public static final String IAP_SAMPLE_PACK_4 = BuildConfig.DEBUG ? IAP_SAMPLE_PACK_T : PREFIX + "4";

}
