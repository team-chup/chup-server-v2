package team.themoment.thup.global.mail;

public interface MailService {

    void send(String to, String subject, String htmlBody);
}
