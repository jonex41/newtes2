package com.sixtech.rider.ui.activity.help;

import com.sixtech.rider.base.BasePresenter;
import com.sixtech.rider.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HelpPresenter<V extends HelpIView> extends BasePresenter<V> implements HelpIPresenter<V> {

    @Override
    public void help() {
        getCompositeDisposable().add(APIClient.getAPIClient().help()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(help -> getMvpView().onSuccess(help),
                        throwable -> getMvpView().onError(throwable)));
    }
}
