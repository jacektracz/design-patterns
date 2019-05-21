package victor.training.oo.behavioral.template;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import lombok.Data;

@SpringBootApplication
public class TemplateSpringApp implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(TemplateSpringApp.class, args);
	}

	@Autowired
	private Emails emails;

	@Autowired
	private EmailService emailService;

	public void run(String... args) throws Exception {
		emailService.sendEmail("a@b.com", emails::composeOrderReceived);
		emailService.sendEmail("a@b.com", emails::composeOrderShipped);
	}
}
@Service
class EmailService {

	public void sendEmail(String emailAddress, EmailComposer composer) {
		EmailContext context = new EmailContext(/*smtpConfig,etc*/);
		int MAX_RETRIES = 3;
		for (int i = 0; i < MAX_RETRIES; i++ ) {
			Email email = new Email(); // constructor generates new unique ID
			email.setSender("noreply@corp.com");
			email.setReplyTo("/dev/null");
			email.setTo(emailAddress);
			composer.compose(email);
			boolean success = context.send(email);
			if (success) break;
		}
	}
}
interface EmailComposer {
	void compose(Email email);
}
@Service
class Emails {
//	@Autowired private altaDep
	public void composeOrderReceived(Email email) {
		email.setSubject("Order Received");
		email.setBody("Thank you for your order");
	}
	public void composeOrderShipped(Email email) {
		email.setSubject("Order Shipped");
		email.setBody("Tz-am trimas! Speram s-ajunga (de data asta)");
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