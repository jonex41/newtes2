package com.sixtech.rider.ui.activity.location_pick;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseActivity;
import com.sixtech.rider.common.RecyclerItemClickListener;
import com.sixtech.rider.data.network.model.Address;
import com.sixtech.rider.data.network.model.AddressResponse;
import com.sixtech.rider.data.network.model.AddedStop;
import com.sixtech.rider.ui.adapter.PlacesAutoCompleteAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sixtech.rider.MvpApplication.DEFAULT_ZOOM;
import static com.sixtech.rider.MvpApplication.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.sixtech.rider.MvpApplication.mLastKnownLocation;

public class LocationPickActivity extends BaseActivity
        implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationPickIView {

    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sourceTv)
    EditText source;
    @BindView(R.id.addstop_1_destination)
    EditText stop1Destination;
    @BindView(R.id.addstop_1_destination_layout)
    LinearLayout stop1DestinationLayout;
    @BindView(R.id.addstop_2_destination)
    EditText stop2Destination;
    @BindView(R.id.addstop_2_destination_layout)
    LinearLayout stop2DestinationLayout;
    @BindView(R.id.addstop_3_destination)
    EditText stop3Destination;
    @BindView(R.id.addstop_3_destination_layout)
    LinearLayout stop3DestinationLayout;

    @BindView(R.id.addstop_1_reset)
    ImageView stop1Reset;
    @BindView(R.id.addstop_1_add)
    ImageView stop1Add;
    @BindView(R.id.addstop_2_reset)
    ImageView stop2Reset;
    @BindView(R.id.addstop_2_add)
    ImageView stop2Add;

    @BindView(R.id.addstop_3_reset)
    ImageView stop3Reset;
    @BindView(R.id.addstop_3_add)
    ImageView stop3Add;

    @BindView(R.id.home_address_layout)
    LinearLayout homeAddressLayout;
    @BindView(R.id.work_address_layout)
    LinearLayout workAddressLayout;
    @BindView(R.id.home_address)
    TextView homeAddress;
    @BindView(R.id.work_address)
    TextView workAddress;
    @BindView(R.id.locations_rv)
    RecyclerView locationsRv;
    @BindView(R.id.location_bs_layout)
    CardView locationBsLayout;
    @BindView(R.id.dd)
    CoordinatorLayout dd;
    @BindView(R.id.stop1txt)
    TextView stop1txt;
    private PlaceDetectionClient mPlaceDetectionClient;
    private boolean isLocationRvClick = false;
    private boolean isSettingLocationClick = false;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private boolean mLocationPermissionGranted;
    private GoogleMap mGoogleMap;
    private String s_address, d_address, d2_address, d3_address;
    private Double s_latitude, d_latitude, d2_latitude, d3_latitude;
    private Double s_longitude, d_longitude, d2_longitude, d3_longitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private BottomSheetBehavior mBottomSheetBehavior;
    private Boolean isEditable = true;
    private Address home, work = null;

    private LocationPickPresenter<LocationPickActivity> presenter = new LocationPickPresenter<>();

    private EditText selectedEditText;
    boolean isEnableIdle = false;
    private boolean isMapKeyShow = false;
    protected GoogleApiClient mGoogleApiClient;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private String isSetting;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));

    private PlacesClient placesClient;

    ArrayList<AddedStop> addedStops = new ArrayList<>();
    private boolean isLocal = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_location_pick;
    }


    @Override
    public void initView() {
        buildGoogleApiClient();
        ButterKnife.bind(this);
        presenter.attachView(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        com.google.android.libraries.places.api.Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        placesClient = com.google.android.libraries.places.api.Places.createClient(this);

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String sourceEdit = getIntent().getStringExtra("srcClick");
        String destEdit = getIntent().getStringExtra("destClick");
        String fieldClicked = getIntent().getStringExtra("fieldClicked");
        String previousDestination = getIntent().getStringExtra("destination");

        if (isMapKeyShow) hideKeyboard();
        else showKeyboard();

        if (!TextUtils.isEmpty(fieldClicked)){
            source.setText(previousDestination);
            selectedEditText = source;
        }
        if (fieldClicked.equalsIgnoreCase("dropAddress")) {
            selectedEditText = stop1Destination;
            stop1Destination.requestFocus();
        }

        mBottomSheetBehavior = BottomSheetBehavior.from(locationBsLayout);
        mBottomSheetBehavior.setPeekHeight(150);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, placesClient);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        locationsRv.setLayoutManager(mLinearLayoutManager);
        locationsRv.setAdapter(mAutoCompleteAdapter);

        source.addTextChangedListener(filterTextWatcher);
        stop1Destination.addTextChangedListener(filterTextWatcher);
        stop2Destination.addTextChangedListener(filterTextWatcher);
        stop3Destination.addTextChangedListener(filterTextWatcher);

        source.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) selectedEditText = source;
        });

        stop1Destination.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                selectedEditText = stop1Destination;

            }
        });
        stop2Destination.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                selectedEditText = stop2Destination;
            }
        });
        stop3Destination.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                selectedEditText = stop3Destination;
            }
        });


        stop1Destination.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setResult(Activity.RESULT_OK, new Intent());
                finish();
                return true;
            }
            return false;
        });

        isSetting = getIntent().getStringExtra("isSetting");

        if (RIDE_REQUEST.containsKey("positions")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<AddedStop>>() {
            }.getType();
            String jsonText = RIDE_REQUEST.get("positions").toString();
            ArrayList<AddedStop> stopsArrayList = gson.fromJson(jsonText, type);
            for (int i = 0; i < stopsArrayList.size(); i++) {
                if (i == 0) {
                    d_address = stopsArrayList.get(i).d_address;
                    d_latitude = stopsArrayList.get(i).d_latitude;
                    d_longitude = stopsArrayList.get(i).d_longitude;
                    stop1Destination.setText(d_address);
                    stop2DestinationLayout.setVisibility(View.VISIBLE);
                    stop1Add.setVisibility(View.GONE);
                    stop1Reset.setVisibility(View.VISIBLE);
                } else if (i == 1) {
                    d2_address = stopsArrayList.get(i).d_address;
                    d2_latitude = stopsArrayList.get(i).d_latitude;
                    d2_longitude = stopsArrayList.get(i).d_longitude;
                    stop2Destination.setText(d2_address);
                    stop3DestinationLayout.setVisibility(View.VISIBLE);
                    stop2Add.setVisibility(View.GONE);

//                    stop1Add.setVisibility(View.VISIBLE);

                    stop2Reset.setVisibility(View.VISIBLE);

                } else if (i == 2) {
                    d3_address = stopsArrayList.get(i).d_address;
                    d3_latitude = stopsArrayList.get(i).d_latitude;
                    d3_longitude = stopsArrayList.get(i).d_longitude;
                    stop3Destination.setText(d3_address);
                    stop3Add.setVisibility(View.GONE);

//                    stop2Add.setVisibility(View.VISIBLE);

                    stop3Reset.setVisibility(View.VISIBLE);

                }
            }
        }


