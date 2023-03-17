package com.sixtech.rider.ui.fragment.raise_fare;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.DataResponse;


public interface RaiseFareIView extends MvpView {
    void onSuccessAccept(Object responseBody);
    void onSuccessReject(Object responseBody);
//    void onSuccess(DataResponse dResponse);
    void onError(Throwable e);
    void onCancelRideSuccess(Object object);
    void onBookRideSuccess(Object object);
}
