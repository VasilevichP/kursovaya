package org.example.app.entities;

public class Admin extends Account {
    static final long SerialVersionUID = 2;
    //private transient AccountList accountList;

    public Admin(String userLogin, String userPassword) {
        super(userLogin,userPassword);
    }

    public AccountRole getRole() {
        return AccountRole.admin;
    }
}
