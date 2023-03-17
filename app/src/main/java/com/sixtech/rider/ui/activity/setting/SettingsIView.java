package com.sixtech.rider.ui.activity.setting;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.AddressResponse;

public interface SettingsIView extends MvpView {

    void onSuccessAddress(Object object);

    void onLanguageChanged(Object object);

    void onSuccess(AddressResponse address);

    void onError(Throwable e);
}
