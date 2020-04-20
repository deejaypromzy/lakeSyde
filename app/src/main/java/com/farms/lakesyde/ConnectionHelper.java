package com.farms.lakesyde;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class ConnectionHelper {


    @SuppressLint("NewApi")
    public static Connection CONN() {

        String _user = "a3328c_farms";
        String _pass = "Kw@si@123@sei";
        String _DB = "db_a3328c_farms";
        String _server = "MYSQL5017.site4now.net";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://" + _server + ";"
                    + "databaseName=" + _DB + ";user=" + _user + ";password="
                    + _pass + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", Objects.requireNonNull(se.getMessage()));
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", Objects.requireNonNull(e.getMessage()));
        } catch (Exception e) {
            Log.e("ERRO", Objects.requireNonNull(e.getMessage()));
        }
        return conn;
    }

}
