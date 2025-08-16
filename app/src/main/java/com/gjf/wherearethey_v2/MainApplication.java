package com.gjf.wherearethey_v2;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.gjf.wherearethey_v2.bean.Friends;
import com.gjf.wherearethey_v2.bean.MessageIO;
import com.gjf.wherearethey_v2.bean.NowLocation;
import com.gjf.wherearethey_v2.bean.User;
import com.gjf.wherearethey_v2.databaseoperation.factory.DaoFactory;
import com.gjf.wherearethey_v2.encrypt.AesUtil;

import java.util.ArrayList;

/**
 * 应用类，存放全局数据，采用单利模式
 * @author gjf
 * @version 1.0
 */
public class MainApplication extends Application {
    public static MainApplication mainApplication;
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

    public static MainApplication getInstance(){
        if(mainApplication == null){
            mainApplication = new MainApplication();
            mainApplication.setLocationFrequency(5);
            user = new User();
            tga = null;
            user.setUserName("我");
            friends = new ArrayList<>();
            messages = new ArrayList<>();
            nowLocations = new ArrayList<>();
            isShare = false;
            url = "1B85AB74ED02CF8EA2968E79D408E3924789F2CC25F0FB8B7C2EDCD7B025BAE3E1A4CA866FE3C089CBA2075B0798F8E3";
            username = "146B2196CBF168C17DE310AB0905CF4";
            password = "914E39B3C560FEE27B49F1C13CF416F7";
            key="wat";
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
        SDKInitializer.initialize(getApplicationContext());
        // Initialize the DAO factory
        DaoFactory.initialize(this);
    }

    private String getUrl() {
        return url;
    }

    public String getRealUrl() {
        String realUrl = "";
        try {
            realUrl = AesUtil.decrypt(key,getUrl());
        }catch (Exception e){
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
            realPassword = AesUtil.decrypt(key,getPassword());
        }catch (Exception e){
            e.printStackTrace();
        }
        return realPassword;
    }

    private String getUsername() {
        return username;
    }

    public String getRealUsername(){
        String realUsername = "";
        try {
            realUsername = AesUtil.decrypt(key,getUsername());
        }catch (Exception e){
            e.printStackTrace();
        }
        return realUsername;
    }

}