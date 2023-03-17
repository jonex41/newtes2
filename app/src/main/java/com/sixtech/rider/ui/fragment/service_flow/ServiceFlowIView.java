package com.sixtech.rider.ui.fragment.service_flow;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.DataResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ServiceFlowIView extends MvpView{
    void onSuccess(DataResponse dataResponse);
    void onSuccess(Object dataResponse);
    void onUpdateRideSuccess(Object o);
    void onError(Throwable e);
}
