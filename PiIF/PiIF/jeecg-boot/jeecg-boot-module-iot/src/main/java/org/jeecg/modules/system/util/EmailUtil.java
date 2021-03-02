package org.jeecg.modules.system.util;


import com.sun.mail.util.MailSSLSocketFactory;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailUtil {

    /**
     * 发送邮件工具类:通过qq邮件发送,因为具有ssl加密,采用的是smtp协议

     * @param emailSubject     邮件的主题
     * @param emailContent     邮件的内容
     * @return
     */
    public  int sendEmail ( String mailServer ,String loginAccount, String loginAuthCode, String sender, String[] recipients ,
                                String emailSubject, String emailContent) {
        int res = 0;
/*       //邮件服务器的主机名
       String mailServer ="smtp.163.com";
       //登录邮箱的账号
       String loginAccount = "13684938301@163.com";
       //登录邮箱时候需要的授权码
       String loginAuthCode = "JTSPMIWTGUJDLBIK";
       //发件人
       String sender = "13684938301@163.com";*/
       //邮件内容的类型,支持纯文本:"text/plain;charset=utf-8";,带有Html格式的内容:"text/html;charset=utf-8"
       String emailContentType ="text/html;charset=utf-8";
       //收件人:支持群发
      // String[] recipients = new String[]{"hjzhang@pisx.com"};

        try {
            //跟smtp服务器建立一个连接
            Properties p = new Properties();
            //设置邮件服务器主机名
            p.setProperty("mail.host", mailServer);
            //发送服务器需要身份验证,要采用指定用户名密码的方式去认证
            p.setProperty("mail.smtp.auth", "true");
            //发送邮件协议名称
            p.setProperty("mail.transport.protocol", "smtp");

            //开启SSL加密，否则会失败
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            p.put("mail.smtp.ssl.enable", "true");
            p.put("mail.smtp.ssl.socketFactory", sf);

            // 创建session
            Session session = Session.getDefaultInstance(p, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    //用户名可以用QQ账号也可以用邮箱的别名:第一个参数为邮箱账号,第二个为授权码
                    PasswordAuthentication pa = new PasswordAuthentication(loginAccount, loginAuthCode);
                    return pa;
                }
            });

            //设置打开调试状态
            session.setDebug(true);

            //可以发送几封邮件:可以在这里 for循环多次
            //声明一个Message对象(代表一封邮件),从session中创建
            MimeMessage msg = new MimeMessage(session);
            //邮件信息封装
            //1发件人
            msg.setFrom(new InternetAddress(sender));

            //2收件人:可以多个
            //一个的收件人
            //msg.setRecipient(RecipientType.TO, new InternetAddress("linsenzhong@126.com"));

            InternetAddress[] receptientsEmail = new InternetAddress[recipients.length];
            for (int i = 0; i < recipients.length; i++) {
                receptientsEmail[i] = new InternetAddress(recipients[i]);
            }

            //多个收件人
            msg.setRecipients(RecipientType.TO, receptientsEmail);

            //3邮件内容:主题、内容
            msg.setSubject(emailSubject);
            //msg.setContent("Hello, 我是debug!!!", );//纯文本
            msg.setContent(emailContent, emailContentType);//发html格式的文本
            //发送动作
            Transport.send(msg);
            System.out.println("邮件发送成功");
            res = 1;

        } catch (Exception e) {
            System.out.println("邮件发送失败: " + e.getMessage());

            res = 0;
        }
        return res;
    }


}