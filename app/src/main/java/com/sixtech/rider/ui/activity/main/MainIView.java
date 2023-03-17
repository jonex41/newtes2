package com.sixtech.rider.ui.activity.main;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.AddressResponse;
import com.sixtech.rider.data.network.model.CheckUpdate;
import com.sixtech.rider.data.network.model.DataResponse;
import com.sixtech.rider.data.network.model.InitSettingsResponse;
import com.sixtech.rider.data.network.model.Message;
import com.sixtech.rider.data.network.model.Provider;
import com.sixtech.rider.data.network.model.User;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface MainIView extends MvpView{
    void onSuccess(User user);
    void onSuccess(DataResponse dataResponse);
    void onSuccess(Object dataResponse);
    void onSuccessLogout(Object object);
    void onSuccess(AddressResponse response);
    void onSuccess(List<Provider> objects);
    void onSuccess(InitSettingsResponse initSettingsResponse);
    void onError(Throwable e);
    void onCheckStatusError(Throwable e);
    void onSuccessCheckUpdate(CheckUpdate checkUpdate);
    void onSuccess(Message object);

}
