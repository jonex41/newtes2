package com.sixtech.rider.ui.activity.upcoming_trip_detail;

import com.sixtech.rider.base.BasePresenter;
import com.sixtech.rider.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UpcomingTripDetailsPresenter<V extends UpcomingTripDetailsIView> extends BasePresenter<V>
        implements UpcomingTripDetailsIPresenter<V> {

    @Override
    public void getUpcomingTripDetails(Integer requestId) {

        getCompositeDisposable().add(APIClient.getAPIClient().upcomingTripDetails(requestId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(data -> getMvpView().onSuccess(data),
                        throwable -> getMvpView().onError(throwable)));
    }
}
