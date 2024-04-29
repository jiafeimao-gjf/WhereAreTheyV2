package com.gjf.wherearethey_v3.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gjf.wherearethey_v3.MainApplication;
import com.gjf.wherearethey_v3.R;
import com.gjf.wherearethey_v3.bean.Friends;
import com.gjf.wherearethey_v3.bean.MessageIO;
import com.gjf.wherearethey_v3.task.BecomeFriendsTask;
import com.gjf.wherearethey_v3.task.DeleteMsgTask;
import com.gjf.wherearethey_v3.task.SendMsgTask;
import com.gjf.wherearethey_v3.util.DateUtil;

import java.util.ArrayList;

/**
 * @author gjf
 * @version 1.0
 */
public class MessagesListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {
    /**
     * 当前的APP对象
     */
    private MainApplication app;
    /**
     * 布局inflater对象，初始化一个布局文件
     */
    private LayoutInflater mInflater; //布局
    /**
     * 上下文对象
     */
    private Context mContext;
    /**
     * 布局ID
     */
    private int mLayoutId;
    /**
     * 列表数据
     */
    private ArrayList<MessageIO> msgList;

    /**
     * 视图持有内部类，持有每一行内的所有视图
     *
     */
    private final class ViewHolder {
        /**
         * 线性布局对象
         */
        private LinearLayout linearLayoutItem;
        /**
         * 文本视图对象，显示标题
         */
        private TextView tvTitle;
        /**
         * 文本视图对象，显示时间
         */
        private TextView tvTime;
        /**
         * 文本视图对象，显示内容
         */
        private TextView tvDesc;
    }

    /**
     * 构造函数
     *
     * @param context 上下文变量
     * @param layoutId 布局文件的ID
     * @param messageList 传入的信息数组
     */
    public MessagesListAdapter(Context context, int layoutId, ArrayList<MessageIO> messageList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mLayoutId = layoutId;
        msgList = messageList;
        app = MainApplication.getInstance();
    }

    /**
     * @return 列表数组的长度
     */
    @Override
    public int getCount() {
        return msgList.size();
    }

    /**
     * @param position 列表位置
     * @return 对应位置的信息对象
     */
    @Override
    public MessageIO getItem(int position) {
        return msgList.get(position);
    }

