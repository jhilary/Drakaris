package ru.yandex.shad.java.drakaris.dao.jdbc;

import ru.yandex.shad.java.drakaris.dao.ConnectionFactory;
import ru.yandex.shad.java.drakaris.dao.DAOException;
import ru.yandex.shad.java.drakaris.dao.UserDAO;
import ru.yandex.shad.java.drakaris.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeoutException;

public class UserDAOJDBC implements UserDAO{
    ConnectionFactory connectionFactory;

    public UserDAOJDBC(ConnectionFactory connectionFactory){
        this.connectionFactory = connectionFactory;
    }
    @Override
    public User create(String login, String passHash) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO `Drakaris`.`Users` (`login`,`passhash`) " +
                                                            "VALUES (?, ?)",
                                                            Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, passHash);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            user = new User(resultSet.getLong(1), login, passHash);
        } catch (SQLException e) {
            if(e.getSQLState().equals("23000")){
                throw new IllegalArgumentException("User with login " + login + " exist");
            }
            throw new DAOException(e.getMessage());
        } catch (TimeoutException e) {
            throw new DAOException(e.getMessage());
        } finally {
            DAOJDBCUtil.close(resultSet);
            DAOJDBCUtil.close(preparedStatement);
            DAOJDBCUtil.close(connection);
        }
        return user;
    }

    @Override
    public User get(String login) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement("select * from Users where login=?");
            preparedStatement.setString(1, login);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                user = new User(resultSet.getLong("ID"), resultSet.getString("login"), resultSet.getString("passhash"));
            } else {
                throw new IllegalArgumentException("User with login " + login + " does not exist");
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } catch (TimeoutException e) {
            throw new DAOException(e.getMessage());
        } finally {
            DAOJDBCUtil.close(resultSet);
            DAOJDBCUtil.close(preparedStatement);
            DAOJDBCUtil.close(connection);
        }
        return user;
    }
}
