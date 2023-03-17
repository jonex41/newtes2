package com.sixtech.rider.ui.activity.edit_location;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.AddressResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface EditLocationIView extends MvpView {

    void onSuccess(AddressResponse address);
    void onError(Throwable e);
}
