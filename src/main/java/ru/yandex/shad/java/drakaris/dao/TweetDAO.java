package ru.yandex.shad.java.drakaris.dao;

import org.joda.time.DateTime;
import ru.yandex.shad.java.drakaris.model.Tweet;
import ru.yandex.shad.java.drakaris.model.User;

import java.util.List;

public interface TweetDAO {
    public Tweet create(User user, String text, DateTime date) throws DAOException;
    public List<Tweet> getByUsers(List<User> users, DateTime endDate, int quantity) throws DAOException;
}
