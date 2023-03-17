package com.sixtech.rider.ui.fragment.service_flow;

import com.sixtech.rider.base.MvpPresenter;

import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ServiceFlowIPresenter<V extends ServiceFlowIView> extends MvpPresenter<V> {
    void checkStatus();
    void updateRide(HashMap<String, Object> obj);
}
