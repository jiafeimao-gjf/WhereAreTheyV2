package com.gjf.wherearethey_v3.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v3.bean.User;
import com.gjf.wherearethey_v3.databaseoperation.dao.UserDao;

/**
 * 通过ID获取用户信息异步任务类
 * @author gjf
 * @version 1.0
 */
public class GetInfoByIdTask extends AsyncTask<String,Void,User> {
    private UserDao userDao;

    public GetInfoByIdTask(){
        super();
        userDao = UserDao.getInstance();
    }
    @Override
    protected User doInBackground(String... strings) {
        User userOut = new User();
        if(!userDao.initConnection()){
            userOut.setUserType("连接失败,请检查网络！");
        }else {
            String userId = strings[0];
            int checkResult = userDao.isUserIDExist(userId);
            if(checkResult == 1){
                userOut = userDao.getUserInfoByKey(userId);
            }else if (checkResult == 0) {
                userOut.setUserType("ID不存在");
            }else{
                userOut.setUserType("UnknownError");
            }
        }
        userDao.closeConnection();
        return userOut;
    }

    @Override
    protected void onPostExecute(User user) {
        resultListener.onGetIBITResult(user);
    }

    private OnGetInfoResultListener resultListener;
    public void setOnGetInfoResultListener(OnGetInfoResultListener listener){
        resultListener = listener;
    }

    public interface OnGetInfoResultListener{
        void onGetIBITResult(User user);
    }
}
