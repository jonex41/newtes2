package com.sixtech.rider.ui.activity.card;

import com.sixtech.rider.base.MvpPresenter;


public interface CarsIPresenter<V extends CardsIView> extends MvpPresenter<V> {
    void card();
}
