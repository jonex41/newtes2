package com.sixtech.rider.ui.activity.add_card;

import com.sixtech.rider.base.MvpView;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface AddCardIView extends MvpView{
    void onSuccess(Object card);
    void onError(Throwable e);
}
