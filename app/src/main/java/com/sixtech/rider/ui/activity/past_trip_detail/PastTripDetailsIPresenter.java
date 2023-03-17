package com.sixtech.rider.ui.activity.past_trip_detail;

import com.sixtech.rider.base.MvpPresenter;

public interface PastTripDetailsIPresenter<V extends PastTripDetailsIView> extends MvpPresenter<V> {

    void getPastTripDetails(Integer requestId);
}
