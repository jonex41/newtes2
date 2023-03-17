package com.sixtech.rider.ui.activity.profile;

import com.sixtech.rider.base.MvpPresenter;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Part;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ProfileIPresenter<V extends ProfileIView> extends MvpPresenter<V> {
    void update(HashMap<String, RequestBody> obj, @Part MultipartBody.Part filename);

    void profile();
}
