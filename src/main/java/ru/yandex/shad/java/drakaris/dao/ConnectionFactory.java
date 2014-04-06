package ru.yandex.shad.java.drakaris.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public interface ConnectionFactory {
    public Connection getConnection() throws SQLException, TimeoutException;
    public void closeConnection(Connection connection) throws SQLException;
}
