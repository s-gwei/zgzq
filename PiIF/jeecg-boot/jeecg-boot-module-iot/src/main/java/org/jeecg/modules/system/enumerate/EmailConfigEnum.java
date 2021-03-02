package org.jeecg.modules.system.enumerate;

/**
 * @author zhang ho jian
 * @date 2020/9/23
 * @time 13:37
 * @Description :邮件配置枚举
 */
public enum EmailConfigEnum {

    ZHJ("smtp.163.com","13684938301@163.com","JTSPMIWTGUJDLBIK")
    ;

    private String mailServer;
    private String loginAuthCode;
    private String loginAccount;

    EmailConfigEnum(String mailServer, String loginAuthCode, String loginAccount) {
        this.mailServer = mailServer;
        this.loginAuthCode = loginAuthCode;
        this.loginAccount = loginAccount;
    }

    public String getMailServer() {
        return mailServer;
    }

    public void setMailServer(String mailServer) {
        this.mailServer = mailServer;
    }

    public String getLoginAuthCode() {
        return loginAuthCode;
    }

    public void setLoginAuthCode(String loginAuthCode) {
        this.loginAuthCode = loginAuthCode;
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }}

