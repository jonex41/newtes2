package com.sixtech.rider.ui.activity;

import static com.sixtech.rider.MvpApplication.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sixtech.rider.R;
import com.sixtech.rider.base.BaseActivity;
import com.sixtech.rider.data.network.model.WalkThrough;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnBoardActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private MyViewPagerAdapter adapter;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private AlertDialog alertDialog;


    @Override
    public int getLayoutId() {
        return R.layout.activity_on_board;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

        List<WalkThrough> list = new ArrayList<>();
        list.add(new WalkThrough(R.drawable.ic_book_comfortable,
                getString(R.string.walk_1), getString(R.string.walk_1_description)));
        list.add(new WalkThrough(R.drawable.ic_schedule_your_ride,
                getString(R.string.walk_2), getString(R.string.walk_2_description)));
        list.add(new WalkThrough(R.drawable.ic_expert_drivers,
                getString(R.string.walk_3), getString(R.string.walk_3_description)));

        adapter = new MyViewPagerAdapter(this, viewPager, list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);

        DotsIndicator dotsIndicator = findViewById(R.id.dots_indicator);
        dotsIndicator.setViewPager(viewPager);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void checkLocation() {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            getLocationPermission();
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            startActivity(new Intent(this, SignInUpActivity.class));
        }
        else
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    private void buildAlertMessageNoGps() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.loc_dialog, viewGroup, false);
        dialogView.findViewById(R.id.allow).setOnClickListener(v-> {
            alertDialog.cancel();
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        });
        dialogView.findViewById(R.id.deny).setOnClickListener(v-> alertDialog.cancel());
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogHard);
        builder.setView(dialogView);

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private final ViewPager viewPager;
        List<WalkThrough> list;
        Context context;

        MyViewPagerAdapter(Context context, ViewPager viewPager1, List<WalkThrough> list) {
            this.list = list;
            this.context = context;
            this.viewPager = viewPager1;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.pager_item, container, false);
            WalkThrough walk = list.get(position);

            TextView title = itemView.findViewById(R.id.title);
            TextView description = itemView.findViewById(R.id.description);
            ImageView imageView = itemView.findViewById(R.id.img_pager_item);

            title.setText(walk.title);
            description.setText(walk.description);
            imageView.setImageResource(walk.drawable);
            container.addView(itemView);

            itemView.findViewById(R.id.imgLeftArrow).setVisibility(position == 0? View.GONE : View.VISIBLE);
            itemView.findViewById(R.id.imgLeftArrow).setOnClickListener(v -> {
                if (position > 0) {
                    viewPager.setCurrentItem(position - 1, true);
                }
            });
            itemView.findViewById(R.id.imgRightArrow).setOnClickListener(v -> {
                if (position < 2) {
                    viewPager.setCurrentItem(position + 1, true);
                }
                else {
                    checkLocation();
                }
            });
            itemView.findViewById(R.id.skip).setOnClickListener(v-> checkLocation());
            return itemView;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
