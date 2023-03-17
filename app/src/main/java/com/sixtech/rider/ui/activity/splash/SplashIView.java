package com.sixtech.rider.ui.activity.splash;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.User;


public interface SplashIView extends MvpView{
    void onSuccess(User user);
    void onError(Throwable e);
}
