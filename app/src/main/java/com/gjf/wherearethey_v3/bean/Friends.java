package com.gjf.wherearethey_v3.bean;

/** 朋友类
 * @author gjf
 * @version 2.0
 */
public class Friends {
    private String userId;
    private String friendId;
    private String isReceiveLocation;

    public Friends(){
        userId = "";
        friendId = "";
    }

    /**
     * 构造函数
     * @param userId
     * @param friendId
     */
    public Friends(String userId,String friendId){
        this.userId = userId;
        this.friendId = friendId;
        this.isReceiveLocation = "yes";
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setIsReceiveLocation(String isReceiveLocation) {
        this.isReceiveLocation = isReceiveLocation;
    }

    public String getIsReceiveLocation() {
        return isReceiveLocation;
    }
}
