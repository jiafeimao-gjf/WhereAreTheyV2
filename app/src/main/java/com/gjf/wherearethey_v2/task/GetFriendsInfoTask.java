package com.gjf.wherearethey_v2.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v2.bean.Friends;
import com.gjf.wherearethey_v2.databaseoperation.dao.FriendsDao;

import java.util.ArrayList;

/**
 * 获取某个朋友信息异步任务类
 * @author gjf
 * @version 1.0
 */
public class GetFriendsInfoTask extends AsyncTask<Friends,Void,ArrayList<String>> {
    FriendsDao friendsDao;
    public GetFriendsInfoTask(){
        super();
        friendsDao = FriendsDao.getInstance();
    }

    @Override
    protected ArrayList<String> doInBackground(Friends... friends) {
        ArrayList<String> friendInfo = new ArrayList<>();
        if(!friendsDao.initConnection()){
            friendInfo.add("连接失败，请检查网络");
        }else{
            friendInfo = friendsDao.getFriendsInfo(friends[0]);
        }
        friendsDao.closeConnection();
        return friendInfo;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        listener.onGetFITResult(strings);
    }

    private OnGetFriendsInfoResultListener listener;
    public void setOnGetFriendsInfoResultListener(OnGetFriendsInfoResultListener resultListener){
        listener = resultListener;
    }
    public interface OnGetFriendsInfoResultListener{
        void onGetFITResult(ArrayList<String> friendInfo);
    }
}
