package com.gjf.wherearethey_v2.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v2.bean.User;
import com.gjf.wherearethey_v2.databaseoperation.dao.UserDao;

/**
 * 修改昵称的异步任务类
 * @author gjf
 * @version 1.0
 */
public class ChangeNameTask extends AsyncTask<User,Void,String> {
    private UserDao userDao;
    public ChangeNameTask(){
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
            int changePwdRes = userDao.updateName(user.getUserId(),user.getUserName());
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
        resultListener.onGetCNTResult(res);
    }

    private OnChangeNameResultListener resultListener;
    public void setOnChangePwdResultListener(OnChangeNameResultListener listener){
        resultListener = listener;
    }

    public interface OnChangeNameResultListener {
        void onGetCNTResult(String res);
    }
}
