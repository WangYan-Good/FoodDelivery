package com.example.fooddelivery;

import android.content.Intent;
import android.net.sip.SipSession;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
/*
* 功能描述：
* 1.能够动态添加订单
* 2.能够动态删除订单
* 3.提交订单
* */

public class Order_Fragment extends Fragment implements OnClickListener, TextWatcher, Parcelable {
    //获取布局管理器信息
    View LContent;
    //订单数目，线性布局管理器ID
    private int i=-1;
    //编辑框ID
    private  int[] j={0,1,2,3};
    //外围的LinearLayout容器
    private LinearLayout llContentView_out;
    //字符数目标志
    private int c_x_len =0;
    private int c_y_len =0;
    private int b_x_len =0;
    private int b_y_len =0;
    //准备添加的线性布局管理器
    private LinearLayout linearLayout;
    //"添加订单"按钮
    Button AddOrder_Button;
    //"删除订单"按钮
    Button DelOrder_Button;
    //“提交订单”按钮
    Button Submit_Button;
    //存放坐标信息
    private LinkedList<LinearLayout> order = new LinkedList<LinearLayout>();
    //坐标集合
    public static ArrayList<String> Addr_B_X = new ArrayList<>();
    public static ArrayList<String> Addr_B_Y = new ArrayList<>();
    public static ArrayList<String> Addr_C_X = new ArrayList<>();
    public static ArrayList<String> Addr_C_Y = new ArrayList<>();
    //
    private  static DataListener mDataListener;
    double[][] c_point;
    double[][] b_point;
    Bundle bundle;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //获取布局文件信息
        LContent = inflater.inflate(R.layout.fragment_order_,container,false);

        //获取外部线性布局管理器
        llContentView_out = LContent.findViewById(R.id.fragment_order_linear_out);

        //创建滚动视图

        //设置按钮监听器
        AddOrder_Button = LContent.findViewById(R.id.Add_Button);//添加订单
        AddOrder_Button.setOnClickListener(listener_Add);
        DelOrder_Button = LContent.findViewById(R.id.Del_Button);//删除订单
        DelOrder_Button.setOnClickListener(listener_Del);
        Submit_Button = LContent.findViewById(R.id.Submit_Button);//提交按钮
        Submit_Button.setOnClickListener(listener_Submit);
        Log.i("onCreatView","onCreatView success");

        //创建编辑框监听器
        
