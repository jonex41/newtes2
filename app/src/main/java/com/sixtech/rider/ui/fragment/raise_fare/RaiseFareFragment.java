package com.sixtech.rider.ui.fragment.raise_fare;

import static com.sixtech.rider.base.BaseActivity.DATUM;
import static com.sixtech.rider.base.BaseActivity.travelTime;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseFragment;
import com.sixtech.rider.common.Utilities;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.APIClient;
import com.sixtech.rider.data.network.model.Datum;
import com.sixtech.rider.data.network.model.EstimateFare;
import com.sixtech.rider.data.network.model.Request;
import com.sixtech.rider.ui.activity.main.MainActivity;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carbon.widget.Button;
import carbon.widget.ConstraintLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RaiseFareFragment extends BaseFragment implements RaiseFareIView {
    public static boolean started = false;
    public boolean wasRideCanceledForRaiseFare = false;
    private int driverRequestCounter = 1;
    RecyclerView rvDriverRequest;
    DriverRequestAdapter mAdapter;
    Handler rvHandler = new Handler();
    ConstraintLayout layoutTrip;
    AppCompatTextView txtTripName;
    Runnable rvRunnable;
    private boolean hasPending = true;
    //sahamnadeem2016@gmail.com
//123456
    private TextView textfare, textRaise;

    private double raisedFare = 0, oldRaise = 0;
    EstimateFare estimateFareFromApi;
    private boolean isRaiseFareClicked;
    private final RaiseFarePresenter<RaiseFareFragment> presenter = new RaiseFarePresenter<>();
    private ArrayList<Request> dataList;

    TextView txtRaiseFareUp;
    TextView txtRaiseFareDown;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_raise_fare;
    }

    public void repeaterCheck() {
        new Handler().postDelayed(() -> {

            try {
                if(DATUM.requests==null){
                    dataList.clear();
                    mAdapter.notifyDataSetChanged();
                    rvDriverRequest.setVisibility(View.GONE);
                    layoutTrip.setVisibility(View.VISIBLE);
                }
                else if (!(DATUM.getRequests().size() ==0)) {
                    //Toast.makeText(requireActivity(), "size "+DATUM.getRequests().size(), Toast.LENGTH_SHORT).show();
                    rvDriverRequest.setVisibility(View.VISIBLE);
                    layoutTrip.setVisibility(View.GONE);
                    if (DATUM.getRequests().size() > dataList.size()) {
                        for (int i = 0; i < DATUM.getRequests().size(); i++) {
                            Boolean exist = false;
                            for (int j = 0; j < dataList.size(); j++) {
                                if (DATUM.getRequests().get(i).getId().equals(dataList.get(j).getId())) {
                                    exist = true;
                                }
                            }
                            if (!exist) {
                                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,150);
                                //Toast.makeText(requireContext(), "id "+DATUM.getRequests().get(i).getId() , Toast.LENGTH_SHORT).show();
                                dataList.add(DATUM.getRequests().get(i));
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    else if (DATUM.getRequests().size() < dataList.size()) {
                        for (int i = 0; i < dataList.size(); i++) {
                            Boolean exist = false;
                            for (int j = 0; j < DATUM.getRequests().size(); j++) {
                                if (dataList.get(i).getId().equals(DATUM.getRequests().get(j).getId())) {
                                    exist = true;
                                }
                            }
                            if (!exist) {
                                dataList.remove(dataList.get(i));
                                mAdapter.notifyDataSetChanged();
                                //adapterer.notifyDataSetChanged()
                            }
                        }
                    }

                    //rvDriverRequest.setVisibility(View.VISIBLE);
    //                rvDriverRequest.setItemAnimator(new DefaultItemAnimator());
                } else {
                    dataList.clear();
                    mAdapter.notifyDataSetChanged();
                    rvDriverRequest.setVisibility(View.GONE);
                    layoutTrip.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                dataList.clear();
                mAdapter.notifyDataSetChanged();
                e.printStackTrace();
            }
            //presenter.checkStatus();
            repeaterCheck();
        }, 2000);
    }

    @Override
    public View initView(View view) {
        started = true;
        presenter.attachView(this);
        ((MainActivity) getActivity()).txtCancelBookingRequest.setVisibility(View.VISIBLE);
        dataList = new ArrayList<>();
        if (DATUM.getStops().size() > 0) {

            Log.v("form", "raise-key: dest0 " + DATUM.getStops().get(0).getDAddress());
            ((TextView) view.findViewById(R.id.txtDestination)).setText(DATUM.getStops().get(0).getDAddress());
        }
        layoutTrip = view.findViewById(R.id.layoutTrip);
        txtTripName = view.findViewById(R.id.txtTripName);
        if(DATUM.getIsRound()==1){
            txtTripName.setText("Round Trip");
        }
        //Log.e("anjum datum data",DATUM.toString());
        ((TextView) view.findViewById(R.id.txtRideCategory)).setText(DATUM.getServiceType().getName());
        ((TextView) view.findViewById(R.id.txtSource)).setText(DATUM.getSAddress());
        ((TextView) view.findViewById(R.id.txtMiles)).setText(DATUM.getDistance()+"");
        textfare = ((TextView) view.findViewById(R.id.txtFare));
        textfare.setText("C $" + DATUM.getOffer_price());
        textRaise = ((TextView) view.findViewById(R.id.txtRaisedFare));
        textRaise.setText("C $" + DATUM.getOffer_price());
        rvDriverRequest = view.findViewById(R.id.rvDriverRequest);
        raisedFare = DATUM.getOffer_price();
        oldRaise = DATUM.getOffer_price();
        try {
            ((TextView) view.findViewById(R.id.travelTimeInMinutes)).setText(getTravelTimeInSpannable(getString(R.string.travel_time_approx), "" + travelTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((MainActivity) getActivity()).txtCancelBookingRequest.setOnClickListener(v -> {
            alertRaiseFare(false);
        });
        txtRaiseFareUp = view.findViewById(R.id.txtRaiseFareUp);
        txtRaiseFareDown = view.findViewById(R.id.txtRaiseFareDown);
        if (raisedFare == oldRaise) {
            view.findViewById(R.id.btn_raise_fare).setEnabled(false);
            view.findViewById(R.id.btn_raise_fare).setBackgroundColor(Color.parseColor("#898989"));
        }
        view.findViewById(R.id.txtRaiseFareDown).setOnClickListener(v -> {
            if(raisedFare <= oldRaise){
                return;
            }
            txtRaiseFareUp.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.CF8F8F8));
            txtRaiseFareUp.setTextColor(ContextCompat.getColor(getActivity(), R.color.C898989));
            txtRaiseFareDown.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.black));
            txtRaiseFareDown.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));

            raisedFare -= 1;
            String text = SharedHelper.getKey(getActivity(), "currency") + " " +
                    getNewNumberFormat(raisedFare);
            ((TextView) getView().findViewById(R.id.txtRaisedFare)).setText(text);
            if (raisedFare == oldRaise) {
                view.findViewById(R.id.btn_raise_fare).setEnabled(false);
                view.findViewById(R.id.btn_raise_fare).setBackgroundColor(Color.parseColor("#898989"));
            } else {
                view.findViewById(R.id.btn_raise_fare).setEnabled(true);
                view.findViewById(R.id.btn_raise_fare).setBackgroundColor(Color.parseColor("#000000"));
            }
        });
        view.findViewById(R.id.txtRaiseFareUp).setOnClickListener(v -> {

            txtRaiseFareUp.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.black));
            txtRaiseFareUp.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
            txtRaiseFareDown.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.CF8F8F8));
            txtRaiseFareDown.setTextColor(ContextCompat.getColor(getActivity(), R.color.C898989));

            raisedFare += 1;
            String text = SharedHelper.getKey(getActivity(), "currency") + " " +
                    getNewNumberFormat(raisedFare);
            ((TextView) getView().findViewById(R.id.txtRaisedFare)).setText(text);
            if (raisedFare == oldRaise) {
                view.findViewById(R.id.btn_raise_fare).setEnabled(false);
                view.findViewById(R.id.btn_raise_fare).setBackgroundColor(Color.parseColor("#898989"));
            } else {
                view.findViewById(R.id.btn_raise_fare).setEnabled(true);
                view.findViewById(R.id.btn_raise_fare).setBackgroundColor(Color.parseColor("#000000"));
            }
        });
        view.findViewById(R.id.btn_raise_fare).setOnClickListener(v -> {
            alertRaiseFare(true);
        });
        mAdapter = new DriverRequestAdapter(dataList);
        rvDriverRequest.setAdapter(mAdapter);
        rvDriverRequest.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        repeaterCheck();
