package bot.botFolder;

import bot.model.User;

public class BotContext {
    private final ChatBot bot; //библиотечный1 объект , который реализует бота
    private User user; // Entity описывающий пользователя для которого все эти состояния работают
    private final String input; // То, что пользователь ввел на данном этапе в ответ на какой то вопрос

    public static BotContext of (ChatBot bot, User user, String text) {
        return new BotContext(bot, user, text);
    }

    private BotContext(ChatBot bot, User user, String input) {
        this.bot = bot;
        this.user = user;
        this.input = input;
    }

    public ChatBot getBot() {
        return bot;
    }

    public User getUser() {
        return user;
    }

    public String getInput() {
        return input;
    }
}
