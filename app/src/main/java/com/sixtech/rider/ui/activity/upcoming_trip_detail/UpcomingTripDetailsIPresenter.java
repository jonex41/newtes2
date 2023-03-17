package com.sixtech.rider.ui.activity.upcoming_trip_detail;

import com.sixtech.rider.base.MvpPresenter;

public interface UpcomingTripDetailsIPresenter<V extends UpcomingTripDetailsIView> extends MvpPresenter<V> {

    void getUpcomingTripDetails(Integer requestId);
}
