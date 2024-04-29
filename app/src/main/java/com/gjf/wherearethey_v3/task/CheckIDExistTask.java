package com.gjf.wherearethey_v3.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v3.databaseoperation.dao.UserDao;

/**
 * 检查ID是否存在的异步任务类
 * @author gjf
 * @version 1.0
 */
public class CheckIDExistTask extends AsyncTask<String,Void,String> {
    private UserDao userDao;

    public CheckIDExistTask(){
        super();
        userDao = UserDao.getInstance();
    }
    @Override
    protected String doInBackground(String... strings) {
        String res;
        if(!userDao.initConnection()){
            res = "连接失败,请检查网络！";
        }else {
            String userId = strings[0];
            int checkResult = userDao.isUserIDExist(userId);
            if(checkResult == 1){
                res = "exist";
            }else if (checkResult == 0) {
                res = "ID不存在";
            }else{
                res = "UnknownError";
            }
        }
        userDao.closeConnection();
        return res;
    }

    @Override
    protected void onPostExecute(String res) {
        resultListener.onGetCIETResult(res);
    }

    private OnCheckIdResultListener resultListener;
    public void setOnCheckIdResultListener(OnCheckIdResultListener listener){
        resultListener = listener;
    }

    public interface OnCheckIdResultListener{
        void onGetCIETResult(String res);
    }
}
