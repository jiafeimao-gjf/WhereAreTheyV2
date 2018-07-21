package com.gjf.wherearethey_v2;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.gjf.wherearethey_v2.adapter.FriendsListAdapter;
import com.gjf.wherearethey_v2.bean.Friends;
import com.gjf.wherearethey_v2.task.ShowFriendsTask;

import java.util.ArrayList;

/**
 * 朋友列表页面类
 * @author gjf
 * @version 1.0
 */
public class DisplayFriendsActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener,
        ShowFriendsTask.OnShowFriendsResultListener{
    private SwipeRefreshLayout srl_friendsFresh;//刷新小视图
    private ListView lv_friends;
    private MainApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_friends);
        app = MainApplication.getInstance();
        srl_friendsFresh = findViewById(R.id.srl_friendsRefresh);
        srl_friendsFresh.setOnRefreshListener(this);
        srl_friendsFresh.setColorSchemeResources(R.color.red,R.color.orange,
                R.color.green,R.color.blue);
        lv_friends = findViewById(R.id.lv_friends);
        if(!app.getFriends().isEmpty()){//好友列表不为空
            FriendsListAdapter friendsListAdapter = new FriendsListAdapter(
                    this,R.layout.item_friend,app.getFriends());
            lv_friends.setAdapter(friendsListAdapter);
            lv_friends.setOnItemClickListener(friendsListAdapter);
            lv_friends.setOnItemLongClickListener(friendsListAdapter);
        }else{//好友列表为空，启动线程获取好友
            ShowFriendsTask showFriendsTask = new ShowFriendsTask();
            showFriendsTask.setOnShowFriendsResultListener(this);
            showFriendsTask.execute(app.getUser().getUserId());
        }
        Toast.makeText(this, "下拉刷新朋友列表", Toast.LENGTH_SHORT).show();
        Toolbar toolbar = findViewById(R.id.tb_yourFriends);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_DOWN){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRefresh() {
        ShowFriendsTask showFriendsTask = new ShowFriendsTask();
        showFriendsTask.setOnShowFriendsResultListener(this);
        showFriendsTask.execute(app.getUser().getUserId());
    }

    /**
     * 获取朋友列表的结果方法
     * @param friends 朋友列表
     */
    @Override
    public void onGetSFTResult(ArrayList<Friends> friends) {
        if(friends.get(0).getUserId().equals("")){//没有好友
            Toast.makeText(this, friends.get(0).getFriendId(), Toast.LENGTH_SHORT).show();
            friends.clear();
            FriendsListAdapter friendsListAdapter = new FriendsListAdapter(
                    this,R.layout.item_friend,friends);
            lv_friends.setAdapter(friendsListAdapter);
            lv_friends.setOnItemClickListener(friendsListAdapter);
            lv_friends.setOnItemLongClickListener(friendsListAdapter);
        }else{//有好友
            app.setFriends(friends);
            FriendsListAdapter friendsListAdapter = new FriendsListAdapter(
                    this,R.layout.item_friend,friends);
            lv_friends.setAdapter(friendsListAdapter);
            lv_friends.setOnItemClickListener(friendsListAdapter);
            lv_friends.setOnItemLongClickListener(friendsListAdapter);
        }
        srl_friendsFresh.setRefreshing(false);
    }
}

