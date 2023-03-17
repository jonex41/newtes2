package com.sixtech.rider.ui.activity.social;

import com.sixtech.rider.base.BasePresenter;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class SocialPresenter<V extends SocialIView> extends BasePresenter<V> implements SocialIPresenter<V> {
    @Override
    public void loginGoogle(HashMap<String, Object> obj) {
        SharedHelper.apiState="loginGoogle";

        getCompositeDisposable().add(APIClient.getAPIClient().loginGoogle(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(token -> getMvpView().onSuccess(token),
                        throwable -> getMvpView().onError(throwable)));
    }
    @Override
    public void loginFacebook(HashMap<String, Object> obj) {
        SharedHelper.apiState="loginFacebook";

        getCompositeDisposable().add(APIClient.getAPIClient().loginFacebook(obj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        trendsResponse -> getMvpView().onSuccess(trendsResponse),
                        throwable -> getMvpView().onError(throwable)
                ));
    }

    @Override
    public void isMobileVerified(HashMap<String, Object> obj) {
        SharedHelper.apiState="isMobileVerified";

        getCompositeDisposable().add(APIClient.getAPIClient().isMobileVerified(obj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        verificationReponse -> getMvpView().onSuccess(verificationReponse),
                        throwable -> getMvpView().onError(throwable)
                ));
    }

}
