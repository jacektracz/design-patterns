package victor.training.oo.behavioral.template;

import java.util.Random;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import lombok.Data;
import victor.training.oo.behavioral.template.EmailSender.ContentWriter;

@SpringBootApplication
public class TemplateSpringApp implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(TemplateSpringApp.class, args);
	}

	@Autowired
	private EmailContentWriter emailContentWriter;
	@Autowired
	private EmailSender emailSender;
	
	public void run(String... args) throws Exception {
//		emailSender.sendEmail("a@b.com", email -> emailContentWriter.writeEmailReceivedContent(email));
//		emailSender.sendEmail("a@b.com", emailContentWriter::writeEmailReceivedContent);
		emailSender.sendEmail("a@b.com", emailContentWriter::writeEmailShippedContent);
//		ContentWriter v1 = emailContentWriter::writeEmailShippedContent;
		Consumer<Email> v2 = emailContentWriter::writeEmailShippedContent;
	}
}

@Service
class EmailSender {
	//CHANGE REQUEST: ALSO SEND AN EMAIL PENTRU 'ORDER SHIPPED'

//	@FunctionalInterface
//	interface ContentWriter {
//		void writeContent(Email email);
//	}
	public void sendEmail(String emailAddress, Consumer<Email> contentWriter) {
		EmailContext context = new EmailContext(/*smtpConfig,etc*/);
		int FISE = 3;
		for (int i = 0; i < FISE; i++ ) {
			Email email = new Email(); // constructor generates new unique ID
			email.setSender("noreply@corp.com");
			email.setReplyTo("/dev/null");
			email.setTo(emailAddress);
			contentWriter.accept(email);
			boolean success = context.send(email);
			if (success) break;
		}
	}
}


@Service
class EmailContentWriter {
	//@Autowired alta Dependinta
	public void writeEmailReceivedContent(Email email) {
		email.setSubject("Order Received");
		email.setBody("Thank you for your order");
	}
	public void writeEmailShippedContent(Email email) {
		email.setSubject("Order Shipped");
		email.setBody("Ti-am trimas. Speram s-ajunga (de data asta).");
	}
}

class EmailContext {
	public boolean send(Email email) {
		System.out.println("Trying to send " + email);
		return new Random(System.nanoTime()).nextBoolean();
	}
}

@Data
class Email {
	private final long id = new Random(System.nanoTime()).nextLong();
	private String sender;
	private String subject;
	private String body;
	private String replyTo;
	private String to;
}