package projekat.services;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import projekat.exception.MailException;
import projekat.repository.MailRepository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;

@Service
public class MailService implements MailRepository {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    Configuration config;

    public MailService(JavaMailSender mailSender, Configuration config) {
        this.mailSender = mailSender;
        this.config = config;
    }

    @Override
    public void sendEmail(HashMap map,String email, String template, String subject){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final var mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            final var t = config.getTemplate(template);
            final var html = FreeMarkerTemplateUtils.processTemplateIntoString(t,map);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom("adminteam@example.com");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setText(html, true);

            mailSender.send(mimeMessageHelper.getMimeMessage());

        } catch (MessagingException | IOException | TemplateException e) {
            throw new MailException( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
