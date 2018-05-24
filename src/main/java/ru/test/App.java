package ru.test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Properties;

public class App {

    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Properties props = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.properties");
        props.load(in);

        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        Connection conn = DriverManager.getConnection(props.getProperty("url"), props.getProperty("user"), props.getProperty("password"));
        Statement stmt = conn.createStatement();
        /*stmt.execute("CREATE TABLE MYDATA (ID BIGINT PRIMARY KEY, NAME VARCHAR(255), AGE INT )");
        stmt.close();

        stmt = conn.createStatement();
        stmt.execute("INSERT INTO MYDATA VALUES (1, 'TEST1', 32), (2, 'TEST2', 44)");
        stmt.close();

        stmt = conn.createStatement();*/

        UserDataSet uds = new UserDataSet("test3", 33,  3L);
        //saveConcrete(uds, conn);
        UserDataSet resultUserDataSet = load(2L, UserDataSet.class, conn);
        System.out.println(resultUserDataSet);
        ResultSet results = stmt.executeQuery("SELECT * FROM MYDATA");
        results.close();
        stmt.close();

        conn.close();
    }

    public static void saveConcrete(UserDataSet uds, Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("INSERT INTO MYDATA VALUES (" + uds.getId() + ", '" + uds.getName() + "', " + uds.getAge() + ")");
        stmt.close();
    }

    public static <T extends DataSet> void save(T uds) {

    }

    public static <T extends DataSet> T load(long id, Class<T> tClass, Connection conn) throws SQLException, IllegalAccessException, InstantiationException {
        T elem = tClass.newInstance();
        for(Field field : elem.getClass().getDeclaredFields()) {
            field.setAccessible(true);
        }

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM MYDATA WHERE ID = " + id + "");

        rs.next();
        for(Field field : elem.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            field.set(elem , rs.getObject(field.getName()));
        }
        elem.setId(id);

        stmt.close();
        return elem;
    }
}
