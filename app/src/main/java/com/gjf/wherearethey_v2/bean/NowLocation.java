package com.gjf.wherearethey_v2.bean;

import com.gjf.wherearethey_v2.encrypt.AesUtil;

import java.util.Date;

/** 位置信息类
 * @author gjf
 * @version 2.0
 */
public class NowLocation {
    private String userId;
    private double latitude;
    private double longitude;
    private String locationDesc;
    private Date time;

    public NowLocation(){
        userId = "";
        latitude = -1;
        longitude = -1;
        locationDesc = "";
        time = new Date();
    }

    /**
     * 深复制
     * @param nowLocation
     */
    public NowLocation(NowLocation nowLocation){
        userId = nowLocation.getUserId();
        latitude = nowLocation.getLatitude();
        longitude = nowLocation.getLongitude();
        locationDesc = nowLocation.getLocationDesc();
        time = nowLocation.getTime();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }

    public String getLocationDesc() {
        return locationDesc;
    }

    public void setTime(Date date) {
        this.time = date;
    }

    public Date getTime() {
        return time;
    }

    /**
     * 数据加密
     */
    public void encrypt(){
        try{
            this.locationDesc = AesUtil.encrypt("wat",this.locationDesc);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 数据解密
     */
    public void decrypt(){
        try{
            this.locationDesc = AesUtil.decrypt("wat",this.locationDesc);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
