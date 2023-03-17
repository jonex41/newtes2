package com.sixtech.rider.ui.fragment.invoice;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.Message;

public interface InvoiceIView extends MvpView{
    void onSuccess(Message message);
    void onSuccess(Object o);
    void onError(Throwable e);
}
