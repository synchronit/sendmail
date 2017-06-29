package com.synchronit.sendmail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.*;

public class SendMail {

	private static final long serialVersionUID = 1L;

	private static SendMail instance = null;
	
	private static String filePassword;

	public static synchronized SendMail getInstance() {
	    if (instance == null) {
	        instance = new SendMail();
	    }

	    return instance;
	}	
	
	public String send (String senderMessage, String senderName, String senderEmail)
	{
		String result = "not set";
		String firstError = "not set";
		
		try
		{
			if (filePassword == null)
			{
			    filePassword = new String(Files.readAllBytes(Paths.get("/var/www/synchronit.com/mailpwd")));	    
			    filePassword = filePassword.replace("\n", "").replace("\r", "");
			}
			
			result = sendMailTLS (senderMessage, senderName, senderEmail);
			if (!result.equals("OK"))
			{
				firstError = result;
				
				// TLS fails => trying SSL
				result = sendMailSSL (senderMessage, senderName, senderEmail, firstError);
			}
		} 
		catch (Exception e) 
		{
			System.out.println("***ERROR*** MESSAGE NOT SENT PER EMAIL ***ERROR***");
			System.out.println("From: "+senderName);
			System.out.println("Mail: "+senderEmail);
			System.out.println("Message: "+senderMessage);
			System.out.println("***ERROR*** ERROR DETAILS (LOG) ***ERROR***");

			result = e.toString()+" and previous error was: "+firstError;
			System.out.println(result);
			System.out.println("***ERROR*** END OF ERROR LOG ***ERROR***");
		}
		return result;
	}
	
	private String sendMailTLS (String senderMessage, String senderName, String senderEmail) 
	{
		
		String result;

		final String username = "contact@synchronit.com";
		final String password = filePassword;

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "209.236.112.62");
		props.put("mail.smtp.port", "587");

		try
		{			
			Session session = Session.getInstance(props,
			  new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			  });
	
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(senderEmail));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("fguigou@gmail.com"));
			message.setSubject("Testing Subject");
			message.setText("Message from "+senderName+": "+senderMessage+" (sent via TLS on first attempt ... OK)");
	
			Transport.send(message);

			result = "OK";
		}
		catch (Exception e)
		{
			result = e.toString();
		}

		return result;
	}
	
	private String sendMailSSL (String senderMessage, String senderName, String senderEmail, String error ) throws Exception
	{
		
		String result;

		final String username = "contact@synchronit.com";
		final String password = "Muchachit0123";

		Properties props = new Properties();
		props.put("mail.smtp.host", "mail.synchronit.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
	
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(senderEmail));
		message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("fguigou@gmail.com"));
		message.setSubject("Testing Subject");
		message.setText("Message from "+senderName+": "+senderMessage+" (sent via SSL ... TLS has failed because: "+error+" )");

		Transport.send(message);

		result = "OK";

		return result;
	}
		
}
