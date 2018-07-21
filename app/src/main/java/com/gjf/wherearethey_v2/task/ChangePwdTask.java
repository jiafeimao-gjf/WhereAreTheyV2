package com.gjf.wherearethey_v2.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v2.bean.User;
import com.gjf.wherearethey_v2.databaseoperation.dao.UserDao;

/**
 * 修改密码的异步任务类
 * @author gjf
 * @version 1.0
 */
public class ChangePwdTask extends AsyncTask<User,Void,String> {
    private UserDao userDao;
    public ChangePwdTask(){
        super();
        userDao = UserDao.getInstance();
    }
    @Override
    protected String doInBackground(User... users) {
        String res;
        if(!userDao.initConnection()){
            res = "连接失败,请检查网络！";
        }else{
            User user = users[0];
            int changePwdRes = userDao.updatePassword(user.getUserId(),user.getPassword());
            if(changePwdRes == 1){
                res = "修改成功";
            }else {
                res = "修改失败";
            }
        }
        userDao.closeConnection();
        return res;
    }

    @Override
    protected void onPostExecute(String res) {
        resultListener.onGetCPTResult(res);
    }

    private OnChangePwdResultListener resultListener;
    public void setOnChangePwdResultListener(OnChangePwdResultListener listener){
        resultListener = listener;
    }

    public interface OnChangePwdResultListener{
        void onGetCPTResult(String res);
    }
}

