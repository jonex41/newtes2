package com.sixtech.rider.ui.fragment.dispute;

import com.sixtech.rider.base.BasePresenter;
import com.sixtech.rider.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class DisputePresenter<V extends DisputeIView> extends BasePresenter<V> implements DisputeIPresenter<V> {

    @Override
    public void dispute(HashMap<String, Object> obj) {

        getCompositeDisposable().add(APIClient.getAPIClient().dispute(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(disputeResponse -> getMvpView().onSuccess(disputeResponse),
                        throwable -> getMvpView().onError(throwable)));
    }
}
