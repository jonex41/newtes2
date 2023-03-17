package com.sixtech.rider.ui.activity.coupon;

import com.sixtech.rider.base.MvpPresenter;

public interface CouponIPresenter<V extends CouponIView> extends MvpPresenter<V> {
    void coupon();
}
