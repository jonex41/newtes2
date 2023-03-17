package com.sixtech.rider.ui.activity.register;

import com.sixtech.rider.base.MvpPresenter;

import java.util.HashMap;

public interface PhoneNumIPresenter<V extends PhoneNumIView> extends MvpPresenter<V>{
    void sendVerificationCode(HashMap<String, Object> obj);
}
