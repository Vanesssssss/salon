package POJO;

import java.io.Serializable;

public class Users implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String login;
    private String password;
    private String role;

    // Конструктор без параметров
    public Users() {}

    // Конструктор с параметрами
    public Users(int id, String login, String password, String role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Users{ login='" + login + "', password='[REDACTED]', role='" + role + "'}";
    }
}
