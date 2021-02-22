package bot.service;


import bot.model.User;
import bot.repo.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User findByChatId(long id) {
        return userRepository.findByChatId(id);
    } // дублирует метод из репозитория

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {          // метод нужен для рассылки всем , кто хоть раз общался с ботом
        return userRepository.findAll();
    }

    @Transactional
    public List<User> findNewUsers() {   // метод который достает новых пользователей, которые ввели все данные , но о них не было уведомления
        List<User> users = userRepository.findNewUsers();

        users.forEach((user) -> user.setNotified(true));
        userRepository.saveAll(users);

        return users;
    }

    @Transactional  // назначение админа
    public void addUser(User user){
        user.setAdmin(userRepository.count() == 0);  // пока пользователей 0 , первый попавший в канал = админ
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(User user){  // изменение и сохранение параметров юзера
        userRepository.save(user);
    }



}
