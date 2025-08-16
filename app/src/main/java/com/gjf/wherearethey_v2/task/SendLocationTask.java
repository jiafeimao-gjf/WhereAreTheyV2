package com.gjf.wherearethey_v2.task;

import android.os.AsyncTask;
import android.util.Log;

import com.gjf.wherearethey_v2.bean.NowLocation;
import com.gjf.wherearethey_v2.databaseoperation.dao.INowLocationDao;
import com.gjf.wherearethey_v2.databaseoperation.factory.DaoFactory;

/**
 * 发送位置的异步任务类
 *
 * @author gjf
 * @version 1.0
 */
public class SendLocationTask extends AsyncTask<NowLocation, Void, String> {
    private static final String TAG = "SendLocationTask";
    private INowLocationDao nowLocationDao;

    public SendLocationTask() {
        super();
        nowLocationDao = DaoFactory.getNowLocationDao();
    }

    @Override
    protected String doInBackground(NowLocation... nowLocations) {
        String res = "success";
        if (!nowLocationDao.initConnection()) {
            res = "连接失败,请检查网络！";
        } else {
            NowLocation nowLocation = nowLocations[0];
            int sendRes = nowLocationDao.sendLocation(nowLocation);
            if (sendRes != 1) {
                res = "位置发送失败";
                Log.e(TAG, "位置发送失败");
            }
        }
        nowLocationDao.closeConnection();
        return res;
    }

    @Override
    protected void onPostExecute(String s) {
        resultListener.onGetSLTResult(s);
    }

    private OnSendLocationResultListener resultListener;

    public void setOnSendLocationResultListener(OnSendLocationResultListener listener) {
        resultListener = listener;
    }

    public interface OnSendLocationResultListener {
        void onGetSLTResult(String res);
    }
}
