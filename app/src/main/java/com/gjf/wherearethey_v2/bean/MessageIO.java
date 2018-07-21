package com.gjf.wherearethey_v2.bean;

import com.gjf.wherearethey_v2.encrypt.AesUtil;

import java.util.Date;

/** 消息类
 * @author gjf
 * @version 2.0
 */
public class MessageIO {
    private String destId;
    private String message;
    private String srcId;
    private String msgType;
    private Date time;

    public MessageIO(){
        destId = "";
        message = "";
        srcId = "";
        msgType = "";
        time = new Date();
    }

    /**
     * 构造函数
     * @param srcId
     * @param destId
     * @param message
     * @param msgType
     */
    public MessageIO(String srcId,String destId,String message,String msgType){
        this.destId = destId;
        this.srcId = srcId;
        this.message = message;
        this.msgType = msgType;
        time = new Date();
    }

    public void setDestId(String destId) {
        this.destId = destId;
    }

    public String getDestId() {
        return destId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    /**
     * 数据加密
     */
    public void encrypt(){
        try {
            this.message = AesUtil.encrypt("wat",this.message);
            this.msgType = AesUtil.encrypt("wat",this.msgType);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 数据解密
     */
    public void decrypt(){
        try {
            this.message = AesUtil.decrypt("wat",this.message);
            this.msgType = AesUtil.decrypt("wat",this.msgType);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
