package com.example.desty;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.AsyncTask;
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


public class LoginActivity extends AppCompatActivity {
    EditText user_name,user_mail,passwd;
    Button login_button;
    TextView reg_text;
    Connection db_conn=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Connect().execute();
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

    public void login(){
        new Authenticate(user_name.getText().toString(),passwd.getText().toString()).execute();
    }

    public void signup(){
        new CreateUser(user_mail.getText().toString(),user_name.getText().toString(),passwd.getText().toString()).execute();
    }
    public void register_page(){
        reg_text.setVisibility(View.INVISIBLE);
        login_button.setText(R.string.register_now);
        user_mail.setVisibility(View.VISIBLE);
    }
    private class Connect extends AsyncTask<Void, Void, Connection> {
        @Override
        protected Connection doInBackground(Void... urls) {

            Connection connection = null;
            String conn_url;

            try {
                conn_url = BuildConfig.db_url;
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(conn_url);
                if (connection != null) {
                    Log.i("Connection Status", "Connected");
                } else
                    Log.i("Connection Status", "Not Connected");

            } catch (SQLException throwable) {
                throwable.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            db_conn=connection;

            return connection;
        }
    }

    private class CreateUser extends AsyncTask<String, String, String> {
        String mail,name,passwd;
        boolean check=false;
        @Override
        public String doInBackground(String ... strings){
            int id = 0;
            byte[] salt = null;
            try {
                salt=getSalt();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            byte[] hashedPasswd = getSaltedHash(passwd,salt);

            String str_salt = byteToHex(salt);
            String str_passwd = byteToHex(hashedPasswd);

            Statement statement;
            String query = "SELECT max(id) FROM [dbo].[User]";
            int status = -1;
            try {
                statement = db_conn.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                while(resultSet.next()){
                    id = resultSet.getInt(1);
                }
                id++;
                PreparedStatement insertSt=db_conn.prepareStatement("INSERT INTO [dbo].[User] values(?,?,?,?,?,?)");
                insertSt.setObject(1,id);
                insertSt.setObject(2,name);
                insertSt.setObject(3,mail);
                insertSt.setObject(4,str_passwd);
                insertSt.setObject(5,str_salt);
                insertSt.setObject(6,""); // photo url
                status = insertSt.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            if (status>0){
                check = true;
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                i.putExtra("UserId", id);
                try {
                    db_conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                startActivity(i);
                finish();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(check)
                Toast.makeText(getBaseContext(),"Sign up Successful!",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getBaseContext(),"Sign up Failed!", Toast.LENGTH_LONG).show();
        }

        public CreateUser(String usermail,String username,String password){
            this.mail = usermail;
            this.name = username;
            this.passwd=password;
        }
    }
    private class  Authenticate extends AsyncTask<String, String, String> {
        String name,passwd;
        boolean check=false;
        @Override
        protected String doInBackground(String... strings) {
            PreparedStatement statement;
            String query = "SELECT * FROM [dbo].[User] WHERE username = ?";
            Object[] columns = new Object[6]; // User table columns
            try {
                statement = db_conn.prepareStatement(query);
                statement.setString(1, name);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    columns[0] = resultSet.getInt(1);
                    columns[1] = resultSet.getString(2);
                    columns[2] = resultSet.getString(3);
                    columns[3] = resultSet.getString(4);
                    columns[4] = resultSet.getString(5);
                    columns[5] = resultSet.getString(6);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            String db_password = (String) columns[3];
            String db_salt = (String) columns[4];
            byte[] original_password = hexToByte(db_password);
            byte[] original_salt = hexToByte(db_salt);

            byte[] typed_password = getSaltedHash(passwd, original_salt);
            boolean result = Arrays.equals(original_password, typed_password);
            if (result) {
                check = true;
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra("UserId", (int)columns[0]);
                try {
                    db_conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                startActivity(i);
                finish();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(check)
                Toast.makeText(getBaseContext(),"Login Successful!",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getBaseContext(),"Login Failed!", Toast.LENGTH_LONG).show();
        }

        public Authenticate(String username,String password){
            this.name = username;
            this.passwd = password;
        }
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