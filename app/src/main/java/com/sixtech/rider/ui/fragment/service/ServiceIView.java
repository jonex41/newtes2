package com.sixtech.rider.ui.fragment.service;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.EstimateFare;
import com.sixtech.rider.data.network.model.Message;
import com.sixtech.rider.data.network.model.Service;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ServiceIView extends MvpView{
    void onSuccess(List<Service> serviceList);
    void onSuccess(EstimateFare estimateFare);
    void onError(Throwable e);
    void onSuccess(Message object);
}
