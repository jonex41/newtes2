package com.sixtech.rider.ui.fragment.raise_fare;

import com.sixtech.rider.base.BasePresenter;
import com.sixtech.rider.data.network.APIClient;
import com.sixtech.rider.ui.fragment.searching.SearchingIPresenter;
import com.sixtech.rider.ui.fragment.searching.SearchingIView;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.FieldMap;


public class RaiseFarePresenter<V extends RaiseFareIView> extends BasePresenter<V> implements RaiseFareIPresenter<V> {

    @Override
    public void cancelRequest(@FieldMap HashMap<String, Object> params) {

        getCompositeDisposable().add(APIClient.getAPIClient().cancelRequest(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(cancelResponse -> getMvpView().onCancelRideSuccess(cancelResponse),
                        throwable -> getMvpView().onError(throwable)));
    }

    @Override
    public void accept(String r_id, String b_id) {

        getCompositeDisposable().add(
                APIClient
                        .getAPIClient()
                        .acceptRequest(r_id, b_id)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getMvpView()::onSuccessAccept,
                                getMvpView()::onError));
    }

    @Override
    public void reject(String id, String id2) {
        getCompositeDisposable().add(
                APIClient
                        .getAPIClient()
                        .rejectRequest(id, id2)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getMvpView()::onSuccessReject,
                                getMvpView()::onError));
    }


    @Override
    public void rideNow(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .sendRequest(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onBookRideSuccess, getMvpView()::onError));
    }

//    public void checkStatus() {
//        getCompositeDisposable().add(APIClient
//                .getAPIClient()
//                .checkStatus()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(sendRequestResponse -> getMvpView().onSuccess(sendRequestResponse),
//                        throwable -> getMvpView().onError(throwable)));
//
//    }
}
