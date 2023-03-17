package com.sixtech.rider.ui.activity.profile;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.User;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ProfileIView extends MvpView{
    void onSuccess(User user);
    void onError(Throwable e);
}
