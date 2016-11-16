package com.rollncode.colorlines;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.rollncode.colorlines.util.SavingUtils;

import io.fabric.sdk.android.Fabric;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 01.07.16
 */
public class App extends Application {

    @Override
    public final void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        {
            SavingUtils.init(this);
        }
    }
}