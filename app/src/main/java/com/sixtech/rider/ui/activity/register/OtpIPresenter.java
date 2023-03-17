package com.sixtech.rider.ui.activity.register;

import com.sixtech.rider.base.MvpPresenter;

import java.util.HashMap;

public interface OtpIPresenter<V extends OtpIView> extends MvpPresenter<V>{
    void verifyCode(HashMap<String, Object> obj);
    void resendCode(HashMap<String, Object> obj);
}
