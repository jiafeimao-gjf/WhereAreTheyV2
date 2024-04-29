package com.gjf.wherearethey_v3.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v3.bean.Friends;
import com.gjf.wherearethey_v3.databaseoperation.dao.FriendsDao;

/**
 * 修改是否接收位置信息的异步任务类
 * @author gjf
 * @version 1.0
 */
public class ChangeIsReceiveLocationTask extends AsyncTask<Friends,Void,String> {
    private FriendsDao friendsDao;
    public ChangeIsReceiveLocationTask(){
        super();
        friendsDao = FriendsDao.getInstance();
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
            int result = friendsDao.changeIsReceiveLocation(friends[0]);
            if(result == 1){
                res = "success";
            }else{
                res = "修改失败";
            }
        }
        friendsDao.closeConnection();
        return res;
    }
    /**
     * 任务完成后的方法
     * @param s 处理结果
     */
    @Override
    protected void onPostExecute(String s) {
        listener.onGetCIRLTResult(s);
    }

    private OnGetChangeIsReceiveResultListener listener;//获取结果的监听器
    /**
     * 设置监听器
     * @param resultListener 传入的监听器
     */
    public void setOnGetChangeIsReceiveResultListener(OnGetChangeIsReceiveResultListener resultListener){
        listener = resultListener;
    }
    /**
     * 监听器接口
     */
    public interface OnGetChangeIsReceiveResultListener {
        void onGetCIRLTResult(String string);
    }
}
