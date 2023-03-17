package com.sixtech.rider.ui.activity.upcoming_trip_detail;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.Datum;

import java.util.List;

public interface UpcomingTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> upcomingTripDetails);
    void onError(Throwable e);
}
