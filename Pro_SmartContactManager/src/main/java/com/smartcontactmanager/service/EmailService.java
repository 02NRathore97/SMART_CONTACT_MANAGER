package com.smartcontactmanager.service;

import java.util.Properties;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	public boolean sendEmail(String subject, String message,  String to) {
		
		boolean f = false;
		
		String from = "rathoreneeraj448@gmail.com";
		
		String host = "smtp.gmail.com";
			
		//get the system properties
		Properties properties = System.getProperties();
		System.out.println("PROPERTIES "+ properties);
		
		//setting important information to properties object
		
		//host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		//Step-1: get the session object
		Session session = Session.getInstance(properties, new Authenticator(){
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("rathoreneeraj448@gmail.com", "mlanhqnguvuutbij");
			}
		});
		
		session.setDebug(true);
		//step-2: compose the message[text, multi media]
		MimeMessage m = new MimeMessage(session);
		
		
		try {
			//from email
			m.setFrom(from);
			
			
			//adding recipient
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			//adding subject to message
			m.setSubject(subject);
			
			//adding text to message
			//m.setText(message);
			m.setContent(message, "text/html");
			
			//step-3: send the message using Transport class
			Transport.send(m);
			
			f  = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}
	
}
