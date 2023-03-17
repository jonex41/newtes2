package com.sixtech.rider.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.sixtech.rider.GlideApp;
import com.sixtech.rider.R;
import com.sixtech.rider.data.SharedHelper;
import com.sixtech.rider.data.network.model.EstimateFare;
import com.sixtech.rider.data.network.model.Service;
import com.sixtech.rider.ui.fragment.service.ServiceFragment;

import java.util.List;

import static com.sixtech.rider.base.BaseActivity.getNewNumberFormat;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {

    private List<Service> list;
    private TextView capacity;
    private Context context;
    private int lastCheckedPos = 0;
    private Animation zoomIn;
    private ServiceFragment.ServiceListener mListener;
    private EstimateFare estimateFare;
    private boolean canNotifyDataSetChanged = false;

    public ServiceAdapter(Context context, List<Service> list,
                          ServiceFragment.ServiceListener listener,
                          TextView capacity, EstimateFare fare) {
        this.context = context;
        this.list = list;
        this.capacity = capacity;
        this.mListener = listener;
        this.estimateFare = fare;
        zoomIn = AnimationUtils.loadAnimation(this.context, R.anim.zoom_in_animation);
        zoomIn.setFillAfter(true);
    }

    public void setEstimateFare(EstimateFare estimateFare) {
        this.estimateFare = estimateFare;
        canNotifyDataSetChanged = true;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_service, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"StringFormatMatches", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        Service obj = list.get(position);
        if (obj != null) {
            holder.serviceName.setText(obj.getName());
            holder.capacityvalue.setText(obj.getCapacity().toString());
            if (obj.getKilometer() > 1 || obj.getKilometer() > 1.0) {
                holder.price.setText(obj.getKilometer() + " " + context.getString(R.string.kms) + "/" + obj.getDuration());
            } else if (obj.getKilometer() == 1 || obj.getKilometer() == 1.0) {
                holder.price.setText(obj.getKilometer() + " " + context.getString(R.string.km) + "/" + obj.getDuration());
            } else {
                holder.price.setText(obj.getKilometer() + " " + context.getString(R.string.km));
            }
        }
        if (estimateFare != null) {
            holder.estimated_fixed.setText(SharedHelper.getKey(context, "currency") + " " +
                    getNewNumberFormat(Double.parseDouble(String.valueOf(estimateFare.getEstimatedFare()))));
//                    + " - " + SharedHelper.getKey(context, "currency") +
//                    getNewNumberFormat(Double.parseDouble(String.valueOf(estimateFare.getEstimatedFare() + 3))));
            /*if (SharedHelper.getKey(context, "measurementType").equalsIgnoreCase(Constants.MeasurementType.KM)) {
                if (estimateFare.getDistance() > 1 || estimateFare.getDistance() > 1.0) {
                    holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.kms));
                } else {
                    holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.km));
                }
            } else {
                if (estimateFare.getDistance() > 1 || estimateFare.getDistance() > 1.0) {
                    holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.miles));
                } else {
                    holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.mile));
                }
            }*/
        }
//        Toast.makeText(context, obj.getImage(), Toast.LENGTH_SHORT).show();
        GlideApp.with(context)
                .load(obj.getImage())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_car).dontAnimate().error(R.drawable.ic_car))
                .into(holder.image);

        if (position == lastCheckedPos && canNotifyDataSetChanged) {
            canNotifyDataSetChanged = false;
            capacity.setText(String.valueOf(obj.getCapacity()));
            holder.mFrame_service.setBackground(context.getResources().getDrawable(R.drawable.circle_background));
            holder.serviceName.setTextColor(context.getResources().getColor(R.color.colorAccent));
            //holder.price.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1);
            holder.estimated_fixed.setVisibility(View.VISIBLE);
            if (estimateFare != null) {
                /*if (SharedHelper.getKey(context, "measurementType").equalsIgnoreCase(Constants.MeasurementType.KM)) {
                    if (estimateFare.getDistance() > 1 || estimateFare.getDistance() > 1.0)
                        holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.kms));
                    else
                        holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.km));
                } else {
                    if (estimateFare.getDistance() > 1 || estimateFare.getDistance() > 1.0)
                        holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.miles));
                    else
                        holder.price.setText(estimateFare.getDistance() + " " + context.getString(R.string.mile));
                }*/
                holder.estimated_fixed.setText(SharedHelper.getKey(context, "currency") + " " +
                        getNewNumberFormat(Double.parseDouble(String.valueOf(estimateFare.getEstimatedFare()))));
//                        + " - " + SharedHelper.getKey(context, "currency") +
//                        getNewNumberFormat(Double.parseDouble(String.valueOf(estimateFare.getEstimatedFare() + 3))));
                holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                holder.card.setElevation(5);

            }

//           holder.itemView.startAnimation(zoomIn);
        } else {
            holder.mFrame_service.setBackground(context.getResources().getDrawable(R.drawable.service_bkg));
            holder.serviceName.setTextColor(context.getResources().getColor(R.color.colorPrimaryText));
            holder.itemView.setAlpha((float) 0.7);
            holder.estimated_fixed.setVisibility(View.INVISIBLE);
            //holder.price.setVisibility(View.INVISIBLE);
            holder.card.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
            holder.card.setElevation(0);
        }

        holder.itemView.setOnClickListener(view -> {
            Service object = list.get(position);
            if (object != null) {
                if (view.getId() == R.id.item_view) {
                    if (lastCheckedPos == position) {
//                        RateCardFragment.SERVICE = object;
//                        ((MainActivity) context).changeFragment(new RateCardFragment());
                    }
                    lastCheckedPos = position;
                    notifyDataSetChanged();
                }
                mListener.whenClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Service getSelectedService() {
        if (list.size() > 0) return list.get(lastCheckedPos);
        else return null;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout itemView;
        private TextView serviceName, price, estimated_fixed, capacityvalue;
        private ImageView image;
        private FrameLayout mFrame_service;
        private CardView card;

        MyViewHolder(View view) {
            super(view);
            mFrame_service = view.findViewById(R.id.frame_service);
            estimated_fixed = view.findViewById(R.id.estimated_fixed);
            serviceName = view.findViewById(R.id.service_name);
            price = view.findViewById(R.id.price);
            image = view.findViewById(R.id.image);
            itemView = view.findViewById(R.id.item_view);
            card = view.findViewById(R.id.CardView);
            capacityvalue = view.findViewById(R.id.capacity_value);
        }
    }
}
