package com.gjf.wherearethey_v3.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v3.bean.MessageIO;
import com.gjf.wherearethey_v3.databaseoperation.dao.MessageIODao;

import java.util.ArrayList;

/**
 * 获取用户消息异步人物类
 * @author gjf
 * @version 1.0
 */
public class GetMyMsgTask extends AsyncTask<String,Void,ArrayList<MessageIO>> {
    private MessageIODao messageIODao;

    public GetMyMsgTask(){
        super();
        messageIODao = MessageIODao.getInstance();
    }

    @Override
    protected ArrayList<MessageIO> doInBackground(String... strings) {
        ArrayList<MessageIO> msgList = new ArrayList<>();
        MessageIO messageIO = new MessageIO();
        if(!messageIODao.initConnection()){
            messageIO.setMessage("连接失败，请检查网络!");
            msgList.add(messageIO);
        }else{
            String userid = strings[0];
            msgList = messageIODao.getMsgByDestId(userid);
            if(msgList.isEmpty()){
                messageIO.setMessage("没有消息");
                msgList.add(messageIO);
            }
        }
        messageIODao.closeConnection();
        return msgList;
    }

    @Override
    protected void onPostExecute(ArrayList<MessageIO> messageIOS) {
        listener.onGetMMTResult(messageIOS);
    }

    private OnGetMyMsgResultListener listener;
    public void setOnGetMyMsgListener(OnGetMyMsgResultListener resultListener){
        listener = resultListener;
    }


    public interface OnGetMyMsgResultListener{
        void onGetMMTResult(ArrayList<MessageIO> msgList);
    }
}
