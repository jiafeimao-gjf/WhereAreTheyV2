package com.gjf.wherearethey_v2.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v2.bean.User;
import com.gjf.wherearethey_v2.databaseoperation.dao.UserDao;

/**
 * 注册异步任务类
 * @author gjf
 * @version 1.0
 */
public class RegisterTask extends AsyncTask<User,Void,String> {
    private UserDao userDao;
    public RegisterTask(){
        super();
        userDao = UserDao.getInstance();
    }
    @Override
    protected String doInBackground(User... users) {
        String res;
        if(!userDao.initConnection()){
            res = "连接失败,请检查网络！";
        }else {
            User newUser = users[0];
            int registerRes = userDao.insertNewUser(newUser);
            if(registerRes == 1){
                res = "成功注册";
            }else if(registerRes == 0){
                res = "ID已被注册";
            }else if(registerRes == 2){
                res = "注册失败";
            }else{
                res = "UnknownError";
            }
        }
        userDao.closeConnection();
        return res;
    }

    @Override
    protected void onPostExecute(String res) {
        resultListener.onGetRTResult(res);
    }


    private OnRegisterResultListener resultListener;
    public void setOnRegisterResultListener(OnRegisterResultListener listener){
        resultListener = listener;
    }

    public interface OnRegisterResultListener{
        void onGetRTResult(String res);
    }
}