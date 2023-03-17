package com.sixtech.rider.ui.fragment.schedule;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.Message;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ScheduleIView extends MvpView{
    void onSuccess(Message object);
    void onError(Throwable e);
}
