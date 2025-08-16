package com.gjf.wherearethey_v2.databaseoperation.dao;

import com.gjf.wherearethey_v2.bean.MessageIO;
import java.util.ArrayList;

/**
 * 消息表数据访问接口
 * @author gjf
 * @version 1.0
 */
public interface IMessageIODao {
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
     * 插入信息
     * @param messageIO 信息类
     * @return 插入结果：1-成功，-1-插入失败
     */
    int insertMsg(MessageIO messageIO);

    /**
     * 根据id，获得接受的信息
     * @param destId 信息目的id
     * @return 信息列表
     */
    ArrayList<MessageIO> getMsgByDestId(String destId);

    /**
     * 删除好友之间的信息
     * @param messageIO 消息
     * @return 删除结果：1-成功，0-没有找到记录，-1-删除失败
     */
    int deleteExactMsg(MessageIO messageIO);
}