package com.sixtech.rider.ui.fragment.service_flow;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseFragment;
import com.sixtech.rider.chat.Chat;
import com.sixtech.rider.chat.ChatActivity;
import com.sixtech.rider.chat.ChatMessageAdapter;
import com.sixtech.rider.common.CancelRequestInterface;
import com.sixtech.rider.common.fcm.MyFireBaseMessagingService;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.APIClient;
import com.sixtech.rider.data.network.model.DataResponse;
import com.sixtech.rider.data.network.model.Datum;
import com.sixtech.rider.data.network.model.EstimateFare;
import com.sixtech.rider.data.network.model.Provider;
import com.sixtech.rider.data.network.model.ProviderService;
import com.sixtech.rider.data.network.model.ServiceType;
import com.sixtech.rider.data.network.model.UpdateStops;
import com.sixtech.rider.ui.activity.add_remove_stops.AddRemoveStopsActivity;
import com.sixtech.rider.ui.activity.main.MainActivity;
import com.sixtech.rider.ui.fragment.cancel_ride.CancelRideIView;
import com.sixtech.rider.ui.fragment.cancel_ride.CancelRidePresenter;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sixtech.rider.MvpApplication.EDIT_LOCATION_REQUEST_CODE;
import static com.sixtech.rider.MvpApplication.PERMISSIONS_REQUEST_PHONE;
import static com.sixtech.rider.base.BaseActivity.DATUM;
import static com.sixtech.rider.base.BaseActivity.RIDE_REQUEST;
import static com.sixtech.rider.data.SharedHelper.getKey;

