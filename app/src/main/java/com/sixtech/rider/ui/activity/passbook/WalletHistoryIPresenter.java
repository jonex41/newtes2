package com.sixtech.rider.ui.activity.passbook;

import com.sixtech.rider.base.MvpPresenter;

public interface WalletHistoryIPresenter<V extends WalletHistoryIView> extends MvpPresenter<V> {
    void wallet();
}
