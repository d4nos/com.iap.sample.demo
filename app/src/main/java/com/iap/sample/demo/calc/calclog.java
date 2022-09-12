package com.iap.sample.demo.calc;

import android.util.Log;

public class calclog {
    private static final String TAG = "CALC: ";


    public static void d(int intValue) {
        Log.d(TAG, message(String.valueOf(intValue)));
    }

    private static String message(String message) {
        return cmethod() + " = " + message;
    }

    public static void method() {
        Log.i(TAG, message(""));
    }

    private static String cmethod() {
        try {
            StackTraceElement[] stacktraceObj = Thread.currentThread().getStackTrace();
            StackTraceElement stackTraceElement = stacktraceObj[5];
            String className = stackTraceElement.getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
            return " [" + className + "] " + stackTraceElement.getMethodName();
        } catch (Exception e) {
            return "";
        }
    }

    public static void e(Throwable exception) {
        try {
            if (exception == null) {
                return;
            }
            try {
                Log.e(TAG, exception.getMessage());
            } catch (Exception e) {
            }
            exception.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void d(String message) {
        Log.d(TAG, message(message));
    }


}