//        stop1Destination.setText(RIDE_REQUEST.containsKey("d_address")
//                ? TextUtils.isEmpty(Objects.requireNonNull(RIDE_REQUEST.get("d_address")).toString())
//                ? ""
//                : String.valueOf(RIDE_REQUEST.get("d_address"))
//                : "");
        if (!Objects.requireNonNull(getIntent().getExtras()).getBoolean("isHideDestination")) {
            /*source.setText(RIDE_REQUEST.containsKey("s_address")
                    ? TextUtils.isEmpty(Objects.requireNonNull(RIDE_REQUEST.get("s_address")).toString())
                    ? ""
                    : String.valueOf(RIDE_REQUEST.get("s_address"))
                    : "");*/
        } else source.setHint(getString(R.string.select_location));


        locationsRv.addOnItemTouchListener(new RecyclerItemClickListener(this, (view, position) -> {
                    if (mAutoCompleteAdapter.getItemCount() == 0) return;
                    final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                    String placeId = null;
                    if (item != null) {
                        placeId = String.valueOf(item.placeId);
                    }

                    Log.e("LocationPickActivity", "Autocomplete item selected: " + item.address);


                    List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS
                            , Place.Field.LAT_LNG);

                    FetchPlaceRequest request = null;
                    if (placeId != null) {
                        request = FetchPlaceRequest.builder(placeId, placeFields)
                                .build();
                    }

                    if (request != null) {
                        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(FetchPlaceResponse task) {

                                LatLng latLng = task.getPlace().getLatLng();
                                isLocationRvClick = true;
                                isMapKeyShow = false;
                                isSettingLocationClick = true;
                                isLocal = true;
                                setLocationText(String.valueOf(item.address), latLng,
                                        isLocationRvClick, isSettingLocationClick);
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "SOMETHING WRONG", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


//                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
//                    placeResult.setResultCallback(places -> {
//
//                        Log.e("LocationPickActivity", "Places count: " + places.getCount());
//
//
//                        if (places.getCount() == 1) {
//                            LatLng latLng = places.get(0).getLatLng();
//                            isLocationRvClick = true;
//                            isMapKeyShow = false;
//                            isSettingLocationClick = true;
//                            setLocationText(String.valueOf(item.address), latLng,
//                                    isLocationRvClick, isSettingLocationClick);
//                            //Toast.makeText(getApplicationContext(), String.valueOf(places.get(0).getLatLng()), Toast.LENGTH_SHORT).show();
//                        } else
//                            Toast.makeText(getApplicationContext(), "SOMETHING WRONG", Toast.LENGTH_SHORT).show();
//                    });

                    Log.e("LocationPickActivity", "Clicked: " + item.address);
                    Log.e("LocationPickActivity", "Called getPlaceById to get Place details for " + item.placeId);
                })
        );

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            stop1DestinationLayout.setVisibility(extras.getBoolean("isHideDestination", false) ? View.GONE : View.VISIBLE);
            stop1Add.setVisibility(extras.getBoolean("isHideDestination", false) ? View.GONE : View.VISIBLE);
            stop1txt.setVisibility(extras.getBoolean("isHideDestination", false) ? View.GONE : View.VISIBLE);
            source.setHint(getString(R.string.select_location));
        } else source.setHint(getString(R.string.pickup_location));
        presenter.address();
    }

    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback
            = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();

                LatLng latLng = places.get(0).getLatLng();
                isLocationRvClick = true;
                isMapKeyShow = false;
                isSettingLocationClick = true;
                setLocationText(String.valueOf(places.get(1).getAddress()), latLng,
                        isLocationRvClick, isSettingLocationClick);

                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                return;
            }
        }
    };

    private void setLocationText(String address, LatLng latLng, boolean isLocationRvClick,
                                 boolean isSettingLocationClick) {
        if (address != null && latLng != null) {
            isEditable = false;
            selectedEditText.setText(address);
            isEditable = true;

            if (selectedEditText.getTag().equals("source")) {

                s_address = address;
                s_latitude = latLng.latitude;
                s_longitude = latLng.longitude;

//                s_latitude = 33.6844;
//                s_longitude = 73.0479;

                RIDE_REQUEST.put("s_address", address);
                RIDE_REQUEST.put("s_latitude", latLng.latitude);
                RIDE_REQUEST.put("s_longitude", latLng.longitude);
            } else if (selectedEditText.getTag().equals("stop1Destination")) {
                d_address = address;
                d_latitude = latLng.latitude;
                d_longitude = latLng.longitude;

                RIDE_REQUEST.put("d_address", address);
                RIDE_REQUEST.put("d_latitude", latLng.latitude);
                RIDE_REQUEST.put("d_longitude", latLng.longitude);

               /* RIDE_REQUEST.put("d_address", address);
                RIDE_REQUEST.put("d_latitude", latLng.latitude);
                RIDE_REQUEST.put("d_longitude", latLng.longitude);*/
               /* if (stopsCount() == 1) {
                    Gson gson = new Gson();
                    String destinationStops = gson.toJson(addedStops);
                    RIDE_REQUEST.put("positions", destinationStops);
                    RIDE_REQUEST.remove("d_address");
                    RIDE_REQUEST.remove("d_latitude");
                    RIDE_REQUEST.remove("d_longitude");
                    *//*if(isLocal){
                         Intent intent = new Intent();
                    intent.putExtra("positions", destinationStops);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    }*//*

                }else{

                }*/

                /*if (isLocationRvClick) {
                    //  Done functionality called...
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }*/
            } else if (selectedEditText.getTag().equals("stop2Destination")) {
                d2_address = address;
                d2_latitude = latLng.latitude;
                d2_longitude = latLng.longitude;

                RIDE_REQUEST.put("d2_address", address);
                RIDE_REQUEST.put("d2_latitude", latLng.latitude);
                RIDE_REQUEST.put("d2_longitude", latLng.longitude);

                /*if (isLocationRvClick) {
                    //  Done functionality called...
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }*/
            } else if (selectedEditText.getTag().equals("stop3Destination")) {
                d3_address = address;
                d3_latitude = latLng.latitude;
                d3_longitude = latLng.longitude;

                RIDE_REQUEST.put("d3_address", address);
                RIDE_REQUEST.put("d3_latitude", latLng.latitude);
                RIDE_REQUEST.put("d3_longitude", latLng.longitude);

               /* if (isLocationRvClick) {
                    //  Done functionality called...
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }*/
            }
        } else {
            isEditable = false;
            selectedEditText.setText("");
            locationsRv.setVisibility(View.GONE);
            isEditable = true;

            if (selectedEditText.getTag().equals("source")) {
                RIDE_REQUEST.remove("s_address");
                RIDE_REQUEST.remove("s_latitude");
                RIDE_REQUEST.remove("s_longitude");
            }

            if (selectedEditText.getTag().equals("stop1Destination")) {
                RIDE_REQUEST.remove("d_address");
                RIDE_REQUEST.remove("d_latitude");
                RIDE_REQUEST.remove("d_longitude");
            }
        }

        if (isSettingLocationClick) {
            hideKeyboard();
            locationsRv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            Log.v("Google API", "Connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            Log.v("Google API", "Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @OnClick({R.id.sourceTv, R.id.reset_source, R.id.home_address_layout, R.id.work_address_layout,
            R.id.addstop_1_destination_layout, R.id.addstop_1_destination, R.id.addstop_1_reset_destination, R.id.addstop_1_add, R.id.addstop_1_reset,
            R.id.addstop_2_destination_layout, R.id.addstop_2_destination, R.id.addstop_2_reset_destination, R.id.addstop_2_add, R.id.addstop_2_reset,
            R.id.addstop_3_destination_layout, R.id.addstop_3_destination, R.id.addstop_3_reset_destination, R.id.addstop_3_add, R.id.addstop_3_reset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sourceTv:
                break;

            case R.id.reset_source:
                selectedEditText = source;
                source.requestFocus();
                setLocationText(null, null, isLocationRvClick, isSettingLocationClick);
                break;

            case R.id.home_address_layout:
                if (home != null)
                    setLocationText(home.getAddress(), new LatLng(home.getLatitude(), home.getLongitude()), isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.work_address_layout:
                if (work != null)
                    setLocationText(work.getAddress(), new LatLng(work.getLatitude(), work.getLongitude()), isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.destination:
                break;
            case R.id.addstop_1_destination:
            case R.id.addstop_1_destination_layout:
                selectedEditText = stop1Destination;
                stop1Destination.requestFocus();
                break;
            case R.id.addstop_1_reset_destination:
                selectedEditText = stop1Destination;
                stop1Destination.requestFocus();
                setLocationText(null, null, isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.addstop_1_reset:
                stop2DestinationLayout.setVisibility(View.GONE);
                stop1Add.setVisibility(View.VISIBLE);
                stop1Reset.setVisibility(View.GONE);
                break;
            case R.id.addstop_1_add:
                stop2DestinationLayout.setVisibility(View.VISIBLE);
                stop1Add.setVisibility(View.GONE);
                stop1Reset.setVisibility(View.VISIBLE);
//                stop11.setVisibility(View.VISIBLE);
                break;
            case R.id.addstop_2_destination_layout:
                selectedEditText = stop2Destination;
                stop2Destination.requestFocus();

                break;
            case R.id.addstop_2_destination:
                selectedEditText = stop2Destination;
                stop2Destination.requestFocus();

                break;
            case R.id.addstop_2_reset_destination:
                selectedEditText = stop2Destination;
                stop2Destination.requestFocus();

                setLocationText(null, null, isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.addstop_2_reset:
                stop2DestinationLayout.setVisibility(View.GONE);
                stop2Add.setVisibility(View.VISIBLE);
                stop2Reset.setVisibility(View.GONE);
                stop2Destination.setText("");
                stop1Add.setVisibility(View.VISIBLE);
                stop1Reset.setVisibility(View.GONE);

                if (stop3Reset.getVisibility() == View.VISIBLE) {
                    stop2Add.setVisibility(View.GONE);
                    stop2Reset.setVisibility(View.VISIBLE);
                }
//                stop11.setVisibility(View.GONE);
//                stop22.setVisibility(View.GONE);
                break;
            case R.id.addstop_2_add:
                stop3DestinationLayout.setVisibility(View.VISIBLE);
                stop2Add.setVisibility(View.GONE);
                stop2Reset.setVisibility(View.VISIBLE);
                stop3Add.setVisibility(View.GONE);
                stop3Reset.setVisibility(View.VISIBLE);

//                stop22.setVisibility(View.VISIBLE);

                break;
            case R.id.addstop_3_destination_layout:
                selectedEditText = stop3Destination;
                stop3Destination.requestFocus();

                break;
            case R.id.addstop_3_destination:
                selectedEditText = stop3Destination;
                stop3Destination.requestFocus();

                break;
            case R.id.addstop_3_reset_destination:
                selectedEditText = stop3Destination;
                stop3Destination.requestFocus();
                setLocationText(null, null, isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.addstop_3_reset:
                stop3DestinationLayout.setVisibility(View.GONE);
                stop2Add.setVisibility(View.GONE);
                stop2Reset.setVisibility(View.VISIBLE);
                stop3Destination.setText("");
                stop2Add.setVisibility(View.VISIBLE);
                stop2Reset.setVisibility(View.GONE);
//                stop22.setVisibility(View.GONE);
                break;
            case R.id.addstop_3_add:
                break;

        }
    }

    @Override
    public void onCameraIdle() {
        try {
            CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
            if (isEnableIdle) {
                String address = getAddress(cameraPosition.target);
                System.out.println("onCameraIdle " + address);
                isMapKeyShow = true;
                if (isMapKeyShow) hideKeyboard();
                else showKeyboard();
                if(!isLocal){
                setLocationText(address, cameraPosition.target, isLocationRvClick, isSettingLocationClick);
            }
            isLocal = false;
            }
            isEnableIdle = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
//            Google map custom style...
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            Log.d("Map:Style", "Can't find style. Error: ");
        }
        this.mGoogleMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()
                                ), DEFAULT_ZOOM));
                    } else {
                        Log.d("Map", "Current location is null. Using defaults.");
                        Log.e("Map", "Exception: %s", task.getException());
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude()
                        ), DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.setOnCameraMoveListener(this);
                mGoogleMap.setOnCameraIdleListener(this);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                    getDeviceLocation();
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("Error Code", String.valueOf(connectionResult.getErrorCode()));
        Toast.makeText(this, "API_NOT_CONNECTED", Toast.LENGTH_SHORT).show();
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            if (isEditable) if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                locationsRv.setVisibility(View.VISIBLE);
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (!mGoogleApiClient.isConnected()) {
                Log.e("ERROR", "API_NOT_CONNECTED");
            }
            if (s.toString().equals("")) {
                locationsRv.setVisibility(View.GONE);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    };

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_pick_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new RelativeSizeSpan(1.5f), 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spanString.length(), 0);
            spanString.setSpan(boldSpan, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            item.setTitle(spanString);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                try {
                    if (isSetting != null) {
                        if (isSetting.equalsIgnoreCase("homeSetting") ||
                                isSetting.equalsIgnoreCase("workSetting")) {
                            Intent intent = new Intent();
                            intent.putExtra("s_address", s_address);
                            intent.putExtra("s_latitude", s_latitude);
                            intent.putExtra("s_longitude", s_longitude);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else if (isSetting.equalsIgnoreCase("source") ||
                                isSetting.equalsIgnoreCase("destination")) {
                            if (stopsCount() > 0) {
                                Gson gson = new Gson();
                                String destinationStops = gson.toJson(addedStops);
                                RIDE_REQUEST.put("positions", destinationStops);
                                RIDE_REQUEST.remove("d_address");
                                RIDE_REQUEST.remove("d_latitude");
                                RIDE_REQUEST.remove("d_longitude");
                                Intent intent = new Intent();
                                intent.putExtra("positions", destinationStops);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            } else {
                                setResult(Activity.RESULT_OK, new Intent());
                                finish();
                            }

//                            else {
//                                if(isSetting.equalsIgnoreCase("destination")){
//                                    RIDE_REQUEST.put("d_address", d_address);
//                                    RIDE_REQUEST.put("d_latitude", d_latitude);
//                                    RIDE_REQUEST.put("d_longitude", d_longitude);
//                                    Intent intent = new Intent();
//                                    intent.putExtra("d_address", d_address);
//                                    intent.putExtra("d_latitude", d_latitude);
//                                    intent.putExtra("d_longitude", d_longitude);
//                                    setResult(Activity.RESULT_OK, intent);
//                                    finish();
//                                }else {
//                                    setResult(Activity.RESULT_OK, new Intent());
//                                    finish();
//                                }
//                            }


                        } else {
                            setResult(Activity.RESULT_OK, new Intent());
                            finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
//            case android.R.id.home:
//                Toast.makeText(getApplicationContext(), "Back button clicked", Toast.LENGTH_SHORT).show();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess(AddressResponse address) {
        if (address.getHome().isEmpty()) homeAddressLayout.setVisibility(View.GONE);
        else {
            home = address.getHome().get(address.getHome().size() - 1);
            homeAddress.setText(home.getAddress());
            homeAddressLayout.setVisibility(View.VISIBLE);
        }

        if (address.getWork().isEmpty()) workAddressLayout.setVisibility(View.GONE);
        else {
            work = address.getWork().get(address.getWork().size() - 1);
            workAddress.setText(work.getAddress());
            workAddressLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    private int stopsCount() {
        if (!stop1Destination.getText().toString().isEmpty()) {
            addedStops.add(new AddedStop(d_address, d_latitude, d_longitude));
        }

        if (!stop2Destination.getText().toString().isEmpty()) {
            addedStops.add(new AddedStop(d2_address, d2_latitude, d2_longitude));
        }

        if (!stop3Destination.getText().toString().isEmpty()) {
            addedStops.add(new AddedStop(d3_address, d3_latitude, d3_longitude));
        }

        return addedStops.size();

    }

}
