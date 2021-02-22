package bot.botFolder;

import bot.model.User;
import bot.service.UserService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;


@Component
@PropertySource("classpath:telegram.properties")
public class ChatBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LogManager.getLogger(ChatBot.class);

    private static final String BROADCAST = "broadcast ";
    private static final String LIST_USERS = "users";

    @Value("testNew22_bot")
    private String botName;

    @Value("1695904706:AAHUtnCWXHMLA3QGUav3wMg_fPUblYbhtuQ")
    private String botToken;

    private final UserService userService;

    public ChatBot(UserService userService){
        this.userService = userService;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    // Метод вызывается автоматически , когда пользователь что то вводит
    @Override
    public void onUpdateReceived(Update update) { // Update содержит chatId и данные которые ввел пользователь
        if (!update.hasMessage() || !update.getMessage().hasText()) // проверяем , что пришло что то не пустое
            return;                                   // если пришло пустое - делаем ничего

        // если сообщение не пустое то
        final String text = update.getMessage().getText();// сообщение пользователя
        final long chatId = update.getMessage().getChatId(); // уникальный идентификатор пользователя

        User user = userService.findByChatId(chatId);   // достаём пользователя из базы user по chatId
        // если user с нами общался то это Entity , если нет то будет null

        if (checkIfAdminCommand(user,text)) // проверяет, не является ли сообщение , которое прислали специально зарезервированной командой для админа
            return; // если является, то метод её обрабатывает и дальше делаем ничего

        BotContext context;
        BotState state;

        if (user == null){ // пользователя нет в базе
            state = BotState.getInitialState(); // получаем объект описывающий нулевое состояние

            user = new User(chatId, state.ordinal()); // создаём user и передаём в конструктор чат Id , и нулевое состояние ввиде интеджера
            userService.addUser(user); // сохраняем пользователя в базу

            context = BotContext.of(this, user, text); // BotContext - формируем объект содержаший всё необходимое для обработки states(ссылка на бота, ссылка на описание пользователя, текст ,который он вводит)
            state.enter(context); // войти в это состояние

            LOGGER.info("New user registered: "+ chatId);
        } else {
            context = BotContext.of(this, user,text); // если в базе есть, то мы тоже формируем контекст
            state = BotState.byId(user.getStateId()); // но состояние мы берём не getInitialState()(нулевое) , а спрашиваем текущее getStateId()

            LOGGER.info("Update received for user in state: " + state);
        }

        state.handleInput(context); // обработать , то что ввел пользователь, когда он находится в каком то состоянии

        do {
            state = state.nextState(); // цикл переводит прогу в следующее состояние с присваиванием значения state
            state.enter(context); // вход с ледующее состояние и ввод данных
        } while(!state.isInputNeeded()); // если false ?то пролистываем т.к. в некоторых сотояниях не требуется ввода, если true, то ждем ввода

        user.setStateId(state.ordinal());// назначение текущего state как рабочего
        userService.updateUser(user); // сохраняем изменения в базе
    }


    // Проверяет на команду от админа
    private boolean checkIfAdminCommand(User user, String text){
        if (user == null || !user.getAdmin()) // пользователь должен быть в базе и числится админом
            return false;   // если нет , то работаем с пользователем

        if (text.startsWith(BROADCAST)){
            LOGGER.info("Admin command received: "+ BROADCAST); // рассылка сообщения от админа

            text = text.substring(BROADCAST.length()); // вытягиваем весь текст после команды broadcast
            broadcast(text); // рассылаем текст

            return true; // обработана админская команда
        } else if(text.equals(LIST_USERS)){
            LOGGER.info("Admin command received: " + LIST_USERS); // вывести  список юзеров

            listUsers(user); // выводим список всех юзеров
            return true; // обработана админская команда
        }
        return false; // если что то еще , то false , значит не админская команда
    }

    // на указанный id отправляет любой текст, который вы хотите
    private void sendMessage(long chatId, String text){
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(text);
        try{
            execute(message);
        } catch(TelegramApiException e){
            e.printStackTrace();
        }
    }


    // Выводит список пользователей
    private void listUsers(User admin){
        StringBuilder sb = new StringBuilder("All users list: \r\n"); // создаём с StringBuilder
        List<User> users = userService.findAllUsers(); // через сервер получаем все Entity

        users.forEach(user -> // для каждого Entity добавляем:
                sb.append(user.getId()) // ИД
                .append(' ')
                .append(user.getPhone()) // телефон
                .append(' ')
                .append(user.getEmail()) // емэйл
                .append("\r\n") // конец строки и разделитель
        );

        sendMessage(admin.getChatId(), sb.toString()); // отправляем админу , то что насобиралось в sb.toString()
    }

    // отправить всем пользователям
    private void broadcast(String text){
        List<User> users = userService.findAllUsers(); // собираем всех пользователей
        users.forEach(user -> sendMessage(user.getChatId(), text));// с помощью forEach рассылаем сообщения по ИД пользователя
    }
}
