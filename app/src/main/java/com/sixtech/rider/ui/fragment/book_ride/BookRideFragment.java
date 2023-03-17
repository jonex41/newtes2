package com.sixtech.rider.ui.fragment.book_ride;

import static com.sixtech.rider.base.BaseActivity.DATUM;
import static com.sixtech.rider.base.BaseActivity.RIDE_REQUEST;
import static com.sixtech.rider.base.BaseActivity.isCard;
import static com.sixtech.rider.base.BaseActivity.isCash;
import static com.sixtech.rider.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chaos.view.PinView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;
import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseFragment;
import com.sixtech.rider.common.EqualSpacingItemDecoration;
import com.sixtech.rider.common.Utilities;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.APIClient;
import com.sixtech.rider.data.network.model.EstimateFare;
import com.sixtech.rider.data.network.model.Message;
import com.sixtech.rider.data.network.model.PromoList;
import com.sixtech.rider.data.network.model.PromoResponse;
import com.sixtech.rider.data.network.model.Service;
import com.sixtech.rider.ui.activity.main.MainActivity;
import com.sixtech.rider.ui.activity.payment.PaymentActivity;
import com.sixtech.rider.ui.adapter.CouponAdapter;
import com.sixtech.rider.ui.fragment.schedule.ScheduleFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRideFragment extends BaseFragment implements BookRideIView {

    Unbinder unbinder;
    @BindView(R.id.schedule_ride)
    Button scheduleRide;
    @BindView(R.id.ride_now)
    Button rideNow;
    @BindView(R.id.tvEstimatedFare)
    TextView tvEstimatedFare;
    /* @BindView(R.id.use_wallet)
     CheckBox useWallet;*/
    @BindView(R.id.estimated_image)
    ImageView estimatedImage;
    @BindView(R.id.tv_change)
    TextView tvChange;
    @BindView(R.id.round_trip)
    Button roundtrip;
    @BindView(R.id.view_coupons)
    TextView viewCoupons;
    @BindView(R.id.estimated_payment_mode)
    TextView estimatedPaymentMode;
    //    @BindView(R.id.wallet_balance)
//    TextView walletBalance;
    @BindView(R.id.service_name)
    TextView servicename;
    /*@BindView(R.id.wallet_balancezero)
    TextView balancezero;*/
    private int lastSelectCoupon = 0;
    private String mCouponStatus;
    private String paymentMode = "";
    private Double estimatedFare, round_trip_estimatedFare;
    private int round_trip = 0;
    private int waitingTime = 0;
    private int waitingtime = 0;
    AlertDialog otpDialog, confirm_fare_dialog;
    private BookRidePresenter<BookRideFragment> presenter = new BookRidePresenter<>();
    HashMap<String, Object> updateRideMap;
    HashMap<String, Object> estimateFareMap;

    public BookRideFragment() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_book_ride;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        Bundle args = getArguments();
        if (args != null) {
            String serviceName = args.getString("service_name");
            Service service = (Service) args.getSerializable("mService");
            EstimateFare estimateFare = (EstimateFare) args.getSerializable("estimate_fare");
            double walletAmount = Objects.requireNonNull(estimateFare).getWalletBalance();
            if (serviceName != null && !serviceName.isEmpty()) {
                Glide
                        .with(Objects.requireNonNull(getContext()))
                        .load(Objects.requireNonNull(service).getImage())
                        .apply(RequestOptions
                                .placeholderOf(R.drawable.ic_car)
                                .dontAnimate()
                                .override(100, 100)
                                .error(R.drawable.ic_car))
                        .into(estimatedImage);
                servicename.setText(serviceName);
                estimatedFare = estimateFare.getEstimatedFare();
                //round_trip_estimatedFare = estimateFare.getEstimatedFare() * 2;
                tvEstimatedFare.setText(SharedHelper.getKey(getContext(), "currency") + " " +
                        getNewNumberFormat(estimatedFare));
//                        +" - " +
//                        getNewNumberFormat(estimatedFare+3));

              /*  if (walletAmount == 0) {
                    balancezero.setVisibility(View.VISIBLE);
                    useWallet.setVisibility(View.GONE);
                    walletBalance.setVisibility(View.GONE);

                } else {
                    balancezero.setVisibility(View.GONE);
                    useWallet.setVisibility(View.VISIBLE);
                    walletBalance.setVisibility(View.VISIBLE);
                    walletBalance.setText(
                            SharedHelper.getKey(getContext(), "currency") + " "
                                    + getNewNumberFormat(Double.parseDouble(String.valueOf(walletAmount))));

                }*/
                RIDE_REQUEST.put("distance", estimateFare.getDistance());
            }
        }
       /* Animation logoMoveAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.logoanimation);
        viewCoupons.startAnimation(logoMoveAnimation);*/
        scaleView(viewCoupons, 0f, 0.9f);
        return view;
    }

    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(100);
        v.startAnimation(anim);
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    @OnClick({R.id.schedule_ride, R.id.ride_now, R.id.view_coupons, R.id.round_trip, R.id.tv_change})
    public void onViewClicked(View view) {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (view.getId()) {
            case R.id.schedule_ride:
                ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(new ScheduleFragment());
                break;
            case R.id.ride_now:
                SharedHelper.putKey(getContext(), "bookingTime", (int) SystemClock.elapsedRealtime());
                editor.remove("round_trip");
                editor.apply();
                if (paymentMode.equalsIgnoreCase(Utilities.PaymentMode.corporate))
                    showOTP();
                else
                    sendRequest(round_trip, waitingTime);
                break;
            case R.id.view_coupons:
                showLoading();
                try {
                    presenter.getCouponList();
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        hideLoading();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                break;
            case R.id.round_trip:
                try {
                    showWaitingDialog();

                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                break;
            case R.id.tv_change:
                ((MainActivity) Objects.requireNonNull(getActivity())).updatePaymentEntities();
                startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
                break;
        }
    }


    private void showWaitingDialog() {
        Bundle args = new Bundle();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        final Dialog dialog_waiting = new Dialog(getContext());
        dialog_waiting.setContentView(R.layout.dialog_waiting);
        TextView tv_confirm = dialog_waiting.findViewById(R.id.tv_Confirm);
        TextView tv_0min = dialog_waiting.findViewById(R.id.tv_0min);
        TextView tv_5min = dialog_waiting.findViewById(R.id.tv_5min);
        TextView tv_10min = dialog_waiting.findViewById(R.id.tv_10min);
        TextView tv_15min = dialog_waiting.findViewById(R.id.tv_15min);
        TextView tv_20min = dialog_waiting.findViewById(R.id.tv_20min);
        tv_0min.setOnClickListener(v -> {
            waitingtime = 0;
            Log.e("waiting time", "Waiting time __________________" + waitingtime);
            tv_0min.setSelected(true);
            tv_5min.setSelected(false);
            tv_10min.setSelected(false);
            tv_15min.setSelected(false);
            tv_20min.setSelected(false);
        });
        tv_5min.setOnClickListener(v -> {
            waitingtime = 5;
            Log.e("waiting time", "Waiting time __________________" + waitingtime);
            tv_0min.setSelected(false);
            tv_5min.setSelected(true);
            tv_10min.setSelected(false);
            tv_15min.setSelected(false);
            tv_20min.setSelected(false);
        });
        tv_10min.setOnClickListener(v -> {
            waitingtime = 10;
            Log.e("waiting time", "Waiting time __________________" + waitingtime);
            tv_0min.setSelected(false);
            tv_10min.setSelected(true);
            tv_5min.setSelected(false);
            tv_15min.setSelected(false);
            tv_20min.setSelected(false);
        });
        tv_15min.setOnClickListener(v -> {
            waitingtime = 15;
            Log.e("waiting time", "Waiting time __________________" + waitingtime);
            tv_0min.setSelected(false);
            tv_15min.setSelected(true);
            tv_5min.setSelected(false);
            tv_10min.setSelected(false);
            tv_20min.setSelected(false);
        });
        tv_20min.setOnClickListener(v -> {
            waitingtime = 20;
            Log.e("waiting time", "Waiting time __________________" + waitingtime);
            tv_0min.setSelected(false);
            tv_20min.setSelected(true);
            tv_5min.setSelected(false);
            tv_10min.setSelected(false);
            tv_15min.setSelected(false);
        });
        tv_confirm.setOnClickListener(v -> {
            waitingTime = waitingtime;
            round_trip = 1;
            estimateFareMap = new HashMap<>(RIDE_REQUEST);
            estimateFareMap.put("round_trip", round_trip);
            estimateFareMap.put("waiting_minutes", waitingTime);
            estimatedApiCall();
            /*Log.d("checking", "No round trip is set");
            if (DATUM != null) {
                Toast.makeText(getActivity(), "Yes set the values", Toast.LENGTH_SHORT).show();
                Log.d("checking", "Yes round trip is set");

            }*/
            //showEstimatedDialog(round_trip_estimatedFare);
            dialog_waiting.dismiss();
            showLoading();
        });
        dialog_waiting.show();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) WindowManager.LayoutParams.WRAP_CONTENT;

        dialog_waiting.getWindow().setLayout(width, height);
    }

    private void showEstimatedDialog(double fare) {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_confirm_fare);
        TextView tvEstimatedFare = dialog.findViewById(R.id.tvEstimatedFare);

        tvEstimatedFare.setText(SharedHelper.getKey(getContext(), "currency")
                + ". " + Double.valueOf(Double.valueOf(fare)).doubleValue());
//                + SharedHelper.getKey(getContext(), "currency")
//                + Double.valueOf(Double.valueOf(fare) + 3).intValue());
        TextView tvConfirm = dialog.findViewById(R.id.tvConfirm);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        tvConfirm.setOnClickListener(f -> {
            SharedHelper.putKey(getContext(), "bookingTime", (int) SystemClock.elapsedRealtime());
            if (paymentMode.equalsIgnoreCase(Utilities.PaymentMode.corporate))
                showOTP();
            else if (round_trip == 0) {
                round_trip = 1;
                editor.putInt("round_trip", round_trip);
                editor.apply();
                Log.e("RoundTrip", "RoundTrip is________________________________________________________________ " + round_trip);
                Log.e("waiting time ", "waiting time is________________________________________________________________ " + waitingTime);
                sendRequest(round_trip, waitingTime);


            } else {
                editor.putInt("round_trip", round_trip);
                editor.apply();
                sendRequest(round_trip, waitingTime);
            }


            dialog.dismiss();
        });

        tvCancel.setOnClickListener(f -> {

            dialog.dismiss();
        });

        dialog.show();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);
    }

    private void showOTP() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.otp_dialog, null);

        Button submitBtn = view.findViewById(R.id.submit_btn);
        final PinView pinView = view.findViewById(R.id.pinView);
        builder.setView(view);
        otpDialog = builder.create();
        otpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        submitBtn.setOnClickListener(view1 -> {
            Log.e("BookRide", "showOTP: " + SharedHelper.getKey(getContext(), "corporate_otp"));
            if (SharedHelper.getKey(getContext(), "corporate_otp").trim().equals(pinView.getText().toString())) {
                try {
                    if (getContext() != null)
                        Toasty.success(getContext(), getContext().getResources().getString(R.string.pin_verified), Toast.LENGTH_SHORT).show();
                    sendRequest(round_trip, waitingTime);
                    otpDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else try {
                if (getContext() != null && isAdded())
                    Toasty.error(getContext(), getContext().getResources().getString(R.string.otp_wrong), Toast.LENGTH_SHORT).show();
                else
                    Toasty.error(getContext(), getContext().getResources().getString(R.string.otp_wrong), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        otpDialog.show();
    }


    private Dialog couponDialog(PromoResponse promoResponse) {
        BottomSheetDialog couponDialog = new BottomSheetDialog(Objects.requireNonNull(getContext()), R.style.SheetDialog);
        couponDialog.setCanceledOnTouchOutside(true);
        couponDialog.setCancelable(true);
        couponDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        couponDialog.setContentView(R.layout.activity_coupon_dialog);
        RecyclerView couponView = couponDialog.findViewById(R.id.coupon_rv);
        IndefinitePagerIndicator indicator = couponDialog.findViewById(R.id.recyclerview_pager_indicator);
        List<PromoList> couponList = promoResponse.getPromoList();
        if (couponList != null && !couponList.isEmpty()) {
            CouponAdapter couponAdapter = new CouponAdapter(getActivity(), couponList,
                    mCouponListener, couponDialog, lastSelectCoupon, mCouponStatus);
            assert couponView != null;
            couponView.setLayoutManager(new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false));
            couponView.setItemAnimator(new DefaultItemAnimator());
            couponView.addItemDecoration(new EqualSpacingItemDecoration(16,
                    EqualSpacingItemDecoration.HORIZONTAL));
            Objects.requireNonNull(indicator).attachToRecyclerView(couponView);
            couponView.setAdapter(couponAdapter);
            couponAdapter.notifyDataSetChanged();
        }
        couponDialog.setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                new BottomSheetDialog(getContext()).dismiss();
                Log.d("TAG", "--------- Do Something -----------");
                return true;
            }
            return false;
        });
        Window window = couponDialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams param = window.getAttributes();
        param.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(param);
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        couponDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        return couponDialog;
    }

    public void sendRequest(int roundtrip, int waitingtime) {
        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
//        map.put("use_wallet", useWallet.isChecked() ? 1 : 0);
        map.put("promocode_id", lastSelectCoupon);
        map.put("is_round", roundtrip);
        Log.e("waiting time", "Waiting time __________________" + waitingtime);
        if (roundtrip == 0 && waitingtime == 0) {
            map.put("estimated_fare", getNewNumberFormat(estimatedFare));
            map.put("offer_ammount", getNewNumberFormat(estimatedFare));
            DATUM.setOffer_price(Double.valueOf(getNewNumberFormat(estimatedFare)));
        } else {
            DATUM.setOffer_price(Double.valueOf(getNewNumberFormat(estimatedFare)));
            map.put("offer_ammount", getNewNumberFormat(estimatedFare));
            map.put("waiting_minutes", waitingtime);
            map.put("estimated_fare", getNewNumberFormat(round_trip_estimatedFare));

        }
        if (paymentMode != null && !paymentMode.equalsIgnoreCase("")) {
            if (paymentMode.equalsIgnoreCase(Utilities.PaymentMode.corporate))
                map.put("payment_mode", "CORPORATE_ACCOUNT");
            else if (paymentMode.equalsIgnoreCase(Utilities.PaymentMode.cash))
                map.put("payment_mode", "CASH");
            else
                map.put("payment_mode", "CARD");
        } else {
            map.put("payment_mode", "CASH");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            map.entrySet().forEach(stringObjectEntry ->
                    Log.e("key is", stringObjectEntry.getKey())
                    );
        }
        showLoading();
        try {
            presenter.rideNow(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /* public void sendRequest2() {
         HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
         map.put("use_wallet", useWallet.isChecked() ? 1 : 0);
         map.put("promocode_id", lastSelectCoupon);
         map.put("estimated_fare", getNewNumberFormat(round_trip_estimatedFare));
         map.put("round_trip", )
         if (paymentMode != null && !paymentMode.equalsIgnoreCase("")) {
             if (paymentMode.equalsIgnoreCase(Utilities.PaymentMode.corporate))
                 map.put("payment_mode", "CORPORATE_ACCOUNT");
             else if (paymentMode.equalsIgnoreCase(Utilities.PaymentMode.cash))
                 map.put("payment_mode", "CASH");
             else
                 map.put("payment_mode", "CARD");
         } else {
             map.put("payment_mode", "CASH");
         }
         showLoading();
         try {
             presenter.rideNow(map);
         } catch (Exception e) {
             e.printStackTrace();
         }

     }*/
    @Override
    public void onSuccess(@NonNull Message object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        activity().sendBroadcast(new Intent("F"));
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onSuccessCoupon(PromoResponse promoResponse) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (promoResponse != null && promoResponse.getPromoList() != null
                && !promoResponse.getPromoList().isEmpty()) couponDialog(promoResponse).show();
        else Toast.makeText(activity(), "Coupon is empty", Toast.LENGTH_SHORT).show();
    }

    public interface CouponListener {
        void couponClicked(int pos, PromoList promoList, String promoStatus);
    }

    private CouponListener mCouponListener = new CouponListener() {
        @Override
        public void couponClicked(int pos, PromoList promoList, String promoStatus) {
            if (!promoStatus.equalsIgnoreCase(getString(R.string.remove))) {
                lastSelectCoupon = promoList.getId();
                viewCoupons.setText(promoList.getPromoCode());
                viewCoupons.setTextColor(getResources().getColor(R.color.colorAccent));
                viewCoupons.setBackgroundResource(R.drawable.coupon_transparent);
                mCouponStatus = viewCoupons.getText().toString();
                Double discountFare = (estimatedFare * promoList.getPercentage()) / 100;

                if (discountFare > promoList.getMaxAmount()) {
                    tvEstimatedFare.setText(String.format("%s %s",
                            SharedHelper.getKey(requireContext(), "currency"),
                            getNewNumberFormat(estimatedFare - promoList.getMaxAmount())));
                } else {
                    tvEstimatedFare.setText(String.format("%s %s",
                            SharedHelper.getKey(requireContext(), "currency"),
                            getNewNumberFormat(estimatedFare - discountFare)));
                }
            } else {
                scaleView(viewCoupons, 0f, 0.9f);
                viewCoupons.setText(getString(R.string.view_coupon));
                viewCoupons.setBackgroundResource(R.drawable.button_round_accent);
                viewCoupons.setTextColor(getResources().getColor(R.color.colorPrimaryText));
                mCouponStatus = viewCoupons.getText().toString();
                tvEstimatedFare.setText(String.format("%s %s",
                        SharedHelper.getKey(requireContext(), "currency"),
                        getNewNumberFormat(estimatedFare)));
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put("payment_mode", data.getStringExtra("payment_mode"));
            paymentMode = data.getStringExtra("payment_mode");
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                RIDE_REQUEST.put("card_id", data.getStringExtra("card_id"));
                RIDE_REQUEST.put("card_last_four", data.getStringExtra("card_last_four"));
            }
            initPayment(estimatedPaymentMode);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        initPayment(estimatedPaymentMode);
        tvChange.setVisibility((!isCard && isCash) ? View.GONE : View.VISIBLE);
    }

    private void estimatedApiCall() {
        Call<EstimateFare> call = APIClient.getAPIClient().estimateFare(estimateFareMap);
        call.enqueue(new Callback<EstimateFare>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EstimateFare> call,
                                   @NonNull Response<EstimateFare> response) {
                try {
                    hideLoading();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (response.body() != null) {
                    EstimateFare estimateFare = response.body();
                    showEstimatedDialog(estimateFare.getEstimatedFare());
                    round_trip_estimatedFare = estimateFare.getEstimatedFare();
                    Log.e("tag", "Response " + response.body());
                } else if (response.raw().code() == 500) {
                    Log.e("tag", "error code is 500 : ");

                    try {
                        JSONObject object = new JSONObject(response.errorBody().string());
                        if (object.has("error"))
                            Toast.makeText(activity(), object.optString("error"), Toast.LENGTH_SHORT).show();

                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<EstimateFare> call, @NonNull Throwable t) {
                onErrorBase(t);
                System.out.println("RRR call = [" + call + "], t = [" + t + "]");
                Log.e("tag", "RRR call = [" + call + "], t = [" + t + "]");
            }
        });
    }

    /*private void estimatedApiCall() {
//        RIDE_REQUEST.put("rate_type", "Flat");
        Call<EstimateFare> call = APIClient.getAPIClient().estimateFare(RIDE_REQUEST);
        call.enqueue(new Callback<EstimateFare>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EstimateFare> call, @NonNull Response<EstimateFare> response) {
                try {
                    hideLoading();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (response.body() != null) {
                    EstimateFare estimateFare = response.body();

                    Gson gson = new Gson();
                    String estimateFareString = gson.toJson(estimateFare);

                    Log.e("EstimateFare Response", "" + estimateFareString);

                    RateCardFragment.SERVICE = estimateFare.getService();
                    mEstimateFare = estimateFare;
                    surge = estimateFare.getSurge();
                    walletAmount = estimateFare.getWalletBalance();
                    SharedHelper.putKey(getActivity(), "wallet", String.valueOf(estimateFare.getWalletBalance()));
                    if (walletAmount == 0) walletBalance.setVisibility(View.GONE);
                    else {
                        walletBalance.setVisibility(View.VISIBLE);
                        walletBalance.setText(SharedHelper.getKey(getContext(), "currency") + " "
                                + getNewNumberFormat(Double.parseDouble(String.valueOf(walletAmount))));
                    }
                    if (surge == 0) {
                        surgeValue.setVisibility(View.GONE);
                        tvDemand.setVisibility(View.GONE);
                    } else {
                        surgeValue.setVisibility(View.VISIBLE);
                        surgeValue.setText(estimateFare.getSurgeValue());
                        tvDemand.setVisibility(View.VISIBLE);
                    }
                    if (isFromAdapter) {
                        mServices.get(servicePos).setEstimatedTime(estimateFare.getTime());
                        RIDE_REQUEST.put("distance", estimateFare.getDistance());
                        adapter.setEstimateFare(mEstimateFare);
                        adapter.notifyDataSetChanged();
                        if (mServices.isEmpty()) errorLayout.setVisibility(View.VISIBLE);
                        else errorLayout.setVisibility(View.GONE);
                    } else {
                        if (adapter != null) {
                            isFromAdapter = false;
                            Service service = adapter.getSelectedService();
                            if (service != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("service_name", service.getName());
                                bundle.putSerializable("mService", service);
                                bundle.putSerializable("estimate_fare", estimateFare);
//                                bundle.putInt("is_round",);
                                bundle.putDouble("use_wallet", walletAmount);
                                BookRideFragment bookRideFragment = new BookRideFragment();
                                bookRideFragment.setArguments(bundle);
                                ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(bookRideFragment);
                            }
                        }
                    }
                } else if (response.raw().code() == 500) {
                    Log.e("tag", "error code is 500 : ");

                    try {
                        JSONObject object = new JSONObject(response.errorBody().string());
                        if (object.has("error"))
                            Toast.makeText(activity(), object.optString("error"), Toast.LENGTH_SHORT).show();

                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<EstimateFare> call, @NonNull Throwable t) {
//                try {
//                    hideLoading();
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
                onErrorBase(t);
                System.out.println("RRR call = [" + call + "], t = [" + t + "]");
                Log.e("tag", "RRR call = [" + call + "], t = [" + t + "]");
            }
        });
    }

    private void sendRequest() {
        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("use_wallet", useWallet.isChecked() ? 1 : 0);
        showLoading();
        presenter.rideNow(map);
    }*/
}
