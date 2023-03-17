package com.sixtech.rider.ui.activity.login;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.ForgotResponse;
import com.sixtech.rider.data.network.model.Token;

public interface LoginIView extends MvpView{
    void onSuccess(Token token);
    void onSuccess(ForgotResponse object);
    void onError(Throwable e);
}
