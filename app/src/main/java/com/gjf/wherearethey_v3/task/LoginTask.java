package com.gjf.wherearethey_v3.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v3.bean.User;
import com.gjf.wherearethey_v3.databaseoperation.dao.UserDao;

/**
 * 登陆的异步任务类
 * @author gjf
 * @version 1.0
 */
public class LoginTask extends AsyncTask<User,Void,User> {
    private UserDao userDao;
    public LoginTask(){
        super();
        userDao = UserDao.getInstance();
    }

    @Override
    protected User doInBackground(User... users) {
        User userOut = new User();
        if(!userDao.initConnection()){
            userOut.setUserType("连接失败,请检查网络！");
        }else{
            User userIn = users[0];
            int loginResult = userDao.loginCheck(userIn.getUserId(), userIn.getPassword());
            if (loginResult == 1) {
                userOut = userDao.getUserInfoByKey(userIn.getUserId());
            } else if (loginResult == 2) {
                userOut.setUserType("密码错误");
            } else if (loginResult == 0) {
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
        resultListener.onGetLTResult(user);
    }

    private OnLoginResultListener resultListener;
    public void setOnLoginResultListener(OnLoginResultListener listener){
        resultListener = listener;
    }

    public interface OnLoginResultListener{
        void onGetLTResult(User user);
    }
}
