package com.gjf.wherearethey_v3.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v3.bean.MessageIO;
import com.gjf.wherearethey_v3.databaseoperation.dao.IMessageIODao;
import com.gjf.wherearethey_v3.databaseoperation.dao.MessageIODao;
import com.gjf.wherearethey_v3.databaseoperation.factory.DaoFactory;

/**
 * 发送消息的异步任务类
 * @author gjf
 * @version 1.0
 */
public class SendMsgTask extends AsyncTask<MessageIO,Void,String> {
    private IMessageIODao messageIODao;
    public SendMsgTask(){
        super();
        messageIODao = DaoFactory.getMessageIODao();
    }

    @Override
    protected String doInBackground(MessageIO... messageIOS) {
        String res = "success";
        if(!messageIODao.initConnection()){
            res = "连接失败,请检查网络！";
        }else{
            MessageIO messageIO = messageIOS[0];
            int insertRes = messageIODao.insertMsg(messageIO);
            if(insertRes != 1){
                res = "消息发送失败";
            }
        }
        messageIODao.closeConnection();
        return res;
    }

    @Override
    protected void onPostExecute(String s) {
        resultListener.onGetSMTResult(s);
    }

    private OnInsertMsgResultListener resultListener;
    public void setOnInsertMsgResultListener(OnInsertMsgResultListener listener){
        resultListener = listener;
    }

    public interface OnInsertMsgResultListener{
        void onGetSMTResult(String res);
    }
}
