package fr.inria.sacha.quizGumTree.server;


import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailSSL {
	
	Session session = null; 
	
	public void configure(String[] args) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("","0");
				}
			});

		
	}

	public void send(String from,String to, String subject, String messageString) {
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(messageString);

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void send2(String from,String to, String subject, String messageString){
		 Properties props = new Properties();
	        Session session = Session.getInstance(props, null);

	        String msgBody = "My ejemplo";

	        try {
	            Message msg = new MimeMessage(session);
	            msg.setFrom(new InternetAddress(from));
	            msg.addRecipient(Message.RecipientType.TO,
	                             new InternetAddress(to));
	            msg.setSubject("Ejemplo2");
	            msg.setText(msgBody);
	            Transport.send(msg);
	           
	            
	            System.out.println("Sent");
	        }  catch (Exception e) {
	            // ...
	        e.printStackTrace();
	        }
	}
	
	public static void main(String[] arg){
		SendMailSSL s = new SendMailSSL();
		s.configure(arg);
		s.send2("", "", "test", "test message");
	}
}