package com.yin.trip.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.yin.trip.R;
import com.yin.trip.util.LocationSendData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yinfeng on 2017/3/22 0022.
 */
public class MapActivity extends Activity {

    MapView mMapView = null;
    BaiduMap mBaidumap = null;

    /**
     * 当前地点击点
     */
    private LatLng currentPt;

    boolean isFirstLoc = true;  //是否首次定位
    boolean isFresh = false;    //是否需要刷新
    public LocationClient mLocClient = null;
    //定位图标
    Marker marker;

    private String touchType;

    /**
     * 用于显示地图状态的面板
     */
    private TextView mStateBar;

    //跳转页面
    private Button button;

    public LocalLocationListener locationListener = new LocalLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.map);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaidumap = mMapView.getMap();

        mStateBar = (TextView) findViewById(R.id.state);
        button = (Button)findViewById(R.id.button);


        initListener();

        //开启定位图层
        mBaidumap.setMyLocationEnabled(true);

        //定位功能
        mLocClient = new LocationClient(this);
        //设置定位内容
        initLocation();

        mLocClient.registerLocationListener(locationListener);

        mLocClient.start();
    }

    //初始化定位模式
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span=1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocClient.setLocOption(option);
    }

    //定位SDK监听函数，继承BDLocationListener类
    public class LocalLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            //map view销毁后不再处理新接收的位置
            if (bdLocation == null || mMapView == null) {
                return ;
            }

            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    //设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();

            if (locationData != null && mBaidumap != null) {
                mBaidumap.setMyLocationData(locationData);
            }

            //如果是第一次定位
            if (isFirstLoc) {
                isFirstLoc = false;

                currentPt = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());

                MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(currentPt,18f);

                mBaidumap.animateMapStatus(update);

//                //构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(currentPt)
                        .icon(bitmap);


                //将marker添加到地图上
                 marker = (Marker) (mBaidumap.addOverlay(option));

            }else if (isFresh) {

                isFresh = false;
                currentPt = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());

                MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(currentPt,18f);

                mBaidumap.animateMapStatus(update);

                //将marker添加到地图上
                marker.setPosition(currentPt);
            }

            //            获取定位结果
            StringBuffer sb = new StringBuffer(256);

            sb.append("locationData:" + bdLocation.getLatitude() + " " + bdLocation.getLongitude());

            sb.append("time : ");
            sb.append(bdLocation.getTime());    //获取定位时间

            sb.append("\nerror code : ");
            sb.append(bdLocation.getLocType());    //获取类型类型

            sb.append("\nlatitude : ");
            sb.append(bdLocation.getLatitude());    //获取纬度信息

            sb.append("\nlongtitude : ");
            sb.append(bdLocation.getLongitude());    //获取经度信息

            sb.append("\nradius : ");
            sb.append(bdLocation.getRadius());    //获取定位精准度


            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation){

                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(bdLocation.getSpeed());    // 单位：公里每小时

                sb.append("\nsatellite : ");
                sb.append(bdLocation.getSatelliteNumber());    //获取卫星数

                sb.append("\nheight : ");
                sb.append(bdLocation.getAltitude());    //获取海拔高度信息，单位米

                sb.append("\ndirection : ");
                sb.append(bdLocation.getDirection());    //获取方向信息，单位度

                sb.append("\naddr : ");
                sb.append(bdLocation.getAddrStr());    //获取地址信息

                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){

                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(bdLocation.getAddrStr());    //获取地址信息

                sb.append("\noperationers : ");
                sb.append(bdLocation.getOperators());    //获取运营商信息

                sb.append("\ndescribe : ");
                sb.append("网络定位成功");

            } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {

                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");

            } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {

                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {

                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");

            } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {

                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

            }

            sb.append("\nlocationdescribe : ");
            sb.append(bdLocation.getLocationDescribe());    //位置语义化信息

            List<Poi> list = bdLocation.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    /**
     * 对地图事件的消息响应
     */
    private void initListener() {

        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          //新建一个显式意图，第一个参数为当前Activity类对象，第二个参数为你要打开的Activity类
                                          Intent intent = new Intent(MapActivity.this,MainActivity.class);

                                          //用Bundle携带数据
                                          Bundle bundle=new Bundle();
                                          //传递name参数为tinyphp
                                          LocationSendData locationSendData = new LocationSendData( currentPt.longitude,currentPt.latitude);

                                          bundle.putSerializable("location", locationSendData);
                                          intent.putExtras(bundle);


                                          Log.i("传递信息", locationSendData.getLongtitude() + " " + locationSendData.getLatitude());
                                          mLocClient.stop();
                                          startActivity(intent);
                                      }
                                  });

        mBaidumap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {

            @Override
            public void onTouch(MotionEvent event) {

            }
        });


        mBaidumap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            /**
             * 单击地图
             */
            public void onMapClick(LatLng point) {
                touchType = "单击地图";
                currentPt = point;
                updateMapState();
            }

            /**
             * 单击地图中的POI点
             */
            public boolean onMapPoiClick(MapPoi poi) {
                touchType = "单击POI点";
                currentPt = poi.getPosition();
                updateMapState();
                return false;
            }
        });
        mBaidumap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            /**
             * 长按地图
             */
            public void onMapLongClick(LatLng point) {
                touchType = "长按";
                isFresh = true;
                updateMapState();
            }
        });
        mBaidumap.setOnMapDoubleClickListener(new BaiduMap.OnMapDoubleClickListener() {
            /**
             * 双击地图
             */
            public void onMapDoubleClick(LatLng point) {
                touchType = "双击";

//                //去除marker
//                marker = null;
                currentPt = point;
                updateMapState();
            }
        });

        /**
         * 地图状态发生变化
         */
        mBaidumap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            public void onMapStatusChangeStart(MapStatus status) {
                updateMapState();
            }

            public void onMapStatusChangeFinish(MapStatus status) {
                updateMapState();
            }

            public void onMapStatusChange(MapStatus status) {
                updateMapState();
            }
        });
    }

    /**
     * 更新地图状态显示面板
     */
    private void updateMapState() {
        if (mStateBar == null) {
            return;
        }
        String state = "";
        if (currentPt == null) {
            state = "单击地图以修正您所处地理位置，显示经纬度和地图状态\n长按地图进行刷新";
        } else {
            state = String.format("当前经度：%f \n当前纬度：%f",
                    currentPt.longitude, currentPt.latitude);
            marker.setPosition(currentPt);
        }
        state += "\n";
        MapStatus ms = mBaidumap.getMapStatus();
        state += String.format(
                "zoom=%.1f rotate=%d overlook=%d",
                ms.zoom, (int) ms.rotate, (int) ms.overlook);
        mStateBar.setText(state);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBaidumap != null) {
            mBaidumap = null;
        }
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        mLocClient.start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();

    }


}

