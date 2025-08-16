package com.gjf.wherearethey_v2.databaseoperation.dao;

import com.gjf.wherearethey_v2.bean.Friends;
import java.util.ArrayList;

/**
 * 朋友表数据访问接口
 * @author gjf
 * @version 1.0
 */
public interface IFriendsDao {
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
     * 设置成为好友
     * @param friends 好友类
     * @return 结果：1-成功，0-已经是好友，2-加好友失败，-1-操作失败
     */
    int becomeFriends(Friends friends);

    /**
     * 删除好友关系（双向删除）
     * @param friends 好友类
     * @return 删除结果：1-成功，2-删除失败，-1-操作失败
     */
    int deleteEachOther(Friends friends);

    /**
     * 删除好友关系（单向删除）
     * @param friends 好友类
     * @return 删除结果：1-成功，2-删除失败，-1-操作失败
     */
    int deleteFriends(Friends friends);

    /**
     * 判断两个用户之间是否有朋友关系
     * @param friends 好友对象
     * @return 结果代码：1-已经是好友，0-不是好友，-1-查询失败
     */
    int isHasBeenFriends(Friends friends);

    /**
     * 查询一个用户的所有朋友
     * @param userid 用户Id
     * @return 朋友列表
     */
    ArrayList<Friends> showFriends(String userid);

    /**
     * 获取某个朋友的信息
     * @param friends 朋友对象
     * @return 信息数组
     */
    ArrayList<String> getFriendsInfo(Friends friends);

    /**
     * 修改是否接收朋友的信息
     * @param friends 朋友对象
     * @return 结果代码：>=0-成功更新的记录数，-1-更新失败
     */
    int changeIsReceiveLocation(Friends friends);
}