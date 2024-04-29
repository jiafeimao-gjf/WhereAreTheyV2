package com.gjf.wherearethey_v3.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v3.bean.MessageIO;
import com.gjf.wherearethey_v3.databaseoperation.dao.MessageIODao;

/**
 * 删除消息的异步任务类
 * @author gjf
 * @version 1.0
 */
public class DeleteMsgTask extends AsyncTask<MessageIO,Void,String> {
    private MessageIODao messageIODao;

    public DeleteMsgTask(){
        super();
        messageIODao = MessageIODao.getInstance();
    }

    @Override
    protected String doInBackground(MessageIO... messageIOS) {
        String res;
        if(!messageIODao.initConnection()){
            res = "连接失败，请检查网络！";
        }else{
            MessageIO messageIO = messageIOS[0];
            int deleteRes = messageIODao.deleteExactMsg(messageIO);
            if(deleteRes == 1){
                res = "信息已删除";
            }else{
                res = "删除失败";
            }
        }
        messageIODao.closeConnection();
        return res;
    }

    @Override
    protected void onPostExecute(String res) {
        listener.onGetDMTResult(res);
    }

    private OnDeleteMsgResultListener  listener;
    public void setOnDeleteMsgResultListener(OnDeleteMsgResultListener resultListener){
        listener = resultListener;
    }
    public interface OnDeleteMsgResultListener{
        void onGetDMTResult(String res);
    }
}
