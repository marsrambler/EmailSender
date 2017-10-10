package com.jack.fund.email;

import java.util.Date;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Class contains email functionality.
 *
 * author: Oleh Zahryvyi
 */
public class MailSender {

    public static final String MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String MAIL_SMTP_PORT = "mail.smtp.port";
    public static final String MAIL_SMTP_SOCKET_FACTORY_PORT = "mail.smtp.socketFactory.port";
    public static final String MAIL_SMTP_SOCKET_FACTORY_CLASS = "mail.smtp.socketFactory.class";
    public static final String MAIL_SMTP_SOCKET_FACTORY_FALLBACK = "mail.smtp.socketFactory.fallback";
    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static final String MAIL_MIME_CHARSET = "mail.mime.charset";
    public static final String MAIL_ATTACHEMENT_NAME = "options.json";
    public static final String DEFAULT_ENCODING = "UTF-8";

    private String host;
    private int smtpPort;
    private int socketPort;
    private String charset;
    private String userName;
    private String password;
    private Session mailSession;
    private Boolean smtpStartTLS;
    private Boolean smtpSocketFactoryFallback;

    /**
     * Declare Spring id for default class instance
     */
    public static final String MAIL_SENDER_BEAN_NAME = "mailSender";

    public void initializeMailSession() {
        Properties properties = new Properties();
        properties.put(MAIL_SMTP_HOST, host);
        properties.put(MAIL_SMTP_PORT, smtpPort);
        properties.put(MAIL_SMTP_SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory");

        if (socketPort != 0) {
            properties.put(MAIL_SMTP_SOCKET_FACTORY_PORT, socketPort);
        }
        if (smtpStartTLS != null) {
            properties.put(MAIL_SMTP_STARTTLS_ENABLE, smtpStartTLS);
        }
        if (smtpSocketFactoryFallback != null) {
            properties.put(MAIL_SMTP_SOCKET_FACTORY_FALLBACK, smtpSocketFactoryFallback);
        }
        properties.put(MAIL_MIME_CHARSET, charset != null ? charset : DEFAULT_ENCODING);

        if (userName != null && password != null) {
            properties.put(MAIL_SMTP_AUTH, true);
            mailSession = Session.getInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, password);
                }
            });
        } else {
            mailSession = Session.getInstance(properties);
        }
    }
    
	private Multipart getMailBody(String emailBody) {
		Multipart body = new MimeMultipart();
		try {
			BodyPart textPart = new MimeBodyPart();
			textPart.setText(emailBody);
			body.addBodyPart(textPart);
		} catch (MessagingException e) {
			System.out.println("set mail body failed "+e.getMessage());
		}
		return body;
	} 
    
    public void sendEmailContent(InternetAddress from, InternetAddress[] to, String emailSubject, String emailBody) {
        if (mailSession == null) {
            initializeMailSession();
        }

        try{        	         
            Multipart content = getMailBody(emailBody);

            Message message = new MimeMessage(mailSession);
            message.setFrom(from);
            message.setRecipients(Message.RecipientType.TO, to);
            message.setSentDate(new Date());
            message.setSubject(emailSubject);
            //message.setText(body);
            message.setContent(content);
            Transport.send(message);
        } catch (AddressException e) {
        	System.out.println("send email address exception."+e.getMessage());
        } catch (MessagingException e) {
        	System.out.println("send email message exception."+e.getMessage());
        }
    }
    
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public void setSocketPort(int socketPort) {
        this.socketPort = socketPort;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getSmtpStartTLS() {
        return smtpStartTLS;
    }

    public void setSmtpStartTLS(Boolean smtpStartTLS) {
        this.smtpStartTLS = smtpStartTLS;
    }

    public Boolean getSmtpSocketFactoryFallback() {
        return smtpSocketFactoryFallback;
    }

    public void setSmtpSocketFactoryFallback(Boolean smtpSocketFactoryFallback) {
        this.smtpSocketFactoryFallback = smtpSocketFactoryFallback;
    }
}
