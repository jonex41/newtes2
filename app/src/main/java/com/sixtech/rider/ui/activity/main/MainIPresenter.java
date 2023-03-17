package com.sixtech.rider.ui.activity.main;

import com.sixtech.rider.base.MvpPresenter;

import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface MainIPresenter<V extends MainIView> extends MvpPresenter<V> {
    void profile();
    void logout(String id);
    void checkStatus();
    void address();
    void settings();
    void providers(HashMap<String, Object> params);
    void checkUpdate(HashMap<String, Object> obj);

    default void rideNow(HashMap<String, Object> obj) {

    }


}
