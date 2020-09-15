package it.smartcommunitylab.climb.domain.manager;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import it.smartcommunitylab.climb.contextstore.model.Institute;
import it.smartcommunitylab.climb.contextstore.model.School;
import it.smartcommunitylab.climb.contextstore.model.User;
import it.smartcommunitylab.climb.domain.common.Utils;
import it.smartcommunitylab.climb.domain.model.PedibusGame;

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
			Session session = setMailerSession();
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
	
	public void sendGameEditorRoleAssign(String email, Institute institute, School school, PedibusGame game) {
		try {
			Session session = setMailerSession();
			String msg = "Assegnato il ruolo di GAME-EDITOR per:\nIstituto: %s\nScuola: %s\nGioco: %s";
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("info@smartcommunitylab.it"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));    
      message.setSubject("Richiesta registrazione accesso console CLIMB");    
      message.setText(String.format(msg, institute.getName(), school.getName(), game.getGameName()));    
      Transport.send(message);
      logger.info("sendRoleAssign mail sent");
		} catch (Exception e) {
			logger.warn("sendRoleAssign error:" + e.getMessage());
		}
	}

	public void sendTeacherRoleAssign(String email, Institute institute, School school, PedibusGame game) {
		try {
			Session session = setMailerSession();
			String msg = "Assegnato il ruolo di INSEGNANTE per:\nIstituto: %s\nScuola: %s\nGioco: %s";
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("info@smartcommunitylab.it"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));    
      message.setSubject("Richiesta registrazione accesso console CLIMB");    
      message.setText(String.format(msg, institute.getName(), school.getName(), game.getGameName()));    
      Transport.send(message);
      logger.info("sendTeacherRoleAssign mail sent");
		} catch (Exception e) {
			logger.warn("sendTeacherRoleAssign error:" + e.getMessage());
		}
	}
	
	public void sendVolunteerRoleAssign(String email, Institute institute, School school) {
		try {
			Resource resource = new ClassPathResource("mail/volontario_pedibus.txt", 
					this.getClass().getClassLoader());
			Reader reader = new InputStreamReader(resource.getInputStream(), "UTF-8");
			String msg = FileCopyUtils.copyToString(reader);
			msg = msg.replace("%istituto%", institute.getName());
			msg = msg.replace("%scuola%", school.getName());
			Session session = setMailerSession();
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("info@smartcommunitylab.it"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));    
      message.setSubject("Assegnazione ruolo progetto CLIMB");    
      message.setText(msg);    
      Transport.send(message);
      logger.info("sendTeacherRoleAssign mail sent to " + email);
		} catch (Exception e) {
			logger.warn("sendTeacherRoleAssign error:" + e.getMessage());
		}
	}
	
	public void sendBatteryLowWarning(Institute institute, List<String> messages) {
		String email = institute.getWarningBatteryLowMail();
		if(Utils.isNotEmpty(email)) {
			try {
				Session session = setMailerSession();
				StringBuffer msg = new StringBuffer("Nodi con batteria bassa - " + institute.getName() + "\n");
				for(String s : messages) {
					msg.append(s);
				}
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress("info@smartcommunitylab.it"));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));    
	      message.setSubject("Livello batteria nodi");    
	      message.setText(msg.toString());    
	      Transport.send(message);
	      logger.info("sendBatteryLowWarning mail sent for " + institute.getObjectId());			
			} catch (Exception e) {
				logger.warn("sendBatteryLowWarning error:" + e.getMessage());
			}
		}
	}
	
	private Session setMailerSession() {
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtp.host", host);    
		props.put("mail.smtp.socketFactory.port", port);    
		props.put("mail.smtp.port", port);  
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.socketFactory.class",    
		          "javax.net.ssl.SSLSocketFactory");  
		props.put("mail.smtp.localhost", "climb.smartcommunitylab.it");
		Session session = Session.getInstance(props,    
		  new javax.mail.Authenticator() {    
		  	protected PasswordAuthentication getPasswordAuthentication() {    
		  		return new PasswordAuthentication(userName, password);  
		  	}    
		 	}
		);
		return session;
	}
	
}
