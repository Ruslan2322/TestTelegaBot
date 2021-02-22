package bot.botFolder;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

// состояние в котором находится бот
public enum BotState {

    // отправляем сообщение Hello и переходим в следующее состояние
    Start {
        @Override
        public void enter(BotContext context){
            sendMessage(context, "Hello!");
        }

        @Override
        public BotState nextState(){
            return EnterPhone;
        }
    },

    // отправляем введите телефон , человек вводит  номер и переходим в состояние ввести емэйл
    EnterPhone {
        @Override
        public void enter(BotContext context) { sendMessage(context, "Enter your phone number Please");}

        // мы добираемся до Entity описывающего нашего пользователя, устанавливаем телефон в то что было введено
        @Override
        public void handleInput(BotContext context) {context.getUser().setPhone(context.getInput());}

        @Override
        public BotState nextState(){return EnterEmail;}
    },
    EnterEmail{
        private BotState next;

        @Override
        public void enter(BotContext context) { sendMessage(context, "Enter your email address Please");}

        public void handleInput(BotContext context){ // пользователь вводит емэйл
            String email = context.getInput();

            if (Utils.isValidEmailAddress(email)) {  // если пользователь ввел все правильно
                context.getUser().setEmail(context.getInput()); // то записываем в пользователя
                next = Approved; // следующее состояние Approved
            } else {
                sendMessage (context, "Wrong e-mail address!"); // в противном случае просим ввести заново
                next = EnterEmail; // следующее состояние EnterEmail
            }
        }

        @Override
        public BotState nextState() {return next;}
    },


    // подтверждение
    Approved(false) {
        @Override
        public void enter (BotContext context){
            sendMessage(context, "Thank you for application!"); // спасибо за заявку
        }

        @Override
        public BotState nextState () {   // возвращаем в старт
            return Start;
        }
    };

    private static BotState[] states;
    private final boolean inputNeeded; // указывает нужно ли ждать ввода информации от пользователя в текущем состоянии, например при заполнении строк

    BotState(){this.inputNeeded = true;}

    BotState(boolean inputNeeded){this.inputNeeded = inputNeeded;}

    public static BotState getInitialState() {
        return byId(0);
    }

    public static BotState byId(int id){  // по ид возвращает состояние из массива
        if (states == null){
            states = BotState.values();
        }

        return states[id];
    }
    // позволяет отправить одно сообщение нашему пользователю
    protected void sendMessage(BotContext context, String text){ // text - текст сообщения
        SendMessage message = new SendMessage()
                .setChatId(context.getUser().getChatId()) // chatId - кому отправлять
                .setText(text);
        try{
            context.getBot().execute(message);          // вызываем метод execute
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    public boolean isInputNeeded(){return inputNeeded;}

    // метод который обрабатывает ввод пользователя в текущем состоянии
    public void handleInput(BotContext context){
        // do nothing by default
    }

    // войти в состояние
    public abstract void enter(BotContext context);

    // в какое состояние переходить , после того , как текущее уже обработано
    public abstract BotState nextState();





}
