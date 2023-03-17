package com.sixtech.rider.ui.fragment.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.sixtech.rider.GlideApp;
import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseActivity;
import com.sixtech.rider.base.BaseFragment;
import com.sixtech.rider.common.EqualSpacingItemDecoration;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.APIClient;
import com.sixtech.rider.data.network.model.EstimateFare;
import com.sixtech.rider.data.network.model.Message;
import com.sixtech.rider.data.network.model.Provider;
import com.sixtech.rider.data.network.model.Service;
import com.sixtech.rider.ui.activity.main.MainActivity;
import com.sixtech.rider.ui.activity.payment.PaymentActivity;
import com.sixtech.rider.ui.adapter.ServiceAdapter;
import com.sixtech.rider.ui.fragment.RateCardFragment;
import com.sixtech.rider.ui.fragment.book_ride.BookRideFragment;
import com.sixtech.rider.ui.fragment.schedule.ScheduleFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PagerContainer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sixtech.rider.base.BaseActivity.RIDE_REQUEST;
import static com.sixtech.rider.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class ServiceFragment extends BaseFragment implements ServiceIView {

    private final ServicePresenter<ServiceFragment> presenter = new ServicePresenter<>();
    //private BottomSheetBehavior mBottomSheetBehavior;

    @BindView(R.id.capacity)
    TextView capacity;
    @BindView(R.id.payment_type)
    TextView paymentType;
    @BindView(R.id.error_layout)
    TextView errorLayout;
    Unbinder unbinder;
    ServicePagerAdapter adapter;
    List<Service> mServices = new ArrayList<>();
    @BindView(R.id.use_wallet)
    CheckBox useWallet;
    @BindView(R.id.wallet_balance)
    TextView walletBalance;
    @BindView(R.id.surge_value)
    TextView surgeValue;
    @BindView(R.id.tv_demand)
    TextView tvDemand;
    ViewPager pager;

    private boolean isFromAdapter = true;
    private int servicePos = 0;
    private EstimateFare mEstimateFare;
    private double walletAmount;
    private int surge;
    int isWithCable = 0;

    public ServiceFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        String lat = String.valueOf(RIDE_REQUEST.get("s_latitude"));
        String lon = String.valueOf(RIDE_REQUEST.get("s_longitude"));

//        String lat = "33.6844";
//        String lon = "73.0479";

        Log.e("tag", "source lat is : " + lat);
        Log.e("tag", "source lang is : " + lon);

        presenter.services(lat, lon);
        return view;
    }

    /**
     * https://android-arsenal.com/details/1/3530
     */
    private void setupCoverFlow() {
        View view = getView();
        if (view == null) {
            return;
        }

        PagerContainer mContainer = view.findViewById(R.id.pager_container);

        pager = mContainer.getViewPager();

        adapter = new ServicePagerAdapter(getActivity(), mServices, mListener);
        pager.setAdapter(adapter);

        pager.setOffscreenPageLimit(adapter.getCount());

        pager.setClipChildren(false);

        mContainer.setPageItemClickListener((view1, position) -> {
            //Toast.makeText(Normal2Activity.this,"position:" + position,Toast.LENGTH_SHORT).show();
        });


        boolean showRotate = true;

        if(showRotate){
            new CoverFlow.Builder()
                    .with(pager)
                    .scale(0.3f)
                    .pagerMargin(0f)
                    .spaceSize(0f)
                    .rotationY(25f)
                    .build();
        }
    }

    @OnClick({R.id.payment_type, R.id.get_pricing, R.id.schedule_ride, R.id.ride_now})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.payment_type:
                ((MainActivity) Objects.requireNonNull(getActivity())).updatePaymentEntities();
                startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
                break;
            case R.id.get_pricing:
                if (adapter != null) {
                    isFromAdapter = false;
                    Service service = adapter.getSelectedService();
                    if (service != null) {
                        RIDE_REQUEST.put("service_type", service.getId());
                        if (RIDE_REQUEST.containsKey("service_type") && RIDE_REQUEST.get("service_type") != null) {
                            showLoading();
                            estimatedApiCall();
                            Log.e("tag", "proceed btn click");
                        }
                    }
                }
                break;
            case R.id.schedule_ride:
                ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(new ScheduleFragment());
                break;
            case R.id.ride_now:
                sendRequest();
                break;
            default:
                break;
        }
    }

    private void estimatedApiCall() {
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

                    Log.d("EstimateFare Response", "" + estimateFareString);

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
                        ((MainActivity) getActivity()).updateFare(estimateFare);
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
                                //BookRideFragment bookRideFragment = new BookRideFragment();
                                //bookRideFragment.setArguments(bundle);
                                //((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(bookRideFragment);
                                RIDE_REQUEST.put("distance", estimateFare.getDistance());
                                ((MainActivity) getActivity()).updateFare(estimateFare);
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

    @Override
    public void onSuccess(List<Service> services) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (services != null && !services.isEmpty()) {
            RIDE_REQUEST.put("service_type", 1);
            // estimatedApiCall();
            mServices.clear();
            mServices.addAll(services);

            setupCoverFlow();

            if (adapter != null) {
                Service mService = adapter.getSelectedService();
                if (mService != null) RIDE_REQUEST.put("service_type", mService.getId());
            }
            mListener.whenClicked(0);
            //estimatedApiCall();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(EstimateFare estimateFare) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (estimateFare != null) {
            mEstimateFare = estimateFare;
            double walletAmount = estimateFare.getWalletBalance();
            SharedHelper.putKey(getContext(), "wallet",
                    String.valueOf(estimateFare.getWalletBalance()));
            if (walletAmount == 0) walletBalance.setVisibility(View.GONE);
            else {
                walletBalance.setVisibility(View.VISIBLE);
                walletBalance.setText(
                        SharedHelper.getKey(getContext(), "currency") + " "
                                + getNewNumberFormat(Double.parseDouble(String.valueOf(walletAmount))));
            }
            if (estimateFare.getSurge() == 0) {
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
                adapter.notifyDataSetChanged();
                ((MainActivity) getActivity()).updateFare(estimateFare);
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
                        bundle.putDouble("use_wallet", walletAmount);
                        //BookRideFragment bookRideFragment = new BookRideFragment();
                        //bookRideFragment.setArguments(bundle);
                        //((MainActivity) getActivity()).changeFragment(bookRideFragment);
                        RIDE_REQUEST.put("distance", estimateFare.getDistance());
                        ((MainActivity) getActivity()).updateFare(estimateFare);
                    }
                }
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put("payment_mode", data.getStringExtra("payment_mode"));
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                RIDE_REQUEST.put("card_id", data.getStringExtra("card_id"));
                RIDE_REQUEST.put("card_last_four", data.getStringExtra("card_last_four"));
            }
            initPayment(paymentType);
        }
    }

    private final ServiceListener mListener = pos -> {
        isFromAdapter = true;
        servicePos = pos;
        if (mServices.get(pos).getId() == 6) {
            showInstructionDialog(pos);
        } else if (mServices.get(pos).getId() == 7) {
            showBootDialog(pos);
        } else {
            RIDE_REQUEST.put("service_type", mServices.get(pos).getId());
            showLoading();
            estimatedApiCall();
            List<Provider> providers = new ArrayList<>();
            for (Provider provider : SharedHelper.getProviders(Objects.requireNonNull(getActivity())))
                if (provider.getProviderService().getServiceTypeId().equals(mServices.get(pos).getId()))
                    providers.add(provider);

            ((MainActivity) getActivity()).setSpecificProviders(providers);

        }

    };

    public interface ServiceListener {
        void whenClicked(int pos);
    }

    private void sendRequest() {
        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("use_wallet", useWallet.isChecked() ? 1 : 0);
        BaseActivity.useWalletVar=useWallet.isChecked() ? 1 : 0;
        showLoading();
        presenter.rideNow(map);
    }

    @Override
    public void onSuccess(@NonNull Message object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        activity().sendBroadcast(new Intent("INTENT_FILTER"));
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initPayment(paymentType);
    }


    private void showInstructionDialog(int pos) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_tow_truck);
        EditText instructionsEt = dialog.findViewById(R.id.etInstructions);

        RadioGroup radioGroup = dialog.findViewById(R.id.rg_c);
        TextView tvProceed = dialog.findViewById(R.id.tvProceed);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_wc) {
                    isWithCable = 1;
                } else if (checkedId == R.id.rb_woc) {
                    isWithCable = 0;
                }
            }

        });


        tvProceed.setOnClickListener(v -> {
            String instructions = instructionsEt.getText().toString();
            RIDE_REQUEST.put("service_type", mServices.get(pos).getId());
            RIDE_REQUEST.put("instructions", instructions);
            RIDE_REQUEST.put("is_booster_cable", isWithCable);
            showLoading();
            estimatedApiCall();
            List<Provider> providers = new ArrayList<>();
            for (Provider provider : SharedHelper.getProviders(Objects.requireNonNull(getActivity())))
                if (provider.getProviderService().getServiceTypeId().equals(mServices.get(pos).getId()))
                    providers.add(provider);

            ((MainActivity) getActivity()).setSpecificProviders(providers);

            dialog.dismiss();
        });

        tvCancel.setOnClickListener(v -> {

            RIDE_REQUEST.put("service_type", mServices.get(pos).getId());
            RIDE_REQUEST.put("instructions", "");
            RIDE_REQUEST.put("is_booster_cable", 0);
            showLoading();
            estimatedApiCall();
            List<Provider> providers = new ArrayList<>();
            for (Provider provider : SharedHelper.getProviders(Objects.requireNonNull(getActivity())))
                if (provider.getProviderService().getServiceTypeId().equals(mServices.get(pos).getId()))
                    providers.add(provider);

            ((MainActivity) getActivity()).setSpecificProviders(providers);
            dialog.dismiss();
        });

        dialog.show();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);
    }

    private void showBootDialog(int pos) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_boot);
        RadioGroup radioGroup = dialog.findViewById(R.id.rg_c);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_wc) {
                    isWithCable = 1;
                } else if (checkedId == R.id.rb_woc) {
                    isWithCable = 0;
                }
            }

        });
        TextView tvProceed = dialog.findViewById(R.id.tvProceed);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        tvProceed.setOnClickListener(v -> {
            RIDE_REQUEST.put("service_type", mServices.get(pos).getId());
            RIDE_REQUEST.put("instructions", "");
            RIDE_REQUEST.put("is_booster_cable", isWithCable);
            showLoading();
            estimatedApiCall();
            List<Provider> providers = new ArrayList<>();
            for (Provider provider : SharedHelper.getProviders(Objects.requireNonNull(getActivity())))
                if (provider.getProviderService().getServiceTypeId().equals(mServices.get(pos).getId()))
                    providers.add(provider);

            ((MainActivity) getActivity()).setSpecificProviders(providers);

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

    private class ServicePagerAdapter extends PagerAdapter {

        private final Context context;
        private LayoutInflater li;
        List<Service> mServices;
        private int lastCheckedPos;
        private ServiceFragment.ServiceListener mListener;

        public ServicePagerAdapter(Context context1, List<Service> services1,
                                   ServiceFragment.ServiceListener listener) {
            context = context1;
            li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mServices = services1;
            mListener = listener;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

           // li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = li.inflate(R.layout.item_select_ride, container, false);

            Service obj = mServices.get(position);

            TextView name = view.findViewById(R.id.service_name);
            TextView value = view.findViewById(R.id.capacity_value);
            ImageView imageView = view.findViewById(R.id.img_service_ride);

            name.setText(obj.getName());
            value.setText(obj.getCapacity() + " person can ride");
            imageView.setImageResource(R.drawable.ic_expert_drivers);
            //imageView.setColorFilter();

            int visibility = lastCheckedPos == position? View.VISIBLE : View.GONE;
            view.findViewById(R.id.txtSelectedService).setVisibility(visibility);
            Log.v("coverr", "coverr "+position);
            /*GlideApp.with(context)
                    .load(obj.getImage())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_car).dontAnimate().error(R.drawable.ic_car))
                    .into(imageView);*/
            imageView.setImageResource(position == 1? R.drawable.ic_premium_car : R.drawable.ic_comfort);
            view.setOnClickListener(v -> {
                lastCheckedPos = position;
                notifyDataSetChanged();
                avoidStickToPageEachOther();
                mListener.whenClicked(position);
            });
            ((ViewPager) container).addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public int getCount() {
            return mServices.size();
        }


        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return (view == object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        public Service getSelectedService() {
            if (mServices.size() > 0) return mServices.get(lastCheckedPos);
            else return null;
        }

        public void setEstimateFare(EstimateFare estimateFare) {
            //this.estimateFare = estimateFare;
            notifyDataSetChanged();
            avoidStickToPageEachOther();
        }

        private void avoidStickToPageEachOther() {
            pager.postDelayed(()-> {
                pager.beginFakeDrag();
                // I dont know what this number mean
                pager.fakeDragBy(10);
                pager.postDelayed(() -> pager.endFakeDrag(), 100);
            }, 100);
        }
    }
}
