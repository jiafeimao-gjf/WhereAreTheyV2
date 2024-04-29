package com.gjf.wherearethey_v3.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v3.bean.Friends;
import com.gjf.wherearethey_v3.databaseoperation.dao.FriendsDao;

/**
 * 检查用户是否有朋友关系的异步人物类
 * @author gjf
 * @version 1.0
 */
public class IsFriendsTask extends AsyncTask<Friends,Void,String> {
    private FriendsDao friendsDao;
    public IsFriendsTask(){
        super();
        friendsDao = FriendsDao.getInstance();
    }
    @Override
    protected String doInBackground(Friends... friends) {
        String res;
        if(!friendsDao.initConnection()){
            res = "连接失败，请检查网路！";
        }else{
            Friends friend = friends[0];
            int result = friendsDao.isHasBeenFriends(friend);
            if(result == 1){
                res = "yes";
            }else{
                res = "no";
            }
        }
        friendsDao.closeConnection();
        return res;
    }

    @Override
    protected void onPostExecute(String res) {
        listener.onGetIFTResult(res);
    }

    private OnCheckIsFriendsResultListener listener;
    public void setOnCheckIsFriendsResultListener(OnCheckIsFriendsResultListener resultListener){
        listener = resultListener;
    }

    public interface OnCheckIsFriendsResultListener {
        void onGetIFTResult(String res);
    }
}
