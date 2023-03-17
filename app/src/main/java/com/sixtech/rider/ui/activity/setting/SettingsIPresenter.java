package com.sixtech.rider.ui.activity.setting;

import com.sixtech.rider.base.MvpPresenter;

import java.util.HashMap;

public interface SettingsIPresenter<V extends SettingsIView> extends MvpPresenter<V>{
    void addAddress(HashMap<String, Object> params);
    void deleteAddress(Integer id, HashMap<String, Object> params);
    void address();
    void changeLanguage(String languageID);
}
