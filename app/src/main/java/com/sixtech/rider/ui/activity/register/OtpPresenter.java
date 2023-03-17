package com.sixtech.rider.ui.activity.register;

import com.sixtech.rider.base.BasePresenter;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class OtpPresenter<V extends OtpIView>
        extends BasePresenter<V>
        implements OtpIPresenter<V> {

    @Override
    public void verifyCode(HashMap<String, Object> obj) {
        SharedHelper.apiState="verifyCode";

        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .verifyOtp(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void resendCode(HashMap<String, Object> obj) {
        SharedHelper.apiState="resendCode";

        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .sendVerificationCode(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }
}
