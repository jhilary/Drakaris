package ru.yandex.shad.java.drakaris.dao;

import ru.yandex.shad.java.drakaris.model.User;

public interface UserDAO {
    public User create(String login, String passHash) throws DAOException;
    public User get(String login) throws DAOException;
}
