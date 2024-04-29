package com.gjf.wherearethey_v3.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v3.bean.Friends;
import com.gjf.wherearethey_v3.databaseoperation.dao.FriendsDao;

import java.util.ArrayList;

/**
 * 获取朋友ID列表异步人物类
 * @author gjf
 * @version 1.0
 */
public class ShowFriendsTask extends AsyncTask<String,Void,ArrayList<Friends> > {
    private FriendsDao friendsDao;
    public ShowFriendsTask(){
        super();
        friendsDao = FriendsDao.getInstance();
    }
    @Override
    protected ArrayList<Friends> doInBackground(String... userIds) {
        ArrayList<Friends>  friendsRes = new ArrayList<>();
        Friends friends = new Friends();
        if(!friendsDao.initConnection()){
            friends.setFriendId("连接失败，请检查网路！");
            friendsRes.add(friends);
        }else {
            friendsRes = friendsDao.showFriends(userIds[0]);
            if(friendsRes.size()==0){//没有盆友
                friends.setFriendId("没有朋友，通过朋友ID加好友吧！");
                friendsRes.add(friends);
            }
        }
        friendsDao.closeConnection();
        return friendsRes;
    }

    @Override
    protected void onPostExecute(ArrayList<Friends> friends) {
        listener.onGetSFTResult(friends);
    }

    private OnShowFriendsResultListener listener;
    public void setOnShowFriendsResultListener(OnShowFriendsResultListener resultListener){
        listener = resultListener;
    }

    public interface OnShowFriendsResultListener {
        void onGetSFTResult(ArrayList<Friends> friends);
    }
}
