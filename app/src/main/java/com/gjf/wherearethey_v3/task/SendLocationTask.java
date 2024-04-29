package com.gjf.wherearethey_v3.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v3.bean.NowLocation;
import com.gjf.wherearethey_v3.databaseoperation.dao.NowLocationDao;

/**
 * 发送位置信息的异步任务类
 * @author gjf
 * @version 1.0
 */
public class SendLocationTask extends AsyncTask<NowLocation,Void,String> {
    private NowLocationDao nowLocationDao;

    public SendLocationTask(){
        super();
         nowLocationDao = NowLocationDao.getInstance();
    }
    @Override
    protected String doInBackground(NowLocation... nowLocations) {
        String res;
        if(!nowLocationDao.initConnection()){
            res = "连接失败，请检查网络！";
        }else {
            NowLocation nowLocation = nowLocations[0];
            int insertRes = nowLocationDao.sendLocation(nowLocation);
            if(insertRes == 1){
                res = "success";
            }else if(insertRes == 0){
                res = "发送失败";
            }else{
                res = "-1";
            }
        }
        nowLocationDao.closeConnection();
        return res;
    }


    @Override
    protected void onPostExecute(String res) {
        listener.onGetSLTResult(res);
    }

    private OnSendLocationResultListener listener;
    public void setOnSendLocationResultListener(OnSendLocationResultListener resultListener){
        listener = resultListener;
    }
    public interface OnSendLocationResultListener {
        void onGetSLTResult(String res);
    }
}
