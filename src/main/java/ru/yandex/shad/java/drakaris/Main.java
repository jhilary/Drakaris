package ru.yandex.shad.java.drakaris;

import org.joda.time.DateTime;
import ru.yandex.shad.java.drakaris.dao.DAOException;
import ru.yandex.shad.java.drakaris.dao.SubscriptionDAO;
import ru.yandex.shad.java.drakaris.dao.TweetDAO;
import ru.yandex.shad.java.drakaris.dao.UserDAO;
import ru.yandex.shad.java.drakaris.dao.jdbc.DBProperties;
import ru.yandex.shad.java.drakaris.dao.jdbc.DriverManagerDAOJDBCFactory;
import ru.yandex.shad.java.drakaris.dao.jdbc.SubscriptionDAOJDBC;
import ru.yandex.shad.java.drakaris.dao.jdbc.TweetDAOJDBC;
import ru.yandex.shad.java.drakaris.dao.jdbc.UserDAOJDBC;
import ru.yandex.shad.java.drakaris.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;

class ConnectionStructure{
    boolean opened;
    boolean closed;
    int availablePermits;
    @Override
    public String toString(){
        return String.format("Opened: %s Closed: %s Available permits: %d", opened, closed, availablePermits);
    }
}
public class Main {

    public static float calculateSuccesses(int numOfThreads, int connPoolSize, int timeoutMS, final int sessionTime) throws InterruptedException{
        ExecutorService threadPool = Executors.newFixedThreadPool(numOfThreads);
        final Stack<ConnectionStructure> result = new Stack<ConnectionStructure>();
        final Semaphore semaphore = new Semaphore(connPoolSize, true);

        ArrayList<Callable<Integer>> callables = new ArrayList<Callable<Integer>>();
        String driverName = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/Drakaris";
        String name = "root";
        String pass = "";
        DBProperties dbProperties = new DBProperties(driverName, url, name, pass);
        try{
            final DriverManagerDAOJDBCFactory connectionPool = new DriverManagerDAOJDBCFactory(dbProperties, semaphore, timeoutMS);
            for(int i = 0; i < numOfThreads; i++){
                callables.add(new Callable<Integer>() {
                    @Override
                    public Integer call() throws SQLException, TimeoutException {
                        ConnectionStructure cs = new ConnectionStructure();
                        Connection conn = null;
                        try {
                            conn = connectionPool.getConnection();
                            cs.opened=true;
                        } catch(TimeoutException e){
                            cs.opened = false;
                        } catch(SQLException e){
                            cs.opened = false;
                        }
                        try {
                            Thread.sleep(sessionTime);
                        } catch(InterruptedException ignored){

                        }
                        try {
                            if(conn != null){
                                connectionPool.closeConnection(conn);
                                cs.closed = true;
                            } else {
                                cs.closed = false;
                            }
                        } catch (SQLException e){
                            cs.closed = false;
                        }
                        cs.availablePermits = semaphore.availablePermits();

                        result.push(cs);
                        return 0;
                    }
                });
            }
        } catch (DAOException e){
            e.printStackTrace();
        }
        threadPool.invokeAll(callables);
        int counter = 0;
        for(ConnectionStructure r: result){
            if(r.opened){
                counter ++;
            }
        }
        threadPool.shutdown();
        return (float)counter/numOfThreads;
    }

    /**
     *  Attempt to look at behaviour depending on:
     *       Number Of Thread,
     *       Thread Pool Size
     *       Timeout limit
     *       Session Time of some connection
     * @throws InterruptedException
     */
    public static void checkBahaviour() throws InterruptedException{
        List<Integer> listNumOfThreads = Arrays.asList(20, 40, 100);
        List<Integer> listThreadPoolSize = Arrays.asList(5, 10, 20);
        List<Integer> listTimeoutMS = Arrays.asList(10, 100, 1000);
        List<Integer> listSessionTime = Arrays.asList(10, 100, 200, 500, 1000);
        for(int i: listNumOfThreads){
            for(int j: listThreadPoolSize){
                for(int k: listTimeoutMS) {
                    for(int l: listSessionTime){
                        float res = calculateSuccesses(i, j, k, l);
                        System.out.println(new ArrayList<Integer>(Arrays.asList(i,j,k,l)));
                        System.out.println(res);
                    }
                }
            }
        }
    }
    public static void main(String[] args) throws InterruptedException{

        //checkBehaviour()
        // For example:
        //  40% will success
        float res = calculateSuccesses(100, 20, 10, 100);
        System.out.println(res);

        final Semaphore semaphore = new Semaphore(20, true);
        String url = "jdbc:mysql://localhost:3306/Drakaris";
        String name = "root";
        String pass = "";
        DBProperties dbProperties = new DBProperties("com.mysql.jdbc.Driver", url, name, pass);
        try {
            final DriverManagerDAOJDBCFactory dmFactory = new DriverManagerDAOJDBCFactory(dbProperties, semaphore, 1000);
            SubscriptionDAO sDAO = new SubscriptionDAOJDBC(dmFactory);
            UserDAO uDAO = new UserDAOJDBC(dmFactory);
            TweetDAO tDAO = new TweetDAOJDBC(dmFactory);

            // Performance: get user, getSubscriptions
            User user1 = uDAO.get("Jaime Lannister");
            System.out.println(sDAO.getSubscriptions(user1));

            // Performance: createUser
            User user0 = uDAO.create("Jaqen H'ghar", "345");
            System.out.println(user0);

            // Performance: subscribe, unsubscribe, isSubscribed
            User user2 = uDAO.get("Cersei Lannister");
            System.out.println(user1.getLogin() + " subscribe to " + user2.getLogin() + ": " + sDAO.isSubscribed(user1, user2));
            System.out.println(user2.getLogin() + " subscribe to " + user1.getLogin() + ": " + sDAO.isSubscribed(user2, user1));
            System.out.println("Unsubscribe " +  user1.getLogin() + " to " + user2.getLogin());
            sDAO.unSubscribe(user1, user2);
            System.out.println(user1.getLogin() + " subscribe to " + user2.getLogin() + ": " + sDAO.isSubscribed(user1, user2));
            System.out.println("Subscribe back " +  user1.getLogin() + " to " + user2.getLogin());
            sDAO.subscribe(user1, user2);
            System.out.println(user1.getLogin() + " subscribe to " + user2.getLogin() + ": " + sDAO.isSubscribed(user1, user2));

            // Performance: create Tweet
            User user3 = uDAO.get("Tirion Lannister");
            System.out.println(user3);
            System.out.println(tDAO.create(user3, "Lannisters pay their debts", DateTime.now()));

            // Performance: getByUsers tweets
            User user4 = uDAO.get("Ygritte");
            List<User> tUsers = Arrays.asList(user0, user3, user1, user4);
            System.out.println(tDAO.getByUsers(tUsers, DateTime.now(), 2));
        } catch (DAOException e){
            e.printStackTrace();
        }

    }
}