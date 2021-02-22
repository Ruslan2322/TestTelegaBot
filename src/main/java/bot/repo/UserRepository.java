package bot.repo;

import bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.notified = false "+  // вытащить пользователей notified = false , а так же телефон и емэйл должны быть заполнены
    "AND u.phone IS NOT NULL u.email IS NOT NULL")
    List<User> findNewUsers(); // задача. Вытащить всез новых пользователей, которым еще не отправлялась нотификация

    User findByChatId(Long id); // поиск пользователя по его идентификатору
}
