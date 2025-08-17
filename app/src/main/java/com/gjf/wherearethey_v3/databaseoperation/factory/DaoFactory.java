package com.gjf.wherearethey_v3.databaseoperation.factory;

import com.gjf.wherearethey_v3.databaseoperation.dao.*;
import com.gjf.wherearethey_v3.util.SharedUtil;
import android.content.Context;

/**
 * 数据库访问工厂类，支持运行时切换不同的数据访问实现
 * @author gjf
 * @version 1.0
 */
public class DaoFactory {
    // 数据访问实现类型
    public static final String IMPL_TYPE_JDBC = "jdbc";
    public static final String IMPL_TYPE_REST = "rest";
    
    // 默认实现类型
    private static String defaultImplType = IMPL_TYPE_JDBC;
    
    // 上下文引用
    private static Context appContext;
    
    /**
     * 初始化工厂，设置应用上下文
     * @param context 应用上下文
     */
    public static void initialize(Context context) {
        appContext = context;
        // 从共享偏好设置中读取配置的实现类型
        SharedUtil sharedUtil = SharedUtil.getInstance("app_config");
        defaultImplType = sharedUtil.readShared("db_impl_type", IMPL_TYPE_JDBC);
    }
    
    /**
     * 设置默认的数据访问实现类型
     * @param implType 实现类型
     */
    public static void setDefaultImplType(String implType) {
        defaultImplType = implType;
        // 保存到共享偏好设置
        if (appContext != null) {
            SharedUtil sharedUtil = SharedUtil.getInstance("app_config");
            sharedUtil.writeShared("db_impl_type", implType);
        }
    }
    
    /**
     * 获取默认的数据访问实现类型
     * @return 实现类型
     */
    public static String getDefaultImplType() {
        return defaultImplType;
    }
    
    /**
     * 获取用户数据访问对象
     * @return IUserDao 实例
     */
    public static IUserDao getUserDao() {
        return getUserDao(defaultImplType);
    }
    
    /**
     * 根据指定类型获取用户数据访问对象
     * @param implType 实现类型
     * @return IUserDao 实例
     */
    public static IUserDao getUserDao(String implType) {
        if (IMPL_TYPE_JDBC.equals(implType)) {
            return UserDao.getInstance();
        } else if (IMPL_TYPE_REST.equals(implType)) {
            // TODO: 实现REST API版本
            throw new UnsupportedOperationException("REST API implementation not yet available");
        } else {
            // 默认使用JDBC实现
            return UserDao.getInstance();
        }
    }
    
    /**
     * 获取位置信息数据访问对象
     * @return INowLocationDao 实例
     */
    public static INowLocationDao getNowLocationDao() {
        return getNowLocationDao(defaultImplType);
    }
    
    /**
     * 根据指定类型获取位置信息数据访问对象
     * @param implType 实现类型
     * @return INowLocationDao 实例
     */
    public static INowLocationDao getNowLocationDao(String implType) {
        if (IMPL_TYPE_JDBC.equals(implType)) {
            return NowLocationDao.getInstance();
        } else if (IMPL_TYPE_REST.equals(implType)) {
            // TODO: 实现REST API版本
            throw new UnsupportedOperationException("REST API implementation not yet available");
        } else {
            // 默认使用JDBC实现
            return NowLocationDao.getInstance();
        }
    }
    
    /**
     * 获取消息数据访问对象
     * @return IMessageIODao 实例
     */
    public static IMessageIODao getMessageIODao() {
        return getMessageIODao(defaultImplType);
    }
    
    /**
     * 根据指定类型获取消息数据访问对象
     * @param implType 实现类型
     * @return IMessageIODao 实例
     */
    public static IMessageIODao getMessageIODao(String implType) {
        if (IMPL_TYPE_JDBC.equals(implType)) {
            return MessageIODao.getInstance();
        } else if (IMPL_TYPE_REST.equals(implType)) {
            // TODO: 实现REST API版本
            throw new UnsupportedOperationException("REST API implementation not yet available");
        } else {
            // 默认使用JDBC实现
            return MessageIODao.getInstance();
        }
    }
    
    /**
     * 获取朋友数据访问对象
     * @return IFriendsDao 实例
     */
    public static IFriendsDao getFriendsDao() {
        return getFriendsDao(defaultImplType);
    }
    
    /**
     * 根据指定类型获取朋友数据访问对象
     * @param implType 实现类型
     * @return IFriendsDao 实例
     */
    public static IFriendsDao getFriendsDao(String implType) {
        if (IMPL_TYPE_JDBC.equals(implType)) {
            return FriendsDao.getInstance();
        } else if (IMPL_TYPE_REST.equals(implType)) {
            // TODO: 实现REST API版本
            throw new UnsupportedOperationException("REST API implementation not yet available");
        } else {
            // 默认使用JDBC实现
            return FriendsDao.getInstance();
        }
    }
}