package com.gjf.wherearethey_v2.databaseoperation.dbconnection;

import com.gjf.wherearethey_v2.MainApplication;
import com.gjf.wherearethey_v2.util.LogUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库连接器
 * @author gjf
 * @version 1.0
 */
public class DBConnection {
    private static String TAG = "DBConnection";
    private static Connection conn = null;//连接的对象
    private static MainApplication app;
    /**
     * 创建数据库的链接
     * @return  返回一个数据库的链接
     */
    public static Connection getConnection() {
        LogUtil.i(TAG,"[getConnection]");
        try {
            try {
                app = MainApplication.getInstance();
                Class.forName("org.mariadb.jdbc.Driver");  //加载数据库驱动
                conn = DriverManager.getConnection("jdbc:mysql://192.168.1.7:3306/locationshare", app.getRealUsernameWithoutCrypt(),
                        app.getRealPasswordWithoutCrypt());   //获得数据库的链接
            } catch (SQLException e) {//处理是否正确连接
                LogUtil.e(TAG,"[getConnection]", e);
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {//处理是否找到mysql的驱动类
            LogUtil.e(TAG,"[getConnection]", e);
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭数据库资源
     * @param obj  数据库打开的资源对象（在此处用Object，因为链接数据库是会打开多个资源）
     */
    public static void closeObject(Object obj){
        if(obj != null){
            if(obj instanceof ResultSet){
                try {
                    ((ResultSet) obj).close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(obj instanceof PreparedStatement){
                try {
                    ((PreparedStatement) obj).close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(obj instanceof Connection){
                try {
                    ((Connection) obj).close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
