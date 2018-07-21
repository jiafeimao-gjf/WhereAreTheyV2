package com.gjf.wherearethey_v2.task;

import android.os.AsyncTask;

import com.gjf.wherearethey_v2.bean.Friends;
import com.gjf.wherearethey_v2.bean.NowLocation;
import com.gjf.wherearethey_v2.databaseoperation.dao.NowLocationDao;

import java.util.ArrayList;

/**
 * 获取所有朋友的位置信息异步任务类
 * @author gjf
 * @version 2.0
 */
public class GetFriendsLocationsTask extends AsyncTask<ArrayList<Friends>,Void,ArrayList<NowLocation>> {

    private NowLocationDao nowLocationDao;

    public GetFriendsLocationsTask(){
        super();
        nowLocationDao = nowLocationDao.getInstance();
    }

    @Override
    protected ArrayList<NowLocation> doInBackground(ArrayList<Friends>... friends) {
        ArrayList<NowLocation> nowLocations = new ArrayList<>();
        NowLocation nowLocation = new NowLocation();
        if(!nowLocationDao.initConnection()){
            nowLocation.setLocationDesc("连接失败，请检查网络!");
            nowLocations.add(nowLocation);
        }else{
            for(int i = 0;i < friends[0].size();i++){
                if(friends[0].get(i).getIsReceiveLocation().equals("yes")){//接收位置信息
                    nowLocation = nowLocationDao.getLocationById(friends[0].get(i).getFriendId());
                    if(!nowLocation.getUserId().equals("")){
                        nowLocations.add(nowLocation);
                    }
                }
            }
        }
        if(nowLocations.isEmpty()){
            nowLocation.setLocationDesc("没有任何好友位置信息");
            nowLocations.add(nowLocation);
        }
        nowLocationDao.closeConnection();
        return nowLocations;
    }

    @Override
    protected void onPostExecute(ArrayList<NowLocation> nowLocations) {
        listener.onGetFLTResult(nowLocations);
    }

    private OnGetMyMsgResultListener listener;
    public void setOnGetMyMsgListener(OnGetMyMsgResultListener resultListener){
        listener = resultListener;
    }


    public interface OnGetMyMsgResultListener{
        void onGetFLTResult(ArrayList<NowLocation> nowLocations);
    }
}
