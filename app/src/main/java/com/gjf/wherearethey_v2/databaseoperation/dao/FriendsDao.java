package com.gjf.wherearethey_v2.databaseoperation.dao;

import com.gjf.wherearethey_v2.bean.Friends;
import com.gjf.wherearethey_v2.databaseoperation.dbconnection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 朋友表的增、删、查
 * @author gjf
 * @version 1.0
 */
public class FriendsDao implements IFriendsDao {
    private static FriendsDao friendsDao;
    private static Connection conn;
    private static PreparedStatement ps;
    private static ResultSet rs;

    private FriendsDao(){
        friendsDao = null;
        conn = null;
        ps = null;
        rs = null;
    }

    public static FriendsDao getInstance(){
        if(friendsDao == null){
            friendsDao = new FriendsDao();
        }
        return friendsDao;
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

    /**
     * 设置成为好友
     * @param friends 好友类
     * @return 结果
     */
    public int becomeFriends(Friends friends){
        int res = -1;
        String sql = "insert into friends (userid,friendid,isreceivelocation)values(?,?,?)";
        try{
            int checkRes = isHasBeenFriends(friends);
            if(checkRes == 0){
                ps = conn.prepareStatement(sql);
                ps.setString(1,friends.getUserId());
                ps.setString(2,friends.getFriendId());
                ps.setString(3,friends.getIsReceiveLocation());
                if(!ps.execute()){
                    DBConnection.closeObject(ps);
                    ps = conn.prepareStatement(sql);
                    ps.setString(1,friends.getFriendId());
                    ps.setString(2,friends.getUserId());
                    ps.setString(3,friends.getIsReceiveLocation());
                    if(!ps.execute()){
                        res = 1;//加好友成功
                    }else{
                        res = 2;//加好友失败
                    }
                }else{
                    res = 2;
                }
            }else{
                res = 0;//已经是好友了
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
        }
        return res;
    }


    public int deleteEachOther(Friends friends){
        int res;
        if(deleteFriends(friends) == 1){
            res = deleteFriends(new Friends(friends.getFriendId(),friends.getUserId()));
        }else{
            res = 2;
        }
        return res;
    }
    /**
     * 删除好友关系
     * @param friends 好友类
     * @return 删除结果
     */
    public int deleteFriends(Friends friends){
        int res = -1;
        String sql = "delete from friends where userid = ? && friendid = ?";
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1,friends.getUserId());
            ps.setString(2,friends.getFriendId());
            if(ps.executeUpdate() == 1){
                res = 1;
            }else{
                res = 2;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
        }
        return res;
    }

    /**
     * 判断两个用户之间是否有朋友关系
     * @param friends
     * @return 结果代码
     */
    public int isHasBeenFriends(Friends friends){
        int res = -1;
        String sql = "select * from friends where userid = ? && friendid = ?";
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1,friends.getUserId());
            ps.setString(2,friends.getFriendId());
            rs = ps.executeQuery();
            if(rs.next()){
                res = 1;//已经是好友
            }else{
                res = 0;//还不是好友
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
     * 查询一个用户的所有朋友
     * @param userid 用户Id
     * @return 结果代码
     */
    public ArrayList<Friends> showFriends(String userid){
        ArrayList<Friends> friendsList = new ArrayList<>();
        String sql = "select userid,friendid,isreceivelocation from friends where userid = ?";
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1,userid);
            rs = ps.executeQuery();
            while(rs.next()){
                Friends friend = new Friends();
                friend.setFriendId(rs.getString("friendid"));
                friend.setUserId(rs.getString("userid"));
                friend.setIsReceiveLocation(rs.getString("isreceivelocation"));
                friendsList.add(friend);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
            DBConnection.closeObject(rs);
        }
        return friendsList;
    }

    /**
     * 获取某个朋友的信息
     * @param friends 朋友对象
     * @return 信息数组
     */
    public ArrayList<String> getFriendsInfo(Friends friends){
        ArrayList<String> friendInfo = new ArrayList<>();
        String sql = "select user.userid,user.username,friends.isreceivelocation from friends,user " +
                "where user.userid = ? and friends.userid = ? and friends.friendid = ?";
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1,friends.getFriendId());
            ps.setString(2,friends.getUserId());
            ps.setString(3,friends.getFriendId());
            rs = ps.executeQuery();
            if(rs.next()){
                friendInfo.add(rs.getString("user.userid"));
                friendInfo.add(rs.getString("user.username"));
                friendInfo.add(rs.getString("friends.isreceivelocation"));
            }else{
                friendInfo.add("未查到相关记录");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(rs);
            DBConnection.closeObject(ps);
        }
        return friendInfo;
    }

    /**
     * 修改是否接收朋友的信息
     * @param friends 朋友对象
     * @return 结果代码
     */
    public int changeIsReceiveLocation(Friends friends){
        int res = -1;
        String sql = "update friends set isreceivelocation = ? where userid = ? and friendid = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1,friends.getIsReceiveLocation());
            ps.setString(2,friends.getUserId());
            ps.setString(3,friends.getFriendId());
            res = ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
        }
        return res;
    }
}
