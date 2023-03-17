package com.sixtech.rider;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDex;

//import com.crashlytics.android.Crashlytics;


//import com.facebook.stetho.Stetho;
import com.sixtech.rider.common.ConnectivityReceiver;
import com.sixtech.rider.common.LocaleHelper;

//import io.fabric.sdk.android.Fabric;

//import com.facebook.stetho.Stetho;

public class MvpApplication extends Application {

    private static MvpApplication mInstance;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int PICK_LOCATION_REQUEST_CODE = 3;
    public static final int PERMISSIONS_REQUEST_PHONE = 4;
    public static final int REQUEST_CHECK_SETTINGS = 5;
    public static final int EDIT_LOCATION_REQUEST_CODE = 6;
    public static float DEFAULT_ZOOM = 18;
    public static Location mLastKnownLocation;
    public  Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG)
            //Fabric.with(this, new Crashlytics());
            //Stetho.initializeWithDefaults(this);
            mInstance = this;
        context=getApplicationContext();

        MultiDex.install(this);
    }

    public static synchronized MvpApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
        //super.attachBaseContext(newBase);
        MultiDex.install(newBase);
    }


    public double getNumber(double value) {
        long factor = (long) Math.pow(value, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}
