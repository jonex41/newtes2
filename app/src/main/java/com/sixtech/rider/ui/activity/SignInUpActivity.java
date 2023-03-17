package com.sixtech.rider.ui.activity;


import android.content.Intent;
import android.view.View;
import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseActivity;
import com.sixtech.rider.ui.activity.login.EmailActivity;
import com.sixtech.rider.ui.activity.register.PhoneNumActivity;
import com.sixtech.rider.ui.activity.social.SocialLoginActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInUpActivity extends BaseActivity {


    @Override
    public int getLayoutId() {
        return R.layout.activity_sign_in_up;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

    }

    @OnClick({R.id.sign_in, R.id.sign_up, R.id.social_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_in:
                startActivity(new Intent(this, EmailActivity.class));
                break;
            case R.id.sign_up:
                startActivity(new Intent(this, PhoneNumActivity.class));
                break;
            case R.id.social_login:
                startActivity(new Intent(this, SocialLoginActivity.class));
                break;
        }
    }
}
