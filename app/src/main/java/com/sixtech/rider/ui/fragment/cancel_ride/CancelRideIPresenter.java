package com.sixtech.rider.ui.fragment.cancel_ride;

import com.sixtech.rider.base.MvpPresenter;

import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface CancelRideIPresenter<V extends CancelRideIView> extends MvpPresenter<V> {
    void cancelRequest(HashMap<String, Object> params);
}
