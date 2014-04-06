package ru.yandex.shad.java.drakaris.model;

public class User {
    private Long ID;
    private String login;
    private String passHash;

    public User(long ID, String login, String passHash) {
        this.ID = ID;
        this.login = login;
        this.passHash = passHash;
    }

    public String getLogin() {
        return login;
    }

    public String getPassHash() {
        return passHash;
    }

    public long getID() {
        return ID;
    }

    @Override
    public String toString(){
        return "(" + this.ID + ", " + this.login + ", " + this.passHash + ")";
    }
}
