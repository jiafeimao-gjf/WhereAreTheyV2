package com.gjf.wherearethey_v3.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gjf.wherearethey_v3.FriendsInfoActivity;
import com.gjf.wherearethey_v3.R;
import com.gjf.wherearethey_v3.bean.Friends;
import com.gjf.wherearethey_v3.bean.MessageIO;
import com.gjf.wherearethey_v3.task.DeleteFriendTask;
import com.gjf.wherearethey_v3.task.SendMsgTask;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author gjf
 * @version 1.0
 *
 */
public class FriendsListAdapter extends BaseAdapter
        implements AdapterView.OnItemClickListener,
                    AdapterView.OnItemLongClickListener{
    private LayoutInflater mInflater;   //布局实例化对象
    private Context mContext;           //内容对象
    private int mLayoutId;              //单个列表的布局
    private ArrayList<Friends> friendList; //列表对象数组
    /**
     * 视图持有内部类，持有每一行内的所有视图
     */
    private final class ViewHolder {
        public LinearLayout ll_item;
        public TextView tv_friendId;
    }
    /**
     * 构造函数
     * @param context  页面对象
     * @param layout_id 布局id
     * @param list 数据数组
     */
    public FriendsListAdapter(Context context, int layout_id, ArrayList<Friends> list) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mLayoutId = layout_id;
        friendList = list;
    }
    /**
     * @return 列表长度
     */
    @Override
    public int getCount() {
        return friendList.size();
    }

    /**
     * @param position 位置序号号
     * @return 特定位置的数据
     */
    @Override
    public Friends getItem(int position) {
        return friendList.get(position);
    }

    /**
     * @param position 位置序号
     * @return  特定位置的视图ID
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @param position 位置序号
     * @param convertView 单列视图
     * @param parent 上级视图
     * @return 单列视图对象
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){//为空就初始化
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(mLayoutId,null);
            viewHolder.ll_item = convertView.findViewById(R.id.ll_item);
            viewHolder.tv_friendId = convertView.findViewById(R.id.tv_friendId);
            convertView.setTag(viewHolder);
        }else{//不为空直接获取单列视图
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Friends friends = getItem(position);
        viewHolder.tv_friendId.setText(friends.getFriendId());
        return convertView;
    }

    /**
     * 列表项点击事件响应
     * @param parent
     * @param view 视图
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        Intent intent = new Intent(mContext, FriendsInfoActivity.class);
        intent.putExtra("friendPosition",position);
        mContext.startActivity(intent);
    }

    /**
     * 列表项长按事件响应
     * @param parent
     * @param view
     * @param position 位置序号
     * @param id
     * @return 无
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("你要删除该好友吗？");
        builder.setMessage(getItem(position).getFriendId());
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //将该好友从公网数据库中删除
                DeleteFriendTask deleteFriendTask = new DeleteFriendTask();
                deleteFriendTask.setOnDeleteFriendResultListener(
                        new DeleteFriendTask.OnDeleteFriendResultListener() {
                    @Override
                    public void onGetDFTResult(String res) {
                        Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
                    }
                });
                deleteFriendTask.execute(getItem(position));
                //发送解除好友关系信息给对方
                SendMsgTask sendMsgTask = new SendMsgTask();
                sendMsgTask.setOnInsertMsgResultListener(new SendMsgTask.OnInsertMsgResultListener() {
                    @Override
                    public void onGetSMTResult(String res) {
                        if(res.equals("success")){
                            Toast.makeText(mContext, "已告知对方，解除好友关系",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                MessageIO messageIO = new MessageIO();
                messageIO.setSrcId(getItem(position).getUserId());
                messageIO.setDestId(getItem(position).getFriendId());
                messageIO.setMessage(getItem(position).getUserId()+"和你解除了好友关系");
                messageIO.setTime(new Date());
                messageIO.setMsgType("normal");
                sendMsgTask.execute(messageIO);
                //从列表删除
                friendList.remove(position);
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("不删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
        return true;
    }
}
