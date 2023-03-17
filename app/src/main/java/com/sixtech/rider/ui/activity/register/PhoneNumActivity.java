package com.sixtech.rider.ui.activity.register;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseActivity;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.model.PhoneNumReponse;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhoneNumActivity extends BaseActivity implements PhoneNumIView{

    GoogleApiClient googleApiClient;
    @BindView(R.id.dial_code)
    EditText dialCode;
    @BindView(R.id.phone_number)
    EditText phoneNum;

    String loginBy="";
    String accessToken="";

    private PhoneNumPresenter phoneNumPresenter = new PhoneNumPresenter();
    @Override
    public int getLayoutId() {
        return R.layout.activity_phone_num;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        phoneNumPresenter.attachView(this);
        setTitle(getString(R.string.phone_number));
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
        requestPhoneNumber();

        if(getIntent().getStringExtra("login_by")!=null){
        if(getIntent().getStringExtra("login_by").equals("facebook")
        || getIntent().getStringExtra("login_by").equals("google")){
            loginBy = getIntent().getStringExtra("login_by");
            accessToken= getIntent().getStringExtra("accessToken");
        }
        }

    }


    public void requestPhoneNumber() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), PHONE_NUMBER_RC, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e("Phone Picker", "Could not start hint picker Intent", e);
        }
    }

    @OnClick({R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next:
                if (validate()) {
                    if(SharedHelper.getKey(PhoneNumActivity.this, "dial_code").isEmpty() &&
                            SharedHelper.getKey(PhoneNumActivity.this, "mobile").isEmpty()){
                        SharedHelper.putKey(PhoneNumActivity.this, "dial_code", "+" +
                                dialCode.getText().toString());
                        SharedHelper.putKey(PhoneNumActivity.this, "mobile", phoneNum.getText().toString());
                    } else {
                        SharedHelper.putKey(PhoneNumActivity.this, "dial_code", "+" +
                                dialCode.getText().toString());
                        SharedHelper.putKey(PhoneNumActivity.this, "mobile", phoneNum.getText().toString());
                    }

                    sendVerificationCode();
                }
                break;
        }
    }



    private boolean validate() {
        if (dialCode.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_dialcode), Toast.LENGTH_SHORT).show();
            return false;
        } else if (phoneNum.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_phonenum), Toast.LENGTH_SHORT).show();
            return false;
        }  else
            return true;
    }

    private void sendVerificationCode(){

        HashMap<String, Object> map = new HashMap<>();
        map.put("mobile_no", SharedHelper.getKey(this, "dial_code") + SharedHelper.getKey(this, "mobile"));
        showLoading();

        phoneNumPresenter.sendVerificationCode(map);
    }


    @Override
    public void onSuccess(PhoneNumReponse object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if(object.getStatus()){
            if(object.isVerified()){
                 Toast.makeText(this,object.getMessage(),Toast.LENGTH_SHORT).show();
            }  else {
                if(loginBy.equals("facebook") || loginBy.equals("google")){
                    Intent otpIntent= new Intent(PhoneNumActivity.this, OtpActivity.class);
                    otpIntent.putExtra("login_by", loginBy);
                    otpIntent.putExtra("accessToken", accessToken);
                    startActivity(otpIntent);
                } else {
                    startActivity(new Intent(PhoneNumActivity.this, OtpActivity.class));
                }
            }

        }
    }

    @Override
    public void onError(Throwable throwable) {
        handleError(throwable);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHONE_NUMBER_RC) {
            if (resultCode == RESULT_OK) {
                Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
                Log.d("Phone NUmber",cred.getId());
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                try {
                    Phonenumber.PhoneNumber numberProto = phoneUtil.parse(cred.getId(), "");
                    dialCode.setText(String.valueOf(numberProto.getCountryCode()));
                    phoneNum.setText(String.valueOf(numberProto.getNationalNumber()));
                } catch (NumberParseException e) {
                    System.err.println("NumberParseException was thrown: " + e.toString());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        phoneNumPresenter.onDetach();
        super.onDestroy();
    }


}
