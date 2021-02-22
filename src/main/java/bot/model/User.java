package bot.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private Long chatId;       // телега выделяет ид всем пользователям заходящих на бота, нужен для отправки сообщений пользователю
    private Integer stateId;   // состояние клиента в определенный период времени согласно сценария взаимодействия(текущее состояние)
    private String phone;      // ввод номера
    private String email;      // ввод емэйл
    private Boolean admin;     // булевский флажок отмечающий администратора
    private Boolean notified = false;//по данному клиенту мы отправили уведомления на почту

    public User(){}

    public User(Long chatId, Integer stateId) {
        this.id = id;
        this.chatId = chatId;
    }

    public User(Long chatId, Integer stateId, Boolean admin) {
        this.chatId = chatId;
        this.stateId = stateId;
        this.admin = admin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }
}
