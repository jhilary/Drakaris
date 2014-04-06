package ru.yandex.shad.java.drakaris.dao;

import ru.yandex.shad.java.drakaris.model.User;

import java.util.List;

public interface SubscriptionDAO {
    public void subscribe(User fromUser, User toUser) throws DAOException;
    public List<User> getSubscriptions(User fromUser) throws DAOException;
    public boolean isSubscribed(User fromUser, User toUser) throws DAOException;
    public void unSubscribe(User fromUser, User toUser) throws DAOException;
}
