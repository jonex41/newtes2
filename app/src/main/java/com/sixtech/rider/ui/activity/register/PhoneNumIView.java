package com.sixtech.rider.ui.activity.register;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.PhoneNumReponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface PhoneNumIView extends MvpView{
    void onSuccess(PhoneNumReponse object);
    void onError(Throwable e);
}
