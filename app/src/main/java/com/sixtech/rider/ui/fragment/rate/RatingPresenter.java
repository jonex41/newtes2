package com.sixtech.rider.ui.fragment.rate;

import com.sixtech.rider.base.BasePresenter;
import com.sixtech.rider.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class RatingPresenter<V extends RatingIView> extends BasePresenter<V> implements RatingIPresenter<V> {

    @Override
    public void rate(HashMap<String, Object> obj) {

        getCompositeDisposable().add(APIClient.getAPIClient().rate(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(rateResponse -> getMvpView().onSuccess(rateResponse),
                        throwable -> getMvpView().onError(throwable)));
    }
}
