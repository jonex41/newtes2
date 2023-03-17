package com.sixtech.rider.ui.activity.register;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.RegisterResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface RegisterIView extends MvpView{
    void onSuccess(RegisterResponse object);
    void onSuccess(Object object);
    void onError(Throwable e);
    void onVerifyEmailError(Throwable e);
}
