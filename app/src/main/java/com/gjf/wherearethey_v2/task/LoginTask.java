package com.gjf.wherearethey_v2.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v2.bean.User;
import com.gjf.wherearethey_v2.databaseoperation.dao.IUserDao;
import com.gjf.wherearethey_v2.databaseoperation.factory.DaoFactory;

/**
 * 登陆的异步任务类
 * @author gjf
 * @version 1.0
 */
public class LoginTask extends AsyncTask<User,Void,User> {
    private IUserDao userDao;
    public LoginTask(){
        super();
        userDao = DaoFactory.getUserDao();
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
