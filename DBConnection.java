package org.example.app.DBConnection;

import com.mysql.cj.xdevapi.*;

import java.lang.module.Configuration;
import java.sql.*;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DBConnection {
    private static final String USER_NAME = "Polina";
    private static final String PASSWORD = "zaq!2wsX";
    private static final String URL = "jdbc:mysql://localhost:3306/mysql";
    private static final String SCHEMA_NAME = "chocolate_shop";
    private static Properties properties;
    private Session session;
    private Schema database;
    private static DBConnection instance;

    private DBConnection() {
        properties = new Properties();
        properties.put("host", "localhost");
        properties.put("user", USER_NAME);
        properties.put("password", PASSWORD);
    }

    private Session openConnection() {
        if (session == null) {
            try {
                session = new SessionFactory().getSession(properties);
            } catch (Exception exc) {
                exc.printStackTrace();
                session = null;
            }
        }
        return session;
    }

    private Schema openDatabase() {
        if (database == null){
            try {
                database = session.getSchema(SCHEMA_NAME);
            } catch (Exception exc) {
                exc.printStackTrace();
                database = null;
            }
        }
        return database;
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        if (instance != null) {
            if (instance.openConnection() != null) {
                if (instance.openDatabase() == null) System.out.println("Scheme failed to open");
            } else System.out.println("Connection failed to establish");
            if (instance.session == null || instance.database == null) {
                instance.close();
                instance = null;
            }
        }
        return instance;
    }

    public void close() {
        if (session != null) session.close();
        session = null;
    }

    public Table getTable(String tableName) {
        Table t = null;
        if (database != null) {
            try {
                t = database.getTable(tableName);
            } catch (Exception exc) {
                exc.printStackTrace();
                t = null;
            }
        }
        return t;
    }
}

