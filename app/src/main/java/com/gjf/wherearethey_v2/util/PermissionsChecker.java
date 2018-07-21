package com.gjf.wherearethey_v2.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * 用于权限检查类
 * @author gjf
 * @version 1.0
 */
public class PermissionsChecker {
    private final Context mContext;

    /**
     * 构造函数，创建对象时，传入上下文对象
     * @param _mContext 传入上下文
     */
    public PermissionsChecker(Context _mContext){
        mContext = _mContext.getApplicationContext();
    }

    /**
     * 实现判断一组权限是否已获取
     * @param permissions 权限组
     * @return 返回是否全部已获取
     */
    public boolean lacksPermissions(String[] permissions){
        for(String permission :permissions){
            if(lacksPermission(permission)){
                return true;
            }
        }
        return false;
    }

    /**
     * 实现判断单一权限是否已获取
     * @param permission 单个权限
     * @return 返回权限是否可以
     */
    private boolean lacksPermission(String permission){
        return ContextCompat.checkSelfPermission(mContext,permission) ==
                PackageManager.PERMISSION_DENIED;
    }
}
