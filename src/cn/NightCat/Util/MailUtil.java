package cn.NightCat.Util;

import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/*
	Create by Crazyist at 2015年11月19日 下午1:38:55 Filename:MailUtil.java
	CopyRight © 2014-2015 夜猫工作室 YMTeam.Cn, All Rights Reserved. 
 */
public class MailUtil {
	/**
	 * 发送邮件
	 * @param mailServerHost 服务地址
	 * @param mailServerPort 服务端口 
	 * @param validate 是否验证
	 * @param nick 发送昵称
	 * @param fromAddress 验证账号(发送账号)
	 * @param userPass 密码
	 * @param toAddress 发送地址
	 * @param subject 猪蹄
	 * @param content 内容
	 * @param isHTML 是否为HTML
	 * @param isSSL 是否SSL
	 * @return
	 */
    public static boolean sendMail(String mailServerHost,
                    String mailServerPort, boolean validate,String nick, String fromAddress,
                    String userPass, String toAddress, String subject, String content,
                    boolean isHTML, boolean isSSL) {
            Properties p = new Properties();
            p.put("mail.smtp.host", mailServerHost);
            p.put("mail.smtp.port", mailServerPort);
            p.put("mail.smtp.auth", validate ? "true" : "false");
            if (isSSL) {
                    p.put("mail.smtp.starttls.enable", "true");
                    p.put("mail.smtp.socketFactory.fallback", "false");
                    p.put("mail.smtp.socketFactory.port", mailServerPort);
            }
            Authenticator auth = null;
            if (validate) {
                    auth = new myAuthenticator(fromAddress, userPass);
            }

            try {
                    Session session = Session.getDefaultInstance(p, auth);
                    Message message = new MimeMessage(session);
                    nick = MimeUtility.encodeText(nick);
                    Address from = new InternetAddress(nick + " <" +fromAddress + ">");
                    Address to = new InternetAddress(toAddress);
                    message.setFrom(from);
                    message.setRecipient(Message.RecipientType.TO, to);
                    message.setSubject(subject);
                    message.setSentDate(new Date());
                    if (isHTML) {
                            Multipart m = new MimeMultipart();
                            BodyPart bp = new MimeBodyPart();
                            bp.setContent(content, "text/html; charset=utf-8");
                            m.addBodyPart(bp);
                            message.setContent(m);
                    } else
                            message.setText(content);
                    Transport.send(message);
                    return true;
            } catch (Exception e) {
                    return false;
            }
    }
}

	class myAuthenticator extends Authenticator {
	    String userName;
	    String userPass;
	    public myAuthenticator() { }
	    public myAuthenticator(String userName, String userPass) {
            this.userName = userName;
            this.userPass = userPass;
	    }
	    protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, userPass);
	    }
}
