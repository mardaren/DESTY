package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;


public class LoginActivity extends AppCompatActivity {
    EditText user_name,user_mail,passwd;
    Button login_button;
    TextView reg_text;
    Connection db_conn=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        db_conn = initializeConn();
        setContentView(R.layout.activity_login);
        user_mail = this.findViewById(R.id.useremail);
        user_name = this.findViewById(R.id.username);
        passwd = this.findViewById(R.id.password);
        login_button = this.findViewById(R.id.login);
        reg_text = this.findViewById(R.id.register_text);
        reg_text.setOnClickListener(v -> register_page());
        login_button.setOnClickListener(v -> {
            String txt = login_button.getText().toString();
            if(txt.equals(getResources().getString(R.string.action_sign_in)))
                login();
            else if(txt.equals(getResources().getString(R.string.register_now)))
                signup();
        });
    }

    public Connection initializeConn(){
        Connection connection = null;
        String conn_url;

        try {
            conn_url = BuildConfig.DB_URL;
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(conn_url);
            if (connection != null) {
                System.out.println("Connected");
                System.out.println(connection.getMetaData());
            }
            else
                System.out.println("Not connected");

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void login(){
        authenticateUser(user_name.getText().toString(),passwd.getText().toString());
    }

    public void signup(){
        createUser(user_mail.getText().toString(),user_name.getText().toString(),passwd.getText().toString());
    }
    public void register_page(){
        reg_text.setVisibility(View.INVISIBLE);
        login_button.setText(R.string.register_now);
        user_mail.setVisibility(View.VISIBLE);
    }

    public byte[] getSalt() throws NoSuchAlgorithmException{
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public  byte[] getSaltedHash(String passwd, byte[] salt){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] data = md.digest(passwd.getBytes());
            md.reset();
            return data;
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger("SHA-512").log(Level.SEVERE,"SHA-512 is not valid algorithm name!",e);
            return null;
        }
    }

    public void createUser(String mail,String username,String password){
        byte[] salt = null;
        try {
            salt=getSalt();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] hashedPasswd = getSaltedHash(password,salt);

        String str_salt = byteToHex(salt);
        String str_passwd = byteToHex(hashedPasswd);

        Statement statement;
        String query = "SELECT max(id) FROM [dbo].[User]";
        int status = -1;
        try {
            statement = db_conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int id = 0;
            while(resultSet.next()){
                id = resultSet.getInt(1);
            }
            id++;
            PreparedStatement insertSt=db_conn.prepareStatement("INSERT INTO [dbo].[User] values(?,?,?,?,?)");
            insertSt.setObject(1,id);
            insertSt.setObject(2,username);
            insertSt.setObject(3,mail);
            insertSt.setObject(4,str_passwd);
            insertSt.setObject(5,str_salt);
            status = insertSt.executeUpdate();
            System.out.println(status);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if (status>0){
            Toast.makeText(this,"Sign up Successful!",Toast.LENGTH_LONG).show();
            Intent i = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
        else
            Toast.makeText(this,"Sign up Failed!",Toast.LENGTH_LONG).show();

    }

    public boolean authenticateUser(String username, String password){
        // Retrieve user here from db (password,salt)
        String db_salt = "salt_in_hex_format_from_db";
        String db_password = "password_in_hex_format_from_db";
        byte[] original_salt = hexToByte(db_salt);
        byte[] original_password = hexToByte(db_password);

        byte[] typed_password = getSaltedHash(password,original_salt);
        boolean result = Arrays.equals(original_password,typed_password);
        if (result){
            Toast.makeText(this,"Login Successful!",Toast.LENGTH_LONG).show();
            Intent i = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
        else
            Toast.makeText(this,"Login Failed!",Toast.LENGTH_LONG).show();
        return result;
    }

    public String byteToHex(byte[] arr){
        BigInteger i = new BigInteger(1,arr);
        String hex = i.toString(16);
        int padding_len = (arr.length*2) - hex.length();
        if (padding_len>0)
            return String.format("%0" + padding_len + "d", 0) + hex;
        else
            return hex;
    }

    public byte[] hexToByte(String hex){
        byte[] bytes = new byte[hex.length()/2];
        for (int i=0;i<bytes.length;i++)
            bytes[i] = (byte) Integer.parseInt(hex.substring(2*i,2*i+2),16);
        return bytes;
    }
}
