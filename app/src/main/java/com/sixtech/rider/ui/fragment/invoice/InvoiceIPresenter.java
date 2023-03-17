package com.sixtech.rider.ui.fragment.invoice;

import com.sixtech.rider.base.MvpPresenter;
import java.util.HashMap;

public interface InvoiceIPresenter<V extends InvoiceIView> extends MvpPresenter<V> {
    void payment(Integer requestId, Double tips);
    void updateRide(HashMap<String, Object> obj);
}
