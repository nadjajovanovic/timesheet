package projekat.templates;

import freemarker.template.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

public abstract class MailTemplate {
    public abstract void sendEmail(JavaMailSender mailSender, Configuration config);
}
