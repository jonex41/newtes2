package com.sixtech.rider.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sixtech.rider.R;
import com.sixtech.rider.common.Utilities;
import com.sixtech.rider.data.network.model.Token;

import java.util.Calendar;
import java.util.Locale;

import static com.sixtech.rider.base.BaseActivity.RIDE_REQUEST;
import static com.sixtech.rider.common.CompatUtils.getTypeFace;
import static com.sixtech.rider.common.CompatUtils.getTypefaceSpan;

public abstract class BaseFragment extends Fragment implements MvpView {

    View view;
    private BaseActivity baseActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(getLayoutId(), container, false);

            initView(view);

        }

        return view;
    }

    public abstract int getLayoutId();

    public abstract View initView(View view);

    @Override
    public FragmentActivity activity() {
        return getActivity();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission, Activity activity) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void showLoading() {
        try {
            ((BaseActivity) getActivity()).showLoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hideLoading() throws Exception {
        ((BaseActivity) getActivity()).hideLoading();
    }

    public String printJSON(Object o) {
        return ((BaseActivity) getActivity()).printJSON(o);
    }

    public void datePicker(DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(activity(), dateSetListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    public void timePicker(TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar myCalendar = Calendar.getInstance();
        TimePickerDialog mTimePicker = new TimePickerDialog(getContext(), timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        mTimePicker.show();
    }


    public double getNumber(double value) {
        long factor = (long) Math.pow(value, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void hideKeyboard() {
        if (baseActivity != null) baseActivity.hideKeyboard();
    }

    public void showKeyboard() {
        if (baseActivity != null) baseActivity.showKeyboard();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            this.baseActivity = (BaseActivity) context;
        }
    }

    public void initPayment(TextView paymentMode) {
        if (RIDE_REQUEST.containsKey("payment_mode"))
            switch (RIDE_REQUEST.get("payment_mode").toString()) {
                case Utilities.PaymentMode.cash:
                    paymentMode.setText(getString(R.string.cash));
                    //    paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
                    break;
                case Utilities.PaymentMode.card:
                    if (RIDE_REQUEST.containsKey("card_last_four"))
                        paymentMode.setText(getString(R.string.card_) + RIDE_REQUEST.get("card_last_four"));
                    else paymentMode.setText(getString(R.string.add_card_));
                      paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card, 0, 0, 0);
                    break;
                case Utilities.PaymentMode.payPal:
                    paymentMode.setText(getString(R.string.paypal));
                    //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_paypal, 0, 0, 0);
                    break;
                case Utilities.PaymentMode.wallet:
                    paymentMode.setText(getString(R.string.wallet));
                    //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
                    break;
                case Utilities.PaymentMode.corporate:
                    paymentMode.setText(getString(R.string.corperate));
                    //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
                    break;
            }
        else {
            if (BaseActivity.isCash) {
                RIDE_REQUEST.put("payment_mode", Utilities.PaymentMode.cash);
                paymentMode.setText(getString(R.string.cash));
                //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
            } else if (BaseActivity.isCard) {
                paymentMode.setText(R.string.add_card_);
                //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card, 0, 0, 0);
                RIDE_REQUEST.put("payment_mode", Utilities.PaymentMode.card);
            }
        }
    }

    public void onErrorBase(Throwable t) {
        ((BaseActivity) getActivity()).onErrorBase(t);
    }

    public void handleError(Throwable e) {
        try {
            try {
                hideLoading();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            ((BaseActivity) getActivity()).handleError(e);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        //((BaseActivity) getActivity()).handleError(e);
    }

    @Override
    public void onSuccessRefreshToken(Token token) {
        ((BaseActivity) getActivity()).onSuccessRefreshToken(token);
    }

    @Override
    public void onErrorRefreshToken(Throwable throwable) {
        ((BaseActivity) getActivity()).onErrorRefreshToken(throwable);
    }

    @Override
    public void onSuccessLogout(Object object) {
        ((BaseActivity) getActivity()).onSuccessLogout(object);
    }

    @Override
    public void onError(Throwable throwable) {
        ((BaseActivity) getActivity()).onError(throwable);
    }

    public String getNewNumberFormat(double d) {
        return ((BaseActivity) getActivity()).getNewNumberFormat(d);
    }

    protected String getDistanceInMiless(double distanceInKm) {
        return String.format(Locale.getDefault(), "%.1f", 0.621371f * distanceInKm);
    }

    protected SpannableStringBuilder getTravelTimeInSpannable(String label, String value) throws Exception {
        SpannableStringBuilder travelTimeSpan1 = new SpannableStringBuilder();
        SpannableStringBuilder travelTimeSpan2 = new SpannableStringBuilder();

        Typeface tf500 = getTypeFace(getActivity(), R.font.gilroy_medium);
        TypefaceSpan tfs500 = getTypefaceSpan (tf500);

        travelTimeSpan1.append(label);
        travelTimeSpan1.setSpan(tfs500, 0, label.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        Typeface tf700 = getTypeFace(getActivity(), R.font.gilroy_bold);
        TypefaceSpan tfs700 = getTypefaceSpan (tf700);

        travelTimeSpan2.append(value);
        travelTimeSpan2.setSpan(tfs700, 0, value.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(travelTimeSpan1).append(" ").append(travelTimeSpan2);
        return sb;
    }

}
