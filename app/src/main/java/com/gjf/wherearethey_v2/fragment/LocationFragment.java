package com.gjf.wherearethey_v2.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.gjf.wherearethey_v2.FriendsInfoActivity;
import com.gjf.wherearethey_v2.MainApplication;
import com.gjf.wherearethey_v2.R;
import com.gjf.wherearethey_v2.bean.Friends;
import com.gjf.wherearethey_v2.bean.MessageIO;
import com.gjf.wherearethey_v2.bean.NowLocation;
import com.gjf.wherearethey_v2.task.GetFriendsInfoTask;
import com.gjf.wherearethey_v2.task.GetFriendsLocationsTask;
import com.gjf.wherearethey_v2.task.SendLocationTask;
import com.gjf.wherearethey_v2.task.SendMsgTask;
import com.gjf.wherearethey_v2.task.ShowFriendsTask;
import com.gjf.wherearethey_v2.util.DateUtil;
import com.gjf.wherearethey_v2.util.LogUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * 定位子页面，地图显示、地图交互
 *
 * @author gjf
 * @version 2.0
 */
public class LocationFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {
    private static String TAG = "LocationFragment";
    public LocationClient locationClient;//定位客户端
    private MainApplication app;//app的引用
    private NowLocation nowLocation;//实时位置
    private MyLocationListener locationListener;//自定义的位置信息接收器
    private MapView mMapView;//地图视图
    private BaiduMap mBaiduMap;//
    private View mView;//子页面视图
    private Spinner sp_center;
    private Spinner sp_showMode;
    private boolean isFirstSet;
    private CheckBox cb_shareSwitch;
    private Context mContext;//获取当前的类对象
    private boolean isShowOnline;
    private NowLocation listenerLocation;
    private String[] showMode = {"全部", "仅在线", "仅自己"};
    SendLocationTask sendLocationTask;
    ShowFriendsTask showFriendsTask;
    GetFriendsLocationsTask getFriendsLocationsTask;
    /**
     * 红色表示自己，蓝色表示在线朋友、灰色表示离线的朋友
     */
    private int[] ic_location = {R.drawable.ic_location_mine, R.drawable.ic_location_friends_online,
            R.drawable.ic_location_friends_offline};
    private boolean isFirst;//判断是否是初始状态

    /**
     * 创建页面对象
     *
     * @param inflater           布局实例化
     * @param container          容器
     * @param savedInstanceState 保存的参数
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.i(TAG, "[onCreateView]");
        mContext = getActivity();
        app = MainApplication.getInstance();
        nowLocation = new NowLocation();
        LocationClientOption option = new LocationClientOption();
        try {
            locationClient = new LocationClient(mContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        isFirst = true;
        isFirstSet = true;
        isShowOnline = false;
        mView = inflater.inflate(R.layout.fragment_location, container, false);
        //发送和接受
        sp_center = mView.findViewById(R.id.sp_center);
        cb_shareSwitch = mView.findViewById(R.id.cb_shareSwitch);
        cb_shareSwitch.setOnCheckedChangeListener(this);
        cb_shareSwitch.setChecked(false);
        ArrayAdapter adapter = new ArrayAdapter(mContext, R.layout.item_select, showMode);
        sp_showMode = mView.findViewById(R.id.sp_showMode);
        sp_showMode.setPrompt("显示模式");
        sp_showMode.setAdapter(adapter);
        sp_showMode.setOnItemSelectedListener(this);
        sp_showMode.setSelection(0);
        mMapView = mView.findViewById(R.id.mv_location);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.getUiSettings().setOverlookingGesturesEnabled(false);
        locationListener = new MyLocationListener();
        locationClient.registerLocationListener(locationListener);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//坐标
        option.setScanSpan(app.getLocationFrequency() * 1010);//ms
        option.setOpenGps(true);
        option.setIgnoreKillProcess(true);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        locationClient.setLocOption(option);
        locationClient.start();
        if (!app.getNowLocations().isEmpty()) {
            mBaiduMap.clear();
            drawFriendsLocations(app.getNowLocations());
            initCenterSelector();
            isFirstSet = false;
        }
        return mView;
    }

    /**
     * 中线点列表初始化
     */
    private void initCenterSelector() {
        LogUtil.i(TAG, "[initCenterSelector]");
        ArrayAdapter centerAdapter = new ArrayAdapter(mContext, R.layout.item_select, getFriendsIdList());
        sp_center.setPrompt("切换好友");
        sp_center.setAdapter(centerAdapter);
        sp_center.setOnItemSelectedListener(LocationFragment.this);
        sp_center.setSelection(0);
    }

