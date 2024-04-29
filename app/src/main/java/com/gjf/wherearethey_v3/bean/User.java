package com.gjf.wherearethey_v3.bean;

/**
 * 用户类，匹配数据库信息
 * @author gjf
 * @version 2.0
 */
public class User {
    private String userId;
    private String password;
    private String userName;
    private int pwdProtectId;
    private String pwdProtectA;
    private String userType;
    public User(){
        this.userId = "";
        this.password = "";
        this.userName = "";
        this.pwdProtectId = -1;
        this.pwdProtectA = "";
        this.userType = "";
    }

    /**
     * 深拷贝
     * @param user 拷贝对象
     */
    public User(User user){
        this.userId = user.getUserId();
        this.password = user.getPassword();
        this.userName = user.getUserName();
        this.pwdProtectId = user.getPwdProtectId();
        this.pwdProtectA = user.getPwdProtectA();
        this.userType = user.getUserType();
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setPwdProtectId(int pwdProtectId) {
        this.pwdProtectId = pwdProtectId;
    }

    public int getPwdProtectId() {
        return pwdProtectId;
    }

    public void setPwdProtectA(String pwdProtectA) {
        this.pwdProtectA = pwdProtectA;
    }

    public String getPwdProtectA() {
        return pwdProtectA;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }

    /**
     * 数据加密
     */
    public void encrypt(){
        try {
//            this.password = AesUtil.encrypt("wat",this.password);
//            this.pwdProtectA = AesUtil.encrypt("wat",this.pwdProtectA);
//            this.userType = AesUtil.encrypt("wat",this.userType);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 数据解密
     */
    public void decrypt(){
        try {
//            this.password = AesUtil.decrypt("wat",this.password);
//            this.pwdProtectA = AesUtil.decrypt("wat",this.pwdProtectA);
//            this.userType = AesUtil.decrypt("wat",this.userType);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
