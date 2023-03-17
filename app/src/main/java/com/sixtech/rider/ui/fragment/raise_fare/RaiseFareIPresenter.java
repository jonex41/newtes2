package com.sixtech.rider.ui.fragment.raise_fare;

import com.sixtech.rider.base.MvpPresenter;
import com.sixtech.rider.ui.fragment.searching.SearchingIView;

import java.util.HashMap;

import retrofit2.http.FieldMap;


public interface RaiseFareIPresenter<V extends RaiseFareIView> extends MvpPresenter<V> {
    void cancelRequest(@FieldMap HashMap<String, Object> params);
    void rideNow(@FieldMap HashMap<String, Object> params);
    void accept(String id, String id2);
    void reject(String id, String id2);
    //void checkStatus();
}
