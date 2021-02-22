package bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableScheduling       // планировщик, в определенное время отправляет уведомления по заявкам котрые пришли
public class DemoApplication {
    public static void main(String[] args) {
        ApiContextInitializer.init();  // init() - инициализация библиотеки для ботов
        SpringApplication.run(DemoApplication.class, args);
    }
}
