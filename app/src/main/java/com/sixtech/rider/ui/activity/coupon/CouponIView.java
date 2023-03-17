package com.sixtech.rider.ui.activity.coupon;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.PromoResponse;

public interface CouponIView extends MvpView {
    void onSuccess(PromoResponse object);
    void onError(Throwable e);
}
