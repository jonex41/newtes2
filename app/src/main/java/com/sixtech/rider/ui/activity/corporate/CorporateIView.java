package com.sixtech.rider.ui.activity.corporate;

import com.sixtech.rider.base.MvpView;
import com.sixtech.rider.data.network.model.Company;
import com.sixtech.rider.data.network.model.Message;
import com.sixtech.rider.data.network.model.User;

import java.util.List;

public interface CorporateIView extends MvpView {
    void onSuccess(Message object);
    void onSuccessCompanyList(List<Company> companies);
    void onSuccessUser(User user);
    void onError(Throwable e);
    void onErrorCorporate(Throwable e);
}
