package projekat.templates;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import projekat.api.model.ResetPasswordDTO;
import projekat.exception.MailException;
import projekat.exception.NotFoundException;
import projekat.models.Teammember;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordMailTemplate extends MailTemplate{

    private String password;

    public ResetPasswordMailTemplate(ResetPasswordDTO resetPasswordDTO) {
        this.password = resetPasswordDTO.getNewPassword();
    }

    @Override
    public void sendEmail(JavaMailSender mailSender,Configuration config) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        final var user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final var email = ((Teammember)user).getEmail();
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("password", password);
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            Template t = config.getTemplate("reset-password-email-template.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t,model);
            mimeMessageHelper.setSubject("Reset password");
            mimeMessageHelper.setFrom("adminteam@example.com");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setText(html, true);

            mailSender.send(mimeMessageHelper.getMimeMessage());

        } catch (MessagingException | IOException | TemplateException e) {
            throw new MailException( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
