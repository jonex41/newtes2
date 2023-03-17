package com.sixtech.rider.ui.activity.past_trip_detail;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.Datum;

import java.util.List;

public interface PastTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> pastTripDetails);
    void onError(Throwable e);
}
