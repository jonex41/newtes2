package com.sixtech.rider.ui.fragment.cancel_ride;

import com.sixtech.rider.base.MvpView;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface CancelRideIView extends MvpView{
    void onSuccessCancel(Object object);
    void onErrorCancel(Throwable e);
}
