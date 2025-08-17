package com.gjf.wherearethey_v3.databaseoperation.dao;


import com.gjf.wherearethey_v3.bean.User;
import com.gjf.wherearethey_v3.databaseoperation.dbconnection.DBConnection;
import com.gjf.wherearethey_v3.util.LogUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用户表的增、删、改、查
 * @author gjf
 * @version 2.0
 */
public class UserDao implements IUserDao {
    private static String TAG = "UserDao";
    private static UserDao userDao; //唯一实例
    private static Connection conn; //数据库连接对象
    private static PreparedStatement ps; //语句执行器
    private static ResultSet rs;    //结果获取器

    private UserDao(){
        userDao = null;
        conn = null;
        ps = null;
        rs = null;
    }

    /**
     *  获取唯一实例
     * @return 唯一实例
     */
    public static UserDao getInstance(){
        LogUtil.i(TAG,"[getInstance]");
        if(userDao == null){
            userDao = new UserDao();
        }
        return userDao;
    }

    /**
     * 初始化数据库连接,
     * @return 数据库连接结果，成功为true，失败为false
     */
    public boolean initConnection(){
        LogUtil.i(TAG,"[initConnection]");
        conn = DBConnection.getConnection();
        return (conn!=null);
    }

    /**
     * 关闭数据库连接
     */
    public void closeConnection(){
        LogUtil.i(TAG,"[closeConnection]");
        if(conn != null) DBConnection.closeObject(conn);
    }

    /**
     * 登录验证
     * @param userID 用户ID
     * @param password 用户密码
     * @return 返回是否成功登陆
     */
    public int loginCheck(String userID,String password){
        LogUtil.i(TAG,"[loginCheck]");
        int res = -1;
        String sql = "select password from user where userid = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1,userID);
            rs = ps.executeQuery();
            if(rs.next()){
                String pwd = "";
                try {//解密，
//                    pwd = AesUtil.decrypt("wat",rs.getString(1));
                    pwd = rs.getString(1);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(password.equals(pwd)){
                    res = 1;
                }else{
                    res = 2;
                }
            }else{
                res = 0;
            }
        }catch (SQLException e){
            LogUtil.e(TAG,"[loginCheck] SQLException ", e);
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(rs);
            DBConnection.closeObject(ps);
        }
        return res;
    }

    /**
     * 判断用户名是否已被注册
     * @param userId 用户名
     * @return 判断结果
     */
    public int isUserIDExist(String userId){
        LogUtil.i(TAG,"[isUserIDExist]");
        int res = -1;
        String sql = "select userid from user where userid = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                res = 1;   //已存在该帐号
            } else {
                res = 0;  //该账号不存在
            }
        } catch (SQLException e) {
            LogUtil.e(TAG,"[isUserIDExist] SQLException ", e);
            e.printStackTrace();
        } finally {
            DBConnection.closeObject(rs);
            DBConnection.closeObject(ps);
        }
        return res;
    }


    /**
     * 根据用户Id获取全部用户信息
     */
    public User getUserInfoByKey(String userId){
        LogUtil.i(TAG,"[getUserInfoByKey]");
        User user = new User();
        String sql = "select * from user where userid = ?" ;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            if (rs.next()){
                user.setUserId(rs.getString("userid"));
                user.setPassword(rs.getString("password"));
                user.setUserName(rs.getString("username"));
                user.setPwdProtectId(rs.getInt("pwdprotectid"));
                user.setPwdProtectA(rs.getString("pwdprotecta"));
                user.setUserType(rs.getString("usertype"));
            }else{
                user.setUserType("ID不存在");
            }
        } catch (SQLException e) {
            LogUtil.e(TAG,"[getUserInfoByKey] SQLException ", e);
            e.printStackTrace();
        } finally {
            DBConnection.closeObject(rs);
            DBConnection.closeObject(ps);
        }
        return user;
    }

    /**
     * 插入新用户信息
     * @param newUser 新用户
     * @return 结果
     */
    public int insertNewUser(User newUser){
        LogUtil.i(TAG,"[insertNewUser]");
        int res = -1;
        String sql = "insert into user (userid,password,username," +
                "pwdprotectid,pwdprotecta,usertype)values(?,?,?,?,?,?)";
        try {
            int checkId = userDao.isUserIDExist(newUser.getUserId());
            if(checkId == 1){
                res = 0; //ID已存在
            }else {
                ps = conn.prepareStatement(sql);
                ps.setString(1,newUser.getUserId());
                ps.setString(2,newUser.getPassword());
                ps.setString(3,newUser.getUserName());
                ps.setInt(4,newUser.getPwdProtectId());
                ps.setString(5,newUser.getPwdProtectA());
                ps.setString(6,newUser.getUserType());
                res = ps.executeUpdate();//成功插入，插入失败
            }
        }catch (SQLException e){
            LogUtil.e(TAG,"[insertNewUser] SQLException ", e);
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
        }
        return res;
    }

    /**
     * 修改密码
     * @param id ID
     * @param newPassword 新密码
     * @return 修改结果
     */
    public int updatePassword(String id,String newPassword){
        LogUtil.i(TAG,"[updatePassword]");
        int res = -1;
        String sql = "update user set password = ? where userid = ?";
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1,newPassword);
            ps.setString(2,id);
            res = ps.executeUpdate();
        }catch (SQLException e){
            LogUtil.e(TAG,"[updatePassword] SQLException ", e);
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
        }
        return res;
    }

    /**
     * 修改昵称
     * @param id ID
     * @param newName 新昵称
     * @return 修改结果
     */
    public int updateName(String id,String newName){
        LogUtil.i(TAG,"[updateName]");
        int res = -1;
        String sql = "update user set username = ? where userId = ?";
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1,newName);
            ps.setString(2,id);
            res = ps.executeUpdate();
        }catch (SQLException e){
            LogUtil.e(TAG,"[updateName] SQLException ", e);
            e.printStackTrace();
        }finally {
            DBConnection.closeObject(ps);
        }
        return res;
    }
}
