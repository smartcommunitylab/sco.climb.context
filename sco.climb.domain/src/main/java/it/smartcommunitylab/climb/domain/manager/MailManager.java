package it.smartcommunitylab.climb.domain.manager;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.smartcommunitylab.climb.contextstore.model.User;

@Service
public class MailManager {
	private static final transient Logger logger = LoggerFactory.getLogger(MailManager.class);
	
	@Autowired
	@Value("${mail.notification.host}")
	private String host;
	
	@Autowired
	@Value("${mail.notification.port}")
	private String port;
	
	@Autowired
	@Value("${mail.notification.to}")
	private String mailTo;	

	@Autowired
	@Value("${mail.notification.user}")
	private String userName;
	
	@Autowired
	@Value("${mail.notification.password}")
	private String password;
	
	public void sendConsoleRegistration(User user) {
		try {
			Properties props = new Properties();
			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtp.host", host);    
      props.put("mail.smtp.socketFactory.port", port);    
      props.put("mail.smtp.port", port);  
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.ssl.enable", "true");
      props.put("mail.smtp.socketFactory.class",    
                "javax.net.ssl.SSLSocketFactory");    
			Session session = Session.getInstance(props,    
        new javax.mail.Authenticator() {    
        	protected PasswordAuthentication getPasswordAuthentication() {    
        		return new PasswordAuthentication(userName, password);  
        	}    
       	}
			);
			String msg = "Richiesta da %s %s - %s\nPlesso scolastico:%s";
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("info@smartcommunitylab.it"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));    
      message.setSubject("Richiesta registrazione accesso console CLIMB");    
      message.setText(String.format(msg, user.getName(), user.getSurname(), user.getEmail(), 
      		user.getCustomData().get("plessoScolastico")));    
      Transport.send(message);
      logger.info("sendConsoleRegistration mail sent");
		} catch (Exception e) {
			logger.warn("sendConsoleRegistration error:" + e.getMessage());
		}
	}
}
