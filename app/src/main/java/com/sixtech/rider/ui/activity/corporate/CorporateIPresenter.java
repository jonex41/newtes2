package com.sixtech.rider.ui.activity.corporate;

import com.sixtech.rider.base.MvpPresenter;

import java.util.HashMap;

public interface CorporateIPresenter<V extends CorporateIView> extends MvpPresenter<V> {
    void postCorperateUser(HashMap<String, Object> obj);
    void getCompanyList();
    void profile();
}
