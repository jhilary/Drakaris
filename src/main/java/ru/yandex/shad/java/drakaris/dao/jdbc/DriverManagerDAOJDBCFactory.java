package ru.yandex.shad.java.drakaris.dao.jdbc;

import ru.yandex.shad.java.drakaris.dao.ConnectionFactory;
import ru.yandex.shad.java.drakaris.dao.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DriverManagerDAOJDBCFactory implements ConnectionFactory{

    private Semaphore semaphore;
    private int timeoutMS;
    private volatile Set<Connection> usedPool = new HashSet<Connection>();
    private volatile Stack<Connection> freePool = new Stack<Connection>();


    public DriverManagerDAOJDBCFactory(DBProperties dbProperties, Semaphore semaphore, int timeoutMS) throws DAOException{
        this.semaphore = semaphore;
        this.timeoutMS = timeoutMS;
        try{
            Class.forName(dbProperties.getDriverName());
        } catch (ClassNotFoundException e){
            throw new IllegalArgumentException("Driver with name " + dbProperties.getDriverName() + " not found");
        }
        try {
            for(int i = 0; i < semaphore.availablePermits(); i++){
                Connection conn =  DriverManager.getConnection(dbProperties.getUrl(), dbProperties.getUser(), dbProperties.getPassword());
                freePool.add(conn);
            }
        } catch (SQLException e){
            throw new DAOException(e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException, TimeoutException {
        Connection conn = null;
        try{
            if(semaphore.tryAcquire(timeoutMS, TimeUnit.MILLISECONDS)){
                conn = acquireConnection();
            } else {
                throw new TimeoutException();
            }
        } catch (InterruptedException e){
            System.err.println("getConnection() interrupted");
        }
        return conn;
    }

    public void closeConnection(Connection connection) throws SQLException {
        releaseConnection(connection);
        semaphore.release();
    }

    private Connection acquireConnection() throws SQLException{
        Connection conn = freePool.pop();
        usedPool.add(conn);
        return conn;
    }

    private void releaseConnection(Connection conn) throws SQLException{
        if (usedPool.remove(conn)) {
            freePool.push(conn);
        } else {
            throw new IllegalArgumentException("Try to release not used connection");
        }
    }
}
