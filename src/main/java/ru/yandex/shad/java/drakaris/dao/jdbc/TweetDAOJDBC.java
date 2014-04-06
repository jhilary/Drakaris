package ru.yandex.shad.java.drakaris.dao.jdbc;

import com.google.common.base.Joiner;
import org.joda.time.DateTime;
import ru.yandex.shad.java.drakaris.dao.ConnectionFactory;
import ru.yandex.shad.java.drakaris.dao.DAOException;
import ru.yandex.shad.java.drakaris.dao.TweetDAO;
import ru.yandex.shad.java.drakaris.model.Tweet;
import ru.yandex.shad.java.drakaris.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class TweetDAOJDBC implements TweetDAO {
    ConnectionFactory connectionFactory;

    public TweetDAOJDBC(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Tweet create(User user, String text, DateTime date) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Tweet tweet = null;
        try {
            connection = connectionFactory.getConnection();

            preparedStatement = connection.prepareStatement("INSERT INTO Drakaris.Tweets (userID, text, data) " +
                    "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, user.getID());
            preparedStatement.setString(2, text);
            preparedStatement.setTimestamp(3, new Timestamp(date.toDateTime().getMillis()));
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            tweet = new Tweet(resultSet.getLong(1), user.getID(), text, date);
        } catch (SQLException e) {
            if(e.getSQLState().equals("23000")) {
                throw new IllegalArgumentException("User " + user.getLogin() + " doesn't exist");
            }
            if(e.getSQLState().equals("42000")) {
                throw new IllegalArgumentException("Text size is too big");
            }
            throw new DAOException(e.getMessage());
        } catch (TimeoutException e) {
            throw new DAOException(e.getMessage());
        } finally {
            DAOJDBCUtil.close(resultSet);
            DAOJDBCUtil.close(preparedStatement);
            DAOJDBCUtil.close(connection);
        }
        return tweet;
    }

    @Override
    public List<Tweet> getByUsers(List<User> users, DateTime endDate, int quantity) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            List<Tweet> tweets = new ArrayList<Tweet>();
            connection = connectionFactory.getConnection();
            List<String> userIDs = new ArrayList<String>(users.size());
            for(User user: users){
                userIDs.add("userID = " + user.getID());
            }
            Joiner joiner = Joiner.on(" or ").skipNulls();
            String whereClause =joiner.join(userIDs);
            preparedStatement = connection.prepareStatement(
                    "select * from (select * from Drakaris.Tweets where " + whereClause + ") as S where data < ? order by data limit ?;");
            preparedStatement.setTimestamp(1, new Timestamp(endDate.toDateTime().getMillis()));
            preparedStatement.setInt(2, quantity);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Tweet tweet = new Tweet(resultSet.getLong("ID"), resultSet.getLong("UserID"),resultSet.getString("text"),new DateTime(resultSet.getTimestamp("data").getTime()));
                tweets.add(tweet);
            }
            return tweets;
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
