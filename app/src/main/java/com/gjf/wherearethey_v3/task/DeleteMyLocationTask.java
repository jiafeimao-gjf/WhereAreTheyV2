package com.gjf.wherearethey_v3.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v3.databaseoperation.dao.NowLocationDao;

/**
 * 删除自己的位置的异步任务类
 * @author gjf
 * @version 1.0
 */
public class DeleteMyLocationTask extends AsyncTask<String,Void,String>{
    private NowLocationDao nowLocationDao;
    private OnDeleteLocationResultListener listener;

    public DeleteMyLocationTask(){
        super();
        nowLocationDao = NowLocationDao.getInstance();
    }

    @Override
    protected String doInBackground(String... strings) {
        String res;
        if(!nowLocationDao.initConnection()){
            res = "连接失败，请检查网路";
        }else{
            int result = nowLocationDao.deleteMyLocation(strings[0]);
            if(result == 1){
                res = "已成功移除出在线位置";
            }else if(result == 0){
                res = "您在云上没有位置";
            }else{
                res = "移除出在线位置失败";
            }
        }
        nowLocationDao.closeConnection();
        return res;
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onGetDMLTResult(s);
    }

    public void setOnDeleteLocationResultListener(OnDeleteLocationResultListener resultListener){
        listener = resultListener;
    }
    public interface OnDeleteLocationResultListener{
        void onGetDMLTResult(String res);
    }

}
