package com.gjf.wherearethey_v3.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v3.bean.Friends;
import com.gjf.wherearethey_v3.databaseoperation.dao.FriendsDao;

/**
 * 增加朋友关系的异步任务
 * @author gjf
 * @version 1.0
 */
public class BecomeFriendsTask extends AsyncTask<Friends,Void,String> {
    private FriendsDao friendsDao;//朋友表操作对象
    public BecomeFriendsTask(){
        super();
        friendsDao = FriendsDao.getInstance();//获取唯一实例
    }

    /**
     * 后台处理方法
     * @param friends 传入的数据
     * @return 处理结果
     */
    @Override
    protected String doInBackground(Friends... friends) {
        String res;
        if(!friendsDao.initConnection()){
            res = "连接失败，请检查网络！";
        }else{
            Friends friend = friends[0];
            int result = friendsDao.becomeFriends(friend);
            if(result == 1){
                res = "加好友成功";
            }else if(result == 0){
                res = "已经是好友了";
            }else{
                res = "加好友失败";
            }
        }
        friendsDao.closeConnection();
        return res;
    }

    /**
     * 任务完成后的方法
     * @param res 处理结果
     */
    @Override
    protected void onPostExecute(String res) {
        listener.onGetBFTResult(res);//调用结果监听器方法
    }

    private OnBecomeFriendsResultListener listener;//获取结果的监听器

    /**
     * 设置监听器
     * @param resultListener 传入的监听器
     */
    public void setOnBecomeFriendsResultListener(OnBecomeFriendsResultListener resultListener){
        listener = resultListener;
    }

    /**
     * 监听器接口
     */
    public interface OnBecomeFriendsResultListener{
        void onGetBFTResult(String res);
    }
}
