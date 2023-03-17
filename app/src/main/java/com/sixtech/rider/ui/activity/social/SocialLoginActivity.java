package com.sixtech.rider.ui.activity.social;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sixtech.rider.BuildConfig;
import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseActivity;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.model.Token;
import com.sixtech.rider.data.network.model.VerificationReponse;
import com.sixtech.rider.ui.activity.main.MainActivity;
import com.sixtech.rider.ui.activity.register.PhoneNumActivity;
import com.sixtech.rider.ui.activity.register.RegisterActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

// implement GoogleApiClient.OnConnectionFailedListener by 92ItSolutions

public class SocialLoginActivity extends BaseActivity implements SocialIView {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    //private GoogleApiClient mGoogleApiClient;
    private SocialPresenter<SocialLoginActivity> presenter = new SocialPresenter<>();
    private CallbackManager callbackManager;
    private HashMap<String, Object> map = new HashMap<>();
    private String accessToken = "";
    private String loginBy = "";

    @Override
    public int getLayoutId() {
        return R.layout.activity_social_login;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_signin_server_client_id))
                .requestEmail()
                .build();

        // add by 92ItSolutions

      /*  mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/
        /////

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @OnClick({R.id.facebook, R.id.google})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.facebook:
                fbLogin();
                break;
            case R.id.google:
                showLoading();
                //startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);

                // add by 92ItSolutions
                // Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

                //
                break;
        }
    }

    void fbLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
                if (AccessToken.getCurrentAccessToken() != null) {

                    map.put("login_by", "facebook");
                    map.put("accessToken", loginResult.getAccessToken().getToken());
                    loginBy = "facebook";
                    accessToken = loginResult.getAccessToken().getToken();
                    checkMobileVerification();
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(SocialLoginActivity.this, "User Canceled. Please Clear Cache and Data", Toast.LENGTH_SHORT).show();
                Log.d("tag", "Facebook : Cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                if (exception instanceof FacebookAuthorizationException)
                    if (AccessToken.getCurrentAccessToken() != null)
                        LoginManager.getInstance().logOut();
                Log.d("tag", "Facebook : " + exception.getMessage());
                Toast.makeText(SocialLoginActivity.this, exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkMobileVerification() {

        String deviceToken = SharedHelper.getKey(this, "device_token");
        String deviceId = SharedHelper.getKey(this, "device_id");

        if (deviceToken != null && !deviceToken.isEmpty() && deviceId != null && !deviceId.isEmpty()) {
            map.put("device_token", SharedHelper.getKey(this, "device_token"));
            map.put("device_id", SharedHelper.getKey(this, "device_id"));
            map.put("device_type", BuildConfig.DEVICE_TYPE);
            presenter.isMobileVerified(map);
        } else {

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Get new FCM registration token
                            String token = task.getResult();
                            String deviceId2 = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            map.put("device_token", token);
                            map.put("device_id", deviceId2);
                            map.put("device_type", BuildConfig.DEVICE_TYPE);
                            presenter.isMobileVerified(map);
                        }
                        else {
                            Log.w("RegisterActivity", "getInstanceId failed", task.getException());
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                hideLoading();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            String TAG = "Google";

            Log.e("tag", "in request code : " + RC_SIGN_IN);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //String token = account.getIdToken();
                map.put("login_by", "google");
                Runnable runnable = () -> {
                    try {
                        String scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE;
                        String accessToken = GoogleAuthUtil.getToken(getApplicationContext(), account.getAccount(), scope, new Bundle());
                        Log.e("tag", "accessToken:" + accessToken);
                        map.put("accessToken", accessToken);
                        loginBy = "google";
                        this.accessToken = accessToken;
                        checkMobileVerification();
                    } catch (IOException | GoogleAuthException e) {
                        e.printStackTrace();
                    }
                };
                AsyncTask.execute(runnable);

            } catch (ApiException e) {
                Log.e("tag", "signInResult : failed code = " + e.getStatus() +" "+ e.getMessage());
            }
        } else if (requestCode == APP_REQUEST_CODE && data != null) {
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (!loginResult.wasCancelled())
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        Log.e("tag", "onSuccess: Account Kit" + AccountKit.getCurrentAccessToken().getToken());
                        if (Objects.requireNonNull(AccountKit.getCurrentAccessToken()).getToken() != null) {
                            PhoneNumber phoneNumber = account.getPhoneNumber();
                            SharedHelper.putKey(SocialLoginActivity.this, "dial_code", "+" + phoneNumber.getCountryCode());
                            SharedHelper.putKey(SocialLoginActivity.this, "mobile", phoneNumber.getPhoneNumber());
                            SharedHelper.putKey(SocialLoginActivity.this, "isMobileVerified", 1);
                            register();
                        }
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        Log.e("tag", "onError: Account Kit" + accountKitError);
                    }
                });
        }
    }

    private void registerWithoutNum() {
        String deviceToken = SharedHelper.getKey(this, "device_token");
        String deviceId = SharedHelper.getKey(this, "device_id");

        if (deviceToken != null && !deviceToken.isEmpty() && deviceId != null && !deviceId.isEmpty()) {
            map.put("device_token", SharedHelper.getKey(this, "device_token"));
            map.put("device_id", SharedHelper.getKey(this, "device_id"));
            map.put("device_type", BuildConfig.DEVICE_TYPE);
            if (map.get("login_by").equals("google")) presenter.loginGoogle(map);
            else if (map.get("login_by").equals("facebook")) presenter.loginFacebook(map);

        } else {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Get new FCM registration token
                            String token = task.getResult();
                            String deviceId2 = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            map.put("device_token", token);
                            map.put("device_id", deviceId2);
                            map.put("device_type", BuildConfig.DEVICE_TYPE);
                            if (map.get("login_by").equals("google")) presenter.loginGoogle(map);
                            else if (map.get("login_by").equals("facebook")) presenter.loginFacebook(map);
                        }
                        else {
                            Log.w("RegisterActivity", "getInstanceId failed", task.getException());
                        }
                    });
        }
        showLoading();
    }

    private void register() {
        map.put("mobile", SharedHelper.getKey(this, "dial_code") + SharedHelper.getKey(this, "mobile"));
        map.put("isMobileVerified", 1);

        String deviceToken = SharedHelper.getKey(this, "device_token");
        String deviceId = SharedHelper.getKey(this, "device_id");

        if (deviceToken != null && !deviceToken.isEmpty() && deviceId != null && !deviceId.isEmpty()) {
            map.put("device_token", SharedHelper.getKey(this, "device_token"));
            map.put("device_id", SharedHelper.getKey(this, "device_id"));
            map.put("device_type", BuildConfig.DEVICE_TYPE);
            if (map.get("login_by").equals("google")) presenter.loginGoogle(map);
            else if (map.get("login_by").equals("facebook")) presenter.loginFacebook(map);

        } else {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Get new FCM registration token
                            String token = task.getResult();
                            String deviceId2 = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            map.put("device_token", token);
                            map.put("device_id", deviceId2);
                            map.put("device_type", BuildConfig.DEVICE_TYPE);
                            if (map.get("login_by").equals("google")) presenter.loginGoogle(map);
                            else if (map.get("login_by").equals("facebook")) presenter.loginFacebook(map);
                        }
                        else {
                            Log.w("RegisterActivity", "getInstanceId failed", task.getException());
                        }
                    });
        }


        showLoading();
    }


    @Override
    public void onSuccess(Token token) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (token.isStatus()) {
            String accessToken = token.getTokenType() + " " + token.getAccessToken();
            SharedHelper.putKey(this, "access_token", accessToken);
            SharedHelper.putKey(this, "logged_in", true);
            finishAffinity();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Toast.makeText(this, token.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onSuccess(VerificationReponse verificationReponse) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (verificationReponse.getStatus()) {
            if (verificationReponse.getIsVerified() == 1) {
                registerWithoutNum();
            } else {
                numVerification();
            }
        } else {
            numVerification();
        }

    }

    private void numVerification() {
        Intent phoneNumIntent = new Intent(SocialLoginActivity.this, PhoneNumActivity.class);
        phoneNumIntent.putExtra("login_by", loginBy);
        phoneNumIntent.putExtra("accessToken", accessToken);
        startActivity(phoneNumIntent);
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    // add by 92ItSolutions
   /* @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/

}