        return LContent;
    }

    /*
    利用时间监听器添加订单
     */
    OnClickListener listener_Add = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.Add_Button){
                llContentView_out.addView(setLinearLayout());
                Log.i("Add_Order","Add Order success !");
            }else {
                Toast.makeText(getContext(),"添加失败",Toast.LENGTH_LONG).show();
                Log.i("Add_Order","success");
            }
        }
    };

    //监听文本框

       /*
    利用时间监听器删除订单
     */
       OnClickListener listener_Del = new OnClickListener() {
           @Override
           public void onClick(View v) {
               if (v.getId()== R.id.Del_Button){
                   if(i==-1){
                       Log.i("EmptuList","订单为空");
                       Toast.makeText(getContext(),"已经没有订单了呢...",Toast.LENGTH_SHORT).show();
                   }else {
                       llContentView_out.removeView(order.getLast());
                       Log.d("Del_Order","success");
                       //Toast.makeText(getContext(),"第"+(i+1)+"个订单删除成功",Toast.LENGTH_SHORT).show();
                       order.remove(i);
                       i--;
                   }
               }
               else {
                   Toast.makeText(getContext(),"第"+(i+1)+"个订单删除失败",Toast.LENGTH_SHORT).show();
               }
           }
       };
    /*
 利用时间监听器提交订单
  */
    OnClickListener listener_Submit = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.Submit_Button){
                if (i != -1){
                    //传递数据
                    Log.i("Order_Length",""+order.size());
                    print();
                    putData();
                    mDataListener.onData(bundle);
                }else {
                    Log.i("EmptuList","订单为空");
                }
                Log.i("Submit_Order","Success");
                Toast.makeText(getContext(),"提交成功",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void putData() {
        bundle = new Bundle();//数据容器
        int len = order.size();
        bundle.putInt("order.size()",order.size());//计数标志
        for (int i = 0;i<len;i++){
            bundle.putString("Addr_C_X"+i,Addr_C_X.get(i));
            bundle.putString("Addr_C_Y"+i,Addr_C_Y.get(i));
            bundle.putString("Addr_B_X"+i,Addr_B_X.get(i));
            bundle.putString("Addr_B_Y"+i,Addr_B_Y.get(i));
        }
        Log.i("putData","success!  "+"bundle.isEmpty:"+bundle.isEmpty());
    }

    //输出订单地址
    private void print() {
        int len = order.size();
        for (int i = 0;i<len;i++){
            Log.i("address","("+Addr_C_X.get(i)+","+Addr_C_Y.get(i)+") , ("+Addr_B_X.get(i)+","+Addr_B_Y.get(i)+")");
        }
    }

    public LinearLayout setLinearLayout(){
        final LinearLayout linearLayout = new LinearLayout(getActivity());
        //设置ID
        i++;
        linearLayout.setId(i);//设置线性布局管理器ID
        //宽度与父容器相同，高度包裹其自身内容
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        //设置整体布局水平排列
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        //创建编辑框
        final EditText Cust_X = new EditText(getActivity());
        Cust_X.setHint("顾客纬度");
        Cust_X.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //将初始字符串加入列表
                if (Addr_C_X.size()<order.size()){//判断顾客数目是否少于订单数目
                    Addr_C_X.add(s.toString());
                }else if (Addr_C_X.get(i).length()<s.toString().length()){//判断列表对应位置字符串是否比当前短
                    Addr_C_X.set(i,s.toString());
                }
                Log.i("EditTextChangeListener","afterTextChanged---"+Addr_C_X.size());
            }
        });

        final EditText Cust_Y = new EditText(getActivity());
        Cust_Y.setHint("顾客经度");
        Cust_Y.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //将初始字符串加入列表
                if (Addr_C_Y.size()<order.size()){//判断顾客数目是否少于订单数目
                    Addr_C_Y.add(s.toString());
                }else if (Addr_C_Y.get(i).length()<s.toString().length()){//判断列表对应位置字符串是否比当前短
                    Addr_C_Y.set(i,s.toString());
                }
                Log.i("EditTextChangeListener","afterTextChanged---"+Addr_C_Y.size());
            }
        });

        final EditText Business_X = new EditText(getActivity());
        Business_X.setHint("商家纬度");
        Business_X.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //将初始字符串加入列表
                if (Addr_B_X.size()<order.size()){//判断顾客数目是否少于订单数目
                    Addr_B_X.add(s.toString());
                }else if (Addr_B_X.get(i).length()<s.toString().length()){//判断列表对应位置字符串是否比当前短
                    Addr_B_X.set(i,s.toString());
                }
                Log.i("EditTextChangeListener","afterTextChanged---"+Addr_B_X.size());
            }
        });

        final EditText Business_Y = new EditText(getActivity());
        Business_Y.setHint("商家经度");
        Business_Y.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //将初始字符串加入列表
                if (Addr_B_Y.size()<order.size()){//判断顾客数目是否少于订单数目
                    Addr_B_Y.add(s.toString());
                }else if (Addr_B_Y.get(i).length()<s.toString().length()){//判断列表对应位置字符串是否比当前短
                    Addr_B_Y.set(i,s.toString());
                }
                Log.i("EditTextChangeListener","afterTextChanged---"+Addr_B_Y.size());
            }
        });

        //添加到线性布局管理器
        linearLayout.addView(Cust_X);
        linearLayout.addView(Cust_Y);
        linearLayout.addView(Business_X);
        linearLayout.addView(Business_Y);
        //设置监听器
        Log.i("setLinearLayout","setLinearLayout success");
        order.add(i,linearLayout);
        return order.getLast();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * are about to be replaced by new text with length <code>after</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.i("EditTextChangeListener","beforeTextChanged---"+s.toString());
    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * have just replaced old text that had length <code>before</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.i("EditTextChangeListener","onTextChanged---"+s.toString());
    }

    /**
     * This method is called to notify you that, somewhere within
     * <code>s</code>, the text has been changed.
     * It is legitimate to make further changes to <code>s</code> from
     * this callback, but be careful not to get yourself into an infinite
     * loop, because any changes you make will cause this method to be
     * called again recursively.
     * (You are not told where the change took place because other
     * afterTextChanged() methods may already have made other changes
     * and invalidated the offsets.  But if you need to know here,
     * you can use  in {@link #onTextChanged}
     * to mark your place and then look up from here where the span
     * ended up.
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {
        Log.i("EditTextChangeListener","afterTextChanged---"+s.toString());
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
    //创建注册回调的函数
    public static void setOnDataListener(DataListener dataListener){
 //将参数赋值给接口类型的成员变量
        mDataListener = dataListener;
 }


}
