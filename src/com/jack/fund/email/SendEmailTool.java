package com.jack.fund.email;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;

public class SendEmailTool {
    private static final Logger LOGGER = Logger.getLogger(SendEmailTool.class);
    private static MailSender mailSender = null;
    
    /*
	服务器地址如下:
    POP3服务器:pop.163.com
	SMTP服务器:smtp.163.com
	IMAP服务器:imap.163.com
    */
    
	public static void main(String[] args) throws IOException {		
		
    	if (args.length != 1) {
    		System.out.println("Should assign a configuration file, content is as below: \n"
    				+ "emailContentFile:   	file_path_name\n"
    				+ "emailHost:    		sendEmail_host\n"
    				+ "smtpPort:     		smtp_port\n"
    				+ "smtpStartTLS: 		true/false\n"
    				+ "userName:     		userName\n"
    				+ "passWord:     		password\n"
    				+ "subject:      		ip address\n"
    				+ "receiver:     		receiver address\n");
    		return;
    	}
    	
    	String confFileName = args[0];
    	
    	String contentfile    = null;
    	String emailHost     = null;
    	String smtpPort      = null;
    	String smtpStartTLS  = null;
    	String userName      = null;
    	String passWord      = null;
    	String emailSubject  = null;
    	String emailReceiver = null;
    	
    	String fileContent  = null;
    	Integer smtpPortInt;
    	Boolean smtpStartTLSBool;
    	
    	try {
    		FileInputStream fis = new FileInputStream(confFileName);
    		Properties properties = new Properties();
			properties.load(fis);
    		
			contentfile   = properties.getProperty("emailContentFile");  if (contentfile != null)   contentfile.trim();
			emailHost    = properties.getProperty("emailHost");   if (emailHost != null)    emailHost.trim();
			smtpPort     = properties.getProperty("smtpPort");    if (smtpPort != null)     smtpPort.trim();
			smtpStartTLS = properties.getProperty("smtpStartTLS");if (smtpStartTLS != null) smtpStartTLS.trim();
			userName     = properties.getProperty("userName");    if (userName != null)     userName.trim();
			passWord     = properties.getProperty("passWord");    if (passWord != null)     passWord.trim();
			
			emailSubject = properties.getProperty("subject");     if (emailSubject != null)  emailSubject.trim();
			emailReceiver= properties.getProperty("receiver");    if (emailReceiver != null) emailReceiver.trim();
    	} catch (FileNotFoundException e) {
    		System.out.println(e.getMessage());
    		return;
    	} 
    	
    	if (contentfile == null || contentfile.isEmpty() ||
    		emailHost  == null || emailHost.isEmpty()  ||
    		smtpPort   == null || smtpPort.isEmpty()   ||
    		smtpStartTLS == null || smtpStartTLS.isEmpty() ||
    		userName == null || userName.isEmpty() ||
    		passWord == null || passWord.isEmpty() ||
    		emailSubject == null || emailSubject.isEmpty() ||
    		emailReceiver == null || emailReceiver.isEmpty()
    	   ) {
    		System.out.println("Error configuration: "+contentfile+"/"+emailHost+"/"+smtpPort+"/"+smtpStartTLS+"/"+"/"+userName+"/"+passWord);
    		return;
    	} else {
    		System.out.println("send "+contentfile+"\n"
    				+"to "+emailReceiver+"\n"
    				+"from "+userName);
    	}
    	
		File file = new File(contentfile);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();
		String tmpLine = null;
		while ( (tmpLine = reader.readLine()) != null) {
			tmpLine = tmpLine.trim();
			builder.append(tmpLine).append("\n");
		}
		fileContent = builder.toString();
		reader.close();
		
		smtpPortInt = Integer.parseInt(smtpPort);
		smtpStartTLSBool = Boolean.parseBoolean(smtpStartTLS);
    	
		mailSender = new MailSender();
		mailSender.setHost(emailHost);
		mailSender.setSmtpPort(smtpPortInt);
		mailSender.setSmtpStartTLS(smtpStartTLSBool);
		mailSender.setUserName(userName);
		mailSender.setPassword(passWord);
		
		try {
			InternetAddress from = new InternetAddress(userName);
			InternetAddress[] recvEmails = new InternetAddress[1];
			recvEmails[0] = new InternetAddress(emailReceiver);
			mailSender.sendEmailContent(from, recvEmails, emailSubject, fileContent);
		} catch (MessagingException e) {
			System.out.println("Send email failed: " + e.getMessage());
		}
		
		System.out.println("email sent successfully.");
	}
}
