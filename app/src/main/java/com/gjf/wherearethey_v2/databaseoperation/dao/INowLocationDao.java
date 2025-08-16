package com.gjf.wherearethey_v2.databaseoperation.dao;

import com.gjf.wherearethey_v2.bean.NowLocation;
import java.util.ArrayList;

/**
 * 位置信息表数据访问接口
 * @author gjf
 * @version 1.0
 */
public interface INowLocationDao {
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
     * 发送位置信息（插入或更新）
     * @param nowLocation 位置信息对象
     * @return 发送结果：1-成功，-1-失败
     */
    int sendLocation(NowLocation nowLocation);

    /**
     * 根据Id获取用户的位置
     * @param userId 用户ID
     * @return 位置信息对象
     */
    NowLocation getLocationById(String userId);

    /**
     * 删除位置信息
     * @param userId 删除用户的ID
     * @return 删除结果：1-成功，0-没有找到记录，-1-删除失败
     */
    int deleteMyLocation(String userId);
}