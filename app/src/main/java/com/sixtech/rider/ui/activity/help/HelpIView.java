package com.sixtech.rider.ui.activity.help;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.Help;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface HelpIView extends MvpView {
    void onSuccess(Help help);
    void onError(Throwable e);
}
