package com.sixtech.rider.ui.fragment.book_ride;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.Message;
import com.sixtech.rider.data.network.model.PromoResponse;


public interface BookRideIView extends MvpView{
    void onSuccess(Message object);
    void onError(Throwable e);
    void onSuccessCoupon(PromoResponse promoResponse);
}