public class ServiceFlowFragment extends BaseFragment
        implements ServiceFlowIView, CancelRequestInterface, DirectionCallback, CancelRideIView {

    Unbinder unbinder;
    @BindView(R.id.newMessage)
    ImageView newMessage;
    @BindView(R.id.sos)
    ImageView sos;
    @BindView(R.id.otp)
    TextView otp;
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.first_name)
    TextView firstName;
    @BindView(R.id.status)
    TextView status;

    @BindView(R.id.layoutStatus)
    LinearLayoutCompat layoutStatus;
    @BindView(R.id.txtDriverRating)
    TextView txtDriverRating;
    //@BindView(R.id.rating)
    //RatingBar rating;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.share_ride)
    Button sharedRide;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.service_type_name)
    TextView serviceTypeName;
    @BindView(R.id.service_number)
    TextView serviceNumber;
    @BindView(R.id.service_model)
    TextView serviceModel;
    @BindView(R.id.call)
    androidx.appcompat.widget.AppCompatImageView call;
    @BindView(R.id.chat)
    ImageView chat;
    @BindView(R.id.edit_destination)
    ImageView editLocation;
    @BindView(R.id.provider_eta)
    TextView providerEta;
    private Runnable runnable;
    private Handler handler;
    private int delay = 5000;

    private Context thisContext;
    EditText cancel_reason;
    AlertDialog cancelDialog;
    androidx.appcompat.app.AlertDialog alertDialog;
    androidx.appcompat.widget.AppCompatTextView lblOTP;
    String a, b;
    int cancellation_charges;

    private DatabaseReference myRef;
    public static String sender = "user";
    private String providerPhoneNumber = null;
    private String shareRideText = "";
    private LatLng providerLatLng;
    private ServiceFlowPresenter<ServiceFlowFragment> presenter = new ServiceFlowPresenter<>();
    private CancelRidePresenter<ServiceFlowFragment> cancelPresenter = new CancelRidePresenter<>();

    HashMap<String, Object> updateRideMap;
    HashMap<String, Object> estimateFareMap;
    private CancelRequestInterface callback;
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d("RRR latitude", "" + intent.getDoubleExtra("latitude", 0));
                Log.d("RRR longitude", "" + intent.getDoubleExtra("longitude", 0));
                providerLatLng = new LatLng(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0));
                ((MainActivity) context).addCar(providerLatLng);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public ServiceFlowFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service_flow;
    }
    private void initChatView() {
        String chatPath = String.valueOf(DATUM.getId());
        if (chatPath == null) return;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child(chatPath)/*.child("chat")*/;
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                assert chat != null;
                if (chat.getSender() != null && chat.getRead() != null)
                    if (!chat.getSender().equals(sender) && chat.getRead() == 0) {
                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT,150);
                        chat.setRead(1);
                        dataSnapshot.getRef().setValue(chat);
                        newMessage.setVisibility(View.VISIBLE);
                    }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        activity().registerReceiver(myReceiver, new IntentFilter(MyFireBaseMessagingService.INTENT_PROVIDER));
        callback = this;
        this.thisContext = getContext();
        presenter.attachView(this);
        cancelPresenter.attachView(this);
        lblOTP = view.findViewById(R.id.lblOTP);
        if (DATUM != null) initView(DATUM);
        initChatView();
        return view;
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        if (myReceiver != null) try {
            activity().unregisterReceiver(myReceiver);
            myReceiver = null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    @OnClick({R.id.sos, R.id.cancel, R.id.share_ride, R.id.call, R.id.edit_destination, R.id.chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sos:
                sos();
                break;
            case R.id.cancel:

                long startTime = SharedHelper.getIntKey(getContext(), "bookingTime");
                long difference = SystemClock.elapsedRealtime();
                difference = difference - startTime;
                difference = difference / 1000;

//                int ridecancellationMinutes = Integer.parseInt(SharedHelper.getKey(getContext(),"rideCancellationMinutes"));
                int ridecancellationMinutes = 0;
                // Toast.makeText(getContext()," "+ridecancellationMinutes,Toast.LENGTH_SHORT).show();
                ridecancellationMinutes = ridecancellationMinutes * 60;


                cancelAlertDialog();
                TextView alert = alertDialog.findViewById(R.id.text_cancel_info);

                if (difference > ridecancellationMinutes) {
                    alert.setText(getResources().getString(R.string.cancle_ride_captain) + getResources().getString(R.string.cancle_fee_captain) + "PKR: " + cancellation_charges);

                } else {
                    alert.setText(getResources().getString(R.string.cancle_ride_captain));

                }
                TextView yes_cancel = alertDialog.findViewById(R.id.btn_yes_cancel);
                TextView no_keepride = alertDialog.findViewById(R.id.btn_no_keep_ride);

                yes_cancel.setOnClickListener(view1 -> {
                    alertDialog.dismiss();
                    cancelRideDialog();
                });

                no_keepride.setOnClickListener(view12 -> alertDialog.dismiss());

                //                CancelRideDialogFragment cancelRideDialogFragment = new CancelRideDialogFragment(callback);
//                cancelRideDialogFragment.show(activity().getSupportFragmentManager(), cancelRideDialogFragment.getTag());

                break;
            case R.id.share_ride:
                sharedRide();
                break;
            case R.id.call:
                callPhoneNumber(providerPhoneNumber);
                break;
            case R.id.chat:
                if (DATUM != null) {
                    newMessage.setVisibility(View.INVISIBLE);
                    Intent i = new Intent(activity(), ChatActivity.class);
                    i.putExtra("request_id", String.valueOf(DATUM.getId()));
                    startActivity(i);
                }
                break;
            case R.id.edit_destination:
                if (DATUM != null) {

                    Intent intent = new Intent(activity(), AddRemoveStopsActivity.class);
                    intent.putExtra("destClick", "isDest");
                    intent.putExtra("isSetting", "destination");
                    Gson gson = new Gson();
                    String positions = gson.toJson(DATUM.getStops());
                    intent.putExtra("positions", positions);
//                    intent.putExtra("destination", DATUM.getDAddress());
//                    intent.putExtra("destination_latitude", DATUM.getDLatitude());
//                    intent.putExtra("destination_longitude", DATUM.getDLongitude());
                    intent.putExtra("fieldClicked", "dropAddress");
                    startActivityForResult(intent, EDIT_LOCATION_REQUEST_CODE);
//                    Intent intent = new Intent(activity(), EditLocationActivity.class);
//                    intent.putExtra("destClick", "isDest");
//                    intent.putExtra("isSetting", "destination");
//                    intent.putExtra("destination", DATUM.getDAddress());
//                    intent.putExtra("destination_latitude", DATUM.getDLatitude());
//                    intent.putExtra("destination_longitude", DATUM.getDLongitude());
//                    intent.putExtra("fieldClicked", "dropAddress");
//                    startActivityForResult(intent, EDIT_LOCATION_REQUEST_CODE);
                }
                break;
        }
    }

    @SuppressLint({"StringFormatInvalid", "RestrictedApi"})
    private void initView(Datum datum) {
        Provider provider = datum.getProvider();
        if (provider != null) {
            firstName.setText(String.format("%s %s", provider.getFirstName(), provider.getLastName()));
            txtDriverRating.setText(provider.getRating());
            Glide.with(activity())
                    .load(provider.getAvatar())
                    .apply(RequestOptions
                            .placeholderOf(R.drawable.ic_user_placeholder)
                            .dontAnimate()
                            .error(R.drawable.ic_user_placeholder))
                    .into(avatar);
            providerPhoneNumber = provider.getMobile();
        }

        ServiceType serviceType = datum.getServiceType();
        if (serviceType != null) {
            serviceTypeName.setText(serviceType.getName());
            Glide.with(activity())
                    .load(serviceType.getImage())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_car)
                            .dontAnimate()
                            .error(R.drawable.ic_car))
                    .into(image);
            cancellation_charges = serviceType.getCancellation_charges();
        }

        if ("PICKEDUP".equalsIgnoreCase(datum.getStatus())) {
            chat.setVisibility(View.GONE);
        } else {
            chat.setVisibility(View.VISIBLE);
        }

        if ("STARTED".equalsIgnoreCase(datum.getStatus()) || "PICKEDUP".equalsIgnoreCase(datum.getStatus()) ||
                "DROPPED".equalsIgnoreCase(datum.getStatus())) {

            handler = new Handler();
            runnable = () -> {
                try {
                    if ("PICKEDUP".equalsIgnoreCase(datum.getStatus()) ||
                            "DROPPED".equalsIgnoreCase(datum.getStatus())) {
                        Double lat = (Double) RIDE_REQUEST.get("d_latitude");
                        Double lng = (Double) RIDE_REQUEST.get("d_longitude");
                        Log.d("Etaahsdfibsd", "ddwala");
                        Log.d("Etaahsdfibsd", lat.toString());
                        Log.d("Etaahsdfibsd", lng.toString());
                        calculateETA(lat, lng);
                    } else {
                        Double lat = (Double) RIDE_REQUEST.get("s_latitude");
                        Double lng = (Double) RIDE_REQUEST.get("s_longitude");
                        Log.d("Etaahsdfibsd", "sswala");
                        Log.d("Etaahsdfibsd", lat.toString());
                        Log.d("Etaahsdfibsd", lng.toString());
                        calculateETA(lat, lng);
                    }
                    handler.postDelayed(runnable, delay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            handler.postDelayed(runnable, delay);
        }


        ProviderService providerService = datum.getProviderService();
        if (providerService != null) {
            serviceNumber.setText(providerService.getServiceNumber());
            serviceModel.setText(providerService.getServiceModel());
        }

        otp.setText(getString(R.string.otp_, datum.getOtp()));
        shareRideText = getString(R.string.app_name) + ": "
                + datum.getUser().getFirstName() + " " + datum.getUser().getLastName() + " is riding in "
                + datum.getServiceType().getName() + " would like to share his ride "
                + "http://maps.google.com/maps?q=loc:" + datum.getStops().get(datum.getStops().size() - 1).getDLatitude() + "," + datum.getStops().get(datum.getStops().size() - 1).getDLongitude();
//                + "http://maps.google.com/maps?q=loc:" + datum.getDLatitude() + "," + datum.getDLongitude();

        switch (datum.getStatus()) {
            case "STARTED":
                if (provider != null && provider.getLatitude() != null && provider.getLongitude() != null) {
                    providerLatLng = new LatLng(provider.getLatitude(), provider.getLongitude());
                    LatLng origin = new LatLng(datum.getSLatitude(), datum.getSLongitude());
                    getDistance(providerLatLng, origin);
                }
                status.setText(R.string.driver_accepted_your_request);
                layoutStatus.setVisibility(View.GONE);
                break;
            case "ARRIVED":
                status.setText(R.string.driver_has_arrived_your_location);
                layoutStatus.setVisibility(View.VISIBLE);
                otp.setVisibility(View.VISIBLE);
                otp.setText(DATUM.getOtp());
                lblOTP.setVisibility(View.VISIBLE);
                break;
            case "PICKEDUP":
                status.setText(R.string.you_are_on_ride);
                cancel.setVisibility(View.GONE);
                //sharedRide.setVisibility(View.VISIBLE);
                layoutStatus.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

//        if ("STARTED".equalsIgnoreCase(datum.getStatus())) {
//            LatLng source = new LatLng(datum.getProvider().getLatitude(), datum.getProvider().getLongitude());
//            LatLng destination = new LatLng(datum.getSLatitude(), datum.getSLongitude());
//            ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(source, destination);
//        } else {
//            LatLng origin = new LatLng(datum.getSLatitude(), datum.getSLongitude());
//            LatLng destination = new LatLng(datum.getDLatitude(), datum.getDLongitude());
//            ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(origin, destination);
//        }

    }

    private void calculateETA(Double lat, Double lng) {
        GoogleDirection
                .withServerKey(SharedHelper.getKey(activity(), "map_key"))
                .from(new LatLng(lat, lng))
                .to(new LatLng(DATUM.getProvider().getLatitude(), DATUM.getProvider().getLongitude()))
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Route route = direction.getRouteList().get(0);
                            if (!route.getLegList().isEmpty()) {
                                Leg leg = route.getLegList().get(0);
                                providerEta.setVisibility(View.VISIBLE);
                                String arrivalTime = String.valueOf(leg.getDuration().getText());
                                Log.d("ETAjsdvs", arrivalTime);
                                if (arrivalTime.contains("hours")){
                                    arrivalTime = arrivalTime.replace("hours", "h\n");
                                    Log.d("ETAjsdvs", arrivalTime);
                                }
                                else if (arrivalTime.contains("hour")){
                                    arrivalTime = arrivalTime.replace("hour", "h\n");
                                    Log.d("ETAjsdvs", arrivalTime);
                                }
                                if (arrivalTime.contains("mins")){
                                    arrivalTime = arrivalTime.replace("mins", "min");
                                    Log.d("ETAjsdvs", arrivalTime);
                                }

                                a = arrivalTime.replace("mins", "min");

                                Log.d("ETAjsdvs", a);

                                providerEta.setText("ETA :" + " " + a);
                            }
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Un used
                    }
                });
    }


    private void sos() {
        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getResources().getString(R.string.sos_alert))
                .setMessage(R.string.are_sure_you_want_to_emergency_alert)
                .setCancelable(true)
                .setPositiveButton(getContext().getResources().getString(R.string.yes), (dialog, which) -> callPhoneNumber(SharedHelper.getKey(getContext(), "sosNumber")))
                .setNegativeButton(getContext().getResources().getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    private void callPhoneNumber(String mobileNumber) {
        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber)));
        }
    }

    private void sharedRide() {
        try {
            String appName = getString(R.string.app_name) + " " + getString(R.string.share_ride);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareRideText);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share Ride"));
//            startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(activity(), "applications not found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(DataResponse dataResponse) {
        System.out.println("RRR ServiceFlowFragment checkStatusResponse = " + printJSON(dataResponse));
        if (!dataResponse.getData().isEmpty()) initView(dataResponse.getData().get(0));
    }

    @Override
    public void onSuccess(Object dataResponse) {

    }

    @Override
    public void onUpdateRideSuccess(Object o) {
        Toast.makeText(getContext(), "Destination Changed Successfully", Toast.LENGTH_SHORT).show();
        Log.d("UpdateRide", o.toString());
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }


    @Override
    public void onSuccessCancel(Object object) {
        try {
            Log.e("tag", "cancel on success :");

            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (DATUM != null)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
        Intent intent = new Intent("INTENT_FILTER");
        getActivity().sendBroadcast(intent);
        callback.cancelRequestMethod();
    }

    @Override
    public void onErrorCancel(Throwable e) {
        handleError(e);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(activity(), "Permission Granted. Try Again!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void cancelRequestMethod() {
    }

    public void getDistance(LatLng source, LatLng destination) {
        GoogleDirection.withServerKey(SharedHelper.getKey(activity(), "map_key"))
                .from(source)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (isAdded()) {
            if (direction.isOK()) {
                Route route = direction.getRouteList().get(0);
                if (!route.getLegList().isEmpty()) {
                    Leg leg = route.getLegList().get(0);
                    //      TODO: Commented by Rajaganapathi... cos some time screens blinks
                    //      status.setText(getString(R.string.driver_accepted_your_request_, leg.getDuration().getText()));
                }
            } else
                Toast.makeText(activity(), direction.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    private void cancelRideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_cancel_ride_dialog, null);

        cancel_reason = view.findViewById(R.id.cancel_reason);
        Button submit = view.findViewById(R.id.submit);
        Button dismiss = view.findViewById(R.id.dismiss);

        builder.setView(view);
        cancelDialog = builder.create();
        cancelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        submit.setOnClickListener(view1 -> {

            if (thisContext != null) {
                Datum datum = DATUM;
                HashMap<String, Object> map = new HashMap<>();
                map.put("request_id", datum.getId());
                map.put("cancel_reason", cancel_reason.getText().toString());
                showLoading();
                cancelPresenter.cancelRequest(map);
                try {
                    hideLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cancelDialog.dismiss();

            }

        });
        dismiss.setOnClickListener(view1 -> {


            try {
                if (thisContext != null)
                    cancelDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        cancelDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String positions = data.getStringExtra("positions");
//                String address= data.getStringExtra("d_address");
//                Double dlatitude= data.getDoubleExtra("d_latitude",0);
//                Double dlongitude= data.getDoubleExtra("d_longitude",0);

                updateRideMap = new HashMap<>();
                estimateFareMap = new HashMap<>();
                if (DATUM != null) {
                    updateRideMap.put("request_id", DATUM.getId());
                    updateRideMap.put("positions", positions);
//                    updateRideMap.put("address", address);
//                    updateRideMap.put("latitude", dlatitude);
//                    updateRideMap.put("longitude", dlongitude);

                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<UpdateStops>>() {
                    }.getType();
                    ArrayList<UpdateStops> stopsArrayList = gson.fromJson(positions, type);
                    ArrayList<UpdateStops> newPositions = new ArrayList<>();
                    for (UpdateStops updatestop : stopsArrayList) {
                        if (updatestop.action.equals("create") || updatestop.action.equals("update")) {
                            newPositions.add(updatestop);
                        }
                    }
                    SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

                    int waiting_time = sharedPreferences.getInt("waiting_time", 0);
                    int isRound = sharedPreferences.getInt("round_trip", 0);
                    String newpositions = gson.toJson(newPositions);
                    estimateFareMap.put("s_latitude", DATUM.getSLatitude());
                    estimateFareMap.put("s_longitude", DATUM.getSLongitude());
                    estimateFareMap.put("positions", newpositions);
                    estimateFareMap.put("round_trip", isRound);
                    estimateFareMap.put("waiting_minutes",waiting_time );
//                    estimateFareMap.put("d_latitude",dlatitude);
//                    estimateFareMap.put("d_longitude",dlongitude);
                    estimateFareMap.put("service_type", DATUM.getServiceTypeId());

                    estimatedApiCall();
                }

            }
        }
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
                    showEstimatedFare(String.valueOf(estimateFare.getEstimatedFare()));
                    Log.e("tag", "Response "+response.body());
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


    private void showEstimatedFare(String fare) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_confirm_fare);
        TextView tvEstimatedFare = dialog.findViewById(R.id.tvEstimatedFare);
        tvEstimatedFare.setText(SharedHelper.getKey(getContext(), "currency")
                + ". " + Double.valueOf(Double.valueOf(fare)).doubleValue());
//                + SharedHelper.getKey(getContext(), "currency")
//                + Double.valueOf(Double.valueOf(fare) + 3).intValue());
        TextView tvConfirm = dialog.findViewById(R.id.tvConfirm);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);

        tvConfirm.setOnClickListener(v -> {
            presenter.updateRide(updateRideMap);
            dialog.dismiss();
        });

        tvCancel.setOnClickListener(v -> {

            dialog.dismiss();
        });

        dialog.show();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);
    }

    private void cancelAlertDialog() {
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_cancel_ride2, viewGroup, false);
        if (android.os.Build.VERSION.SDK_INT < 27) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
            builder.setView(dialogView);
            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } else {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext(), R.style.img_dialog);
            builder.setView(dialogView);
            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }


}
