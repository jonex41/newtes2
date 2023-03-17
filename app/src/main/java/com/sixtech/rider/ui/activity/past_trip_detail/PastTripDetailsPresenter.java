package com.sixtech.rider.ui.activity.past_trip_detail;

import com.sixtech.rider.base.BasePresenter;
import com.sixtech.rider.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PastTripDetailsPresenter <V extends PastTripDetailsIView> extends BasePresenter<V>
        implements PastTripDetailsIPresenter<V> {

    @Override
    public void getPastTripDetails(Integer requestId) {

        getCompositeDisposable().add(APIClient.getAPIClient().pastTripDetails(requestId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(data -> getMvpView().onSuccess(data),
                        throwable -> getMvpView().onError(throwable)));
    }
}
