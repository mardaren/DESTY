package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initializeConn();
    }

    public Connection initializeConn(){
        Connection connection = null;
        String conn_url;

        try {
            conn_url = BuildConfig.DB_URL;
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(conn_url);

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }
    /* kullanıcı bilgileri doğru mu kontrlü, deneme için ekledim */
    private boolean profileCheck(Connection conn,String userName,String password) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM [User] WHERE username = ? AND password = ?  ");
        ps.setObject(1, userName);
        ps.setObject(2, password);
        ResultSet rs = ps.executeQuery();
        if(rs == null)
            return false ;
        return true ;

    }
    /* kullanıcı ekleme */
    private boolean addUser(Connection conn,String userName,String password) throws SQLException {
        PreparedStatement ps1 = conn.prepareStatement("SELECT MAX (id) FROM [User] ");
        ResultSet rs = ps1.executeQuery();
        int id = rs.getInt(1) + 1;
        PreparedStatement ps = conn.prepareStatement("INSERT INTO [user] VALUES (?,?,?)");
        ps.setObject(1, id);
        ps.setObject(2, userName);
        ps.setObject(3, password);
        ResultSet rs1 = ps.executeQuery();


        return true ;

    }
}