package org.example.app.model;

import com.mysql.cj.xdevapi.*;
import org.example.app.DBConnection.DBConnection;
import org.example.app.entities.*;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountModel {
    public static final String FILE_ACCOUNTS_NAME = "Accounts.dat";
    private static final String TABLE_NAME = "Accounts";
    private static final String LOGIN_COL = "acc_login";
    private static final String PASSWORD_COL = "acc_password";
    private static final String STATUS_COL = "acc_status";
    private static final String DEL_STATUS_COL = "acc_del_status";
    private static final String ROLE_COL = "acc_role";
    private boolean isNoAdmin;
    private ArrayList<Account> accounts;
    private static AccountModel instance;

    public static AccountModel getInstance() {
        if (instance == null)
            instance = new AccountModel();
        return instance;
    }
    public void restoreAcc(String log){
        restoreAccDB(log);
    }

    private AccountModel() {
        accounts = new ArrayList<Account>();
        readFromDB();
        isNoAdmin = true;
        for (Account a : accounts) {
            if (a.getRole() == AccountRole.admin) {
                isNoAdmin = false;
                break;
            }
        }
    }

    public static ArrayList<Account> getDeletedAccs() {
        ArrayList<Account> delList = new ArrayList<Account>();
        delList=readDeletedFromDB();
        return delList;
    }

    public void addAccount(Account acc) {
        accounts.add(acc);
        addToDB(acc);
        if (isNoAdmin && (acc.getRole() == AccountRole.admin))
            isNoAdmin = false;
    }

    public Account addAccount(String login, String password) {
        Account acc;
        if (getAccountExtended(login)) return null;
        if (checkAdmin())
            acc = new User(login, password);
        else
            acc = new Admin(login, password);
        addAccount(acc);
        return acc;
    }

    public ArrayList<Account> getList() {
        return accounts;
    }

    public Account getAccount(String log) {
        Account acc = null;
        for (Account a : accounts) {
            if (a.getLogin().equals(log)) {
                acc = a;
                break;
            }
        }
        return acc;
    }
    public boolean getAccountExtended(String log) {
        return searchInDB(log);
    }

    public boolean deleteAcc(String log) {
        Account acc = getAccount(log);
        if (acc == null) return false;
        if (acc.getRole() == AccountRole.admin) return false;
        toDeletedDB(acc);
        accounts.remove(acc);
        return true;
    }

    public boolean changeAcc(String log) {
        Account acc = getAccount(log);
        if (acc == null) return false;
        if (acc.getRole() == AccountRole.admin) return false;
        if (acc.getBlocked())
            acc.unblock();
        else
            acc.block();
        updateDB(acc);
        return true;
    }

    public void addToDB(Account acc) {
        DBConnection connection = DBConnection.getInstance();
        if (connection != null) {
            Table table = connection.getTable(TABLE_NAME);
            if (table == null)
                System.out.println("Невозможно открыть таблицу " + TABLE_NAME);
            else {
                InsertResult res = table.insert(LOGIN_COL, PASSWORD_COL, STATUS_COL, ROLE_COL)
                        .values(acc.getLogin(), acc.getPassword(), acc.getBlocked() ? AccountStatus.blocked.getText() : AccountStatus.notBlocked.getText(),
                                acc.getRole().getText()).execute();
                System.out.printf("Insert completed. Number of affected counts: %d. Number of warnings: %d\n", res.getAffectedItemsCount(), res.getWarningsCount());
                while (res.getWarnings().hasNext()) {
                    Warning w = res.getWarnings().next();
                    System.out.printf("\t%s\n", w.getMessage());
                }
            }
//            connection.close();
        }
    }

    private void readFromDB() {
        DBConnection connection = DBConnection.getInstance();
        if (connection != null) {
            Table table = connection.getTable(TABLE_NAME);
            if (table == null)
                System.out.println("Невозможно открыть таблицу " + TABLE_NAME);
            else {
                RowResult rows = table.select().where(STATUS_COL + " != :acc_del_status").bind("acc_del_status", AccountStatus.deleted.getText()).execute();
                while (rows.hasNext()) {
                    Row row = rows.next();
                    String accRole = row.getString(ROLE_COL);
                    Account acc;
                    if (accRole.equals("a")) {
                        acc = new Admin(row.getString(LOGIN_COL), row.getString(PASSWORD_COL));
                        acc.unblock();
                    } else {
                        acc = new User(row.getString(LOGIN_COL), row.getString(PASSWORD_COL));
                        if (row.getString(STATUS_COL).equals("b")) acc.block();
                        else acc.unblock();
                    }
                    accounts.add(acc);
                }
                System.out.printf("Select completed. Number of affected counts: %d. Number of warnings: %d\n", rows.getAffectedItemsCount(), rows.getWarningsCount());
                while (rows.getWarnings().hasNext()) {
                    Warning w = rows.getWarnings().next();
                    System.out.printf("\t%s\n", w.getMessage());
                }

            }
//            connection.close();
        }
    }

    private static ArrayList<Account> readDeletedFromDB() {
        ArrayList<Account> list = new ArrayList<Account>();
        DBConnection connection = DBConnection.getInstance();
        if (connection != null) {
            Table table = connection.getTable(TABLE_NAME);
            if (table == null)
                System.out.println("Невозможно открыть таблицу " + TABLE_NAME);
            else {
                RowResult rows = table.select().where(STATUS_COL + " = :acc_status").bind("acc_status", AccountStatus.deleted.getText()).execute();
                while (rows.hasNext()) {
                    Row row = rows.next();
                    String accRole = row.getString(ROLE_COL);
                    Account acc;
                    if (accRole.equals("a")) {
                        acc = new Admin(row.getString(LOGIN_COL), row.getString(PASSWORD_COL));
                        acc.unblock();
                    } else {
                        acc = new User(row.getString(LOGIN_COL), row.getString(PASSWORD_COL));
                        if (row.getString(STATUS_COL).equals("b")) acc.block();
                        else acc.unblock();
                    }
                    list.add(acc);
                }
                System.out.printf("Select completed. Number of affected counts: %d. Number of warnings: %d\n", rows.getAffectedItemsCount(), rows.getWarningsCount());
                while (rows.getWarnings().hasNext()) {
                    Warning w = rows.getWarnings().next();
                    System.out.printf("\t%s\n", w.getMessage());
                }

            }
//            connection.close();
        }
        return list;
    }

    private void updateDB(Account acc) {
        if (acc == null) {
            System.out.println("Передан null для сохранения");
        } else {
            DBConnection connection = DBConnection.getInstance();
            if (connection != null) {
                Table table = connection.getTable(TABLE_NAME);
                if (table == null)
                    System.out.println("Невозможно открыть таблицу " + TABLE_NAME);
                else {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put(LOGIN_COL, acc.getLogin());
                    map.put(PASSWORD_COL, acc.getPassword());
                    map.put(STATUS_COL, acc.getBlocked() ? AccountStatus.blocked.getText() : AccountStatus.notBlocked.getText());
                    map.put(ROLE_COL, acc.getRole().getText());
                    Result res = table.update().set(map).where(LOGIN_COL + " = :login").bind("login", acc.getLogin()).execute();
                    System.out.printf("Update completed. Number of affected counts: %d. Number of warnings: %d\n", res.getAffectedItemsCount(), res.getWarningsCount());
                    while (res.getWarnings().hasNext()) {
                        Warning w = res.getWarnings().next();
                        System.out.printf("\t%s\n", w.getMessage());
                    }
                }
            }
        }
    }

    public void toDeletedDB(Account acc) {
        if (acc == null) {
            System.out.println("Передан null для сохранения");
        } else {
            DBConnection connection = DBConnection.getInstance();
            if (connection != null) {
                Table table = connection.getTable(TABLE_NAME);
                if (table == null)
                    System.out.println("Невозможно открыть таблицу " + TABLE_NAME);
                else {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put(STATUS_COL, AccountStatus.deleted.getText());
                    Result res = table.update().set(map).where(LOGIN_COL + " = :login").bind("login", acc.getLogin()).execute();
                    System.out.printf("Update completed. Number of affected counts: %d. Number of warnings: %d\n", res.getAffectedItemsCount(), res.getWarningsCount());
                    while (res.getWarnings().hasNext()) {
                        Warning w = res.getWarnings().next();
                        System.out.printf("\t%s\n", w.getMessage());
                    }
                }
            }
        }
    }
    private boolean searchInDB(String log){
        if (log == null) {
            System.out.println("Передан null для сохранения");
        } else {
            DBConnection connection = DBConnection.getInstance();
            if (connection != null) {
                Table table = connection.getTable(TABLE_NAME);
                if (table == null)
                    System.out.println("Невозможно открыть таблицу " + TABLE_NAME);
                else {
                    RowResult res = table.select().where(LOGIN_COL + " = :login").bind("login", log).execute();
                    if (res.hasNext()) return true;
                    return false;
                }
            }
        }
        return true;
    }
    private void restoreAccDB(String log) {
        if (log == null) {
            System.out.println("Передан null");
        } else {
            DBConnection connection = DBConnection.getInstance();
            if (connection != null) {
                Table table = connection.getTable(TABLE_NAME);
                if (table == null)
                    System.out.println("Невозможно открыть таблицу " + TABLE_NAME);
                else {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put(STATUS_COL, AccountStatus.notBlocked.getText());
                    Result res = table.update().set(map).where(LOGIN_COL + " = :login").bind("login", log).execute();
                    RowResult rows = table.select().where(LOGIN_COL + " = :login").bind("login", log).execute();
                    while (rows.hasNext()) {
                        Row row = rows.next();
                        String accRole = row.getString(ROLE_COL);
                        Account acc;
                        if (accRole.equals("a")) {
                            acc = new Admin(row.getString(LOGIN_COL), row.getString(PASSWORD_COL));
                            acc.unblock();
                        } else {
                            acc = new User(row.getString(LOGIN_COL), row.getString(PASSWORD_COL));
                            if (row.getString(STATUS_COL).equals("b")) acc.block();
                            else acc.unblock();
                        }
                        accounts.add(acc);
                    }
                    System.out.printf("Update completed. Number of affected counts: %d. Number of warnings: %d\n", res.getAffectedItemsCount(), res.getWarningsCount());
                    while (res.getWarnings().hasNext()) {
                        Warning w = res.getWarnings().next();
                        System.out.printf("\t%s\n", w.getMessage());
                    }
                }
                //connection.close();
            }
        }
    }

    public void removeFromDB(String log) {
        if (log == null) {
            System.out.println("Передан null");
        } else {
            DBConnection connection = DBConnection.getInstance();
            if (connection != null) {
                Table table = connection.getTable(TABLE_NAME);
                if (table == null)
                    System.out.println("Невозможно открыть таблицу " + TABLE_NAME);
                else {
                    Result res = table.delete().where(LOGIN_COL + " = :login").bind("login", log).execute();
                    System.out.printf("Delete completed. Number of affected counts: %d. Number of warnings: %d\n", res.getAffectedItemsCount(), res.getWarningsCount());
                    while (res.getWarnings().hasNext()) {
                        Warning w = res.getWarnings().next();
                        System.out.printf("\t%s\n", w.getMessage());
                    }
                }
//                connection.close();
            }
        }
    }

    public boolean checkAdmin() {
        return !isNoAdmin;
    }

}
