package ru.yandex.shad.java.drakaris.dao.jdbc;

import ru.yandex.shad.java.drakaris.dao.ConnectionFactory;
import ru.yandex.shad.java.drakaris.dao.DAOException;
import ru.yandex.shad.java.drakaris.dao.SubscriptionDAO;
import ru.yandex.shad.java.drakaris.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class SubscriptionDAOJDBC implements SubscriptionDAO{
    ConnectionFactory connectionFactory;

    public SubscriptionDAOJDBC(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void subscribe(User fromUser, User toUser) throws DAOException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(
                    "insert into Drakaris.Subscriptions (fromID, toID) values (?,?);");

            preparedStatement.setLong(1, fromUser.getID());
            preparedStatement.setLong(2, toUser.getID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            if(e.getSQLState().equals("23000")) {
                throw new IllegalArgumentException("User " + fromUser.getLogin() + " or user " + toUser.getLogin() + "doesn't exist");
            }
            if(e.getSQLState().equals("42000")) {
                throw new IllegalArgumentException("User " + fromUser.getLogin() + " is just subscribed to user " + toUser.getLogin());
            }
            e.printStackTrace();
        } catch (TimeoutException e) {
            throw new DAOException(e.getMessage());
        } finally {
            DAOJDBCUtil.close(resultSet);
            DAOJDBCUtil.close(preparedStatement);
            DAOJDBCUtil.close(connection);
        }
    }

    @Override
    public List<User> getSubscriptions(User fromUser) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<User> users = new ArrayList<User>();

        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement("" +
                    "select UB.ID as ID, UB.login as login, UB.passhash as passhash from\n" +
                    "(select * from Drakaris.Subscriptions where fromID=?) as SS\n" +
                    "inner join Drakaris.Users as UA \n" +
                    "on UA.ID=fromID\n" +
                    "inner join Drakaris.Users as UB\n" +
                    "on UB.ID=toID");
            preparedStatement.setLong(1, fromUser.getID());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User userTo = new User(resultSet.getLong("ID"), resultSet.getString("login"),resultSet.getString("passhash"));
                users.add(userTo);
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

        return users;
    }

    @Override
    public boolean isSubscribed(User fromUser, User toUser) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(
                    "select * from Drakaris.Subscriptions where fromID=? and toID=?");
            preparedStatement.setLong(1, fromUser.getID());
            preparedStatement.setLong(2, toUser.getID());
            resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } catch (TimeoutException e) {
            throw new DAOException(e.getMessage());
        } finally {
            DAOJDBCUtil.close(resultSet);
            DAOJDBCUtil.close(preparedStatement);
            DAOJDBCUtil.close(connection);
        }
    }

    @Override
    public void unSubscribe(User fromUser, User toUser) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(
                    "delete from Drakaris.Subscriptions where fromID=? and toID=?");
            preparedStatement.setLong(1, fromUser.getID());
            preparedStatement.setLong(2, toUser.getID());
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows == 0) {
                throw new IllegalArgumentException("Users " + fromUser.getLogin() + " and " + toUser.getLogin() + "are not subscribed");
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
    }
}
