package com.sixtech.rider.ui.fragment.address_picker;

import com.sixtech.rider.base.MvpPresenter;

import java.util.HashMap;

public interface APIPresenter<V extends APIView> extends MvpPresenter<V> {
    void rideNow(HashMap<String, Object> obj);
}