    /**
     * @param position 列表位置
     * @return 返回位置
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @param position 列表位置
     * @param convertView 视图对象
     * @param parent 上级的视图
     * @return 第position行的视图
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(mLayoutId, null);
            viewHolder.linearLayoutItem = convertView.findViewById(R.id.ll_item);
            viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
            viewHolder.tvTime = convertView.findViewById(R.id.tv_time);
            viewHolder.tvDesc = convertView.findViewById(R.id.tv_desc);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MessageIO message = getItem(position);
        viewHolder.tvTitle.setText(message.getSrcId());
        viewHolder.tvTime.setText(DateUtil.getNowDateTime(message.getTime()));
        viewHolder.tvDesc.setText(message.getMessage());
        return convertView;
    }

    /**
     * 点击事件
     *
     * @param parent 当前适配视图
     * @param view 视图
     * @param position 列表视图
     * @param id 列表
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        String type = msgList.get(position).getMsgType();
        if (type.equals("report")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("来自用户的反馈");
            builder.setMessage(DateUtil.getNowDateTime(getItem(position).getTime()) + "\n"
                    + getItem(position).getMessage());
            builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setNegativeButton("回复该用户", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!getItem(position).getSrcId().equals("noId")) {
                        sendMsg(position);
                    }
                }
            });
            builder.show();
        } else if (type.equals("request")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("加好友请求");
            builder.setMessage(DateUtil.getNowDateTime(getItem(position).getTime()) + "\n"
                    + getItem(position).getMessage());
            builder.setPositiveButton("同意并删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //建立朋友关系
                    BecomeFriendsTask becomeFriendsTask = new BecomeFriendsTask();
                    becomeFriendsTask.setOnBecomeFriendsResultListener(
                            new BecomeFriendsTask.OnBecomeFriendsResultListener() {
                                @Override
                                public void onGetBFTResult(String res) {
                                    Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
                                }
                            });
                    becomeFriendsTask.execute(new Friends(getItem(position).getSrcId(),
                            getItem(position).getDestId()));
                    //发送一条反馈信息
                    MessageIO messageIO = new MessageIO(getItem(position).getDestId(),
                            getItem(position).getSrcId(), "同意", "reply");
                    SendMsgTask sendMsgTask = new SendMsgTask();
                    sendMsgTask.setOnInsertMsgResultListener(new SendMsgTask.OnInsertMsgResultListener() {
                        @Override
                        public void onGetSMTResult(String res) {
                            Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
                        }
                    });
                    messageIO.encrypt(); //加密
                    sendMsgTask.execute(messageIO);
                    //再将这条信息删除
                    deleteMsg(position);
                }
            });
            builder.setNegativeButton("拒绝并删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //发送一条反馈信息
                    MessageIO messageIO = new MessageIO(getItem(position).getDestId(),
                            getItem(position).getSrcId(), "拒绝", "reply");
                    SendMsgTask sendMsgTask = new SendMsgTask();
                    sendMsgTask.setOnInsertMsgResultListener(new SendMsgTask.OnInsertMsgResultListener() {
                        @Override
                        public void onGetSMTResult(String res) {
                            Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
                        }
                    });
                    messageIO.encrypt(); //加密
                    sendMsgTask.execute(messageIO);
                    //再将这条信息删除
                    deleteMsg(position);
                }
            });
            builder.show();
        } else if (type.equals("reply")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("请求" + getItem(position).getSrcId() + "为好友结果:");
            builder.setMessage(DateUtil.getNowDateTime(getItem(position).getTime()) + "\n"
                    + getItem(position).getMessage());
            builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    app.setFriends(new ArrayList<Friends>());
                }
            });
            builder.setNegativeButton("和他说句话", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //再将这条信息删除
//                deleteMsg(position);
                    //发送消息页
                    sendMsg(position);
                }
            });
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("消息内容：");
            builder.setMessage(DateUtil.getNowDateTime(getItem(position).getTime()) + "\n"
                    + getItem(position).getMessage());
            builder.setPositiveButton("收到", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setNegativeButton("回复该消息", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //再将这条信息删除
//                    deleteMsg(position);
                    // 回复消息
                    sendMsg(position);
                }
            });
            builder.show();
        }
    }

    /**
     * 长按事件
     *
     * @param parent 适配视图
     * @param view 视图
     * @param position 点击的列表位置
     * @param id id
     * @return 永远是true
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("你要删除该条信息吗？");
        builder.setMessage(getItem(position).getMessage());
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //将该信息从公网数据库中删除
                deleteMsg(position);
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

    /**
     * 删除指定位置的消息
     *
     * @param position 列表位置
     */
    private void deleteMsg(int position) {
        DeleteMsgTask deleteMsgTask = new DeleteMsgTask();
        deleteMsgTask.setOnDeleteMsgResultListener(new DeleteMsgTask.OnDeleteMsgResultListener() {
            @Override
            public void onGetDMTResult(String res) {
                Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
            }
        });
        deleteMsgTask.execute(getItem(position));
        msgList.remove(position);
        notifyDataSetChanged();
    }

    /**
     * 对指定位置的消息进行回复
     *
     * @param position 当前选择的列表序号
     */
    private void sendMsg(final int position) {
        //进入发送聊天室活动页
        android.app.AlertDialog.Builder msgDialog = new android.app.AlertDialog.Builder(mContext);
        msgDialog.setMessage("填写发送消息：");
        final EditText etMessage = new EditText(mContext);
        msgDialog.setView(etMessage);
        msgDialog.setPositiveButton("发送", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!etMessage.getText().toString().equals("")) {
                    SendMsgTask sendMsgTask = new SendMsgTask();
                    sendMsgTask.setOnInsertMsgResultListener(new SendMsgTask.OnInsertMsgResultListener() {
                        @Override
                        public void onGetSMTResult(String res) {
                            if (res.equals("success")) {
                                Toast.makeText(mContext, "消息已发送", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    MessageIO messageIO = new MessageIO(
                            app.getUser().getUserId(), getItem(position).getSrcId(),
                            etMessage.getText().toString(), "normal");
                    messageIO.encrypt(); //加密
                    sendMsgTask.execute(messageIO);
                } else {
                    Toast.makeText(mContext, "不能发送空消息", Toast.LENGTH_SHORT).show();
                }
            }
        });
        msgDialog.setNegativeButton("不发送", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        msgDialog.show();
    }
}
