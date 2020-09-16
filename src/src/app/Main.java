package app;

import java.sql.*;

public class Main {
    public static final String DATABASE_URL = "jdbc:sqlserver://10.20.20.98:1433;databaseName=SCPRD";
    public static final String USER = "wmwhse1";
    public static final String PASSWORD = "WMwhSql1";

    public static void main(String[] args) throws ClassNotFoundException {

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        final Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection;
                try {
                    connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
                    connection.setAutoCommit(false);
                    System.out.println("thread1 start");
                    executeQuery(connection, "SET TRANSACTION ISOLATION LEVEL SNAPSHOT;", 0);
                    executeQuery(connection, "UPDATE A SET COLUMN_A = '8'", 0);
                    // executeQuery(connection, "UPDATE B SET COLUMN_B = '5'");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    System.out.println("thread1 end");
                    connection.commit();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection;
                try {
                    connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
                    connection.setAutoCommit(false);
                    System.out.println("thread2 start");
                    executeQuery(connection, "SET TRANSACTION ISOLATION LEVEL SNAPSHOT;", 0);
                    executeQuery(connection, "UPDATE A SET COLUMN_A = '9'", 20);
                    //executeQuery(connection, "UPDATE B SET COLUMN_B = '6'");
                    System.out.println("thread2 end");
                    connection.commit();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        thread1.start();
        thread2.start();
    }

    public static void executeQuery(Connection connection, String sql, int timeout) {
        try (Statement statement = connection.createStatement()) {
            if (timeout > 0)
                statement.setQueryTimeout(timeout);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
