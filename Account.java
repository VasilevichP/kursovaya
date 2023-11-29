package org.example.app.entities;

import java.io.Serializable;

public class Account implements Serializable {
    static final long SerialVersionUID = 1;

    protected boolean blocked = false;
    protected String login;
    protected String password;

    public Account(String login, String password){
        this.login=login;
        this.password=password;
        this.blocked=false;
    }
    public void block(){
        this.blocked=true;
    }
    public void unblock(){
        this.blocked=false;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean getBlocked() {
        return blocked;
    }
    public AccountRole getRole() { return null;}

    public boolean checkPassword(String pass){
        return this.password.equals(pass);
    }

    public String show() {
        return "Логин: " + getLogin() + "\t Пароль: " + getPassword() + "\t Статус блокировки: " + getBlocked();
    }
    public String toString(){
        if (getBlocked())
            return getLogin() + " (заблокирован)";
        else return getLogin();
    }

}

