package com.sixtech.rider.ui.fragment;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseFragment;
import com.sixtech.rider.common.Constants;
import com.sixtech.rider.common.Utilities;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.model.Service;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RateCardFragment extends BaseFragment {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.capacity)
    TextView capacity;
    @BindView(R.id.base_fare)
    TextView baseFare;
    @BindView(R.id.fare_type)
    TextView fareType;
    @BindView(R.id.fare_km)
    TextView fareKm;
    @BindView(R.id.tvFareDistance)
    TextView tvFareDistance;

    Unbinder unbinder;

    public static Service SERVICE = new Service();

    public RateCardFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_rate_card;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        initView(SERVICE);
        return view;
    }

    @SuppressLint("SetTextI18n")
    void initView(@NonNull Service service) {
        capacity.setText(String.valueOf(service.getCapacity()));
        baseFare.setText(SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency") + " " +
                getNewNumberFormat(service.getFixed()));
        fareKm.setText(SharedHelper.getKey(getContext(), "currency") + " " +
                getNewNumberFormat(service.getPrice()));

        //      MIN,    HOUR,   DISTANCE,   DISTANCEMIN,    DISTANCEHOUR
        switch (service.getCalculator()) {
            case Utilities.InvoiceFare.min:
                fareType.setText(getString(R.string.min));
                break;
            case Utilities.InvoiceFare.hour:
                fareType.setText(getString(R.string.hour));
                break;
            case Utilities.InvoiceFare.distance:
                fareType.setText(getString(R.string.distance));
                break;
            case Utilities.InvoiceFare.distanceMin:
                fareType.setText(getString(R.string.distancemin));
                break;
            case Utilities.InvoiceFare.distanceHour:
                fareType.setText(getString(R.string.distancehour));
                break;
            default:
                fareType.setText(getString(R.string.min));
                break;
        }

        if (SharedHelper.getKey(getContext(), "measurementType").equalsIgnoreCase(Constants.MeasurementType.KM))
            tvFareDistance.setText(getString(R.string.fare_km));
        else tvFareDistance.setText(getString(R.string.fare_km));

        YoYo.with(Techniques.BounceInRight)
                .duration(1000)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(new AccelerateDecelerateInterpolator())
                .playOn(image);
        Glide.with(activity()).load(service.getImage()).into(image);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.done)
    public void onViewClicked() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
