package com.sixtech.rider.ui.activity.corporate;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.AppCompatSpinner;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseActivity;
import com.sixtech.rider.common.Utilities;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.model.Company;
import com.sixtech.rider.data.network.model.Message;
import com.sixtech.rider.data.network.model.User;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CorporateActivity extends BaseActivity implements CorporateIView, AdapterView.OnItemSelectedListener {

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.first_name)
    EditText firstName;

    @BindView(R.id.last_name)
    EditText lastName;

    @BindView(R.id.company_name)
    AppCompatSpinner companyNameSpinner;

    @BindView(R.id.company_id)
    EditText companyID;

    @BindView(R.id.employee_id)
    EditText employeeID;

    @BindView(R.id.phone_number)
    EditText phoneNumber;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.next)
    FloatingActionButton btnNext;

    @BindView(R.id.mobileContainer)
    LinearLayout mobileContainer;

    @BindView(R.id.passwordContainer)
    LinearLayout passwordContainer;

    private List<Company> companyList;
    private Company selectedCompany;

    private CorporatePresenter corperatePresenter = new CorporatePresenter();

    @Override
    public int getLayoutId() {
        return R.layout.activity_corperate;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        corperatePresenter.attachView(this);
        setTitle(getString(R.string.corperate));
        corperatePresenter.getCompanyList();
        if (SharedHelper.getKey(this, "corperate_user").equalsIgnoreCase("1")) {
            passwordContainer.setVisibility(View.GONE);
            mobileContainer.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
        } else {
            passwordContainer.setVisibility(View.VISIBLE);
            mobileContainer.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    private void setView() {
        ArrayAdapter<Company> dataAdapter = new ArrayAdapter<Company>(this,
                android.R.layout.simple_spinner_item, companyList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companyNameSpinner.setAdapter(dataAdapter);
        companyNameSpinner.setOnItemSelectedListener(this);
    }

    @OnClick({R.id.next})
    void onClick(View view) {
        if (view.getId() == R.id.next) {
            if (validation()) {
                postCorperateUser();
            }
        }
    }

    private void postCorperateUser() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("first_name", firstName.getText().toString());
        map.put("last_name", lastName.getText().toString());
        map.put("email", email.getText().toString());
        map.put("cpassword", password.getText().toString());
        map.put("company_name", selectedCompany.getCompanyName());
        map.put("company_id", selectedCompany.getId());
        map.put("empid", employeeID.getText().toString());
        map.put("cmobile",phoneNumber.getText().toString());
        map.put("dial_code", SharedHelper.getKey(this, "dial_code"));
        showLoading();
        corperatePresenter.postCorperateUser(map);
    }

    private boolean validation() {
        return Utilities.isEmpty(email, getString(R.string.enter_your_email))
                && Utilities.isEmpty(firstName, getString(R.string.enter_your_fname))
                && Utilities.isEmpty(lastName, getString(R.string.enter_your_lname))
                && Utilities.isEmpty(employeeID, getString(R.string.enter_your_employeeID))
                && Utilities.isEmpty(password, getString(R.string.enter_your_password));
    }

    @Override
    public void onSuccess(Message object) {
        try {
            hideLoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, object.getMessage(), Toast.LENGTH_SHORT).show();
        corperatePresenter.profile();
    }

    @Override
    public void onSuccessCompanyList(List<Company> response) {
        companyList = response;
        corperatePresenter.profile();
        setView();
    }

    @Override
    public void onSuccessUser(User user) {
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        email.setText(user.getEmail());
        SharedHelper.putKey(this, "corporate_otp", user.getCorporatePin()+"");
        if (user.getCorpDeleted() == 1) {
            SharedHelper.putKey(this, "corperate_user", "1");
            companyID.setText(user.getCompanyID() + "");
            employeeID.setText(user.getEmpID());
            passwordContainer.setVisibility(View.GONE);
            mobileContainer.setVisibility(View.GONE);
            for (int i =0;i<companyList.size();i++){
                if (companyList.get(i).getId().equals(user.getCompanyID())) {
                    companyNameSpinner.setSelection(i);
                }
            }
            firstName.setEnabled(false);
            lastName.setEnabled(false);
            email.setEnabled(false);
            employeeID.setEnabled(false);
            companyNameSpinner.setEnabled(false);
            btnNext.setVisibility(View.GONE);
        } else {
            SharedHelper.putKey(this, "corporate_user", "0");
            firstName.setEnabled(true);
            lastName.setEnabled(true);
            email.setEnabled(true);
            employeeID.setEnabled(true);
            companyNameSpinner.setEnabled(true);
            passwordContainer.setVisibility(View.VISIBLE);
            mobileContainer.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onErrorCorporate(Throwable e) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        handleError(e);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedCompany = companyList.get(i);
        companyID.setText(selectedCompany.getId() + "");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
