package com.sixtech.rider.ui.activity.past_trip_detail;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseActivity;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.model.Datum;
import com.sixtech.rider.data.network.model.Payment;
import com.sixtech.rider.data.network.model.Provider;
import com.sixtech.rider.data.network.model.Rating;
import com.sixtech.rider.ui.fragment.InvoiceDialogFragment;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class PastTripDetailActivity extends BaseActivity implements PastTripDetailsIView {

    @BindView(R.id.static_map)
    ImageView staticMap;
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.s_address)
    TextView sAddress;
    @BindView(R.id.d_address)
    TextView dAddress;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.finished_at)
    TextView finishedAt;
    @BindView(R.id.finished_at_time)
    TextView finishedAtTime;
    @BindView(R.id.booking_id)
    TextView bookingId;
    @BindView(R.id.payment_mode)
    TextView paymentMode;
    @BindView(R.id.payable)
    TextView payable;
    @BindView(R.id.user_comment)
    TextView userComment;
    @BindView(R.id.view_receipt)
    Button viewReceipt;
    @BindView(R.id.payment_image)
    ImageView paymentImage;

    private PastTripDetailsPresenter<PastTripDetailActivity> presenter = new PastTripDetailsPresenter();

    @Override
    public int getLayoutId() {
        return R.layout.activity_past_trip_detail;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        // Activity title will be updated after the locale has changed in Runtime
        setTitle(getString(R.string.past_trip_details));

        if (DATUM != null) {
            Datum datum = DATUM;
            showLoading();
            presenter.getPastTripDetails(datum.getId());
        }
    }

    @OnClick({R.id.view_receipt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.view_receipt:
                InvoiceDialogFragment fragment = new InvoiceDialogFragment();
                fragment.show(getSupportFragmentManager(), fragment.getTag());
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(List<Datum> pastTripDetails) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Datum datum = pastTripDetails.get(0);
        if (datum != null) {

            Rating providerRating = datum.getRating();
            bookingId.setText(datum.getBookingId());

            String strCurrentDate = datum.getFinishedAt();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            SimpleDateFormat timeFormat;
            Date newDate = null;
            try {
                newDate = format.parse(strCurrentDate);
                format = new SimpleDateFormat("dd MMM yyyy");
                timeFormat = new SimpleDateFormat("hh:mm a");
                String date = format.format(newDate);
                String time = timeFormat.format(newDate);
                finishedAt.setText(date);
                finishedAtTime.setText(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Glide.with(activity())
                    .load(datum.getStaticMap())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                            .dontAnimate().error(R.drawable.ic_launcher_background))
                    .into(staticMap);
            sAddress.setText(datum.getSAddress());

            if (datum.getStops() != null && datum.getStops().size() > 0) {
                int size = datum.getStops().size() - 1;
//                dAddress.setText(datum.getStops().get(size).getDAddress());
                StringBuilder destinationStops = new StringBuilder();
                for (int i = 0; i < datum.getStops().size(); i++) {
                    if (i == 0) {
                        destinationStops.append("Stop 1: \n");
                        destinationStops.append(datum.getStops().get(i).getDAddress());
                    } else if (i == 1) {
                        destinationStops.append("\n");
                        destinationStops.append("Stop 2: \n");
                        destinationStops.append(datum.getStops().get(i).getDAddress());
                    } else if (i == 2) {
                        destinationStops.append("\n");
                        destinationStops.append("Stop 3: \n");
                        destinationStops.append(datum.getStops().get(i).getDAddress());

                    }
                }
                dAddress.setText(destinationStops.toString());
            } else {
                dAddress.setText(datum.getDAddress());
            }


            Provider provider = datum.getProvider();
            if (provider != null) {
                Glide.with(activity())
                        .load(provider.getAvatar())
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                                .dontAnimate().error(R.drawable.ic_user_placeholder))
                        .into(avatar);
                name.setText(String.format("%s %s", provider.getFirstName(), provider.getLastName()));
            }
            if (providerRating != null) {
                userComment.setText(providerRating.getProviderComment());
                rating.setRating(providerRating.getProviderRating());
            }

            initPayment(datum.getPaymentMode(), paymentMode, paymentImage);

            Payment payment = datum.getPayment();
            if (payment != null)
                payable.setText(SharedHelper.getKey(this, "currency") + " " +
                        getNewNumberFormat(Double.parseDouble(
                                String.valueOf(BigDecimal.valueOf(Double.parseDouble(payment.getFlatRate())).add(
                                        BigDecimal.valueOf(payment.getTips()))))));
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }
}
