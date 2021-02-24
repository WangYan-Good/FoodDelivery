package com.example.fooddelivery;
/*
负责主界面
 */
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.maps.MapView;
import com.amap.api.services.route.RouteSearch;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnClickListener {
    private RadioGroup rg_main;
    private Fragment fragment = null;//显示页面
    public Order_Fragment order_fragment;
    public Route_Fragment route_fragment;
    private int position;//页面位置
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        bundle = intent.getExtras();
        if (bundle == null){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            initData();
            initView();
        }
        initListener();
    }

    private void initListener() {
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.order_btn://订单
                        fragment = order_fragment;
                        break;
                    case R.id.route_btn://路线
                        fragment = route_fragment;
                        break;
                    default:
                        break;
                }
                switchFragment(fragment);
            }
        });
    }

    private void initData() {
        order_fragment = new Order_Fragment();
        route_fragment = new Route_Fragment();
    }

    private void initView() {
        rg_main = findViewById(R.id.rg_main);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_main,order_fragment,"order").hide(order_fragment);
        ft.add(R.id.fl_main,route_fragment,"route");
        ft.show(route_fragment).commit();
    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (fragment == null){
            ft.add(R.id.fl_main,route_fragment).show(route_fragment);
        }else {
            if (fragment==order_fragment){//显示订单界面
                ft.hide(route_fragment);
                ft.show(order_fragment);
            }else if (fragment == route_fragment){//显示路线界面
                ft.hide(order_fragment);
                ft.show(route_fragment);
            }
        }
        ft.commit();
    }
}