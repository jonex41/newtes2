package com.sixtech.rider.ui.activity.splash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.sixtech.rider.BuildConfig;
import com.sixtech.rider.GPSTracker;
import com.sixtech.rider.R;
import com.sixtech.rider.RootCheck.RootUtil;
import com.sixtech.rider.base.BaseActivity;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.model.User;
import com.sixtech.rider.ui.activity.OnBoardActivity;
import com.sixtech.rider.ui.activity.SignInUpActivity;
import com.sixtech.rider.ui.activity.main.MainActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sixtech.rider.MvpApplication.REQUEST_CHECK_SETTINGS;

public class SplashActivity extends BaseActivity implements SplashIView {

    @BindView(R.id.note)
    TextView note;
    androidx.appcompat.app.AlertDialog alertDialog;
    private SplashPresenter<SplashActivity> presenter = new SplashPresenter<>();

    @Override
    public int getLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_splash;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
        note.setText(getString(R.string.version,
                String.valueOf(BuildConfig.VERSION_CODE)));
        @SuppressLint("PackageManagerGetSignatures") PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(
                    BuildConfig.APPLICATION_ID,
                    PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        for (Signature signature : info.signatures) {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            md.update(signature.toByteArray());
            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
        }
        init();
        //Log.d("FCM", "FCM Token: " + SharedHelper.getKey(SplashActivity.this, "device_token"));
    }

    private void init() {
        if (RootUtil.isDeviceRooted()) {
            closeApp();
        } else {
            new Handler().postDelayed(() -> {
                //Log.d("Loggedin", String.valueOf(SharedHelper.getBoolKey(SplashActivity.this, "logged_in", false)));
                //String device_token = String.valueOf(new SharedHelper().getKey(SplashActivity.this, "device_token"));
                //Log.d("device_token", device_token);
                if (SharedHelper.getBoolKey(SplashActivity.this, "logged_in", false))
                    presenter.profile();
                else if (SharedHelper.getBoolKey(SplashActivity.this,"is_first", true)) {
                    SharedHelper.putKey(SplashActivity.this, "is_first", false);
                    startActivity(new Intent(SplashActivity.this, OnBoardActivity.class));
                } else
                    startActivity(new Intent(SplashActivity.this, SignInUpActivity.class));
            }, 2000);
        }


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //checkLocation();
        //enableGPS();
//        init();
    }

    private void checkLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            enableGPS();
        }
    }

    private void buildAlertMessageNoGps() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.loc_dialog, viewGroup, false);
        dialogView.findViewById(R.id.allow).setOnClickListener(v -> {
            alertDialog.cancel();
            onClick(v);
        });
        dialogView.findViewById(R.id.deny).setOnClickListener(this::onClick);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.DialogHard);
        builder.setView(dialogView);

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    protected void onStart() {

        super.onStart();

//        enableGPS();
    }

    public void closeApp() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
        alertDialogBuilder
                .setMessage(getString(R.string.device_rooted_error_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), (dialog, id) -> {
                    finishAffinity();
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSuccess(User user) {
        SharedHelper.putKey(this, "first_name", user.getFirstName());
        SharedHelper.putKey(this, "last_name", user.getLastName());
        SharedHelper.putKey(this, "email", user.getEmail());
        SharedHelper.putKey(this, "stripe_publishable_key", user.getStripePublishableKey());
        SharedHelper.putKey(this, "user_id", String.valueOf(user.getId()));
        SharedHelper.putKey(this, "appContact", user.getAppContact());
        SharedHelper.putKey(this, "currency", user.getCurrency());
        SharedHelper.putKey(this, "lang", user.getLanguage());
        SharedHelper.putKey(this, "walletBalance", String.valueOf(user.getWalletBalance()));
        SharedHelper.putKey(this, "userNegativeBalanceLimit", String.valueOf(user.getUserNegativeWalletLimit()));
        SharedHelper.putKey(this, "rideCancellationMinutes", String.valueOf(user.getRideCancellationMinutes()));
        SharedHelper.putKey(this, "logged_in", true);
        SharedHelper.putKey(this, "measurementType", user.getMeasurement());
        if (user.getCorpDeleted() == 1)
            SharedHelper.putKey(this, "corporate_user", "1");
        else
            SharedHelper.putKey(this, "corporate_user", "0");
        finishAffinity();
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
       /* if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            Log.e("onError", response.code() + "");

            if (response.code() == 500) note.setText(getString(R.string.internal_server_error));
            *//*if (response.code() == 401) {
                SharedHelper.clearSharedPreferences(activity());
                finishAffinity();
                startActivity(new Intent(activity(), OnBoardActivity.class));
            }*//*
        }*/
    }

    protected void enableGPS() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 10);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> init());

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) try {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                resolvable.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException sendEx) {
                Toast.makeText(this, sendEx.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        init();
                        break;
                    case Activity.RESULT_CANCELED:
//                        if (checkIfLocationOpened()) init();
//                        else finish();
                        break;
                }
                break;
        }
    }

    private boolean checkIfLocationOpened() {
        System.out.println("RRR SplashActivity.checkIfLocationOpened");
        String provider = Settings.Secure.getString(SplashActivity.this.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        return provider.contains("gps") || provider.contains("network");
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.allow:
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                init();
                break;

            case R.id.deny:
                alertDialog.dismiss();
                init();
        }
    }
}
