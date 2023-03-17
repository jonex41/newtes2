package com.sixtech.rider.ui.fragment.address_picker;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.Message;

public interface APIView extends MvpView {
    void onSuccess(Message object);
    void onError(Throwable e);
}
