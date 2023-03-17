package com.sixtech.rider.ui.activity.social;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.Token;
import com.sixtech.rider.data.network.model.VerificationReponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SocialIView extends MvpView{
    void onSuccess(Token token);
    void onSuccess(VerificationReponse verificationReponse);
    void onError(Throwable e);
}
