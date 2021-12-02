package projekat.templates;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import projekat.api.model.TeamMemberDTO;
import projekat.exception.MailException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class WelcomeMailTemplates extends MailTemplate {

    private String username;
    private String password;
    private String email;

    public WelcomeMailTemplates(TeamMemberDTO teamMember) {
        this.username = teamMember.getUsername();
        this.password = teamMember.getPassword();
        this.email = teamMember.getEmail();
    }

    @Override
    public void sendEmail(JavaMailSender mailSender,Configuration config) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("password", password);
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            Template t = config.getTemplate("welcome-email-template.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t,model);
            mimeMessageHelper.setSubject("Welcome");
            mimeMessageHelper.setFrom("adminteam@example.com");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setText(html, true);

            mailSender.send(mimeMessageHelper.getMimeMessage());

        } catch (MessagingException | IOException | TemplateException e) {
            throw new MailException( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
