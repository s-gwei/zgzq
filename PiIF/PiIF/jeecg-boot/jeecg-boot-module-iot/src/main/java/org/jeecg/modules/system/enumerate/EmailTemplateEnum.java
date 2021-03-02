package org.jeecg.modules.system.enumerate;

/**
 * @author zhang ho jian
 * @date 2020/9/25
 * @time 14:32
 * @Description : 邮箱模板枚举
 */
public enum EmailTemplateEnum {
    WARN("警报提醒","事件被触发，请注意")

    ;

    private String emailTheme;
    private String emailContent;


    EmailTemplateEnum(String emailTheme, String emailContent) {
        this.emailTheme = emailTheme;
        this.emailContent = emailContent;
    }

    public String getEmailTheme() {
        return emailTheme;
    }

    public void setEmailTheme(String emailTheme) {
        this.emailTheme = emailTheme;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }}
