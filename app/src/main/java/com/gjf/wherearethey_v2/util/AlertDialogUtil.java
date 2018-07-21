package com.gjf.wherearethey_v2.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * 提示框类
 * @author gjf
 * @version 1.0
 */
public class AlertDialogUtil {

    public static void show(Activity activity, String msg){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        builder.setTitle("提示：");
        builder.setMessage(msg);
        builder.setPositiveButton("好的",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }
    public static void show(Activity activity,String title, String msg){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("好的",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

}
