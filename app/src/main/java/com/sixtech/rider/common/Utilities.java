package com.sixtech.rider.common;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseActivity;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.ui.activity.OnBoardActivity;
import com.sixtech.rider.ui.activity.SignInUpActivity;

import es.dmoral.toasty.Toasty;

public class Utilities {

    public static String device_token;

    public static boolean isEmpty(EditText email, String message) {
        if (email.getText().toString().equalsIgnoreCase("")){
            email.setError(message);
            return false;
        }else return true;
    }

    public interface InvoiceFare {
        String min = "MIN";
        String hour = "HOUR";
        String distance = "DISTANCE";
        String distanceMin = "DISTANCEMIN";
        String distanceHour = "DISTANCEHOUR";
    }

    public interface PaymentMode {
        String cash = "CASH";
        String card = "CARD";
        String payPal = "PAYPAL";
        String wallet = "WALLET";
        String corporate = "CORPORATE";
    }

    private static double milesToKm(double miles) {
        return miles * 1.60934;
    }

    private static double kmToMiles(double km) {
        return km * 0.621371;
    }

    public static void LogoutApp(Activity thisActivity) {
        Log.e("ajdksajdksajdk","asddfdfasd");

        Toasty.success(thisActivity, thisActivity.getString(R.string.logout_successfully), Toast.LENGTH_SHORT).show();
        Log.e("ajdksajdksajdk","asdadfsd");

        SharedHelper.clearSharedPreferences(thisActivity);
        BaseActivity.RIDE_REQUEST.clear();
        NotificationManager notificationManager = (NotificationManager) thisActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        thisActivity.startActivity(new Intent(thisActivity, SignInUpActivity.class));
        thisActivity.finishAffinity();
        Log.e("ajdksajdksajdk","asdas323d");
    }
}
