package com.sixtech.rider.ui.activity.social;

import com.sixtech.rider.base.MvpPresenter;

import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SocialIPresenter<V extends SocialIView> extends MvpPresenter<V>{
    void loginGoogle(HashMap<String, Object> obj);
    void loginFacebook(HashMap<String, Object> obj);
    void isMobileVerified(HashMap<String, Object> obj);
}
