package it.cgmconsulting.myblog.mail;
import com.mailgun.model.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.MessageResponse;


@Service
@Slf4j
public class MailService{



public MessageResponse sendMail(Mail mail) {
	MailgunMessagesApi mailgunMessagesApi = MailgunClient.config("${CORSO_API_KEY_MAIL}")
			.createApi(MailgunMessagesApi.class);

	Message message = Message.builder()
			.from(mail.getMailFrom())
			.to(mail.getMailTo())
			.subject(mail.getMailSubject())
			.text(mail.getMailContent())
			.build();

	//return mailgunMessagesApi.sendMessage(mailgunDomain, message);
	return mailgunMessagesApi.sendMessage("${CORSO_DOMAIN_MAIL}",message);
}
}
