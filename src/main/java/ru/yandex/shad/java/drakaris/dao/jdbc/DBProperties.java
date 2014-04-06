package ru.yandex.shad.java.drakaris.dao.jdbc;

public class DBProperties{
    private String driverName;
    private String url;
    private String user;
    private String password;

    public String getDriverName() {
        return driverName;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public DBProperties(String driverName, String url, String user, String password) {
        this.driverName = driverName;
        this.url = url;
        this.user = user;
        this.password = password;
    }

}