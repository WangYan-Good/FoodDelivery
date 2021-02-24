package com.example.fooddelivery;

import android.os.Bundle;
/*
负责传递两个Fragment的数据传输
 */
public interface DataListener {
    void onData(Bundle savedInstanceState);
}
