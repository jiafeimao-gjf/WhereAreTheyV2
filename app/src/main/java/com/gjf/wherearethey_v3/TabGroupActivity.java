package com.gjf.wherearethey_v3;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.gjf.wherearethey_v3.bean.Friends;
import com.gjf.wherearethey_v3.bean.MessageIO;
import com.gjf.wherearethey_v3.bean.NowLocation;
import com.gjf.wherearethey_v3.bean.User;
import com.gjf.wherearethey_v3.fragment.MessageFragment;
import com.gjf.wherearethey_v3.fragment.MyInfoFragment;
import com.gjf.wherearethey_v3.fragment.LocationFragment;

import java.util.ArrayList;

/**
 * 主页面类，实现三个子页面的动态加载
 * @author gjf
 * @version 1.1
 */
public class TabGroupActivity extends AppCompatActivity {
    private FragmentTabHost tabHost;
    MainApplication app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_group);
        app = MainApplication.getInstance();
        app.setTga(this);
        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup(this,getSupportFragmentManager(),R.id.realtabcontent);
        //添加三个标签
        tabHost.addTab(getTabView(R.string.menu_message, R.drawable.tab_message_selector),
                MessageFragment.class, null);
        tabHost.addTab(getTabView(R.string.menu_location, R.drawable.tab_location_selector),
                LocationFragment.class, null);
        tabHost.addTab(getTabView(R.string.menu_myself, R.drawable.tab_mine_selector),
                MyInfoFragment.class, null);
        tabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        tabHost.setCurrentTab(1);
    }

    /**
     * 创建TabSpec
     */
    private TabHost.TabSpec getTabView(int textId, int imgId){
        // 根据资源编号获得字符串对象
        String text = getResources().getString(textId);
        // 根据资源编号获得图形对象
        Drawable drawable = getResources().getDrawable(imgId);
        // 设置图形的四周边界。这里必须设置图片大小，否则无法显示图标
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        // 根据布局文件item_tabbar.xml生成标签按钮对象
        View item_tabbar = getLayoutInflater().inflate(R.layout.item_tabbar, null);
        TextView tv_item = item_tabbar.findViewById(R.id.tv_item_tabbar);
        tv_item.setText(text);
        // 在文字上方显示标签的图标
        tv_item.setCompoundDrawables(null, drawable, null, null);
        // 生成并返回该标签按钮对应的标签规格
        return tabHost.newTabSpec(text).setIndicator(item_tabbar);
    }
    /**
     * 退出应用交互
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_DOWN){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("退出");
            builder.setMessage("您确定退出吗？");
            builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!app.getUser().getUserId().equals("")) {//已登录
                        //退出时清空全局空间
                        app.setUser(new User());
                        app.setFriends(new ArrayList<Friends>());
                        app.setMessages(new ArrayList<MessageIO>());
                        app.setNowLocations(new ArrayList<NowLocation>());
                    }
                    finish();
                }
            });
            builder.setNegativeButton("再看看", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
