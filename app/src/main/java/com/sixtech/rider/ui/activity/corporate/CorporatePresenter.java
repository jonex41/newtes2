package com.sixtech.rider.ui.activity.corporate;

import com.sixtech.rider.base.BasePresenter;
import com.sixtech.rider.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CorporatePresenter<V extends CorporateIView> extends BasePresenter<V> implements CorporateIPresenter<V> {

    @Override
    public void postCorperateUser(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .postCorperateUser(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onErrorCorporate));
    }

    @Override
    public void getCompanyList() {
        getCompositeDisposable().add(APIClient.getAPIClient().companylist()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(companies -> getMvpView().onSuccessCompanyList(companies),
                        throwable -> getMvpView().onError(throwable)));
    }

    @Override
    public void profile() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .profile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(user -> getMvpView().onSuccessUser(user),
                        throwable -> getMvpView().onError(throwable)));
    }
}
