package com.gjf.wherearethey_v3.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v3.bean.Friends;
import com.gjf.wherearethey_v3.databaseoperation.dao.FriendsDao;

/**
 * 接触朋友关系的异步任务类
 * @author gjf
 * @version 1.0
 */
public class DeleteFriendTask extends AsyncTask<Friends,Void,String> {
    private FriendsDao friendsDao;
    public DeleteFriendTask(){
        super();
        friendsDao = FriendsDao.getInstance();
    }
    @Override
    protected String doInBackground(Friends... friends) {
        String res;
        if(!friendsDao.initConnection()){
            res = "连接是失败，请检查网路！";
        }else {
            int result = friendsDao.deleteEachOther(friends[0]);
            if(result == 1){
                res = "你们已解除好友关系，不能再分享位置了哦！";
            }else{
                res = "删除失败";
            }
        }
        friendsDao.closeConnection();
        return res;
    }

    @Override
    protected void onPostExecute(String res) {
        listener.onGetDFTResult(res);
    }

    private OnDeleteFriendResultListener listener;
    public void setOnDeleteFriendResultListener(OnDeleteFriendResultListener resultListener){
        listener = resultListener;
    }

    public interface OnDeleteFriendResultListener {
        void onGetDFTResult(String res);
    }
}
