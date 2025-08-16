package com.gjf.wherearethey_v2.databaseoperation.dao;

import com.gjf.wherearethey_v2.bean.User;
import java.util.ArrayList;

/**
 * 用户表数据访问接口
 * @author gjf
 * @version 1.0
 */
public interface IUserDao {
    /**
     * 初始化数据库连接
     * @return 数据库连接结果，成功为true，失败为false
     */
    boolean initConnection();

    /**
     * 关闭数据库连接
     */
    void closeConnection();

    /**
     * 登录验证
     * @param userID 用户ID
     * @param password 用户密码
     * @return 返回登录验证结果：1-成功，2-密码错误，0-ID不存在，-1-连接失败
     */
    int loginCheck(String userID, String password);

    /**
     * 判断用户名是否已被注册
     * @param userId 用户名
     * @return 判断结果：1-已存在，0-不存在，-1-查询失败
     */
    int isUserIDExist(String userId);

    /**
     * 根据用户Id获取全部用户信息
     * @param userId 用户ID
     * @return 用户信息对象
     */
    User getUserInfoByKey(String userId);

    /**
     * 插入新用户信息
     * @param newUser 新用户
     * @return 插入结果：1-成功，0-ID已存在，-1-插入失败
     */
    int insertNewUser(User newUser);

    /**
     * 修改密码
     * @param id ID
     * @param newPassword 新密码
     * @return 修改结果：>=0-成功更新的记录数，-1-更新失败
     */
    int updatePassword(String id, String newPassword);

    /**
     * 修改昵称
     * @param id ID
     * @param newName 新昵称
     * @return 修改结果：>=0-成功更新的记录数，-1-更新失败
     */
    int updateName(String id, String newName);
}