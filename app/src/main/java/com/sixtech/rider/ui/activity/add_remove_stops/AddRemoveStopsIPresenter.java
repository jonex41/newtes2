package com.sixtech.rider.ui.activity.add_remove_stops;

import com.sixtech.rider.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface AddRemoveStopsIPresenter<V extends AddRemoveStopsIView> extends MvpPresenter<V>{
    void address();
}
