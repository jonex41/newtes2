package com.sixtech.rider.ui.activity.wallet;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.AddWallet;

public interface WalletIView extends MvpView {
    void onSuccess(AddWallet object);
    void onError(Throwable e);
}
