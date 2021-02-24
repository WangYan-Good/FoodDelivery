package com.example.fooddelivery;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.AMap;
import com.amap.api.maps.*;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import android.provider.SyncStateContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import java.io.*;
import java.net.*;

import Ant_Colony_Algorithm_2.Ant;

/*
* 功能描述：
* 1.能够实现定位
* 2.能够接收Order_Fragment传递的数据
* 3.能够根据坐标绘制路线
* */

public class Route_Fragment extends Fragment implements  AMapLocationListener,LocationSource,AMap.OnMapClickListener, RouteSearch.OnRouteSearchListener {
    View mapLayout; //地图布局文件
    private AMap aMap;//AMap是地图对象
    MapView mMapView;//地图视图
    Button clearMap;//清除地图路线按钮
    private boolean IsPrepares;//初始化是否完成
    private boolean mHasLoadedOnce;//是否已经被加载过一次，第二次就不再请求数据
    private OnLocationChangedListener mListener;
    MyLocationStyle myLocationStyle;
    private LatLng myLocation;//我的位置坐标
    private AMapLocationClient mLocationClient;//定位监听器
    private AMapLocationClientOption mLocationOption;//定位模式设置

    RouteSearch routeSearch;//路线查找
    Marker marker ;
    //全部坐标点集合
    private LinkedList<LatLonPoint> point= new LinkedList<>();
    int i =0;//地点个数
    Bundle bundle;
    int order_number;
    double[][] c_point;
    double[][] b_point;
    private Object[] rs;
    @Nullable
    //
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Order_Fragment order_fragment=(Order_Fragment) getFragmentManager().findFragmentByTag("order");
        order_fragment.setOnDataListener(new OnDataListener());
        //获取地图控件引用
        mapLayout = inflater.inflate(R.layout.fragment_route_,null);
        mMapView = mapLayout.findViewById(R.id.map);
        //获取清除地图路线控件
        clearMap = mapLayout.findViewById(R.id.clearMap);
        clearMap.setOnClickListener(map_clear);
        //创建地图
        mMapView.onCreate(savedInstanceState);
        InitView();
        IsPrepares = true;
        //实现懒加载
        LazyLoad();
        return mapLayout;

    }

    View.OnClickListener map_clear = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            aMap.clear();
            point.clear();
            i=0;
        }
    };
    //初始化控件
    public void InitView(){
        InitMap();
        SetUpLocationStyle();
        aMap.setOnMapClickListener(this);
    }

    public void InitMap(){
        if (aMap ==null){
            aMap = mMapView.getMap();
        }else {
            if (mapLayout.getParent() != null){
                ((ViewGroup) mapLayout.getParent()).removeView(mapLayout);
            }
        }
        aMap.setLocationSource((LocationSource) this);//设置定位监听
        aMap.setMyLocationEnabled(true);
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(15);
        aMap.moveCamera(cameraUpdate);
    }


    private void SetUpLocationStyle(){
        //实现定位蓝点
        myLocationStyle = new MyLocationStyle();
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        //myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        //连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        //myLocationStyle.interval(2000);
        //连续定位下的定位间隔，只在连续定位模式下生效，单次定位模式不会生效。单位为毫秒
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);//设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    public void LazyLoad(){
        if (!IsPrepares || !isVisible() || mHasLoadedOnce){
            return;
        }
        //填充控件数据
        mHasLoadedOnce = true;
    }


    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0){
            if (mListener != null){
                mListener.onLocationChanged(aMapLocation);//显示系统小蓝点
            }
            //获取当前经纬度坐标
            String address = aMapLocation.getAddress();
            myLocation = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
            Log.d("MyLocation","("+aMapLocation.getLatitude()+","+aMapLocation.getLongitude()+")");
            //fixedMarker();
        }
    }

    /*
    激活定位
    * */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null){
            mLocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocation(true);//只定位一次
            mLocationOption.setHttpTimeOut(2000);
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();//开始定位
        }
    }

    /*
    * 停止定位
    * */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null){
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationOption =null;
        mLocationClient = null;
        aMap = null;
        //销毁地图
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //重新加载绘制地图
        mMapView.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();//暂停地图的绘制
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);//保存地图当前的状态
    }

    @Override
    public void onMapClick(LatLng latLng) {
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude,latLng.longitude);
        point.add(latLonPoint);
        DrawLine();
        Log.d("LocationList"+(++i),"("+latLng.latitude+","+latLng.longitude+")");
        Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(""+i).snippet("DefaultMarker"));
    }
    /*
    * 绘制订单order内的地点路线
    * */
    public void DrawLine(){
        //aMap.clear();
        if (point.size() >= 2){
            for (int m = 0;m<point.size()-1;m++){
                Log.i("LatLonPoint","("+point.get(m).getLatitude()+","+point.get(m).getLongitude()+")");
                searchRouteResult(point.get(m),point.get(m+1));
            }
            Log.i("Point number",""+point.size());
        }else {
            return;
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        List<WalkPath> pathList = walkRouteResult.getPaths();
        List<LatLng> walkPaths = new ArrayList<>();
        for (WalkPath dp : pathList) {

            List<WalkStep> stepList = dp.getSteps();
            for (WalkStep ds : stepList) {


                List<LatLonPoint> points = ds.getPolyline();
                for (LatLonPoint llp : points) {
                    walkPaths.add(new LatLng(llp.getLatitude(), llp.getLongitude()));
                }
            }
        }
        aMap.addPolyline(new PolylineOptions()
                .addAll(walkPaths)
                .width(30)
                //是否开启纹理贴图
                .setUseTexture(true)
                //绘制成大地线
                .geodesic(false)
                //设置纹理样式
                .setCustomTexture(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)))
                //设置画线的颜色
                .color(Color.argb(200, 0, 0, 100)));
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    /*
    * 开始Route搜索
    * */
    public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint){
        routeSearch = new RouteSearch(getActivity());//初始化路线询问对象
        routeSearch.setRouteSearchListener(this);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint,endPoint);
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
        routeSearch.calculateWalkRouteAsyn(query);
    }

    class OnDataListener implements DataListener{
        @Override
        public void onData(Bundle savedInstanceState) {
            bundle = savedInstanceState;
            initData();//坐标存入数组
            Log.i("initData","Route success "+bundle.isEmpty());
            //利用服务端调用蚁群算法返回最优序列
            inputData();
            //初始化坐标集
        }
    }

    private void inputData() {
        Thread tAnt = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("Thread Create","Success");
                Socket socket;  //声明Socket对象
                OutputStream outputStream;  //声明输出流OutputStream对象
                try {

                    socket = new Socket("192.168.1.117",12500);//绑定端口号
                    //向服务器端传递信息
                    outputStream = socket.getOutputStream();
                    PrintWriter pw = new PrintWriter(outputStream);
                    pw.write(""+order_number+",");
                    for (int i =0;i<order_number;i++){
                        pw.write(bundle.getString("Addr_C_X"+i)+",");
                        pw.write(bundle.getString("Addr_C_Y"+i)+",");
                        pw.write(bundle.getString("Addr_B_X"+i)+",");
                        pw.write(bundle.getString("Addr_B_Y"+i)+",");
                    }
                    pw.flush();
                    //关闭输出流
                    socket.shutdownOutput();
                    //获取输入流，并读取服务器响应信息
                    InputStream is = socket.getInputStream();//字节流对象
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String info =null;
                    String add=null;
                    while ((info=br.readLine())!= null){
                        System.out.println("服务器："+info);
                        add = info;
                    }
                    br.close();
                    isr.close();
                    is.close();
                    pw.close();
                    outputStream.close();
                    socket.close();
                    dealServerMessage(add);
                }catch (UnknownHostException e){
                    Log.i("Exception","UnknownHostException");
                    e.printStackTrace();
                } catch (IOException e){
                    Log.i("Exception","IOException");
                    e.printStackTrace();
                }
            }
        });
        tAnt.start();

    }

    private void dealServerMessage(String info) {
        Log.i("info",""+info);
        String[] pointStr = info.split(",");
        if (info!= null){
            for (int i =0;i<order_number;i++){
                double bx = Double.parseDouble(pointStr[i*4]);
                double by = Double.parseDouble(pointStr[i*4+1]);
                double cx = Double.parseDouble(pointStr[i*4+2]);
                double cy = Double.parseDouble(pointStr[i*4+3]);
                LatLng latLng_b = new LatLng(bx,by);
                onMapClick(latLng_b);
                LatLng latLng_c = new LatLng(cx,cy);
                onMapClick(latLng_c);
            }
        }
    }

    private void initData() {
        aMap.clear();//清除地图路线
        point.clear();//清除坐标集
        i = 0;//地点数目归零
        if (bundle.isEmpty()==true){
            Log.i("Bundle","NULL");
        }else {
            order_number = bundle.getInt("order.size()");
            c_point = new double[order_number][2];
            b_point = new double[order_number][2];
            for (int i =0;i<order_number;i++){
                c_point[i][0] = Double.parseDouble(bundle.getString("Addr_C_X"+i));
                c_point[i][1] = Double.parseDouble(bundle.getString("Addr_C_Y"+i));
                b_point[i][0] = Double.parseDouble(bundle.getString("Addr_B_X"+i));
                b_point[i][1] = Double.parseDouble(bundle.getString("Addr_B_Y"+i));
                Log.i("c_point","("+c_point[i][0]+","+c_point[i][1]+")");
                Log.i("b_point","("+b_point[i][0]+","+b_point[i][1]+")");
            }
        }
    }

}
