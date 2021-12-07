package projekat.repository;

import java.util.HashMap;

public interface MailRepository {
    void sendEmail(HashMap map, String email, String template, String subject);
}
