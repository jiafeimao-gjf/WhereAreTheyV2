package com.gjf.wherearethey_v3;

import static com.gjf.wherearethey_v3.util.SharedUtil.STATIC_PRIVACY_SP;
import static com.gjf.wherearethey_v3.util.SharedUtil.STATIC_PRIVACY_SP_KEY;

import android.app.Application;
import android.content.Context;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.gjf.wherearethey_v3.bean.Friends;
import com.gjf.wherearethey_v3.bean.MessageIO;
import com.gjf.wherearethey_v3.bean.NowLocation;
import com.gjf.wherearethey_v3.bean.User;
import com.gjf.wherearethey_v3.encrypt.AesUtil;
import com.gjf.wherearethey_v3.util.LogUtil;
import com.gjf.wherearethey_v3.util.SharedUtil;

import java.util.ArrayList;

/**
 * 应用类，存放全局数据，采用单利模式
 *
 * @author gjf
 * @version 1.0
 */
public class MainApplication extends Application {
    private static String TAG = "MainApplication";

    public static MainApplication mainApplication;
    public static Context mBase;
    public static User user;
    private static TabGroupActivity tga;
    private int locationFrequency;
    private static boolean isShare;
    private static ArrayList<Friends> friends;
    private static ArrayList<MessageIO> messages;
    private static ArrayList<NowLocation> nowLocations;
    private static String key;
    private static String url;
    private static String username;
    private static String password;

    public static MainApplication getInstance() {
        if (mainApplication == null) {
            mainApplication = new MainApplication();
            mainApplication.setLocationFrequency(5);
            user = new User();
            tga = null;
            user.setUserName("我");
            friends = new ArrayList<>();
            messages = new ArrayList<>();
            nowLocations = new ArrayList<>();
            isShare = false;
//            url = "1B85AB74ED02CF8EA2968E79D408E3924789F2CC25F0FB8B7C2EDCD7B025BAE3E1A4CA866FE3C089CBA2075B0798F8E3";
//            username = "146B2196CBF168C17DE310AB0905CF4";
//            password = "914E39B3C560FEE27B49F1C13CF416F7";
            url = "jdbc:mysql://192.168.1.7:3306/locationshare";
            username = "jiafeiuser";
            password = "locationshare1104/";
            key = "wat";
        }
        return mainApplication;
    }

    public void setLocationFrequency(int locationFrequency) {
        this.locationFrequency = locationFrequency;
    }

    public int getLocationFrequency() {
        return locationFrequency;
    }

    public static void setUser(User user) {
        MainApplication.user = new User(user);//深拷贝
    }

    public static User getUser() {
        return user;
    }

    public static ArrayList<Friends> getFriends() {
        return friends;
    }

    public static void setFriends(ArrayList<Friends> friends) {
        MainApplication.friends = friends;
    }

    public static ArrayList<MessageIO> getMessages() {
        return messages;
    }

    public static void setMessages(ArrayList<MessageIO> messages) {
        MainApplication.messages = messages;
    }

    public static void setNowLocations(ArrayList<NowLocation> nowLocations) {
        MainApplication.nowLocations = nowLocations;
    }

    public static ArrayList<NowLocation> getNowLocations() {
        return nowLocations;
    }

    public static void setIsShare(boolean isShare) {
        MainApplication.isShare = isShare;
    }

    public static boolean isIsShare() {
        return isShare;
    }

    public static void setTga(TabGroupActivity tga) {
        MainApplication.tga = tga;
    }

    public static TabGroupActivity getTga() {
        return tga;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "[onCreate] this = " + this.hashCode());
        SharedUtil.initSP(this);
        mBase = this;// 这里保证传入百度地图sdk的context的是onCreate作用域的context
        intBaiduMap();
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        MainApplication.getInstance();
        LogUtil.setTAG("LocationShare-");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        LogUtil.i(TAG, "[attachBaseContext]  this = " + this.hashCode());
        LogUtil.i(TAG, "[attachBaseContext]  base = " + base.hashCode());
    }

    public void intBaiduMap() {
        LogUtil.i(TAG, "[intBaiduMap] this = " + this.hashCode());
        if (SharedUtil.getInstance(STATIC_PRIVACY_SP).readBooleanShared(STATIC_PRIVACY_SP_KEY, false)) {
            SDKInitializer.setAgreePrivacy(mBase, true);
            //在使用SDK各组件之前初始化context信息，传入ApplicationContext
            SDKInitializer.initialize(mBase);
            LocationClient.setAgreePrivacy(true);
        }
    }

    private String getUrl() {
        return url;
    }

    public String getRealUrl() {
        String realUrl = "";
        try {
            realUrl = AesUtil.decrypt(key, getUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realUrl;
    }

    private String getPassword() {
        return password;
    }

    public String getRealPassword() {
        String realPassword = "";
        try {
            realPassword = AesUtil.decrypt(key, getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realPassword;
    }

    public String getRealPasswordWithoutCrypt() {
        return getPassword();
    }

    public String getRealUsernameWithoutCrypt() {
        return getUsername();
    }

    public String getUrlWithoutCrypt() {
        return getUrl();
    }

    private String getUsername() {
        return username;
    }

    public String getRealUsername() {
        String realUsername = "";
        try {
            realUsername = AesUtil.decrypt(key, getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realUsername;
    }


}