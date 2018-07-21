package com.gjf.wherearethey_v2.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v2.bean.MessageIO;
import com.gjf.wherearethey_v2.databaseoperation.dao.MessageIODao;

/**
 * 发送消息异步任务类
 * @author gjf
 * @version 1.1
 */
public class SendMsgTask extends AsyncTask<MessageIO,Void,String> {
    private MessageIODao messageIODao;

    public SendMsgTask(){
        super();
        messageIODao = MessageIODao.getInstance();
    }

    @Override
    protected String doInBackground(MessageIO... messageIOS) {
        String res;
        if(!messageIODao.initConnection()){
            res = "连接失败，请检查网络！";
        }else {
            MessageIO messageIO = messageIOS[0];
            int insertRes = messageIODao.insertMsg(messageIO);
            if(insertRes == 1){
                res = "success";
            }else if(insertRes == 0){
                res = "发送失败";
            }else{
                res = "-1";
            }
        }
        messageIODao.closeConnection();
        return res;
    }


    @Override
    protected void onPostExecute(String res) {
        listener.onGetSMTResult(res);
    }

    private OnInsertMsgResultListener listener;
    public void setOnInsertMsgResultListener(OnInsertMsgResultListener resultListener){
        listener = resultListener;
    }
    public interface OnInsertMsgResultListener{
        void onGetSMTResult(String res);
    }
}
