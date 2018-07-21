package com.gjf.wherearethey_v2.databaseoperation.dao;


import com.gjf.wherearethey_v2.bean.MessageIO;
import com.gjf.wherearethey_v2.databaseoperation.dbconnection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * 消息表的增、删、查
 * @author gjf
 * @version 1.0
 */
public class MessageIODao {
    private static MessageIODao messageIODao;   //唯一实例
    private static Connection conn;
    private static PreparedStatement ps;
    private static ResultSet rs;

    private MessageIODao(){
        messageIODao = null;
        conn = null;
        ps = null;
        rs = null;
    }

    /**
     *
     * @return 唯一实例
     */
    public static MessageIODao getInstance() {
        if(messageIODao == null){
            messageIODao = new MessageIODao();
        }
        return messageIODao;
    }
    /**
     * 初始化数据库连接,
     * @return 数据库连接结果，成功为true，失败为false
     */
    public boolean initConnection(){
        conn = DBConnection.getConnection();
        return conn != null;
    }
    /**
     * 关闭数据库连接
     */
    public void closeConnection(){
        if(conn != null) DBConnection.closeObject(conn);
    }
    /**
     * 插入信息
     * @param messageIO 信息类
     * @return 插入结果
     */
    public int insertMsg(MessageIO messageIO){
        int res = -1;
        String sql = "insert into messageio (srcid,destid,message,msgtype,time)values(?,?,?,?,?)";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1,messageIO.getSrcId());
            ps.setString(2,messageIO.getDestId());
            ps.setString(3,messageIO.getMessage());
            ps.setString(4,messageIO.getMsgType());
            Timestamp timestamp = new Timestamp(new Date().getTime());
            ps.setTimestamp(5,timestamp);
            res = ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
        }
        return res;
    }

    /**
     * 根据id，获得接受的信息
     * @param destId 信息目的id
     * @return 信息列表
     */
    public ArrayList<MessageIO> getMsgByDestId(String destId){
        ArrayList<MessageIO> msgList = new ArrayList<>();
        String sql = "select * from messageio where destId = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1,destId);
            rs = ps.executeQuery();
            while (rs.next()){
                MessageIO message = new MessageIO();
                message.setSrcId(rs.getString("srcid"));
                message.setDestId(rs.getString("destid"));
                message.setMessage(rs.getString("message"));
                message.setMsgType(rs.getString("msgtype"));
                message.setTime(new Date(rs.getTimestamp("time").getTime()));
                msgList.add(message);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
            DBConnection.closeObject(rs);
        }
        return msgList;
    }

    /**
     * 删除好友之间的信息
     * @param messageIO 消息
     * @return 删除结果
     */
    public int deleteExactMsg(MessageIO messageIO){
        int res = -1;
        String sql  = "delete from messageio where srcid = ? && destid = ? && time = ?" ;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1,messageIO.getSrcId());
            ps.setString(2,messageIO.getDestId());
            ps.setTimestamp(3,new Timestamp(messageIO.getTime().getTime()));
            res = ps.executeUpdate()>0?1:0;
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
        }
        return res;
    }

}