//        rvRunnable = () -> {
//            Log.d("animm", "animmm " + driverRequestCounter);
//            if (driverRequestCounter < 4 && hasPending) {
//                driverRequestCounter++;
//                mAdapter.notifyItemInserted(0);
//            } else {
//                hasPending = false;
//                driverRequestCounter--;
//                if (driverRequestCounter >= 0) {
//                    mAdapter.notifyItemRemoved(0);
//                } else {
//                    driverRequestCounter = 0;
//                    hasPending = true;
//                }
//            }
//            rvDriverRequest.smoothScrollToPosition(0);
//            rvHandler.postDelayed(rvRunnable, 4000);
//        };
        return view;
    }


    @Override
    public void onSuccessAccept(Object responseBody) {
        try {
            hideLoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(activity().getApplicationContext(), "Accepted Success", Toast.LENGTH_SHORT).show();
        activity().sendBroadcast(new Intent("F"));
    }

    @Override
    public void onSuccessReject(Object responseBody) {
        try {
            hideLoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(activity().getApplicationContext(), "Rejected", Toast.LENGTH_SHORT).show();
        activity().sendBroadcast(new Intent("F"));
    }

//    @Override
//    public void onSuccess(DataResponse dResponse) {
//        Log.e("got it", "here");
//        if(!dResponse.getRequests().isEmpty()){
//            DATUM.setRequests(dResponse.getRequests());
//            rvDriverRequest.setVisibility(View.VISIBLE);
//            //rvDriverRequest.setVisibility(View.VISIBLE);
//            mAdapter = new DriverRequestAdapter(DATUM.getRequests());
//            rvDriverRequest.setAdapter(mAdapter);
//            rvDriverRequest.setLayoutManager(new LinearLayoutManager(getContext(),
//                    LinearLayoutManager.VERTICAL, false));
//            //rvDriverRequest.setVisibility(View.VISIBLE);
//            rvDriverRequest.setItemAnimator(new DefaultItemAnimator());
//            layoutTrip.setVisibility(View.GONE);
//        }
//        else{
//            rvDriverRequest.setVisibility(View.GONE);
//            layoutTrip.setVisibility(View.VISIBLE);
//        }
//    }

    @Override
    public void onError(Throwable throwable) {
        try {
            Log.e("got it", "here22 " + throwable);
            hideLoading();
            Log.e("error code", throwable.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(activity(), "sending request error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelRideSuccess(Object object) {
        wasRideCanceledForRaiseFare = isRaiseFareClicked;
        try {
            Log.e("cancel", "success");
            //Toast.makeText(activity(), "Ride Cancelled Successfully", Toast.LENGTH_SHORT).show();
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (wasRideCanceledForRaiseFare) {
            sendRequest();
        }
        activity().sendBroadcast(new Intent("F"));
    }

    @Override
    public void onBookRideSuccess(Object object) {
        wasRideCanceledForRaiseFare = false;
        // estimatedApiCall();
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            textfare.setText("C $" + df.format(raisedFare));
            textRaise.setText("C $" + df.format(raisedFare));
            hideLoading();
            activity().sendBroadcast(new Intent("F"));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private class DriverRequestAdapter extends RecyclerView.Adapter<MyViewHolder> {
        List<Request> listRequest = new ArrayList();

        DriverRequestAdapter(List<Request> requests) {
            //this.mWallet = walletList;
            listRequest = requests;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_driver_request, parent, false);
            return new MyViewHolder(itemView);
        }

        private void setProgress(MyViewHolder holder, int position) {
            new Handler().postDelayed(() -> {
                if (holder.progressBar.getProgress() > 0) {
                    holder.progressBar.setProgress(holder.progressBar.getProgress() - 1);
                    setProgress(holder, position);
                } else {
                    removeItem(position);
                }
            }, 1000);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Request request = listRequest.get(position);
            holder.vehicleName.setText(request.getProvider().getProviderService().getServiceModel());
            holder.driverName.setText(request.getProvider().getFirstName());
            holder.bid.setText("C$ " + request.getOffer_price());
            holder.rating.setText(request.getProvider().getRating());
            holder.time.setText(DATUM.getTravelTime());
            holder.progressBar.setProgress(15);

            setProgress(holder, position);


            Log.e("values", DATUM.getBookingId() + "//" + listRequest.get(position).getRequest_id());
            holder.accept.setOnClickListener(v -> {
                presenter.accept(String.valueOf(listRequest.get(position).getId()), String.valueOf(DATUM.getId()));
                showLoading();
            });
            holder.decline.setOnClickListener(v -> {
                presenter.reject(String.valueOf(listRequest.get(position).getId()), String.valueOf(DATUM.getId()));
                showLoading();
            });
        }

        @Override
        public int getItemCount() {
            return listRequest.size();
        }
    }

    private void removeItem(int position) {
        try {
            //dataList.remove(position);//removeAt(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Request rItem = dataList.get(position);
//        presenter.reject(String.valueOf(dataList.get(position).getId()),String.valueOf(DATUM.getId()));
//        dataList.remove(position);
//        mAdapter.notifyDataSetChanged();
//        DATUM.getRequests().remove(rItem);
    }

    private void alertRaiseFare(boolean isRaiseFareClicked) {
        this.isRaiseFareClicked = isRaiseFareClicked;
        int msg = (isRaiseFareClicked) ? R.string.are_sure_you_want_to_raise_fare
                : R.string.are_sure_you_want_to_cancel_the_request;

        new AlertDialog.Builder(getContext())
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    if (DATUM != null) {
                        showLoading();
                        Datum datum = DATUM;
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("request_id", datum.getId());


                        presenter.cancelRequest(map);
                        for (java.util.Map.Entry<String, Object> key : map.entrySet()) {
                            Log.v("form", "cancel ride: status is  " + key.getKey() + " = " + key.getValue());
                        }
                    }
                }).setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    private void estimatedApiCall() {
        Map<String, Object> estimateFareMap = new HashMap<>();
        estimateFareMap.put("payment_mode", DATUM.getPaymentMode());
        estimateFareMap.put("service_type", DATUM.getServiceType().getId());
        estimateFareMap.put("s_latitude", DATUM.getSLatitude());
        estimateFareMap.put("s_longitude", DATUM.getSLongitude());
        Gson gson = new Gson();
        String positions = gson.toJson(DATUM.getStops());
        estimateFareMap.put("positions", positions);
        estimateFareMap.put("service_type", DATUM.getServiceTypeId());
        //estimateFareMap.put("s_address", );

        for (Map.Entry<String, Object> key : estimateFareMap.entrySet()) {
            Log.v("estimateApi", "formkey: " + key.getKey() + " = " + key.getValue());
        }
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
                if (response.body() != null && getActivity() != null && getView() != null) {
                    estimateFareFromApi = response.body();

                    raisedFare = estimateFareFromApi.getEstimatedFare();
                    String text = SharedHelper.getKey(getActivity(), "currency") + " " +
                            getNewNumberFormat(raisedFare);


                    String travelTime = estimateFareFromApi.getTime();
                    try {
                        ((TextView) getView().findViewById(R.id.txtFare)).setText(text);
                        ((TextView) getView().findViewById(R.id.txtRaisedFare)).setText(text);
                        ((TextView) getView().findViewById(R.id.travelTimeInMinutes)).setText(getTravelTimeInSpannable(getString(R.string.travel_time_approx), travelTime));
                    } catch (Exception e) {
                        ((TextView) getView().findViewById(R.id.travelTimeInMinutes)).setText(e.getMessage());
                    }
                } else if (response.raw().code() == 500) {

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
            }
        });
    }

    public void sendRequest() {
//        if (estimateFareFromApi == null) {
//            return;
//        }
        /*Log.v("form", "raise-key: vehicle " + DATUM.getServiceType().getName());
        Log.v("form", "raise-key: source " + DATUM.getSAddress());
        Log.v("form", "raise-key: dest " + DATUM.getDAddress());
        Log.v("form", "raise-key: stops " + DATUM.getStops().size());
        Log.v("form", "raise-key: lat " + DATUM.getSLatitude()+", " + DATUM.getSLongitude());
        Log.v("form", "raise-key: lon " + DATUM.getDLatitude()+", " + DATUM.getDLongitude());
        Log.v("form", "raise-key: time " + DATUM.getTravelTime());
        Log.v("form", "raise-key: dist " + DATUM.getDistance());
        Log.v("form", "raise-key: paid " + DATUM.getPaid());
        Log.v("form", "raise-key: payment " + DATUM.getPayment());
        Log.v("form", "raise-key: assi " + DATUM.getAssignedAt());
        Log.v("form", "raise-key: price " + DATUM.getServiceType().getPrice());
        Log.v("form", "raise-key: fixe " + DATUM.getServiceType().getFixed());
        Log.v("form", "raise-key: minut " + DATUM.getServiceType().getMinute());
        Log.v("form", "raise-key: stat " + DATUM.getServiceType().getStatus());*/
        DATUM.setOffer_price(raisedFare);
        HashMap<String, Object> map = new HashMap<>();
        String paymentMode = DATUM.getPaymentMode();
        map.put("payment_mode", paymentMode);
        map.put("service_type", DATUM.getServiceType().getId());
        map.put("distance", DATUM.getDistance());
        map.put("s_latitude", DATUM.getSLatitude());
        map.put("s_longitude", DATUM.getSLongitude());
        map.put("s_address", DATUM.getSAddress());
        map.put("offer_price", raisedFare);
        map.put("is_round", 0);
        map.put("promocode_id", 0);
        Gson gson = new Gson();
        String positions = gson.toJson(DATUM.getStops());
        map.put("positions", positions);

        //raise-key: payment_mode = CASH
        // raise-key: service_type = 1
        //raise-key: distance = 4.245
        //raise-key: s_latitude = 30.214440067941993
        //raise-key: is_round = 0
        //raise-key: positions = [{"d_address":"6F27+X6C, Mohalla Tibi Sher Khan Mohalla Tibbi Sher Khan, Multan, Punjab, Pakistan","d_latitude":30.202438794313966,"d_longitude":71.46286893635988}]
        //raise-key: s_address = 6F7J+QG5, Usmanabad Colony, Multan, Punjab, Pakistan
        //raise-key: s_longitude = 71.48131716996431
        //raise-key: promocode_id = 0
        //raise-key: offer_price = 11.43

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

        for (java.util.Map.Entry<String, Object> key : map.entrySet()) {
            Log.v("form", "raise-key: status is  " + key.getKey() + " = " + key.getValue());
        }
        try {
            presenter.rideNow(map);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                hideLoading();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        //textVehicleName, textDriverName, txtDriverRating, txtDriverMiles, txtBidFare, txtWaitingTime
        private TextView vehicleName, driverName, rating, miles, bid, time;
        private Button decline, accept;
        private ProgressBar progressBar;

        MyViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
            vehicleName = itemView.findViewById(R.id.txtVehicleName);
            driverName = itemView.findViewById(R.id.txtDriverName);
            rating = itemView.findViewById(R.id.txtDriverRating);
            miles = itemView.findViewById(R.id.txtDriverMiles);
            bid = itemView.findViewById(R.id.txtBidFare);
            time = itemView.findViewById(R.id.txtWaitingTime);

            decline = itemView.findViewById(R.id.btn_decline);
            accept = itemView.findViewById(R.id.btn_accept);
        }
    }
}
