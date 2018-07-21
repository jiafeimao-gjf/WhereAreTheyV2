package com.gjf.wherearethey_v2.databaseoperation.dao;


import com.gjf.wherearethey_v2.bean.NowLocation;
import com.gjf.wherearethey_v2.databaseoperation.dbconnection.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * 位置信息表的增、删、改、查
 * @author gjf
 * @version 1.0
 */
public class NowLocationDao {
    private static NowLocationDao nowLocationDao;   //唯一实例
    private static Connection conn;
    private static PreparedStatement ps;
    private static ResultSet rs;

    private NowLocationDao(){
        nowLocationDao = null;
        conn = null;
        ps = null;
        rs = null;
    }

    /**
     *
     * @return 唯一实例
     */
    public static NowLocationDao getInstance(){
        if(nowLocationDao == null){
            nowLocationDao = new NowLocationDao();
        }
        return nowLocationDao;
    }

    /**
     * 初始化数据库连接,
     * @return 数据库连接结果，成功为true，失败为false
     */
    public boolean initConnection(){
        conn = DBConnection.getConnection();
        return (conn!=null);
    }

    /**
     * 关闭数据库连接
     */
    public void closeConnection(){
        if(conn != null) DBConnection.closeObject(conn);
    }


    public int sendLocation(NowLocation nowLocation){
        int res;
        if(isExistLocation(nowLocation.getUserId()) == 1){
            res = updateLocation(nowLocation);
        }else{
            res = insertLocation(nowLocation);
        }
        return res;
    }

    /**
     * 检测用户是否已经上传过位置
     * @param userId 用户ID
     * @return 检查结果
     */
    private int isExistLocation(String userId){
        int res = -1;
        String sql = "select userid from nowlocation where userid = ?";
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1,userId);
            rs = ps.executeQuery();
            if(rs.next()){
                res = 1;//存在
            }else{
                res = 0;//没有
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
            DBConnection.closeObject(rs);
        }
        return res;
    }

    /**
     * 写入数据
     * @param nowLocation 数据对象
     * @return 结果代码
     */
    private int insertLocation(NowLocation nowLocation){
        int res = -1;
        String sql = "insert into nowlocation (userid,latitude,longitude,locationdesc,time)"+
                "values(?,?,?,?,?)";
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1,nowLocation.getUserId());
            ps.setDouble(2,nowLocation.getLatitude());
            ps.setDouble(3,nowLocation.getLongitude());
            ps.setString(4,nowLocation.getLocationDesc());
            ps.setTimestamp(5,new Timestamp(nowLocation.getTime().getTime()));
            res = ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
        }
        return res;
    }

    /**
     * 更新数据
     * @param nowLocation 新数据对象
     * @return 结果代码
     */
    private int updateLocation(NowLocation nowLocation){
        int res = -1;
        String sql = "update nowlocation set latitude=?,longitude=?,locationdesc=?," +
                "time=? where userid=?";
        try{
            ps = conn.prepareStatement(sql);
            ps.setDouble(1,nowLocation.getLatitude());
            ps.setDouble(2,nowLocation.getLongitude());
            ps.setString(3,nowLocation.getLocationDesc());
            ps.setTimestamp(4,new Timestamp(nowLocation.getTime().getTime()));
            ps.setString(5,nowLocation.getUserId());
            res = ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
        }
        return res;
    }

    /**
     * 根据Id获取用户的位置
     * @param userId 用户ID
     * @return 位置信息对象
     */
    public NowLocation getLocationById(String userId){
        NowLocation nowLocation = new NowLocation();
        String sql = "select * from nowlocation where userid = ?" ;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                nowLocation.setUserId(rs.getString("userid"));
                nowLocation.setLatitude(rs.getDouble("latitude"));
                nowLocation.setLongitude(rs.getDouble("longitude"));
                nowLocation.setLocationDesc(rs.getString("locationdesc"));
                nowLocation.setTime(new Date(rs.getTimestamp("time").getTime()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeObject(rs);
            DBConnection.closeObject(ps);
        }
        return nowLocation;
    }

    /**
     * 删除位置信息
     * @param userId 删除用户的ID
     * @return 结果代码
     */
    public int deleteMyLocation(String userId){
        int res = -1;
        String sql = "delete from nowlocation where userId = ?";
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1,userId);
            res = ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
        }
        return res;
    }

}
