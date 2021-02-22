package bot.mail;


import bot.model.User;
import bot.service.UserService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@PropertySource("classpath:telegram.properties")
public class NotificationService {



    private final UserService userService; // сервис для работы с базой

    private final JavaMailSender mailSender; // создаёт спринг бут для работы с почтой

    @Value("${bot.email.subject}")
    private String emailSubject;

    @Value("${bot.email.from}")
    private String emailFrom;

    @Value("${bot.email.to")
    private String emailTo;


    public NotificationService(UserService userService, JavaMailSender mailSender) {
        this.userService = userService;
        this.mailSender = mailSender;
    }

    @Scheduled(fixedRate = 10000)
    public void sendNewApplications(){ // вытаскиваем всех пользователей, которые заполнили информацию
        List<User> users = userService.findNewUsers();
        if (users.size() == 0)  // если пользователей нет, делаем ничего
            return;

        StringBuilder sb = new StringBuilder();  // если польз. есть создаём StringBuilder

        users.forEach(user -> sb.append("Phone: ")
                .append(user.getPhone())   // для каждого пользователя добавляем телефон
                .append("\r\n")
                .append("Email: ")
                .append(user.getEmail())    // и емэйл
                .append("\r\n\r\n"));

        sendEmail(sb.toString());
    }
    private void sendEmail(String text){   // метод формирующий и отправляющий письмо
        SimpleMailMessage message = new SimpleMailMessage();  // объект описывающий одно простое сообщение

        message.setTo(emailTo);     // кому отправлять
        message.setFrom(emailFrom); // от кого сообщение
        message.setSubject(emailSubject); // тема сообщения
        message.setText(text);            // текст
    }

}