    /**
     * 获取好友ID列表
     *
     * @return 好友ID数组
     */
    private String[] getFriendsIdList() {
        LogUtil.i(TAG, "[getFriendsIdList]");
        int size = app.getNowLocations().size();
        String[] Ids = new String[size];
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Ids[i] = app.getNowLocations().get(i).getUserId();
            }
        }
        return Ids;
    }

    /**
     * 覆盖onStart()方法,启动接收信息线程
     */
    @Override
    public void onStart() {
        super.onStart();
        LogUtil.i(TAG, "[onStart]");
        if (app.isIsShare()) {
            cb_shareSwitch.setChecked(true);
            mHander.postDelayed(shareTask, 2000);
        }
    }

    /**
     * 覆盖onStop()方法，关闭线程和已经启动的异步任务
     */
    @Override
    public void onStop() {
        super.onStop();
        LogUtil.i(TAG, "[onStart]");

        mHander.removeCallbacks(shareTask);
        if (sendLocationTask != null) sendLocationTask.cancel(true);
        if (showFriendsTask != null) showFriendsTask.cancel(true);
        if (getFriendsLocationsTask != null) getFriendsLocationsTask.cancel(true);
    }


    /**
     * 覆盖onDestroy()方法，关闭和地图视图和定位服务
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHander.removeCallbacks(shareTask);
    }

    /**
     * 自定义位置信息获取监听类
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null && mMapView != null) {
                final double latitude = bdLocation.getLatitude();    //获取纬度信息
                double longitude = bdLocation.getLongitude();    //获取经度信息
                //地址显示
                final String locationDesc = bdLocation.getLocationDescribe();
                //设置目前位置
                nowLocation.setLatitude(latitude);
                nowLocation.setLongitude(longitude);
                nowLocation.setLocationDesc(locationDesc);
                nowLocation.setTime(new Date());
                nowLocation.setUserId(app.getUser().getUserId());
                //第一次进入定位页面需要的操作
                if (isFirst) {//是第一次
                    isFirst = false;//设置为不是第一次
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    //跟新中心点
                    LatLng nextLatLng = new LatLng(nowLocation.getLatitude(), nowLocation.getLongitude());
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(nextLatLng)//缩放中心点
                            .zoom(18);//缩放级别
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                            .newMapStatus(builder.build()));
                    mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("位置信息详情");
                            for (int i = 0; i < app.getNowLocations().size(); i++) {
                                listenerLocation = app.getNowLocations().get(i);
                                if (listenerLocation.getUserId().equals(marker.getTitle())) {
                                    builder.setMessage(DateUtil.getNowDateTime(listenerLocation.getTime()) + "\n"
                                            + listenerLocation.getUserId() + "：" + listenerLocation.getLocationDesc());
                                    break;
                                }
                            }
                            builder.setNegativeButton("向好友发消息", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!listenerLocation.getUserId().equals(app.getUser().getUserId())) {
                                        //调用发消息对话框
                                        sendMsgDialog(listenerLocation.getUserId());
                                    } else {
                                        Toast.makeText(mContext, "不能给自己发消息", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.setPositiveButton("返回", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                            return true;
                        }
                    });
                    if (app.getNowLocations().isEmpty()) {
                        app.getNowLocations().add(nowLocation);
                        drawFriendsLocation(nowLocation);
                    }
                }
            } else {
                nowLocation.setLocationDesc("没有定位信息！");
            }
        }
    }

    /**
     * 向好友发送消息方法
     *
     * @param friendid 朋友id
     */
    private void sendMsgDialog(final String friendid) {
        //进入发送聊天室活动页
        AlertDialog.Builder msgDialog = new AlertDialog.Builder(mContext);
        msgDialog.setMessage("填写发送消息：");
        final EditText et_message = new EditText(mContext);
        msgDialog.setView(et_message);
        msgDialog.setPositiveButton("发送", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!et_message.getText().toString().equals("")) {
                    SendMsgTask sendMsgTask = new SendMsgTask();
                    sendMsgTask.setOnInsertMsgResultListener(new SendMsgTask.OnInsertMsgResultListener() {
                        @Override
                        public void onGetSMTResult(String res) {
                            if (res.equals("success")) {
                                Toast.makeText(mContext, "消息已发送", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    sendMsgTask.execute(new MessageIO(
                            app.getUser().getUserId(), friendid,
                            et_message.getText().toString(), "normal"));
                } else {
                    Toast.makeText(mContext, "不能发送空消息", Toast.LENGTH_SHORT).show();
                }
            }
        });
        msgDialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        msgDialog.show();
    }

    /**
     * 下拉框监听响应方法
     *
     * @param parent
     * @param view
     * @param position 选择的序号
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.sp_showMode) {
            if (position == 0) {
                isShowOnline = false;
                mBaiduMap.clear();
                drawFriendsLocations(app.getNowLocations());
            } else if (position == 1) {
                isShowOnline = true;
                mBaiduMap.clear();
                drawFriendsLocations(app.getNowLocations());
            } else if (position == 2) {
                //仅显示自己
                mBaiduMap.clear();
                LatLng nextLatLng = new LatLng(nowLocation.getLatitude(), nowLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(nextLatLng)//缩放中心点
                        .zoom(19);//缩放级别
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                        .newMapStatus(builder.build()));
                cb_shareSwitch.setChecked(false);
                drawFriendsLocation(nowLocation);
            }
        } else if (parent.getId() == R.id.sp_center) {
            //更新新地图中心，循环切换地图中心
            NowLocation next = app.getNowLocations().get(position);
            LatLng nextLatLng = new LatLng(next.getLatitude(), next.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(nextLatLng)//缩放中心点
                    .zoom(19);//缩放级别
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                    .newMapStatus(builder.build()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * 位置共享选择框监听方法
     *
     * @param buttonView
     * @param isChecked  选中状态
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {//启动位置共享
            app.setIsShare(true);
            mHander.postDelayed(shareTask, 2000);
            sp_showMode.setSelection(0);
        } else {//关闭位置共享
            app.setIsShare(false);
            mHander.removeCallbacks(shareTask);
        }
    }

    Handler mHander = new Handler();//线程控制句柄
    /**
     * 位置共享线程，实现位置信息的发送和位置信息的接收
     */
    private Runnable shareTask = new Runnable() {
        @Override
        public void run() {
            //获取朋友列表
            getFriendsList();
            //发送自己的位置
            sendLocationTask = new SendLocationTask();
            sendLocationTask.setOnSendLocationResultListener(
                    new SendLocationTask.OnSendLocationResultListener() {
                        @Override
                        public void onGetSLTResult(String res) {
                            if (!res.equals("success")) {
                                Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            NowLocation location = new NowLocation(nowLocation);
            location.encrypt();//加密
            sendLocationTask.execute(location);
            mHander.postDelayed(shareTask, app.getLocationFrequency() * 1000);
        }
    };

    /**
     * 启动异步任务，获取朋友列表
     */
    private void getFriendsList() {
        if (app.getFriends().isEmpty()) {//
            //获取好友列表
            showFriendsTask = new ShowFriendsTask();
            showFriendsTask.setOnShowFriendsResultListener(
                    new ShowFriendsTask.OnShowFriendsResultListener() {
                        @Override
                        public void onGetSFTResult(final ArrayList<Friends> friendsList) {
                            if (!friendsList.get(0).getUserId().equals("")) {//有好友
                                app.setFriends(friendsList);
                                getFriendsLocations();
                            } else {//没有好友
                                Toast.makeText(getActivity(), friendsList.get(0).getFriendId(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            showFriendsTask.execute(app.getUser().getUserId());
        } else {
            getFriendsLocations();
        }
    }

    /**
     * 启动异步任务，获取朋友位置信息
     */
    private void getFriendsLocations() {
        //获取好友位置
        getFriendsLocationsTask = new GetFriendsLocationsTask();
        getFriendsLocationsTask.setOnGetMyMsgListener(
                new GetFriendsLocationsTask.OnGetMyMsgResultListener() {
                    @Override
                    public void onGetFLTResult(ArrayList<NowLocation> nowLocations) {
                        if (!nowLocations.get(0).getUserId().equals("")) {
                            app.getNowLocations().clear();
                            app.getNowLocations().add(nowLocation);
                            for (int i = 0; i < nowLocations.size(); i++) {
                                nowLocations.get(i).decrypt();//解密
                            }
                            app.getNowLocations().addAll(nowLocations);
                            mBaiduMap.clear();
                            drawFriendsLocations(app.getNowLocations());
                        } else {
                            Toast.makeText(getActivity(),
                                    nowLocations.get(0).getLocationDesc(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        if (isFirstSet) {
                            initCenterSelector();//初始化Center Spinner
                            isFirstSet = false;
                        }
                    }
                });
        getFriendsLocationsTask.execute(app.getFriends());
    }

    /**
     * 绘制一组位置标注
     */
    private void drawFriendsLocations(ArrayList<NowLocation> _locations) {
        for (int i = 0; i < _locations.size(); i++) {
            drawFriendsLocation(_locations.get(i));
        }
    }

    /**
     * 绘制单个位置标注
     *
     * @param _nowLocation 位置信息
     */
    private void drawFriendsLocation(NowLocation _nowLocation) {
        boolean isOnline = true;
        LatLng latLng = new LatLng(_nowLocation.getLatitude(), _nowLocation.getLongitude());
        OverlayOptions textOption = new TextOptions()
                .bgColor(0xAAFFFF00)
                .fontSize(24)
                .fontColor(0xFFFF00FF)
                .text(_nowLocation.getUserId())
                .position(latLng);
        MarkerOptions option = new MarkerOptions()
                .title(_nowLocation.getUserId())
                .position(latLng)
                .perspective(true)
                .yOffset(-10);
        if (_nowLocation.getUserId().equals(app.getUser().getUserId())) {
            option.icon(BitmapDescriptorFactory.fromResource(ic_location[0]));
        } else {
            Date time = _nowLocation.getTime();
            if (new Date().getTime() - time.getTime() < 30000) {//位置上传时间小于30秒视为在线
                option.icon(BitmapDescriptorFactory.fromResource(ic_location[1]));
            } else {//不在线
                option.icon(BitmapDescriptorFactory.fromResource(ic_location[2]));
                isOnline = false;
            }
        }
        if (isShowOnline) {//仅显示在线
            if (isOnline) {//在线
                mBaiduMap.addOverlay(textOption);
                mBaiduMap.addOverlay(option);
            }
        } else {
            mBaiduMap.addOverlay(textOption);
            mBaiduMap.addOverlay(option);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }
        locationClient.disableLocInForeground(true);
        locationClient.unRegisterLocationListener(locationListener);
        locationClient.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
}
