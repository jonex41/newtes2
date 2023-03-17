package com.sixtech.rider.ui.activity.edit_location;

import com.sixtech.rider.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface EditLocationIPresenter<V extends EditLocationIView> extends MvpPresenter<V>{
    void address();
}
