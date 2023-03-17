package com.sixtech.rider.ui.activity.wallet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseActivity;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.model.AddWallet;
import com.sixtech.rider.ui.activity.main.MainActivity;
import com.sixtech.rider.ui.activity.payment.PaymentActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sixtech.rider.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

//      TODO: Payment Gateway
//import com.braintreepayments.api.BraintreeFragment;
//import com.braintreepayments.api.PayPal;
//import com.braintreepayments.api.exceptions.InvalidArgumentException;
//import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
//import com.braintreepayments.api.models.PayPalAccountNonce;
//import com.braintreepayments.api.models.PayPalRequest;
//import com.braintreepayments.api.models.PaymentMethodNonce;

public class WalletActivity extends BaseActivity implements WalletIView
//        , PaymentMethodNonceCreatedListener
{

    @BindView(R.id.wallet_balance)
    TextView walletBalance;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id._199)
    Button _199;
    @BindView(R.id._599)
    Button _599;
    @BindView(R.id._1099)
    Button _1099;
    @BindView(R.id.add_amount)
    Button addAmount;
    @BindView(R.id.cvAddMoneyContainer)
    CardView cvAddMoneyContainer;

    private WalletPresenter<WalletActivity> presenter = new WalletPresenter<>();

    String regexNumber = "^(\\d{0,9}\\.\\d{1,4}|\\d{1,9})$";
    //      TODO: Payment Gateway
//    BraintreeFragment mBraintreeFragment;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView() {

        ButterKnife.bind(this);
        presenter.attachView(this);
        // Activity title will be updated after the locale has changed in Runtime
        setTitle(getString(R.string.wallet));

        _199.setText(SharedHelper.getKey(this, "currency") + " " + getString(R.string._199));
        _599.setText(SharedHelper.getKey(this, "currency") + " " + getString(R.string._599));
        _1099.setText(SharedHelper.getKey(this, "currency") + " " + getString(R.string._1099));
        amount.setTag(SharedHelper.getKey(this, "currency"));

        walletBalance.setText(String.format("%s %s", SharedHelper.getKey(this, "currency"),
                getNewNumberFormat(Double.parseDouble(SharedHelper.getKey(this, "walletBalance", "0")))));

        if (!BaseActivity.isCard) {
            cvAddMoneyContainer.setVisibility(View.GONE);
            addAmount.setVisibility(View.GONE);
        }

        //      TODO: Payment Gateway

//        try {
//            mBraintreeFragment = BraintreeFragment.newInstance(activity(), BuildConfig.PAYPAL_CLIENT_TOKEN);
//        } catch (InvalidArgumentException e) {
//            Toast.makeText(activity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick({R.id._199, R.id._599, R.id._1099, R.id.add_amount})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id._199:
                amount.setText(getString(R.string._199));
                break;
            case R.id._599:
                amount.setText(getString(R.string._599));
                break;
            case R.id._1099:
                amount.setText(getString(R.string._1099));
                break;
            case R.id.add_amount:
                if (!amount.getText().toString().trim().matches(regexNumber)) {
                    Toast.makeText(activity(), getString(R.string.invalid_amount), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(activity(), PaymentActivity.class);
                intent.putExtra("hideCash", true);
                startActivityForResult(intent, PICK_PAYMENT_METHOD);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK && data != null)
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                HashMap<String, Object> map = new HashMap<>();
                String cardId = data.getStringExtra("card_id");
                map.put("amount", amount.getText().toString());
                map.put("card_id", cardId);
                showLoading();
                presenter.addMoney(map);
            } else if (data.getStringExtra("payment_mode").equals("PAYPAL")) {
                //      TODO: Payment Gateway

//                PayPalRequest request = new PayPalRequest(amount.getText().toString())
//                        .currencyCode(SharedHelper.getKey(activity(), "currency_code"))
//                        .intent(PayPalRequest.INTENT_AUTHORIZE);
//                PayPal.requestOneTimePayment(mBraintreeFragment, request);
            }
    }

    @Override
    public void onSuccess(AddWallet wallet) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
//        Toast.makeText(this, wallet.getMessage(), Toast.LENGTH_SHORT).show();
        amount.setText("");

        SharedHelper.putKey(this, "walletBalance", String.valueOf(wallet.getBalance()));
        walletBalance.setText(String.format("%s %s", SharedHelper.getKey(this, "currency"),
               getNewNumberFormat(Double.parseDouble(SharedHelper.getKey(this, "walletBalance", "0")))));
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    //      TODO: Payment Gateway
//    @Override
//    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
//        String nonce = paymentMethodNonce.getNonce();
//        Log.d("PayPal", "onPaymentMethodNonceCreated " + nonce);
//        if (paymentMethodNonce instanceof PayPalAccountNonce) {
//            PayPalAccountNonce payPalAccountNonce = (PayPalAccountNonce) paymentMethodNonce;
//            String email = payPalAccountNonce.getEmail();
//        }
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(WalletActivity.this, MainActivity.class));
        finish();
    }
}
