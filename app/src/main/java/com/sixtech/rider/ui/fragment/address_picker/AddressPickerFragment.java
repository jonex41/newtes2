package com.sixtech.rider.ui.fragment.address_picker;

import static com.sixtech.rider.MvpApplication.DEFAULT_ZOOM;
import static com.sixtech.rider.MvpApplication.PICK_LOCATION_REQUEST_CODE;
import static com.sixtech.rider.base.BaseActivity.RIDE_REQUEST;
import static com.sixtech.rider.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.chaos.view.PinView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseActivity;
import com.sixtech.rider.base.BaseFragment;
import com.sixtech.rider.common.Utilities;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.APIClient;
import com.sixtech.rider.data.network.model.AddedStop;
import com.sixtech.rider.data.network.model.EstimateFare;
import com.sixtech.rider.data.network.model.Message;
import com.sixtech.rider.ui.activity.location_pick.LocationPickActivity;
import com.sixtech.rider.ui.activity.main.MainActivity;
import com.sixtech.rider.ui.activity.payment.PaymentActivity;
import com.sixtech.rider.ui.activity.wallet.WalletActivity;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressPickerFragment extends BaseFragment implements APIView {

    @BindView(R.id.sourceTv)
    public TextView sourceTxt;
    @BindView(R.id.destination)
    public TextView destinationTxt;

    @BindView(R.id.stop2_daddress)
    TextView stop2DAddress;
    @BindView(R.id.stop3_daddress)
    TextView stop3DAddress;
    @BindView(R.id.lblMoreStops)
    TextView lblMoreStops;
    @BindView(R.id.txtOfferFare)
    TextView lblOfferFare;
    @BindView(R.id.txtMiles)
    TextView txtMiles;
    @BindView(R.id.travelTimeInMinutes)
    TextView travelTimeInMinutes;

    @BindView(R.id.txtPaymentTypeCash)
    TextView txtPaymentTypeCash;

    @BindView(R.id.txtPaymentTypeCard)
    TextView txtPaymentTypeCard;

    @BindView(R.id.edtOfferFare)
    public AppCompatEditText edtOfferFare;

    @BindView(R.id.edtCommentForDriver)
    public AppCompatEditText edtCommentForDriver;


    @BindView(R.id.vertical_line2)
    LinearLayoutCompat stop2DLayout;
    @BindView(R.id.vertical_line3)
    LinearLayoutCompat stop3DLayout;

    @BindView(R.id.layoutTravelTime)
    public carbon.widget.ConstraintLayout layoutTravelTime;

    @BindView(R.id.layout_action_book_ride)
    public LinearLayoutCompat book_ride_actions;

    @BindView(R.id.imgStopPoint)
    ImageView Where;
    @BindView(R.id.imgStopPoint2)
    ImageView where2;
    @BindView(R.id.imgStopPoint3)
    ImageView where3;

    @BindView(R.id.pick_location_layout)
    ConstraintLayout pickLocationLayout;


    private String paymentMode = "";
    AlertDialog otpDialog;
    private int round_trip = 0;
    private int waitingTime = 0;
    private int waitingtime = 0;
    private int lastSelectCoupon = 0;
    private Double estimatedFare, round_trip_estimatedFare;
    private String travelTime="";

    HashMap<String, Object> estimateFareMap;

    private final APPresenter<AddressPickerFragment> presenter = new APPresenter<>();


    //private AddressPickPresenter<AddressPickerFragment> presenter = new AddressPickPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.pick_location_layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!RIDE_REQUEST.containsKey("card_id")) {

        }

    }

    @Override
    public View initView(View view) {
        ButterKnife.bind(this, view);
        presenter.attachView(this);

        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey("source_hint")) {
                sourceTxt.setHint(args.getString(args.getString("source_hint")));
            }
            else if (args.containsKey("latitude")) {
                String text = ((MainActivity) getActivity()).getAddress(new LatLng(args.getDouble("latitude"), args.getDouble("longitude")));
                RIDE_REQUEST.put("s_address", text);
                RIDE_REQUEST.put("s_latitude", args.getDouble("latitude"));
                RIDE_REQUEST.put("s_longitude", args.getDouble("longitude"));
                sourceTxt.setText(text);
                if (((MainActivity) getActivity()).mGoogleMap != null) {
                    ((MainActivity) getActivity()).mGoogleMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(new LatLng(
                                    args.getDouble("latitude"),
                                    args.getDouble("longitude")
                            ), DEFAULT_ZOOM));
                }
            }
        }
        return view;
    }

    @OnClick({R.id.sourceTv, R.id.destinationLabel, R.id.destination, R.id.txtAddStop,
            R.id.txtPaymentTypeCash, R.id.txtPaymentTypeCard,
            R.id.stop2_daddress, R.id.stop3_daddress, R.id.ride_now, R.id.round_trip})
    public void onViewClicked(View view) {

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) return;

        SharedPreferences sharedPreferences = mainActivity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int viewId = view.getId();
        if (viewId == R.id.sourceTv) {
            if ((int) mainActivity.amnt <= (int) mainActivity.userWalletLimit) {
                mainActivity.outstandingAmountDialog();
                Button ok = mainActivity.alertDialog.findViewById(R.id.btn_ok);
                ok.setOnClickListener(v -> {
                    startActivity(new Intent(mainActivity, WalletActivity.class));
                    mainActivity.finish();
                });

            } else {
                Intent sourceIntent = new Intent(mainActivity, LocationPickActivity.class);
                sourceIntent.putExtra("srcClick", "isSource");
                sourceIntent.putExtra("isSetting", "source");
                sourceIntent.putExtra("destination", sourceTxt.getText().toString());
                sourceIntent.putExtra("fieldClicked", "pickupAddress");
                mainActivity.startActivityForResult(sourceIntent, PICK_LOCATION_REQUEST_CODE);
            }

        }
        else if (viewId == R.id.destination || viewId == R.id.destinationLabel || viewId == R.id.txtAddStop) {
            if ((int) mainActivity.amnt <= (int) mainActivity.userWalletLimit) {
                mainActivity.outstandingAmountDialog();
                Button ok = mainActivity.alertDialog.findViewById(R.id.btn_ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(mainActivity, WalletActivity.class));
                        mainActivity.finish();

                    }
                });

            } else {
                Intent intent = new Intent(mainActivity, LocationPickActivity.class);
                intent.putExtra("destClick", "isDest");
                intent.putExtra("isSetting", "destination");
                intent.putExtra("destination", sourceTxt.getText().toString());
                intent.putExtra("fieldClicked", "dropAddress");
                mainActivity.startActivityForResult(intent, PICK_LOCATION_REQUEST_CODE);
            }
        }
        else if (viewId == R.id.stop2_daddress || viewId == R.id.stop3_daddress) {
//            case R.id.stop1_address_layout:
//            case R.id.stop2_address_layout:
//            case R.id.stop3_address_layout:
            Intent stopsIntent = new Intent(mainActivity, LocationPickActivity.class);
            stopsIntent.putExtra("destClick", "isDest");
            stopsIntent.putExtra("isSetting", "destination");
            stopsIntent.putExtra("destination", sourceTxt.getText().toString());
            stopsIntent.putExtra("fieldClicked", "dropAddress");
            mainActivity.startActivityForResult(stopsIntent, PICK_LOCATION_REQUEST_CODE);
        }
        else if (viewId == R.id.ride_now) {

            SharedHelper.putKey(getContext(), "bookingTime", (int) SystemClock.elapsedRealtime());
            editor.remove("round_trip");
            editor.apply();
            if (paymentMode.equalsIgnoreCase(Utilities.PaymentMode.corporate)) {
                showOTP();
            } else {
                sendRequest(round_trip, waitingTime);
            }
        }
        else if (viewId == R.id.layoutPaymentType) {

            mainActivity.updatePaymentEntities();
            startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
        }
        else if (viewId == R.id.round_trip) {
            try {
                showWaitingDialog();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        else if (viewId == R.id.txtPaymentTypeCash) {
            txtPaymentTypeCash.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.black));
            txtPaymentTypeCash.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
            txtPaymentTypeCard.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.CF0F0F0));
            txtPaymentTypeCard.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
            setTextViewDrawableColor(txtPaymentTypeCash, ContextCompat.getColor(getActivity(), android.R.color.white));
            setTextViewDrawableColor(txtPaymentTypeCard, ContextCompat.getColor(getActivity(), android.R.color.black));
            RIDE_REQUEST.put("payment_mode", "CASH");
        }
        else if (viewId == R.id.txtPaymentTypeCard) {
            setTextViewDrawableColor(txtPaymentTypeCash, ContextCompat.getColor(getActivity(), android.R.color.black));
            setTextViewDrawableColor(txtPaymentTypeCard, ContextCompat.getColor(getActivity(), android.R.color.white));

            txtPaymentTypeCard.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.black));
            txtPaymentTypeCard.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
            txtPaymentTypeCash.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.CF0F0F0));
            txtPaymentTypeCash.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
            if (!RIDE_REQUEST.containsKey("card_id")) {
                mainActivity.updatePaymentEntities();
                startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put("payment_mode", data.getStringExtra("payment_mode"));
            paymentMode = data.getStringExtra("payment_mode");
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                RIDE_REQUEST.put("card_id", data.getStringExtra("card_id"));
                RIDE_REQUEST.put("card_last_four", data.getStringExtra("card_last_four"));
                setTextViewDrawableColor(txtPaymentTypeCash, ContextCompat.getColor(getActivity(), android.R.color.black));
                setTextViewDrawableColor(txtPaymentTypeCard, ContextCompat.getColor(getActivity(), android.R.color.white));
                txtPaymentTypeCard.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.black));
                txtPaymentTypeCard.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
                txtPaymentTypeCash.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.CF0F0F0));
                txtPaymentTypeCash.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
            }
            else{
                txtPaymentTypeCash.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.black));
                txtPaymentTypeCash.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
                txtPaymentTypeCard.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.CF0F0F0));
                txtPaymentTypeCard.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
                setTextViewDrawableColor(txtPaymentTypeCash, ContextCompat.getColor(getActivity(), android.R.color.white));
                setTextViewDrawableColor(txtPaymentTypeCard, ContextCompat.getColor(getActivity(), android.R.color.black));
                RIDE_REQUEST.put("payment_mode", "CASH");
            }
            //initPayment(estimatedPaymentMode);
        }
        else{
            txtPaymentTypeCash.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.black));
            txtPaymentTypeCash.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
            txtPaymentTypeCard.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.CF0F0F0));
            txtPaymentTypeCard.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
            setTextViewDrawableColor(txtPaymentTypeCash, ContextCompat.getColor(getActivity(), android.R.color.white));
            setTextViewDrawableColor(txtPaymentTypeCard, ContextCompat.getColor(getActivity(), android.R.color.black));
            RIDE_REQUEST.put("payment_mode", "CASH");
        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public double getEstimatedFare (){
        return estimatedFare;
    }

    public String getTime (){
        return travelTime;
    }

    public void updateMultiDestinations(ArrayList<AddedStop> stopArrayList) {

        if (stopArrayList == null) {
            return;
        }

        hideMultiStops(View.GONE);

        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (int i = 0; i < stopArrayList.size(); i++) {
            sb.append(prefix).append(stopArrayList.get(i).getD_address());
            prefix = " - ";
            if (i == 0) {
                //destinationTxt.setText(stopArrayList.get(i).getD_address());
            } else if (i == 1) {
/*
                where2.setVisibility(View.VISIBLE);
                stop2DLayout.setVisibility(View.VISIBLE);
                stop2DAddress.setVisibility(View.VISIBLE);
                stop2DAddress.setText(stopArrayList.get(i).getD_address());
*/

            } else if (i == 2) {
/*
                where3.setVisibility(View.VISIBLE);
                stop3DLayout.setVisibility(View.VISIBLE);
                stop3DAddress.setVisibility(View.VISIBLE);
                stop3DAddress.setText(stopArrayList.get(i).getD_address());
*/

            }
        }
        destinationTxt.setText(sb.toString());
    }

    public void hideMultiStops(int gone) {
        stop2DAddress.setText("");
        stop3DAddress.setText("");
        stop2DAddress.setVisibility(gone);
        stop3DAddress.setVisibility(gone);
        stop2DLayout.setVisibility(gone);
        stop3DLayout.setVisibility(gone);
        where2.setVisibility(gone);
        where3.setVisibility(gone);
    }

    public void updateStopsInfo (int stops) {
        if (stops == 2) {
            lblMoreStops.setText("+1 More Stop");
            lblMoreStops.setVisibility(View.VISIBLE);
        }
        else if (stops == 3) {
            lblMoreStops.setText("+2 More Stops");
            lblMoreStops.setVisibility(View.VISIBLE);
        }
        else {
            lblMoreStops.setVisibility(View.GONE);
        }
    }

    public void updateFare(EstimateFare estimateFare) {
        if (getActivity() == null) {
            return;
        }
        estimatedFare = estimateFare.getEstimatedFare();
        lblOfferFare.setText(SharedHelper.getKey(getActivity(), "currency")+" ");
        edtOfferFare.setText(getNewNumberFormat(estimatedFare));

        String label = getString(R.string.travel_time_approx)+" ";
        travelTime = estimateFare.getTime();
        BaseActivity.travelTime = travelTime;
        try {
            travelTimeInMinutes.setText(getTravelTimeInSpannable(label, travelTime));
        } catch (Exception e1) {
            travelTimeInMinutes.setText(e1.getMessage());
        }
        travelTimeInMinutes.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        txtMiles.setText(estimateFare.getDistance()+"");

        book_ride_actions.setVisibility(View.VISIBLE);
        layoutTravelTime.setVisibility(View.VISIBLE);
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

    public void sendRequest(int roundtrip, int waitingtime) {

        if(roundtrip==1){
            edtOfferFare.setText(""+round_trip_estimatedFare);
        }

        String value = (edtOfferFare.getText().toString());
        if(value.isEmpty()){
            Toast.makeText(getContext(), "Enter valid offer!", Toast.LENGTH_SHORT).show();
            return;
        }
        double price = Double.parseDouble(value);
        if(roundtrip == 0)
        {
            String n = new DecimalFormat("##.00").format(estimatedFare-1);
            if(price<(estimatedFare-1)){
                showDialog(Double.parseDouble(n));
                return;
            }
        }
        else{
            String n = new DecimalFormat("##.00").format(round_trip_estimatedFare-1);
            if(price<(round_trip_estimatedFare-1)){
                showDialog(Double.parseDouble(n));
                return;
            }
        }
        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
//        map.put("use_wallet", useWallet.isChecked() ? 1 : 0);
        map.put("promocode_id", lastSelectCoupon);
        map.put("is_round", roundtrip);
        Log.e("waiting time", "Waiting time __________________" + waitingtime);
        if (roundtrip == 0 && waitingtime == 0) {
            map.put("offer_price", edtOfferFare.getText().toString());
        } else {
            map.put("waiting_minutes", waitingtime);
            map.put("offer_price", edtOfferFare.getText().toString());
        }
        try{
            map.put("instructions", edtCommentForDriver.getText().toString());
        }
        catch (Exception e){}

//        if (edtOfferFare.getText().length() != 0) {
//            map.put("offer_price", edtOfferFare.getText().toString());
//        }
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
        for (Map.Entry<String, Object> key : map.entrySet()) {
            Log.v("form", "formkey: "+key.getKey() + " = "+key.getValue());
        }
        showLoading();

        /*for (java.util.Map.Entry<String, Object> key : map.entrySet()) {
            Log.v("form", "raise-key: " + key.getKey() + " = " + key.getValue());
        }*/
        try {
            Log.e("estimate", "here "+new JSONObject(map));
            presenter.rideNow(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void showDialog(double price){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("Update your offer!")
                .setMessage("Minimum offer is C$ "+price)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
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
            //Log.e("waiting time", "Waiting time __________________" + waitingtime);
            tv_0min.setSelected(true);
            tv_5min.setSelected(false);
            tv_10min.setSelected(false);
            tv_15min.setSelected(false);
            tv_20min.setSelected(false);
        });
        tv_5min.setOnClickListener(v -> {
            waitingtime = 5;
            //Log.e("waiting time", "Waiting time __________________" + waitingtime);
            tv_0min.setSelected(false);
            tv_5min.setSelected(true);
            tv_10min.setSelected(false);
            tv_15min.setSelected(false);
            tv_20min.setSelected(false);
        });
        tv_10min.setOnClickListener(v -> {
            waitingtime = 10;
            //Log.e("waiting time", "Waiting time __________________" + waitingtime);
            tv_0min.setSelected(false);
            tv_10min.setSelected(true);
            tv_5min.setSelected(false);
            tv_15min.setSelected(false);
            tv_20min.setSelected(false);
        });
        tv_15min.setOnClickListener(v -> {
            waitingtime = 15;
            //Log.e("waiting time", "Waiting time __________________" + waitingtime);
            tv_0min.setSelected(false);
            tv_15min.setSelected(true);
            tv_5min.setSelected(false);
            tv_10min.setSelected(false);
            tv_20min.setSelected(false);
        });
        tv_20min.setOnClickListener(v -> {
            waitingtime = 20;
            //Log.e("waiting time", "Waiting time __________________" + waitingtime);
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
                //Log.e("RoundTrip", "RoundTrip is________________________________________________________________ " + round_trip);
                //Log.e("waiting time ", "waiting time is________________________________________________________________ " + waitingTime);
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
                    round_trip_estimatedFare = estimateFare.getEstimatedFare();
                    showEstimatedDialog(estimateFare.getEstimatedFare());

                    //Log.e("tag", "Response " + response.body());
                } else if (response.raw().code() == 500) {
                    //Log.e("tag", "error code is 500 : ");

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
                //System.out.println("RRR call = [" + call + "], t = [" + t + "]");
                //Log.e("tag", "RRR call = [" + call + "], t = [" + t + "]");
            }
        });
    }
    public void slideUp(){

        pickLocationLayout.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                pickLocationLayout.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        pickLocationLayout.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(){
        Log.e("move", "movee slide ");
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                pickLocationLayout.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        pickLocationLayout.startAnimation(animate);
    }

    @Override
    public void onSuccess(Message object) {
        RIDE_REQUEST.clear();
        try {
            hideLoading();

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        activity().sendBroadcast(new Intent("F"));
    }

    @Override
    public void onError(Throwable e) {
        Log.e("ridenow", e.getMessage());
        handleError(e);
    }
}
