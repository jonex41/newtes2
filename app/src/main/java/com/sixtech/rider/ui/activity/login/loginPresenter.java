package com.sixtech.rider.ui.activity.login;

import com.sixtech.rider.base.BasePresenter;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class loginPresenter<V extends LoginIView> extends BasePresenter<V>
        implements LoginIPresenter<V> {
    @Override
    public void login(HashMap<String, Object> obj) {
        SharedHelper.apiState="login";
        getCompositeDisposable().add(APIClient.getAPIClient().login(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(token -> getMvpView().onSuccess(token),
                        throwable -> getMvpView().onError(throwable)));
    }

    @Override
    public void forgotPassword(String email) {
        SharedHelper.apiState="forgotPassword";

        getCompositeDisposable().add(APIClient.getAPIClient().forgotPassword(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object -> getMvpView().onSuccess(object),
                        throwable -> getMvpView().onError(throwable)));
    }
}
