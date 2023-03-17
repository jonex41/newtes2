package com.sixtech.rider.ui.activity.passbook;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.WalletResponse;

public interface WalletHistoryIView extends MvpView {
    void onSuccess(WalletResponse response);
    void onError(Throwable e);
}
