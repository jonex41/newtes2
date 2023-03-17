package com.sixtech.rider.ui.fragment;

import static com.sixtech.rider.base.BaseActivity.DATUM;
import static com.sixtech.rider.data.SharedHelper.getKey;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseBottomSheetDialogFragment;
import com.sixtech.rider.common.Constants;
import com.sixtech.rider.common.Utilities;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.model.Datum;
import com.sixtech.rider.data.network.model.Payment;
import com.sixtech.rider.data.network.model.ServiceType;

import java.math.BigDecimal;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InvoiceDialogFragment extends BaseBottomSheetDialogFragment {

    @BindView(R.id.booking_id)
    TextView bookingId;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.llTravelTime)
    TextView llTravelTime;
    @BindView(R.id.travel_time)
    TextView travelTime;
    @BindView(R.id.fixed)
    TextView fixed;
    @BindView(R.id.distance_fare)
    TextView distanceFare;
    @BindView(R.id.admin_fee)
    TextView admin_fee;
    @BindView(R.id.tax)
    TextView tax;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.tvCommission)
    TextView tvCommission;
    @BindView(R.id.payable)
    TextView payable;
    @BindView(R.id.close)
    Button close;
    @BindView(R.id.time_fare)
    TextView timeFare;
    @BindView(R.id.tips)
    TextView tips;
    @BindView(R.id.tips_layout)
    LinearLayout tipsLayout;
    @BindView(R.id.distance_constainer)
    LinearLayout distanceContainer;
    @BindView(R.id.adminFee_constainer)
    LinearLayout adminFeeContainer;
    @BindView(R.id.time_container)
    LinearLayout timeContainer;
    @BindView(R.id.wallet_deduction)
    TextView walletDeduction;
    @BindView(R.id.discount)
    TextView discount;
    @BindView(R.id.walletLayout)
    LinearLayout walletLayout;
    @BindView(R.id.discountLayout)
    LinearLayout discountLayout;

    public InvoiceDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_invoice_dialog;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void initView(View view) {
        ButterKnife.bind(this, view);

        if (DATUM != null) {
            Datum datum = DATUM;
            bookingId.setText(datum.getBookingId());
            if (SharedHelper.getKey(getContext(), "measurementType").equalsIgnoreCase
                    (Constants.MeasurementType.KM)) {
                if (datum.getDistance() > 1 || datum.getDistance() > 1.0) {
                    distance.setText(String.format("%s %s", datum.getDistance(), Constants.MeasurementType.KM));
                } else {
                    distance.setText(String.format("%s %s", datum.getDistance(), getString(R.string.km)));
                }
            } else {
                if (datum.getDistance() > 1 || datum.getDistance() > 1.0) {
                    distance.setText(String.format("%s %s", datum.getDistance(), Constants.MeasurementType.KM));
                } else {
                    distance.setText(String.format("%s %s", datum.getDistance(), getString(R.string.km)));
                }
            }
            //travelTime.setText(getString(R.string._min, datum.getTravelTime()));

            try {
                if (datum.getTravelTime() != null && Double.parseDouble(datum.getTravelTime()) > 0) {
                    llTravelTime.setVisibility(View.VISIBLE);
                    travelTime.setText(datum.getTravelTime() + " " + getString(R.string._min));
                } else llTravelTime.setVisibility(View.GONE);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                llTravelTime.setVisibility(View.VISIBLE);
                travelTime.setText(getString(R.string._min, datum.getTravelTime()));
            }

            Payment payment = datum.getPayment();
            if (payment != null) {
                if (datum.getIsRound() == 0) {
                    fixed.setText(String.format("%s %s",
                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getFixed())));

                    distanceFare.setText(String.format("%s %s",
                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(Double.parseDouble(String.valueOf(BigDecimal.valueOf(payment.getDistance())
                                    .subtract(BigDecimal.valueOf(payment.getFixed())).subtract(BigDecimal.valueOf(payment.getAdminFee())))))));


                    tvCommission.setText(String.format("%s %s",
                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getCommision())));

                    tax.setText(String.format("%s %s",
                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getTax())));
                } else {
                    fixed.setText(String.format("%s %s",
                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getFixed())));

                    distanceFare.setText(String.format("%s %s",
                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat((Double.parseDouble(String.valueOf(BigDecimal.valueOf(payment.getDistance())
                                    .subtract(BigDecimal.valueOf(payment.getFixed())) .subtract(BigDecimal.valueOf(payment.getAdminFee()))))) * 2)));

                    tvCommission.setText(String.format("%s %s",
                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getCommision() * 2)));

                    tax.setText(String.format("%s %s",
                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getTax() * 2)));
                }

                if (payment.getAdminFee() == 0 || payment.getAdminFee() == 0.0) {
                    adminFeeContainer.setVisibility(View.GONE);
                } else {
                    adminFeeContainer.setVisibility(View.VISIBLE);
                    admin_fee.setText(String.format("%s %s",
                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getAdminFee())));
                }

                Double pastTripTotal = Double.parseDouble(payment.getFlatRate()) + payment.getTips();
                total.setText(String.format("%s %s",
                        SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                        getNewNumberFormat(pastTripTotal)));
                Double payableValue = Double.parseDouble(payment.getFlatRate()) - (payment.getWallet() + payment.getDiscount());
                Double pastTripPayable = payableValue + payment.getTips();
                payable.setText(String.format("%s %s",
                        SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                        getNewNumberFormat(pastTripPayable)));

                if (payment.getTips() == 0 || payment.getTips() == 0.0) {
                    tipsLayout.setVisibility(View.GONE);
                } else {
                    tipsLayout.setVisibility(View.VISIBLE);
                    tips.setText(String.format("%s %s",
                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                            payment.getTips()));
                }

                if (payment.getWallet() == 0 || payment.getWallet() == 0.0) {
                    walletLayout.setVisibility(View.GONE);
                } else {
                    walletLayout.setVisibility(View.VISIBLE);
                    walletDeduction.setText(String.format("%s %s",
                            getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getWallet())));
                }
                if (payment.getDiscount() == 0 || payment.getDiscount() == 0.0) {
                    discountLayout.setVisibility(View.GONE);
                } else {
                    discountLayout.setVisibility(View.VISIBLE);
                    discount.setText(String.format("%s -%s",
                            getKey(Objects.requireNonNull(getContext()), "currency"),
                            getNewNumberFormat(payment.getDiscount())));
                }

                ServiceType serviceType = datum.getServiceType();
                if (serviceType != null) {
                    String serviceCalculator = serviceType.getCalculator();
                    switch (serviceCalculator) {
                        case Utilities.InvoiceFare.min:
                            distanceContainer.setVisibility(View.GONE);
                            timeContainer.setVisibility(View.VISIBLE);
                            timeFare.setText(String.format("%s %s",
                                    SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                    getNewNumberFormat(payment.getMinute())));
                            break;
                        case Utilities.InvoiceFare.hour:
                            distanceContainer.setVisibility(View.GONE);
                            timeContainer.setVisibility(View.VISIBLE);
                            timeFare.setText(String.format("%s %s",
                                    SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                    getNewNumberFormat(payment.getHour())));
                            break;
                        case Utilities.InvoiceFare.distance:
                            timeContainer.setVisibility(View.GONE);
                            if (payment.getDistance() == 0.0 || payment.getDistance() == 0) {
                                distanceContainer.setVisibility(View.GONE);
                            } else {
                                distanceContainer.setVisibility(View.VISIBLE);
                                if (datum.getIsRound() == 0) {
                                    distanceFare.setText(String.format("%s %s",
                                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                            getNewNumberFormat(payment.getDistance())));
                                } else {
                                    distanceFare.setText(String.format("%s %s",
                                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                            getNewNumberFormat(payment.getDistance() * 2)));
                                }
                            }
                            break;
                        case Utilities.InvoiceFare.distanceMin:
                            timeContainer.setVisibility(View.VISIBLE);
                            timeFare.setText(String.format("%s %s",
                                    SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                    getNewNumberFormat(payment.getMinute())));
                            if (payment.getDistance() == 0.0 || payment.getDistance() == 0) {
                                distanceContainer.setVisibility(View.GONE);
                            } else {
                                distanceContainer.setVisibility(View.VISIBLE);
                                if (datum.getIsRound() == 0) {
                                    distanceFare.setText(String.format("%s %s",
                                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                            getNewNumberFormat(payment.getDistance())));
                                } else {
                                    distanceFare.setText(String.format("%s %s",
                                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                            getNewNumberFormat(payment.getDistance() * 2)));
                                }
                            }
                            break;
                        case Utilities.InvoiceFare.distanceHour:
                            timeContainer.setVisibility(View.VISIBLE);
                            timeFare.setText(String.format("%s %s",
                                    SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                    getNewNumberFormat(payment.getHour())));
                            if (payment.getDistance() == 0.0 || payment.getDistance() == 0) {
                                distanceContainer.setVisibility(View.GONE);
                            } else {
                                distanceContainer.setVisibility(View.VISIBLE);
                                if (datum.getIsRound() == 0) {
                                    distanceFare.setText(String.format("%s %s",
                                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                            getNewNumberFormat(payment.getDistance())));
                                } else {
                                    distanceFare.setText(String.format("%s %s",
                                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                            getNewNumberFormat(payment.getDistance() * 2)));
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    @OnClick(R.id.close)
    public void onViewClicked() {
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
